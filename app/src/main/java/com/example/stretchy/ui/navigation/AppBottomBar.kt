package com.example.stretchy.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.stretchy.navigation.BottomNavScreen

@Composable
fun AppBottomBar(
    shouldShowBottomBar: Boolean,
    screens: List<BottomNavScreen>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    AnimatedVisibility(
        visible = shouldShowBottomBar,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = shrinkVertically(),
    ) {
        BottomAppBar(
            modifier = Modifier.navigationBarsPadding()
        ) {
            screens.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(text = stringResource(id = screen.labelRes)) },
                    selected = currentRoute == screen.route,
                    onClick = { onNavigate(screen.route) }
                )
            }
        }
    }
}
