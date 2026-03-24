package com.egormelnikoff.schedulerutmiit.data.datasource.local.parser

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.NewsContent
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.Endpoints.BASE_MIIT_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

object NewsParser {
    suspend operator fun invoke(news: News): NewsContent = withContext(Dispatchers.Default) {
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

        return@withContext NewsContent(
            news, parsedElements, parsedImages
        )
    }



    /* CONVERTERS */
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
