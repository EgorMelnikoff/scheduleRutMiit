package com.egormelnikoff.schedulerutmiit.datasource.local.parser

import com.egormelnikoff.schedulerutmiit.app.network.model.PersonModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.nodes.Element

object SearchParser {
    suspend fun parsePeople(element: Element): List<PersonModel> = withContext(Dispatchers.Default) {
        val people = mutableListOf<PersonModel>()
        element.select("div.search__people").forEach { item ->
            val aElement = item.selectFirst("a.mb-2")
            val spanElement = item.selectFirst("span[itemprop=Post]")
            if (aElement != null && spanElement != null) {
                val name = aElement.text()
                val id = aElement.attr("href")
                    .substringAfter("/people/")
                    .toIntOrNull() ?: -1
                val position = spanElement.text().trim()
                people.add(PersonModel(name, id, position))
            }
        }
        return@withContext people
    }
}