package com.egormelnikoff.schedulerutmiit.search.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.egormelnikoff.schedulerutmiit.search.data.local.entity.SearchQueryEntity

@Database(
    entities = [
        SearchQueryEntity::class
    ],
    version = 1,
    exportSchema = false
)
internal abstract class SearchQueryDatabase : RoomDatabase() {
    abstract fun searchQueryDao(): SearchQueryDao

    companion object {
        @Volatile
        private var INSTANCE: SearchQueryDatabase? = null

        fun getDatabase(context: Context): SearchQueryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SearchQueryDatabase::class.java,
                    "search_history"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}