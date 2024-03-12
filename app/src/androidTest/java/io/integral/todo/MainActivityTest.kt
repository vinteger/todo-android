package io.integral.todo

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.integral.todo.data.*
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private val title = "Todo List"
    private lateinit var listItemDao: ListItemDao
    private lateinit var listItemRepo: ListItemRepository
    private lateinit var listItemViewModel: ListItemViewModel
    private lateinit var db: AppDatabase

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
    }

    @Before
    fun setUp() {
        listItemDao = db.listItemDao()
        listItemRepo = ListItemRepository(listItemDao)
        listItemViewModel =  ListItemViewModel(listItemRepo)

        composeTestRule.setContent {
            TodoListApp(listItemViewModel)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun listIsEmptyOnInit() {
        val countOfListItems = listItemViewModel.listItems.value?.size
        assertEquals(0, countOfListItems)
    }

    @Test
    fun addSingleItemToList() {
        addListItem("Potato")
        val countOfListItems = listItemViewModel.listItems.value?.size
        assertEquals(1, countOfListItems)
    }

    @Test
    fun addManyItemsToList() {
        addListItem("Potato")
        addListItem("Tomato")
        val countOfListItems = listItemViewModel.listItems.value?.size
        assertEquals(2, countOfListItems)
    }

    @Test
    fun deleteItemFromList() {
        val listItem = "Delete Me"
        addListItem(listItem)
        composeTestRule.onNodeWithText(listItem).performTouchInput { longClick() }
        composeTestRule.onNodeWithText("Delete List Item", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm").performClick()
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        val countOfListItems = listItemViewModel.listItems.value?.size
        assertEquals(0, countOfListItems)
    }

    @Test
    fun dismissDialogWhenDeletingItemFromList() {
        val listItem = "Delete Me"
        addListItem(listItem)
        composeTestRule.onNodeWithText(listItem).performTouchInput { longClick() }
        composeTestRule.onNodeWithText("Delete List Item", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Dismiss").performClick()
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(listItem).assertIsDisplayed()
        val countOfListItems = listItemViewModel.listItems.value?.size
        assertEquals(1, countOfListItems)
    }

    private fun addListItem(item: String) {
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithTag("add_button").performClick()
        composeTestRule.onNodeWithText("Add Item to List").assertIsDisplayed()
        composeTestRule.onNodeWithText("New list item").performTextInput(item)
        composeTestRule.onNodeWithText("Add Item to List").performClick()
        composeTestRule.onNodeWithText(item).assertIsDisplayed()
    }
}