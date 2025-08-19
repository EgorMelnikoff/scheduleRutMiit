package com.egormelnikoff.schedulerutmiit.data.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity

@Database(
    entities = [
        NamedScheduleEntity::class,
        ScheduleEntity::class,
        Event::class,
        EventExtraData::class
    ],
    version = 2,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun namedScheduleDao(): NamedScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "schedule_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

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