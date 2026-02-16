package com.stefdp.hackatime.screens.goals.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.hackatime.network.backendapi.models.Goal
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.formatGoalDate
import com.stefdp.hackatime.utils.formatMs
import ir.ehsannarmani.compose_charts.extensions.format

@Composable
fun GoalContainer(
    modifier: Modifier = Modifier,
    goal: Goal
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
                text = formatGoalDate(goal.date),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
            )

            Text(
                text = formatMs(goal.goal * 1000),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        val goalAchieved = goal.achieved
        val goalGoal = goal.goal
        val achievementPercentage = if (goalGoal > 0) (goalAchieved / goalGoal) * 100 else 0.0

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${formatMs(goalAchieved * 1000)} / ${formatMs(goalGoal * 1000)}",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
            )

            Text(
                text = "${achievementPercentage.format(1)}%",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
            )
        }

        LinearProgressIndicator(
            trackColor = MaterialTheme.colorScheme.onSurfaceVariant,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
                .height(10.dp),
            progress = { achievementPercentage.toFloat() / 100 },
            drawStopIndicator = {}
        )
    }
}

@Preview(
    showSystemUi = false, showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun GoalContainerPreview() {
    val goal = Goal(
        date = "2026-02-15",
        goal = 3600.0,
        achieved = 1200.0
    )

    HackatimeStatsTheme {
        GoalContainer(goal = goal)
    }
}