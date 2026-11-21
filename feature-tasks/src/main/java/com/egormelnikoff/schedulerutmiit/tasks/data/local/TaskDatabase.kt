package com.egormelnikoff.schedulerutmiit.tasks.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.egormelnikoff.schedulerutmiit.tasks.data.local.converters.TaskConverters
import com.egormelnikoff.schedulerutmiit.tasks.data.local.dao.TaskCompletionDao
import com.egormelnikoff.schedulerutmiit.tasks.data.local.dao.TaskDao
import com.egormelnikoff.schedulerutmiit.tasks.data.local.entity.TaskCompletionEntity
import com.egormelnikoff.schedulerutmiit.tasks.data.local.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        TaskCompletionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TaskConverters::class)
internal abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun taskCompletionDao(): TaskCompletionDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context, converters: TaskConverters): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .addTypeConverter(converters)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}