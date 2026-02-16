package com.stefdp.hackatime.screens.projects

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ProjectDetail
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserDetailedProjects
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.projects.components.ProjectContainer
import com.stefdp.hackatime.utils.shimmerable

@Composable
fun ProjectsScreen(
    navController: NavHostController,
    context: Context
) {
    val localUserStats = LocalLoggedUser.current

    if (localUserStats == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

    var projects by remember { mutableStateOf<List<ProjectDetail>?>(null) }

    LaunchedEffect(Unit) {
        val projectsRes = getCurrentUserDetailedProjects(
            context = context
        )

        projectsRes
            .onSuccess { projects = it }
            .onFailure { projects = emptyList() }
    }

    if (projects == null) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            val repeatCount = 5

            repeat(repeatCount) {
                SkeletonProject(
                    index = it,
                    size = repeatCount
                )
            }
        }
    } else if (projects!!.isEmpty()) {
        Text(
            text = "No Projects Available",
            modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
            style = MaterialTheme.typography.headlineLarge.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(projects!!.size) { index ->
                ProjectContainer(
                    modifier = Modifier.padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = if (index == 0) 10.dp else 5.dp,
                        bottom = if (index == projects!!.size - 1) 10.dp else 5.dp
                    ),
                    context = context,
                    project = projects!![index],
                )
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