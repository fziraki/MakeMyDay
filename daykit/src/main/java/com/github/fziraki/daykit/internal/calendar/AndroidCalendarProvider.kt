package com.github.fziraki.daykit.internal.calendar

import android.content.Context
import com.github.fziraki.daykit.providers.CalendarProvider
import com.github.fziraki.daykit.providers.CalendarResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AndroidCalendarProvider(
    context: Context
) : CalendarProvider {

    private val dataSource = CalendarDataSource(context)

    override suspend fun getTodayEvents(): CalendarResult =
        withContext(Dispatchers.IO) {
            when (val result = dataSource.queryTodayEvents()) {
                is CalendarQueryResult.Success -> CalendarResult.Success(
                    result.events.map { it.toDomain() }
                )
                is CalendarQueryResult.PermissionDenied -> CalendarResult.PermissionDenied
                is CalendarQueryResult.Error -> CalendarResult.Error
            }
        }
}