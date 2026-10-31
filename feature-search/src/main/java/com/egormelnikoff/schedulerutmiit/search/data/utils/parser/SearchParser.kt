package com.egormelnikoff.schedulerutmiit.search.data.utils.parser

import com.egormelnikoff.schedulerutmiit.search.data.remote.model.PersonDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.nodes.Element

internal object SearchParser {
    suspend fun parsePeople(element: Element): List<PersonDto> = withContext(Dispatchers.Default) {
        val people = mutableListOf<PersonDto>()
        element.select("div.search__people").forEach { item ->
            val aElement = item.selectFirst("a.mb-2")
            val spanElement = item.selectFirst("span[itemprop=Post]")
            if (aElement != null && spanElement != null) {
                val name = aElement.text()
                val id = aElement.attr("href")
                    .substringAfter("/people/")
                    .toIntOrNull() ?: -1
                val position = spanElement.text().trim()
                people.add(PersonDto(name, id, position))
            }
        }
        return@withContext people
    }
}