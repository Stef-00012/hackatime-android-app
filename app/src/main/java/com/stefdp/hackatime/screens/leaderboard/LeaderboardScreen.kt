package com.stefdp.hackatime.screens.leaderboard

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.Pager
import com.stefdp.hackatime.components.PullToRefreshBox
import com.stefdp.hackatime.components.TextInput
import com.stefdp.hackatime.screens.home.components.Container
import com.stefdp.hackatime.screens.leaderboard.components.LeaderboardEntry
import com.stefdp.hackatime.utils.shimmerable
import com.stefdp.hackatime.utils.toCountryName
import com.stefdp.hackatime.utils.toFlagEmoji
import com.stefdp.hackatime.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun LeaderboardScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: LeaderboardViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    val state by viewModel.state.collectAsState()

    val scrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()

    fun reload(isRefresh: Boolean = false) {
        coroutineScope.launch {
            viewModel.updateLeaderboard(
                context = context,
                user = localLoggedUser,
                isRefresh = isRefresh
            )

            scrollState.animateScrollTo(0)
        }
    }

    LaunchedEffect(state.range) {
        reload()
    }

    LaunchedEffect(state.type, state.searchQuery) {
        coroutineScope.launch {
            viewModel.updateLeaderboardFilters()

            scrollState.animateScrollTo(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.userCountry != null) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.weight(1f)
                ) {
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 0,
                            count = 2
                        ),
                        onClick = {
                            viewModel.setLeaderboardType(LeaderboardType.GLOBAL)
                        },
                        selected = state.type == LeaderboardType.GLOBAL,
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.leaderboard_global),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 1,
                            count = 2
                        ),
                        onClick = {
                            viewModel.setLeaderboardType(LeaderboardType.COUNTRY)
                        },
                        selected = state.type == LeaderboardType.COUNTRY,
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    ) {
                        Text(
                            text = "${state.userCountry!!.toFlagEmoji()} ${state.userCountry!!.toCountryName()}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.weight(1f)
            ) {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 0,
                        count = 2
                    ),
                    onClick = {
                        viewModel.setLeaderboardRange(LeaderboardRange.DAILY)
                    },
                    selected = state.range == LeaderboardRange.DAILY,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                ) {
                    Text(
                        text = stringResource(R.string.leaderboard_daily),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 1,
                        count = 2
                    ),
                    onClick = {
                        viewModel.setLeaderboardRange(LeaderboardRange.WEEKLY)
                    },
                    selected = state.range == LeaderboardRange.WEEKLY,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                ) {
                    Text(
                        text = stringResource(R.string.leaderboard_weekly),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        val focusRequester = remember { FocusRequester() }

        TextInput(
            placeholder = stringResource(R.string.leaderboard_search_placeholder),
            value = state.searchQuery,
            onValueChange = {
                viewModel.setSearchQuery(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            trailingIcon = painterResource(R.drawable.search),
            onTrailingIconPress = {
                focusRequester.requestFocus()
            }
        )

        Text(
            text = stringResource(
                R.string.leaderboard_range_and_last_updated,
                state.dateRange ?: stringResource(R.string.leaderboard_unknown_range),
                HumanReadable.timeAgo(Instant.parse(state.generatedAt ?: Clock.System.now().toString())),
            ),
            modifier = Modifier
                .padding(
                    horizontal = 4.dp
                )
                .shimmerable(
                    enabled = state.generatedAt == null || state.dateRange == null
                )
        )

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {
                reload(true)
            },
            modifier = Modifier.weight(1f)
        ) {
            Container(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .shimmerable(
                        enabled = state.chunkedEntries == null
                    )
            ) {
                if (state.chunkedEntries.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.leaderboard_no_results),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    return@Container
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScrollWithScrollbar(scrollState)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.chunkedEntries!![state.page.toInt() - 1].forEach { entry ->
                        LeaderboardEntry(
                            context = context,
                            entry = entry
                        )
                    }
                }
            }
        }

        Pager(
            currentPage = state.page,
            totalPages = state.chunkedEntries?.size?.toLong() ?: 1L,
            onFirstPageClick = {
                viewModel.setPage(1)
            },
            onPreviousPageClick = {
                viewModel.setPage(state.page - 1)
            },
            onCustomPageInput = {
                viewModel.setPage(it)
            },
            onNextPageClick = {
                viewModel.setPage(state.page + 1)
            },
            onLastPageClick = {
                viewModel.setPage(state.chunkedEntries?.size?.toLong() ?: 1L)
            },
            enabled = state.chunkedEntries != null
        )
    }
}

enum class LeaderboardRange {
    DAILY,
    WEEKLY
}

enum class LeaderboardType {
    GLOBAL,
    COUNTRY
}