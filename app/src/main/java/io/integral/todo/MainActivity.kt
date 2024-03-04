package io.integral.todo

import SampleData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Preview
import io.integral.todo.ui.theme.TodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Root()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Root() {
    Column() {
        Text("Todo List")
        displayListItems(SampleData.items)
        addItemButton()
    }
}

@Composable
fun addItemButton() {
    Button(onClick = { {} }) {
        Text("+")
    }
}

@Composable
fun displayListItems(items: List<ListItem>) {
    return items.forEach {
        Text(it.item)
    }
}

data class ListItem(val item: String)