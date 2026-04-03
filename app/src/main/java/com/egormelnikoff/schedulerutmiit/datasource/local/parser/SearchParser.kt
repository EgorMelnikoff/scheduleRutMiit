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


    /* Subjects list */
    suspend fun parsePagesCount(element: Element): Int = withContext(Dispatchers.Default) {
        return@withContext element.select("ul.pagination li[data-page]")
            .mapNotNull {
                it.attr("data-page").toIntOrNull()
            }
            .maxOrNull() ?: 1
    }

    suspend fun parseListSubjectsByPage(
        element: Element
    ): MutableMap<String, MutableSet<String>> = withContext(
        Dispatchers.Default
    ) {
        val subjectTeachers = mutableMapOf<String, MutableSet<String>>()

        element.select("div[itemprop=teachingStaff]").forEach { item ->
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
        return@withContext subjectTeachers
    }
}