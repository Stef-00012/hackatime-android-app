package com.stefdp.hackatime.network.hackatimeapi.models.responses

data class GetUserHeartbeatSpansResponse(
    val spans: List<Heartbeat.Span>
)
