package com.github.fziraki.daykit.providers

import com.github.fziraki.daykit.model.LocationResult

interface LocationSearchRepository {
    suspend fun search(query: String): List<LocationResult>
}