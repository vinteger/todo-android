package io.integral.todo.data

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ListItemViewModel(private val repository: ListItemRepository): ViewModel() {
    val listItems: LiveData<List<ListItem>> = repository.getAllListItems().asLiveData()

    fun insertListItem(listItem: ListItem) = viewModelScope.launch {
        repository.insertListItem(listItem)
    }

    fun updateListItem(listItem: ListItem) = viewModelScope.launch {
        repository.updateListItem(listItem)
    }

    fun deleteListItem(listItem: ListItem) = viewModelScope.launch {
        repository.deleteListItem(listItem)
    }
}

class ListItemViewModelFactory(private val repository: ListItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}