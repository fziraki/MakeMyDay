package com.github.fziraki.daykit.internal.location

import com.github.fziraki.daykit.model.LocationResult
import com.github.fziraki.daykit.network.safeCall
import com.github.fziraki.daykit.providers.LocationProvider
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import com.github.fziraki.daykit.result.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

internal class NominatimLocationProvider(private val client: HttpClient) : LocationProvider {

    override suspend fun search(query: String): Result<List<LocationResult>, DataError.Network> {
        return safeCall<List<NominatimResult>> {
            client.get("https://nominatim.openstreetmap.org/search") {
                parameter("q", query)
                parameter("format", "json")
                parameter("addressdetails", 1)
                parameter("limit", 5)
                header("User-Agent", "MakeMyDay/1.0")
            }
        }.map { results ->
            results.map {
                LocationResult(
                    city = it.address.city ?: "",
                    country = it.address.country ?: "",
                    lat = it.lat.toDouble(),
                    lon = it.lon.toDouble()
                )
            }
        }
    }
}
