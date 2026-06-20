package com.egormelnikoff.schedulerutmiit.core.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.SearchQueryDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.TaskCompletionDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.TaskDao
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventExtraDataEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskCompletionEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskEntity

@Database(
    entities = [
        NamedScheduleEntity::class,
        ScheduleEntity::class,
        EventEntity::class,
        EventExtraDataEntity::class,
        SearchQuery::class,
        TaskEntity::class,
        TaskCompletionEntity::class
    ],
    version = 11,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun namedScheduleDao(): NamedScheduleDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun eventDao(): EventDao
    abstract fun eventExtraDao(): EventExtraDao
    abstract fun searchQueryDao(): SearchQueryDao
    abstract fun taskDao(): TaskDao
    abstract fun taskCompletionDao(): TaskCompletionDao

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
                    .addMigrations(MIGRATION_9_10)
                    .addMigrations(MIGRATION_10_11)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}