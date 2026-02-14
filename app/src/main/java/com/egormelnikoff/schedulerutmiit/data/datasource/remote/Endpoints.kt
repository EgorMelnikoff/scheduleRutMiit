package com.egormelnikoff.schedulerutmiit.data.datasource.remote

import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType

object Endpoints {
    //API
    const val BASE_RUT_MIIT_URL = "https://rut-miit.ru/"
    private const val DATA_SERVICE = "data-service/data/"

    const val GROUPS = "${DATA_SERVICE}timetable/groups-catalog"
    const val TIMETABLE = "${DATA_SERVICE}timetable/v2/{type}/{apiId}"
    const val SCHEDULE = "${DATA_SERVICE}timetable/v2/{type}/{apiId}/{timetableId}"

    const val NEWS_CATALOG = "${DATA_SERVICE}news?idk_information_category=2"
    const val NEWS = "${DATA_SERVICE}news/{newsId}"

    //PARSER
    const val BASE_MIIT_URL = "https://www.miit.ru"

    fun curriculumProfessorsUrl(id: String, page: Int) =
        "${BASE_MIIT_URL}/edu/curriculum/$id/professors?page=$page"

    fun peopleUrl(query: String) = "${BASE_MIIT_URL}/people?query=$query"

    fun scheduleUrl(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        startDate: String,
        type: String,
    ): String {
        return when (namedScheduleType) {
            NamedScheduleType.PERSON -> "${BASE_MIIT_URL}/people/$apiId/timetable?start=$startDate&type=$type"
            else -> "${BASE_MIIT_URL}/timetable/$apiId?start=$startDate&type=$type"
        }
    }


    fun personImageUrl(personId: Int?, width: Int = 100) =
        "$BASE_MIIT_URL/content/e$personId.jpg?id_fe=$personId&SWidth=$width"
}