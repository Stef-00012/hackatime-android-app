package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class Heartbeat(
    val id: Long,
    @SerializedName("user_id") val userId: Long? = null,
    val branch: String? = null,
    val category: Category? = null,
    val dependencies: List<String>? = null,
    val editor: String? = null,
    val entity: String? = null,
    val language: String? = null,
    val machine: String? = null,
    @SerializedName("operating_system") val operatingSystem: String? = null,
    val project: String? = null,
    val type: Type? = null,
    @SerializedName("user_agent") val userAgent: String? = null,
    @SerializedName("line_additions") val lineAdditions: Long? = null,
    @SerializedName("line_deletions") val lineDeletions: Long? = null,
    @SerializedName("lineno") val lineNumber: Long? = null,
    val lines: Long? = null,
    @SerializedName("cursorpos") val cursorPosition: Long? = null,
    @SerializedName("project_root_count") val projectRootCount: Long? = null,
    val time: Double? = null,
    @SerializedName("is_write") val isWrite: Boolean? = null,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("fields_hash") val fieldsHash: String? = null,
    @SerializedName("source_type") val sourceType: String = "direct_entry",
    @SerializedName("ip_address") val ipAddress: String? = null,
    @SerializedName("deleted_at") val deletedAt: String? = null,
    @SerializedName("ja4_id") val ja4Id: String? = null,
    @SerializedName("ai_model") val aiModel: String? = null,
    @SerializedName("ai_session") val aiSession: String? = null,
    @SerializedName("ai_subscription_plan") val aiSubscriptionPlan: String? = null,
    @SerializedName("ai_input_tokens") val aiInputTokens: Long? = null,
    @SerializedName("ai_output_tokens") val aiOutputTokens: Long? = null,
    @SerializedName("ai_prompt_length") val aiPromptLength: Long? = null,
    @SerializedName("ai_line_changes") val aiLineChanges: Long? = null,
    @SerializedName("human_line_changes") val humanLineChanges: Long? = null,
) {
    enum class Category(val value: String) {
        @SerializedName("advising")
        ADVISING("advising"),

        @SerializedName("ai coding")
        AI_CODING("ai coding"),

        @SerializedName("animating")
        ANIMATING("animating"),

        @SerializedName("browsing")
        BROWSING("browsing"),

        @SerializedName("building")
        BUILDING("building"),

        @SerializedName("code reviewing")
        CODE_REVIEWING("code reviewing"),

        @SerializedName("coding")
        CODING("coding"),

        @SerializedName("communicating")
        COMMUNICATING("communicating"),

        @SerializedName("configuring")
        CONFIGURING("configuring"),

        @SerializedName("debugging")
        DEBUGGING("debugging"),

        @SerializedName("designing")
        DESIGNING("designing"),

        @SerializedName("indexing")
        INDEXING("indexing"),

        @SerializedName("learning")
        LEARNING("learning"),

        @SerializedName("manual testing")
        MANUAL_TESTING("manual testing"),

        @SerializedName("meeting")
        MEETING("meeting"),

        @SerializedName("notes")
        NOTES("notes"),

        @SerializedName("planning")
        PLANNING("planning"),

        @SerializedName("researching")
        RESEARCHING("researching"),

        @SerializedName("running tests")
        RUNNING_TESTS("running tests"),

        @SerializedName("supporting")
        SUPPORTING("supporting"),

        @SerializedName("translating")
        TRANSLATING("translating"),

        @SerializedName("writing docs")
        WRITING_DOCS("writing docs"),

        @SerializedName("writing tests")
        WRITING_TESTS("writing tests");

        override fun toString(): String = value
    }

    enum class Type(val value: String) {
        @SerializedName("file")
        FILE("file"),

        @SerializedName("app")
        APP("app");

        override fun toString(): String = value
    }

    data class Span(
        @SerializedName("start_time") val startTime: Double,
        @SerializedName("end_time") val endTime: Double,
        val duration: Double
    )
}
