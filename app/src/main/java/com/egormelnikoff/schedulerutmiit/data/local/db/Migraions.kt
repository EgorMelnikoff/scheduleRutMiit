package com.egormelnikoff.schedulerutmiit.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE NamedSchedules_new (
                NamedScheduleId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                fullName TEXT NOT NULL,
                shortName TEXT NOT NULL,
                apiId TEXT,
                type INTEGER NOT NULL,
                isDefaultNamedSchedule INTEGER NOT NULL,
                lastTimeUpdate INTEGER NOT NULL
            )
        """)
        db.execSQL("""
            INSERT INTO NamedSchedules_new (NamedScheduleId, fullName, shortName, apiId, type, isDefaultNamedSchedule, lastTimeUpdate)
            SELECT NamedScheduleId, fullName, shortName, apiId, type, isDefaultNamedSchedule, lastTimeUpdate FROM NamedSchedules
        """)
        db.execSQL("DROP TABLE NamedSchedules")
        db.execSQL("ALTER TABLE NamedSchedules_new RENAME TO NamedSchedules")


        db.execSQL("""
            CREATE TABLE Schedules_new (
                ScheduleId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                namedScheduleId INTEGER NOT NULL,
                timetableId TEXT NOT NULL,
                typeName TEXT NOT NULL,
                startName TEXT NOT NULL,
                downloadUrl TEXT,
                startDate TEXT NOT NULL,
                endDate TEXT NOT NULL,
                frequency TEXT,
                interval INTEGER,
                currentNumber INTEGER,
                firstWeekNumber INTEGER,
                isDefaultSchedule INTEGER NOT NULL DEFAULT 0
            )
        """)
        db.execSQL("""
            INSERT INTO Schedules_new (ScheduleId, namedScheduleId, timetableId, typeName, startName, downloadUrl, startDate, endDate, frequency, interval, currentNumber, firstWeekNumber, isDefaultSchedule)
            SELECT ScheduleId, namedScheduleId, timetableId, typeName, startName, downloadUrl, startDate, endDate, frequency, interval, currentNumber, firstWeekNumber, isDefaultSchedule
            FROM Schedules
        """)
        db.execSQL("DROP TABLE Schedules")
        db.execSQL("ALTER TABLE Schedules_new RENAME TO Schedules")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Events ADD COLUMN isHidden INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Events ADD COLUMN isCustomEvent INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val tableName = "EventsExtraData"
        val oldTableName = "${tableName}_old"
        val primaryKeyName = "EventExtraId"

        db.execSQL("ALTER TABLE $tableName RENAME TO $oldTableName")

        db.execSQL(
            """
            CREATE TABLE $tableName (
                $primaryKeyName INTEGER NOT NULL,
                eventExtraScheduleId INTEGER NOT NULL DEFAULT 0,
                eventName TEXT,
                eventStartDatetime TEXT,
                comment TEXT NOT NULL DEFAULT '',
                tag INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY($primaryKeyName)
            )
            """.trimIndent()
        )


        db.execSQL(
            """
            INSERT INTO $tableName ($primaryKeyName, eventExtraScheduleId, eventName, eventStartDatetime, comment, tag)
            SELECT $primaryKeyName, eventExtraScheduleId, eventName, eventStartDatetime, comment, tag 
            FROM $oldTableName
            """.trimIndent()
        )

        db.execSQL("DROP TABLE $oldTableName")

        db.execSQL("DELETE FROM sqlite_sequence WHERE name='$tableName'")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE Schedules_new (
                ScheduleId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                namedScheduleId INTEGER NOT NULL,
                timetableId TEXT NOT NULL,
                timetableType INTEGER NOT NULL,
                downloadUrl TEXT,
                startDate TEXT NOT NULL,
                endDate TEXT NOT NULL,
                interval INTEGER,
                currentNumber INTEGER,
                firstWeekNumber INTEGER,
                isDefaultSchedule INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            INSERT INTO Schedules_new (
                ScheduleId, namedScheduleId, timetableId, timetableType, 
                downloadUrl, startDate, endDate, interval, 
                currentNumber, firstWeekNumber, isDefaultSchedule
            )
            SELECT 
                ScheduleId, namedScheduleId, timetableId, 
                CASE typeName 
                    WHEN 'Периодическое' THEN 0 
                    WHEN 'Разовое' THEN 1 
                    WHEN 'Сессия' THEN 2 
                    ELSE 1 
                END, 
                downloadUrl, startDate, endDate, interval, 
                currentNumber, firstWeekNumber, isDefaultSchedule 
            FROM Schedules
        """.trimIndent())

        db.execSQL("DROP TABLE Schedules")
        db.execSQL("ALTER TABLE Schedules_new RENAME TO Schedules")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `SearchHistory` (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                name TEXT NOT NULL, 
                apiId INTEGER NOT NULL, 
                namedScheduleType INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE Events_new (
                EventId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                eventScheduleId INTEGER NOT NULL,
                isHidden INTEGER NOT NULL,
                isCustomEvent INTEGER NOT NULL,
                startDatetime TEXT NOT NULL,
                endDatetime TEXT NOT NULL,
                frequency TEXT,
                interval INTEGER,
                periodNumber INTEGER,
                name TEXT NOT NULL,
                typeName TEXT,
                timeSlotName TEXT,
                lecturers TEXT,
                rooms TEXT,
                groups TEXT
            )
        """.trimIndent())

        db.execSQL("""
            INSERT INTO Events_new (
                EventId,
                eventScheduleId,
                isHidden,
                isCustomEvent,
                startDatetime,
                endDatetime,
                frequency,
                interval,
                periodNumber,
                name,
                typeName,
                timeSlotName,
                lecturers,
                rooms,
                groups
            )
            SELECT
                EventId,
                eventScheduleId,
                isHidden,
                isCustomEvent,
                COALESCE(startDatetime, '1970-01-01T00:00:00'),
                COALESCE(endDatetime, '1970-01-01T00:00:00'),
                frequency,
                interval,
                periodNumber,
                COALESCE(name, ''),
                typeName,
                timeSlotName,
                lecturers,
                rooms,
                groups
            FROM Events
        """.trimIndent())

        db.execSQL("DROP TABLE Events")
        db.execSQL("ALTER TABLE Events_new RENAME TO Events")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS EventsExtraData_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                eventId INTEGER NOT NULL,
                eventExtraScheduleId INTEGER NOT NULL,
                eventName TEXT,
                dateTime TEXT,
                comment TEXT NOT NULL,
                tag INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            INSERT INTO EventsExtraData_new (
                eventId,
                eventExtraScheduleId,
                eventName,
                dateTime,
                comment,
                tag
            )
            SELECT
                EventExtraId,
                eventExtraScheduleId,
                eventName,
                eventStartDatetime,
                comment,
                tag
            FROM EventsExtraData
        """.trimIndent())

        db.execSQL("DROP TABLE EventsExtraData")

        db.execSQL("ALTER TABLE EventsExtraData_new RENAME TO EventsExtraData")
    }
}