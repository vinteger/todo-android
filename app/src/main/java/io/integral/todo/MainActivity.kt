package io.integral.todo

import SampleData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Root()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Root() {
    val navController = rememberNavController()
    Scaffold { padding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.RootView.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(route = NavigationItem.RootView.route) {
                TodoListMainView(navController = navController)
            }
            composable(route = NavigationItem.AddListItem.route) {
                AddListItem(navController = navController)
            }
        }
    }
}

@Composable
fun TodoListMainView(navController: NavController) {
    Column(
        modifier = Modifier.padding(all = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Todo List",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Column {
            DisplayListItems(SampleData.items)
        }
        Spacer(Modifier.weight(1f))
        Row {
            Spacer(Modifier.weight(1f))
            AddItemButton(navController = navController)
        }
    }
}

@Composable
fun AddListItem(navController: NavController) {
    var text by remember { mutableStateOf("") }

    fun addItemToList() {
        SampleData.items.add(ListItem(text))
        navController.popBackStack()
    }

    Column {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Add item") }
        )
        Button(onClick = { addItemToList() }) {
            Text("Add Item to List")
        }
    }
}


@Composable
fun AddItemButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(NavigationItem.AddListItem.route) },
        modifier = Modifier.size(60.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            Icons.Rounded.Add,
            contentDescription = "Add item to list",
        )
    }
}

@Composable
fun DisplayListItems(items: List<ListItem>) {
    return items.forEach { thing ->
        var style by remember { mutableStateOf(TextStyle(textDecoration = if (thing.isActive) TextDecoration.None else TextDecoration.LineThrough)) }
        ClickableText(
            text = AnnotatedString(thing.item),
            style = style,
            onClick = {
                if (thing.isActive) {
                    style = TextStyle(textDecoration = TextDecoration.LineThrough)
                } else {
                    style = TextStyle(textDecoration = TextDecoration.None)
                }
                thing.isActive = !thing.isActive
            }
        )
    }
}

data class ListItem(val item: String, var isActive: Boolean = true)