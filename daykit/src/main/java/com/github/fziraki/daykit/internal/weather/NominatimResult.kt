package com.github.fziraki.daykit.internal.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NominatimResult(
    @SerialName("display_name") val displayName: String,
    @SerialName("lat") val lat: String,
    @SerialName("lon") val lon: String,
    @SerialName("address") val address: NominatimAddress
)

@Serializable
internal data class NominatimAddress(
    @SerialName("city") val city: String? = null,
    @SerialName("town") val town: String? = null,
    @SerialName("village") val village: String? = null,
    @SerialName("country") val country: String? = null
)