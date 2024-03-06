package io.integral.todo.data

import kotlinx.coroutines.flow.Flow

class ListItemRepository(private val listItemDao: ListItemDao) {

    fun getAllListItems(): Flow<List<ListItem>> = listItemDao.getAllListItems()
    suspend fun insertListItem(listItem: ListItem) {
        listItemDao.insertListItem(listItem)
    }

    suspend fun updateListItem(listItem: ListItem) {
        listItemDao.updateListItem(listItem)
    }

    suspend fun deleteListItem(listItem: ListItem) {
        listItemDao.deleteListItem(listItem)
    }
}