package io.integral.todo.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ListItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun listItemDao(): ListItemDao
}
