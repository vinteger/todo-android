package io.integral.todo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ListItemDao {
    @Query("SELECT * FROM list_item")
    fun getAllListItems(): Flow<List<ListItem>>

    @Insert
    fun insertListItem(vararg listItem: ListItem)
}