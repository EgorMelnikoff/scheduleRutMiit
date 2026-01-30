package com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.app.model.Subject
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.Endpoints
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.Endpoints.BASE_MIIT_URL
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import javax.inject.Inject

interface Parser {
    suspend fun parsePeople(query: String): Result<List<Person>>
    suspend fun parseListSubjects(id: String): Result<List<Subject>>
    suspend fun parseCurrentWeek(apiId: String): Int
    fun parseNews(news: News): News
}

class ParserImpl @Inject constructor(
    private val parserHelper: ParserHelper
) : Parser {
    override suspend fun parsePeople(query: String): Result<List<Person>> {
        val document = parserHelper.callParserWithExceptions(
            requestType = "Person",
            requestParams = "Query: $query"
        ) {
            Jsoup.connect(Endpoints.peopleUrl(query)).get()
        }

        return when (document) {
            is Result.Success -> {
                val people = mutableListOf<Person>()
                document.data.select("div.search__people").forEach { item ->
                    val aElement = item.selectFirst("a.mb-2")
                    val spanElement = item.selectFirst("span[itemprop=Post]")
                    if (aElement != null && spanElement != null) {
                        val name = aElement.text()
                        val id = aElement.attr("href")
                            .substringAfter("/people/")
                            .toIntOrNull() ?: -1
                        val position = spanElement.text().trim()
                        people.add(Person(name, id, position))
                    }
                }
                Result.Success(people)
            }

            is Result.Error -> {
                Result.Error(document.typedError)
            }
        }
    }

    override fun parseNews(news: News): News {
        val document = Jsoup.parse(news.content)
        val elements = document.select("p, li, tr, img")
        val parsedElements = mutableListOf<Pair<String, Any>>()
        val parsedImages = mutableListOf<String>()
        for (element in elements) {
            when (element.tagName()) {
                "p" -> {
                    val annotatedString = htmlToAnnotatedString(element.html())
                    if (annotatedString.isNotEmpty()) {
                        parsedElements.add(Pair("p", annotatedString))
                    }
                }

                "li" -> {
                    val annotatedString = htmlToAnnotatedString("• ${element.html()}")
                    if (annotatedString.isNotEmpty()) {
                        parsedElements.add(Pair("li", annotatedString))
                    }
                }

                "tr" -> {
                    val tableRow = element.select("td")
                    val tableRowItems = mutableListOf<String>()
                    tableRow.forEach { td ->
                        val text = td.text().trim()
                        if (text.isNotEmpty()) {
                            tableRowItems.add(text)
                        }
                    }
                    parsedElements.add(Pair("tr", tableRowItems))
                }

                "img" -> {
                    val imageUrl = element.attr("src")
                    if (imageUrl.isNotEmpty()) {
                        parsedImages.add("$BASE_MIIT_URL$imageUrl")
                    }
                }
            }
        }

        news.elements = parsedElements
        news.images = parsedImages
        return news
    }

    override suspend fun parseListSubjects(id: String): Result<List<Subject>> = coroutineScope {
        val document = parserHelper.callParserWithExceptions(
            requestType = "Subjects",
            requestParams = "Id: $id; Page: 1"
        ) {
            Jsoup.connect(Endpoints.curriculumProfessorsUrl(id, 1)).get()
        }

        when (document) {
            is Result.Error -> {
                return@coroutineScope document
            }

            is Result.Success -> {
                val result = mutableListOf<Subject>()
                result += parseListSubjectsByPage(document.data).toSubjects()

                val pages = parsePagesCount(document.data)
                if (pages > 1) {
                    val deferredPages = (2..pages).map { currentPage ->
                        async {
                            parserHelper.callParserWithExceptions(
                                requestType = "Subjects",
                                requestParams = "Id: $id; Page: $currentPage"
                            ) {
                                Jsoup.connect(
                                    Endpoints.curriculumProfessorsUrl(id, currentPage)
                                ).get()
                            }
                        }
                    }

                    val results = deferredPages.awaitAll()

                    results.forEach { item ->
                        when (item) {
                            is Result.Error -> {
                                return@coroutineScope item
                            }

                            is Result.Success -> {
                                result += parseListSubjectsByPage(item.data).toSubjects()
                            }
                        }
                    }
                }
                return@coroutineScope Result.Success(result.normalize().sortedBy { it.title })
            }
        }
    }

    private fun parseListSubjectsByPage(document: Document): MutableMap<String, MutableSet<String>> {
        val subjectTeachers = mutableMapOf<String, MutableSet<String>>()

        document.select("div[itemprop=teachingStaff]").forEach { item ->
            val teacher = item.selectFirst("a[itemprop=fio]")?.text()?.trim()
            val subjects = item.select("span[itemprop=teachingDiscipline]").mapNotNull {
                it.text().trim().takeIf { t -> t.isNotBlank() }
            }
            if (teacher.isNullOrBlank() || subjects.isEmpty()) {
                return@forEach
            }
            subjects.forEach { subject ->
                subjectTeachers.getOrPut(subject) {
                    mutableSetOf()
                }.add(teacher)
            }
        }
        return subjectTeachers
    }

    private fun MutableMap<String, MutableSet<String>>.toSubjects(): List<Subject> {
        val result = mutableListOf<Subject>()

        this.forEach { (subject, teachers) ->
            result.add(
                Subject(
                    title = subject,
                    teachers = teachers
                )
            )
        }

        return result
    }

    private fun List<Subject>.normalize(): List<Subject> {
        val map = mutableMapOf<String, MutableSet<String>>()

        forEach { subject ->
            subject.title.split(";").map {
                it.trim()
            }.forEach { splited ->
                map.getOrPut(splited) {
                    mutableSetOf()
                }.addAll(subject.teachers)
            }
        }

        return map.map { (title, teachers) ->
            Subject(
                title = title,
                teachers = teachers
            )
        }
    }

    private fun parsePagesCount(document: Document): Int {
        return document.select("ul.pagination li[data-page]")
            .mapNotNull { it.attr("data-page").toIntOrNull() }
            .maxOrNull() ?: 1
    }

    override suspend fun parseCurrentWeek(apiId: String): Int {
        val document = parserHelper.callParserWithExceptions(
            requestType = "Current week",
            requestParams = "id: $apiId"
        ) {
            Jsoup.connect(Endpoints.timetableUrl(apiId)).get()
        }

        return when (document) {
            is Result.Success -> {
                val activeLink = document.data.select(".nav-link.active").first()
                val weekText = activeLink?.text()
                val weekNumber = weekText?.split(" ")?.get(0)
                val romanToArabic = mapOf("I" to 1, "II" to 2, "III" to 3, "IV" to 4)
                romanToArabic[weekNumber] ?: 1
            }

            is Result.Error -> 1
        }
    }

    private fun htmlToAnnotatedString(html: String): AnnotatedString {
        val body = Jsoup.parse(html).body()

        return buildAnnotatedString {
            body.childNodes().forEach { node ->
                when (node) {
                    is TextNode -> {
                        append(node.text())
                    }

                    is Element -> {
                        if (node.tagName() == "a") {
                            val url = node.attr("href")
                            val linkText = node.text()

                            pushLink(
                                LinkAnnotation.Url(
                                    url = url,
                                    styles = TextLinkStyles(
                                        style = SpanStyle(
                                            textDecoration = TextDecoration.Underline,
                                            fontSize = 16.sp
                                        )
                                    )
                                )
                            )
                            append(linkText)
                            pop()
                        } else {
                            append(node.text())
                        }
                    }
                }
            }
        }
    }
}
