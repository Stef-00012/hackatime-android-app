package com.stefdp.hackatime.screens.projects.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ProjectDetail
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.formatMs
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant
import androidx.core.net.toUri

@Composable
fun ProjectContainer(
    modifier: Modifier = Modifier,
    context: Context,
    project: ProjectDetail
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            if (project.repoUrl != null) {
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, project.repoUrl.toUri())
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(5.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.folder_code),
                        contentDescription = stringResource(R.string.open_repository_content_description),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Text(
            text = formatMs(project.totalSeconds * 1000L),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Row {
            Icon(
                painter = painterResource(R.drawable.code),
                contentDescription = stringResource(R.string.project_languages_content_description),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = project.languages.joinToString(", "),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row {
            Icon(
                painter = painterResource(R.drawable.timeline),
                contentDescription = stringResource(R.string.project_last_heartbeat_content_description),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = HumanReadable.timeAgo(Instant.parse(project.lastHeartbeat)),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(
    showSystemUi = false, showBackground = false,
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun ProjectContainerPreview() {
    val project = ProjectDetail(
        name = "hackatime",
        totalSeconds = 1000000.0,
        languages = listOf(
            "Kotlin",
            "Toml",
            "Markdown",
            "Kotlin",
            "Toml",
            "Markdown",
            "Kotlin",
            "Toml",
            "Markdown",
            "Kotlin",
            "Toml",
            "Markdown"
        ),
        repoUrl = "https://git.stefdp.com/Stef/hackatime-android-app",
        totalHeartbeats = 100,
        firstHeartbeat = "2026-01-01T00:00:00Z",
        lastHeartbeat = "2026-02-15T12:00:00Z"
    )

    HackatimeStatsTheme {
        ProjectContainer(project = project, context = LocalContext.current)
    }
}