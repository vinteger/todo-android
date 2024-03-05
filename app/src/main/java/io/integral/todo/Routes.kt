package io.integral.todo

enum class Routes {
    RootView,
    AddListItem
}

sealed class NavigationItem(val route: String) {
    object RootView : NavigationItem(Routes.RootView.name)
    object AddListItem : NavigationItem(Routes.AddListItem.name)
}