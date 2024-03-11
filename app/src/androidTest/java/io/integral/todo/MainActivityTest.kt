package io.integral.todo

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.integral.todo.data.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
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
        listItemDao = db.listItemDao()
        listItemRepo = ListItemRepository(listItemDao)
        listItemViewModel =  ListItemViewModel(listItemRepo)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun addItemToList() {

        composeTestRule.setContent {
                TodoListApp(listItemViewModel)
        }

        composeTestRule.onNodeWithTag("add_button").performClick()
        composeTestRule.onNodeWithText("Add Item to List").assertIsDisplayed()
        composeTestRule.onNodeWithText("New list item").performTextInput("Potato")
        composeTestRule.onNodeWithText("Add Item to List").performClick()

        composeTestRule.onNodeWithText("Todo List").assertIsDisplayed()
        composeTestRule.onNodeWithText("Potato").assertIsDisplayed()
    }
}