package io.integral.todo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ListItemDao {
    @Query("SELECT * FROM list_item")
    fun getAllListItems(): Flow<List<ListItem>>

    @Insert
    suspend fun insertListItem(vararg listItem: ListItem)

    @Update
    suspend fun updateListItem(listItem: ListItem)

    @Delete
    suspend fun deleteListItem(listItem: ListItem)
}