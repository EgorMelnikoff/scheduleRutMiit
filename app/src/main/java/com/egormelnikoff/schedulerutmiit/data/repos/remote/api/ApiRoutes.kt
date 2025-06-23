package com.egormelnikoff.schedulerutmiit.data.repos.remote.api

object ApiRoutes {
    private const val BASE_URL = "https://rut-miit.ru/data-service/data/"
    const val GROUPS = "${BASE_URL}timetable/groups-catalog"

    const val GROUP_SCHEDULE = "${BASE_URL}timetable/v2/group/"
    const val PERSON_SCHEDULE = "${BASE_URL}timetable/v2/person/"
    const val ROOM_SCHEDULE = "${BASE_URL}timetable/v2/room/"

    const val NEWS_CATALOG = "${BASE_URL}news?idk_information_category=2&page_size=20"
    const val NEWS = "${BASE_URL}news/"
}