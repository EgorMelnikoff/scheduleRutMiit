package com.egormelnikoff.schedulerutmiit.app.network

import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType

object Endpoints {
    //API GITHUB
    const val API_GITHUB = "https://api.github.com"
    const val APP_GITHUB_API_LATEST_RELEASE = "/repos/EgorMelnikoff/scheduleRutMiit/releases/latest"

    //API MIIT
    const val BASE_RUT_MIIT_URL = "https://rut-miit.ru"
    const val GROUPS = "/data-service/data/timetable/groups-catalog"
    const val TIMETABLE = "/data-service/data/timetable/v2/{type}/{apiId}"
    //const val SCHEDULE = "/data-service/data/timetable/v2/{type}/{apiId}/{timetableId}"
    const val NEWS_CATALOG = "/data-service/data/news"
    const val NEWS = "/data-service/data/news/{newsId}"

    //WEB MIIT
    fun scheduleUrl(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        startDate: String,
        timetableType: String,
    ) = when (namedScheduleType) {
        NamedScheduleType.PERSON -> "${BASE_RUT_MIIT_URL}/people/$apiId/timetable?start=$startDate&type=$timetableType"
        else -> "${BASE_RUT_MIIT_URL}/timetable/$apiId?start=$startDate&type=$timetableType"
    }

    fun peopleUrl(query: String) =
        "${BASE_RUT_MIIT_URL}/people?query=$query"

    fun curriculumProfessorsUrl(id: String, page: Int) =
        "${BASE_RUT_MIIT_URL}/edu/curriculum/$id/professors?page=$page"

    fun personImageUrl(personId: Int?, width: Int = 100) =
        "${BASE_RUT_MIIT_URL}/content/e$personId.jpg?id_fe=$personId&SWidth=$width"

    //LINKS
    const val TG_APP_CHANNEL_URL = "https://t.me/schedule_rut_miit"
    const val TG_AUTHOR_CHANNEL_URL = "https://t.me/EgorMelnikoff"
    const val GITHUB_APP_REPOS = "https://github.com/EgorMelnikoff/scheduleRutMiit"
    const val GITHUB_APP_LATEST_RELEASE_DOWNLOAD =
        "${GITHUB_APP_REPOS}/releases/latest/download/app-release.apk"
    const val RU_STORE = "https://www.rustore.ru/catalog/app/com.egormelnikoff.schedulerutmiit"
}