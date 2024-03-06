package io.integral.todo.data

import android.content.Context
import androidx.room.Room

object DatabaseClient {
    @Volatile private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }
    }

    private fun buildDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "ListItem.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}