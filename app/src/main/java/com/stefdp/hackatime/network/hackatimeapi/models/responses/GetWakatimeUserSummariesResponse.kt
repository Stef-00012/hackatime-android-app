package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class GetWakatimeUserSummariesResponse(
    val data: List<Summary>,
    @SerializedName("cumulative_total") val cumulativeTotal: CumulativeTotal,
    @SerializedName("daily_average") val dailyAverage: DailyAverage,
    val start: String,
    val end: String,
) {
    data class Summary(
        @SerializedName("grand_total") val grandTotal: GrandTotal,
        val projects: List<Project>,
        val range: Range
    ) {
        data class GrandTotal(
            @SerializedName("total_seconds") val totalSeconds: Long,
            val hours: Long,
            val minutes: Long,
            val seconds: Long,
            val digital: String,
            val decimal: String,
            val text: String,
            @SerializedName("ai_input_tokens") val aiInputTokens: Long? = null,
            @SerializedName("ai_output_tokens") val aiOutputTokens: Long? = null,
            @SerializedName("ai_model_breakdown") val aiModelBreakdown: List<AiModel>? = null,
        ) {
            data class AiModel(
                val name: String,
                val lines: Long
            )
        }

        data class Project(
            val name: String,
            @SerializedName("total_seconds") val totalSeconds: Long,
            val percent: Double,
            val hours: Long,
            val minutes: Long,
            val seconds: Long,
            val digital: String,
            val decimal: String,
            val text: String,
        )

        data class Range(
            val date: String,
            val start: String,
            val end: String,
            val text: String,
            val timezone: String
        )
    }

    data class CumulativeTotal(
        val seconds: Long,
        val text: String,
        val decimal: String,
        val digital: String,
    )

    data class DailyAverage(
        val holidays: Long,
        @SerializedName("days_including_holidays") val daysIncludingHolidays: Long,
        @SerializedName("days_minus_holidays") val daysMinusHolidays: Long,
        val seconds: Long,
        val text: String,
    )
}