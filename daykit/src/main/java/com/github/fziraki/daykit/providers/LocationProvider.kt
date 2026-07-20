package com.github.fziraki.daykit.providers

import com.github.fziraki.daykit.model.LocationResult
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result

interface LocationProvider {
    suspend fun search(query: String): Result<List<LocationResult>, DataError.Network>
}
