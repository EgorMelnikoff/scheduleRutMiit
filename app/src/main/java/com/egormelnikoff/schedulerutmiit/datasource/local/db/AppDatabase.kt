package com.egormelnikoff.schedulerutmiit.datasource.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.SearchQueryDao

@Database(
    entities = [
        NamedScheduleEntity::class,
        ScheduleEntity::class,
        EventEntity::class,
        EventExtraData::class,
        SearchQuery::class
    ],
    version = 8,
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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}