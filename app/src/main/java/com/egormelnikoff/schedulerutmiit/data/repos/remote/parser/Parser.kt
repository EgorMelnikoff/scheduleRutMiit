package com.egormelnikoff.schedulerutmiit.data.repos.remote.parser

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.classes.TelegramPage
import com.egormelnikoff.schedulerutmiit.classes.News
import com.egormelnikoff.schedulerutmiit.data.repos.Result
import com.egormelnikoff.schedulerutmiit.classes.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

class Parser {
    companion object {
        suspend fun parsePeople(url: String): Result<List<Person>> {
            return withContext(Dispatchers.IO) {
                val document = Jsoup.connect(url).get()
                try {
                    val people = mutableListOf<Person>()
                    val searchPeople = document.select("div.search__people")
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
                } catch (e: Exception) {
                    Result.Error(e)
                }
            }
        }

        suspend fun parseChannelInfo(url: String): Result<TelegramPage> {
            return withContext(Dispatchers.IO) {
                val document = Jsoup.connect(url)
                try {
                    val page = document.get().selectFirst("div.tgme_page")
                    val imageUrl = page?.select("img.tgme_page_photo_image")?.attr("src")
                    val title = page?.select("div.tgme_page_title span")?.text()
                    Result.Success(
                        TelegramPage(
                            url = url,
                            name = title,
                            imageUrl = imageUrl
                        )
                    )

                } catch (e: IOException) {
                    Result.Error(e)
                }
            }
        }

        fun parseNews(news: News): News {
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
    }
}

fun htmlToAnnotatedString(html: String): AnnotatedString {
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