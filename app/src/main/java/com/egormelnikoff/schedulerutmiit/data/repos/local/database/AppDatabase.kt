package com.egormelnikoff.schedulerutmiit.data.repos.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.egormelnikoff.schedulerutmiit.data.Event
import com.egormelnikoff.schedulerutmiit.data.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.ScheduleEntity


@Database(
    entities = [
        NamedScheduleEntity::class,
        ScheduleEntity::class,
        Event::class,
        EventExtraData::class
    ],
    version = 1,
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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}