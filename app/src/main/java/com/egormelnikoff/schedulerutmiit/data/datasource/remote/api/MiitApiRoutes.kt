package com.egormelnikoff.schedulerutmiit.data.datasource.remote.api

object MiitApiRoutes {
    const val BASE_URL = "https://rut-miit.ru/"
    private const val DATA = "data-service/data/"

    const val GROUPS = "${DATA}timetable/groups-catalog"
    const val TIMETABLE = "${DATA}timetable/v2/{type}/{apiId}"
    const val SCHEDULE = "${DATA}timetable/v2/{type}/{apiId}/{timetableId}"

    const val NEWS_CATALOG = "${DATA}news?idk_information_category=2&page_size=20"
    const val NEWS = "${DATA}news/{newsId}"
}