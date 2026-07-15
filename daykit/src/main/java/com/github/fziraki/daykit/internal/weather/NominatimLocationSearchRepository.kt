package com.github.fziraki.daykit.internal.weather

import android.util.Log
import com.github.fziraki.daykit.model.LocationResult
import com.github.fziraki.daykit.providers.LocationSearchRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

internal class NominatimLocationSearchRepository(private val client: HttpClient) : LocationSearchRepository {

    override suspend fun search(query: String): List<LocationResult> {
        return runCatching {
            val results: List<NominatimResult> = client.get("https://nominatim.openstreetmap.org/search") {
                parameter("q", query)
                parameter("format", "json")
                parameter("addressdetails", 1)
                parameter("limit", 5)
                header("User-Agent", "MakeMyDay/1.0")
            }.body()

            results.map {
                LocationResult(
                    city = it.address.city ?: "",
                    country = it.address.country ?: "",
                    lat = it.lat.toDouble(),
                    lon = it.lon.toDouble()
                )
            }

        }.onFailure {
            Log.e("Nominatim", "Search failed", it)
        }.getOrDefault(emptyList())
    }
}
