package com.stefdp.hackatime.screens.projects

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.hackatime.network.hackatimeapi.models.ProjectDetails
import com.stefdp.hackatime.network.hackatimeapi.requests.getUserProjectDetails
import com.stefdp.hackatime.network.hackatimeapi.requests.listUserProjectDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.openid.appauth.internal.Logger

data class ProjectsUiState(
    val projects: List<ProjectDetails>? = null,
)

private const val TAG = "ProjectsViewModel"

class ProjectsViewModel : ViewModel() {
    private val _state: MutableStateFlow<ProjectsUiState> = MutableStateFlow(ProjectsUiState())
    val state: StateFlow<ProjectsUiState> = _state.asStateFlow()

    fun init(
        context: Context
    ) {
        viewModelScope.launch {
            val projectsRes = listUserProjectDetails(
                context = context,
                username = "my"
            )

            projectsRes
                .onSuccess { projects ->
                    _state.update {
                        it.copy(
                            projects = projects
                        )
                    }
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to fetch projects: ${error.message}", error)

                    _state.update {
                        it.copy(
                            projects = emptyList()
                        )
                    }
                }
        }
    }
}