package com.ruhaan.otakuclick.ui.components.bars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruhaan.otakuclick.data.models.AnimeItem
import com.ruhaan.otakuclick.ui.components.common.CompactSearchResults
import com.ruhaan.otakuclick.ui.components.common.LogoutDialog
import com.ruhaan.otakuclick.ui.components.common.SearchHistoryBubbles
import com.ruhaan.otakuclick.ui.utils.SearchHistoryManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// Enhanced top search bar with history and compact results
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    searchResults: List<AnimeItem> = emptyList(),
    isSearchLoading: Boolean = false,
    searchError: String? = null,
    onAnimeClick: (Int) -> Unit = {},
    onRetrySearch: () -> Unit = {},
    onLogout: () -> Unit = {}  // Add logout callback
) {

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val historyManager = remember { SearchHistoryManager(context) }
    var searchHistory by remember { mutableStateOf(historyManager.getSearchHistory()) }

    var searchJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // State for dropdown menu and logout dialog
    var showDropdownMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }


    Column(modifier = modifier) {
        // Main top bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                // Title bar with search icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "Otaku Click",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Right side icons (Search + Three-dot menu)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search/Close button
                        IconButton(
                            onClick = {
                                onSearchActiveChange(!isSearchActive)
                                if (!isSearchActive) {
                                    // Refresh history when opening search
                                    searchHistory = historyManager.getSearchHistory()
                                } else {
                                    // Clear search when closing
                                    onSearchTextChange("")
                                    keyboardController?.hide()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (isSearchActive) "Close search" else "Open search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Three-dot menu button
                        Box {
                            IconButton(
                                onClick = { showDropdownMenu = true }
                            ) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Dropdown menu
                            DropdownMenu(
                                expanded = showDropdownMenu,
                                onDismissRequest = { showDropdownMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
//                                            Text("🚪")
                                            Text(
                                                text = "Log Out",
                                                fontSize = 14.sp,
                                                color = Color(0xFFEF4444)
                                            )
                                        }
                                    },
                                    onClick = {
                                        showDropdownMenu = false
                                        showLogoutDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
//                {
//                    Text(
//                        text = "Otaku Click",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//
//                    IconButton(
//                        onClick = {
//                            onSearchActiveChange(!isSearchActive)
//                            if (!isSearchActive) {
//                                // Refresh history when opening search
//                                searchHistory = historyManager.getSearchHistory()
//                            } else {
//                                // Clear search when closing
//                                onSearchTextChange("")
//                                keyboardController?.hide()
//                            }
//                        }
//                    ) {
//                        Icon(
//                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
//                            contentDescription = if (isSearchActive) "Close search" else "Open search",
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }

                // Expandable search section
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                    exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                ) {
                    Column {
                        // Search input field
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { newText ->
                                onSearchTextChange(newText)

                                searchJob?.cancel()

                                // Start new search with 500ms delay
                                if (newText.length >= 2) {
                                    searchJob = coroutineScope.launch {
                                        delay(500)  // Wait 500ms before searching
                                        onSearch(newText)
                                    }
                                }



                            },
                            placeholder = {
                                Text(
                                    "Search anime...",
                                    color = Color(0xFF8A8A8A)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search icon",
                                    tint = Color(0xFF8A8A8A)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (searchText.isNotBlank()) {
                                        historyManager.addSearch(searchText)
                                        searchHistory = historyManager.getSearchHistory()
                                        onSearch(searchText)
                                        keyboardController?.hide()
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color(0xFF3A3A3A)
                            ),
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    Row {
                                        // Clear text button
                                        IconButton(
                                            onClick = { onSearchTextChange("") }
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Clear text",
                                                tint = Color(0xFF8A8A8A)
                                            )
                                        }

                                        // Search button
                                        TextButton(
                                            onClick = {
                                                if (searchText.isNotBlank()) {
                                                    historyManager.addSearch(searchText)
                                                    searchHistory = historyManager.getSearchHistory()
                                                    onSearch(searchText)
                                                    keyboardController?.hide()
                                                }
                                            }
                                        ) {
                                            Text(
                                                "Search",
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Search history bubbles (when search is active but no text entered)
        AnimatedVisibility(
            visible = isSearchActive && searchText.isBlank() && searchHistory.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            SearchHistoryBubbles(
                searchHistory = searchHistory,
                onHistoryClick = { historyTerm ->
                    onSearchTextChange(historyTerm)
                    historyManager.addSearch(historyTerm)
                    searchHistory = historyManager.getSearchHistory()
                    onSearch(historyTerm)
                },
                onClearHistory = {
                    historyManager.clearHistory()
                    searchHistory = emptyList()
                }
            )
        }

        // Compact search results (when searching)
        AnimatedVisibility(
            visible = isSearchActive && (searchText.isNotBlank() && (isSearchLoading || searchResults.isNotEmpty() || searchError != null)),
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(animationSpec = tween(300))
        ) {
            CompactSearchResults(
                searchResults = searchResults,
                isLoading = isSearchLoading,
                errorMessage = searchError,
                onAnimeClick = onAnimeClick,
                onRetry = onRetrySearch,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    LogoutDialog(
        isVisible = showLogoutDialog,
        onConfirm = {
            showLogoutDialog = false
            onLogout()
        },
        onDismiss = {
            showLogoutDialog = false
        }
    )

}