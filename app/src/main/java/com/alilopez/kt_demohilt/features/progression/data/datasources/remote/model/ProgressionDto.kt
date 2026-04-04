package com.alilopez.kt_demohilt.features.progression.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class ProgressionEntryDto(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val weight: Float,
    val date: String
)

data class ProgressionSummaryDto(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("target_weight") val targetWeight: Float,
    @SerializedName("initial_weight") val initialWeight: Float,
    @SerializedName("current_weight") val currentWeight: Float,
    @SerializedName("weight_change") val weightChange: Float,
    @SerializedName("entries_count") val entriesCount: Int,
    @SerializedName("on_track") val onTrack: Boolean,
    val trend: String
)

data class AddProgressionEntryDto(
    val weight: Float
)
