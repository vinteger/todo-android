package io.integral.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    listItems = listItems,
                    listItemViewModel = listItemViewModel
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
fun TodoListMainView(navController: NavController, listItems: List<ListItem>, listItemViewModel: ListItemViewModel) {
    val openAlertDialog = remember { mutableStateOf<ListItem?>(null) }
    fun openDialogForListItem(li: ListItem) {
        openAlertDialog.value = li
    }

    when {
        openAlertDialog.value != null -> {
            DeleteConfirmationDialog(
                onDismissRequest = { openAlertDialog.value = null },
                onConfirmation = {
                    listItemViewModel.deleteListItem(openAlertDialog.value!!)
                    openAlertDialog.value = null
                },
                dialogText = "Are you sure you want to delete \"${openAlertDialog.value!!.name}\"?",
                dialogTitle = "Delete List Item"
            )
        }
    }
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
        Divider(
            color = MaterialTheme.colorScheme.primary,
            thickness = 1.dp,
            modifier = Modifier.padding(top = 10.dp)
        )
        Column {
            DisplayListItems(listItems, listItemViewModel, ::openDialogForListItem)
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("New list item") }
        )
        Spacer(Modifier.padding(top = 20.dp))
        Button(onClick = { addItemToList() }) {
            Text("Add Item to List")
        }
    }
}


@Composable
fun AddItemButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(NavigationItem.AddListItem.route) },
        modifier = Modifier.size(60.dp).semantics { testTag = "add_button" },
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            Icons.Rounded.Add,
            contentDescription = "Add item to list",
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayListItems(
    listItems: List<ListItem>,
    listItemViewModel: ListItemViewModel,
    showDeleteDialogForItem: (li: ListItem) -> Unit
) {
    val haptics = LocalHapticFeedback.current

    return listItems.listIterator().forEach { item ->
        var style by remember {
            mutableStateOf(
                TextStyle(
                    textDecoration = if (item.active) TextDecoration.None else TextDecoration.LineThrough,
                )
            )
        }
        Text(
            text = AnnotatedString(item.name),
            style = style,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .combinedClickable(
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        showDeleteDialogForItem(item)
                    },
                    onLongClickLabel = "Delete list item",
                    onClick = {
                        style = if (item.active) {
                            TextStyle(textDecoration = TextDecoration.LineThrough)
                        } else {
                            TextStyle(textDecoration = TextDecoration.None)
                        }
                        item.active = !item.active
                        listItemViewModel.updateListItem(item)
                    }
                ),
            fontSize = 18.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        icon = {
            Icon(
                Icons.Rounded.Delete,
                contentDescription = "Remove list item",
            )
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}