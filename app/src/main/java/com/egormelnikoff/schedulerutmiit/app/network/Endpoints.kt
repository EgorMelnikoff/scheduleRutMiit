package com.egormelnikoff.schedulerutmiit.app.network

import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType

object Endpoints {
    const val BASE_RUT_MIIT_URL = "https://rut-miit.ru/"
    const val BASE_MIIT_URL = "https://www.miit.ru"
    const val API_GITHUB = "https://api.github.com/"


    const val GROUPS = "data-service/data/timetable/groups-catalog"
    const val TIMETABLE = "data-service/data/timetable/v2/{type}/{apiId}"
    const val SCHEDULE = "data-service/data/timetable/v2/{type}/{apiId}/{timetableId}"

    const val NEWS_CATALOG = "data-service/data/news?idk_information_category=2"
    const val NEWS = "data-service/data/news/{newsId}"

    fun curriculumProfessorsUrl(id: String, page: Int) =
        "${BASE_RUT_MIIT_URL}edu/curriculum/$id/professors?page=$page"

    fun peopleUrl(query: String) =
        "${BASE_RUT_MIIT_URL}people?query=$query"

    fun scheduleUrl(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        startDate: String,
        timetableType: String,
    ) = when (namedScheduleType) {
        NamedScheduleType.PERSON -> "${BASE_RUT_MIIT_URL}people/$apiId/timetable?start=$startDate&type=$timetableType"
        else -> "${BASE_RUT_MIIT_URL}timetable/$apiId?start=$startDate&type=$timetableType"
    }
    
    fun personImageUrl(personId: Int?, width: Int = 100) =
        "$BASE_MIIT_URL/content/e$personId.jpg?id_fe=$personId&SWidth=$width"

    const val APP_CHANNEL_URL = "https://t.me/schedule_rut_miit"
    const val APP_GITHUB_REPOS = "https://github.com/EgorMelnikoff/scheduleRutMiit"
    const val APP_GITHUB_LATEST_RELEASE_DOWNLOAD = "https://github.com/EgorMelnikoff/scheduleRutMiit/releases/latest/download/app-release.apk"
    const val APP_GITHUB_API_LATEST_RELEASE = "repos/EgorMelnikoff/scheduleRutMiit/releases/latest"
    const val RU_STORE = "https://www.rustore.ru/catalog/app/com.egormelnikoff.schedulerutmiit"
    const val AUTHOR_CHANNEL_URL = "https://t.me/EgorMelnikoff"
}