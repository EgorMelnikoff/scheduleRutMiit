package com.egormelnikoff.schedulerutmiit.datasource.local.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.nodes.Element

object SubjectsListParser {
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