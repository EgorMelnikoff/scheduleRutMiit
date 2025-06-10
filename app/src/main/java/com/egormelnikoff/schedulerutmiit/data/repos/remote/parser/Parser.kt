package com.egormelnikoff.schedulerutmiit.data.repos.remote.parser

import com.egormelnikoff.schedulerutmiit.classes.TelegramPage
import com.egormelnikoff.schedulerutmiit.classes.News
import com.egormelnikoff.schedulerutmiit.data.repos.Result
import com.egormelnikoff.schedulerutmiit.classes.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import org.jsoup.Jsoup

class Parser {
    companion object {
        suspend fun parsePeople(url: String): Result<List<Person>> {
            return withContext(Dispatchers.IO) {
                val document = Jsoup.connect(url).get()
                return@withContext try {
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
                return@withContext try {
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
            val paragraphs = mutableListOf<String>()
            val imageUrls = mutableListOf<String>()

            val paragraphElements = document.select("p, li")
            val imageElements = document.select("img")
            for (element in paragraphElements) {
                val text = element.text().trim()
                if (text.isNotEmpty()) {
                    var formattedText = text
                    if (element.tagName() == "li") {
                        formattedText = "• $text"
                    }
                    paragraphs.add(formattedText)
                }
            }

            for (element in imageElements) {
                val imageUrl = element.attr("src")
                if (imageUrl.isNotEmpty()) {
                    imageUrls.add("https://www.miit.ru$imageUrl")
                }
            }
            news.paragraphs = paragraphs
            news.imagesUrl = imageUrls
            return news
        }
    }
}