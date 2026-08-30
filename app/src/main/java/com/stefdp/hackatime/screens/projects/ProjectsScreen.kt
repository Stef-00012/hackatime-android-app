package com.stefdp.hackatime.screens.projects

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.PullToRefreshBox
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.projects.components.ProjectContainer
import com.stefdp.hackatime.utils.shimmerable
import com.stefdp.hackatime.utils.verticalLazyScrollbar
import com.stefdp.hackatime.utils.verticalScrollWithScrollbar

@Composable
fun ProjectsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ProjectsViewModel = viewModel()
) {
    val localUserStats = LocalLoggedUser.current

    if (localUserStats == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    fun reload(isRefresh: Boolean = false) {
        viewModel.init(
            context = context,
            username = localUserStats!!.username ?: localUserStats.displayName,
            isRefresh = isRefresh
        )
    }

    LaunchedEffect(Unit) {
        reload()
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            reload(isRefresh = true)
        }
    ) {
        val scrollState = rememberScrollState()

        if (state.projects == null || state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScrollWithScrollbar(scrollState)
            ) {
                val repeatCount = 5

                repeat(repeatCount) {
                    SkeletonProject(
                        index = it,
                        size = repeatCount
                    )
                }
            }
        } else if (state.projects.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScrollWithScrollbar(scrollState)
            ) {
                Text(
                    text = stringResource(R.string.no_projects_available),
                    modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        } else {
            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalLazyScrollbar(listState)
            ) {
                items(state.projects!!.size) { index ->
                    ProjectContainer(
                        modifier = Modifier.padding(
                            start = 10.dp,
                            end = 10.dp,
                            top = if (index == 0) 10.dp else 5.dp,
                            bottom = if (index == state.projects!!.size - 1) 10.dp else 5.dp
                        ),
                        context = context,
                        activity = activity,
                        project = state.projects!![index],
                    )
                }
            }
        }
    }
}

@Composable
fun SkeletonProject(index: Int, size: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 10.dp,
                end = 10.dp,
                top = if (index == 0) 10.dp else 5.dp,
                bottom = if (index == size - 1) 10.dp else 5.dp
            )
            .height(200.dp)
            .shimmerable(
                enabled = true,
                shape = RoundedCornerShape(10.dp)
            )
    ) {}
}