package io.integral.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.integral.todo.data.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listItemDao = DatabaseClient.getDatabase(applicationContext).listItemDao()
        val repository = ListItemRepository(listItemDao)
        val listItemViewModel: ListItemViewModel by viewModels { ListItemViewModelFactory(repository) }

        setContent {
            TodoListApp(listItemViewModel)
        }
    }
}

@Composable
fun TodoListApp(listItemViewModel: ListItemViewModel) {
    val listItems by listItemViewModel.listItems.observeAsState(initial = emptyList())

    val navController = rememberNavController()
    Scaffold { padding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.RootView.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(route = NavigationItem.RootView.route) {
                TodoListMainView(
                    navController = navController,
                    listItems = listItems
                )
            }
            composable(route = NavigationItem.AddListItem.route) {
                AddListItem(
                    navController = navController,
                    listItemViewModel
                )
            }
        }
    }
}

@Composable
fun TodoListMainView(navController: NavController, listItems: List<ListItem>) {
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
            DisplayListItems(listItems)
        }
        Spacer(Modifier.weight(1f))
        Row {
            Spacer(Modifier.weight(1f))
            AddItemButton(navController = navController)
        }
    }
}

@Composable
fun AddListItem(navController: NavController, listItemViewModel: ListItemViewModel) {
    var text by remember { mutableStateOf("") }

    fun addItemToList() {
        listItemViewModel.insertListItem(ListItem(name = text, active = true))
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
fun DisplayListItems(listItems: List<ListItem>) {
    return listItems.listIterator().forEach { item ->
        var style by remember { mutableStateOf(TextStyle(textDecoration = if (item.active) TextDecoration.None else TextDecoration.LineThrough)) }
        ClickableText(
            text = AnnotatedString(item.name),
            style = style,
            onClick = {
                if (item.active) {
                    style = TextStyle(textDecoration = TextDecoration.LineThrough)
                } else {
                    style = TextStyle(textDecoration = TextDecoration.None)
                }
                item.active = !item.active
            }
        )
    }
}
