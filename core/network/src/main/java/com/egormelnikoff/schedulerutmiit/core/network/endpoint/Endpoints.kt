package com.egormelnikoff.schedulerutmiit.core.network.endpoint

import androidx.core.net.toUri

object Endpoints {
    //API GITHUB
    const val API_GITHUB = "https://api.github.com"
    const val APP_GITHUB_API_LATEST_RELEASE = "/repos/EgorMelnikoff/scheduleRutMiit/releases/latest"

    //API MIIT
    const val BASE_RUT_MIIT_URL = "https://rut-miit.ru"
    const val BASE_MIIT_URL = "https://www.miit.ru"


    @Deprecated("Новая ручка")
    const val GROUPS = "/data-service/data/timetable/groups-catalog"
    @Deprecated("Новая ручка")
    const val TIMETABLE = "/data-service/data/timetable/v2/{type}/{apiId}"
    @Deprecated("Новая ручка")
    const val SCHEDULE = "/data-service/data/timetable/v2/{type}/{apiId}/{timetableId}"
    @Deprecated("Новая ручка")
    const val NEWS_CATALOG = "/data-service/data/news"
    @Deprecated("Новая ручка")
    const val NEWS = "/data-service/data/news/{newsId}"

    const val SCHEDULE_GROUP_V2 = "/api/timetable/{groupId}"
    const val SCHEDULE_PERSON_V2 = "/api/profTimetable/{personId}"

    const val GROUPS_V2 = "/api/timetableCatalog"
    const val PEOPLE_V2 = "/api/getPeopleByDeptAndName"

    const val NEWS_CATEGORIES_V2 = "/api/getAllCategories"
    const val NEWS_LIST_V2 = "/api/findArticleByName"
    const val NEWS_V2 = "/api/getArticle/{newsId}"



    fun downloadUrl(
        isPerson: Boolean,
        apiId: Int,
        type: Int
    ): String {
        return if (isPerson) {
            "${BASE_MIIT_URL}/api/pdf-processor/profTimetable/${apiId}/pdf?type=${type}"
        } else {
            "${BASE_MIIT_URL}/api/pdf-processor/timetable/${apiId}/pdf?type=${type}"
        }
    }


    fun peopleUrl(query: String) =
        "${BASE_RUT_MIIT_URL}/people?query=$query"

    fun curriculumProfessorsUrl(id: String, page: Int) =
        "${BASE_RUT_MIIT_URL}/edu/curriculum/$id/professors?page=$page"

    fun personImageUrl(personId: Int?, width: Int = 100) =
        "${BASE_MIIT_URL}/content/e$personId.jpg?id_fe=$personId&SWidth=$width"

    fun newsImageUrl(url: String, width: Int = 360): String {
        val uri = url.toUri()
        return "$BASE_MIIT_URL/content/photo.jpg?id_wm=${uri.getQueryParameter("id_wm")}&SWidth=$width"
    }
}