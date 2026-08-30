package com.stefdp.hackatime.screens.leaderboard.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.R
import com.stefdp.hackatime.screens.leaderboard.LeaderboardEntry
import com.stefdp.hackatime.utils.formatMs
import com.stefdp.hackatime.utils.shimmerable
import com.stefdp.hackatime.utils.toFlagEmoji

@Composable
fun LeaderboardEntry(
    context: Context,
    entry: LeaderboardEntry
) {
    val localLoggedUser = LocalLoggedUser.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (entry.isSelf) {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (entry.isSelf) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = entry.rank.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            var imageFailed by rememberSaveable {
                mutableStateOf(false)
            }

            var imageLoading by rememberSaveable {
                mutableStateOf(true)
            }

            if (imageFailed) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            } else {
                AsyncImage(
                    model = entry.leaderboardUser.avatarUrl,
                    contentDescription = entry.leaderboardUser.username,
                    onSuccess = {
                        imageLoading = false
                    },
                    onError = {
                        imageFailed = true
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .shimmerable(
                            enabled = imageLoading,
                            color = MaterialTheme.colorScheme.surface
                        )
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withLink(
                            link = LinkAnnotation.Url(
                                url = "https://hackatime.hackclub.com/@${entry.leaderboardUser.username}",
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textDecoration = null
                                    )
                                )
                            )
                        ) {
                            append("${entry.leaderboardUser.username} ${entry.user?.countryCode?.toFlagEmoji() ?: ""}".trim())
                        }
                    },
                )

                if (entry.user?.workingOn != null) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            ) {
                                val url = entry.user.workingOn.repoUrl

                                if (url != null) {
                                    withLink(
                                        link = LinkAnnotation.Url(
                                            url = url
                                        )
                                    ) {
                                        append(
                                            stringResource(
                                                R.string.leaderboard_working_on,
                                                entry.user.workingOn.projectName,
                                            )
                                        )
                                    }
                                } else {
                                    append(
                                        stringResource(
                                            R.string.leaderboard_working_on,
                                            entry.user.workingOn.projectName,
                                        )
                                    )
                                }
                            }
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Text(
                text = formatMs(
                    context = context,
                    ms = entry.totalSeconds * 1000L
                )
            )
        }
    }
}