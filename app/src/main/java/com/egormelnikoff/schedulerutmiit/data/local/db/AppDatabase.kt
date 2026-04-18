package com.egormelnikoff.schedulerutmiit.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.EventDao
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.SearchQueryDao
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.SearchQuery

@Database(
    entities = [
        NamedScheduleEntity::class,
        ScheduleEntity::class,
        Event::class,
        EventExtraData::class,
        SearchQuery::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun namedScheduleDao(): NamedScheduleDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun eventDao(): EventDao
    abstract fun eventExtraDao(): EventExtraDao
    abstract fun searchQueryDao(): SearchQueryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, converters: Converters): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "schedule_database"
                )
                    .addTypeConverter(converters)
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_6)
                    .addMigrations(MIGRATION_6_7)
                    .addMigrations(MIGRATION_7_8)
                    .addMigrations(MIGRATION_8_9)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}