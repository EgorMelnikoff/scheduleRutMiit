package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.converters.ScheduleConverters
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.EventExtraDataEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.ScheduleEntity

@Database(
    entities = [
        NamedScheduleEntity::class,
        ScheduleEntity::class,
        EventEntity::class,
        EventExtraDataEntity::class,
    ],
    version = 13,
    exportSchema = false
)
@TypeConverters(ScheduleConverters::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun namedScheduleDao(): NamedScheduleDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun eventDao(): EventDao
    abstract fun eventExtraDao(): EventExtraDao

    companion object {
        @Volatile
        private var INSTANCE: ScheduleDatabase? = null

        fun getDatabase(context: Context, scheduleConverters: ScheduleConverters): ScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDatabase::class.java,
                    "schedule_database"
                )
                    .addTypeConverter(scheduleConverters)
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
                    .addMigrations(MIGRATION_11_12)
                    .addMigrations(MIGRATION_12_13)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}