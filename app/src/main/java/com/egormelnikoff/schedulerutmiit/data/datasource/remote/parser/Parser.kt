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
import com.egormelnikoff.schedulerutmiit.app.model.TelegramPage
import com.egormelnikoff.schedulerutmiit.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import javax.inject.Inject

interface Parser {
    suspend fun parsePeople(url: String): Result<List<Person>>
    suspend fun parseChannelInfo(url: String): Result<TelegramPage>
    suspend fun parseCurrentWeek(url: String): Int
    fun parseNews(news: News): News
}

class ParserImpl @Inject constructor(
    private val parserHelper: ParserHelper
) : Parser {
    override suspend fun parsePeople(url: String): Result<List<Person>> {
        return withContext(Dispatchers.IO) {
            val document = parserHelper.callParserWithExceptions(
                fetchDataType = "Person",
                message = "URL: $url"
            ) {
                Jsoup.connect(url).get()
            }
            when (document) {
                is Result.Success -> {
                    val people = mutableListOf<Person>()
                    val searchPeople = document.data.select("div.search__people")
                    if (searchPeople.isNotEmpty()) {
                        for (searchPerson in searchPeople) {
                            val aElement = searchPerson.selectFirst("a.mb-2")
                            val spanElement = searchPerson.selectFirst("span[itemprop=Post]")
                            if (aElement != null && spanElement != null) {
                                val name = aElement.text()
                                val id =
                                    aElement.attr("href").substringAfter("/people/").toIntOrNull()
                                        ?: -1
                                val position = spanElement.text().trim()
                                people.add(Person(name, id, position))
                            }
                        }
                    }
                    Result.Success(people)
                }

                is Result.Error -> {
                    Result.Error(document.error)
                }
            }
        }
    }

    override suspend fun parseChannelInfo(url: String): Result<TelegramPage> {
        return withContext(Dispatchers.IO) {
            val document = parserHelper.callParserWithExceptions(
                fetchDataType = "ChannelInfo",
                message = "URL: $url"
            ) {
                Jsoup.connect(url).get()
            }
            when (document) {
                is Result.Success -> {
                    val page = document.data.selectFirst("div.tgme_header_info")
                    val imageUrl = page?.select(".tgme_page_photo_image img")?.attr("src")
                    val title = page?.select("div.tgme_header_title")?.text()
                    Result.Success(
                        TelegramPage(
                            url = url,
                            name = title,
                            imageUrl = imageUrl
                        )
                    )
                }

                is Result.Error -> {
                    Result.Error(document.error)
                }
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
                        parsedImages.add("https://www.miit.ru$imageUrl")
                    }
                }
            }
        }

        news.elements = parsedElements
        news.images = parsedImages
        return news
    }

    override suspend fun parseCurrentWeek(url: String): Int {
        return withContext(Dispatchers.IO) {

            val document = parserHelper.callParserWithExceptions(
                fetchDataType = "Current week",
                message = "URL: $url"
            ) {
                Jsoup.connect(url).get()
            }
            when (document) {
                is Result.Success -> {
                    val activeLink = document.data.select(".nav-link.active").first()
                    val weekText = activeLink?.text()
                    val weekNumber = weekText?.split(" ")?.get(0)
                    val romanToArabic = mapOf("I" to 1, "II" to 2, "III" to 3, "IV" to 4, "V" to 5)
                    val weekNumberInt = romanToArabic[weekNumber]
                    weekNumberInt ?: 1
                }

                is Result.Error -> {
                    1
                }
            }

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

