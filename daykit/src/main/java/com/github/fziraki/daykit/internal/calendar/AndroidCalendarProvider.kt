package com.github.fziraki.daykit.internal.calendar

import android.content.Context
import com.github.fziraki.daykit.providers.CalendarProvider
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AndroidCalendarProvider(
    context: Context
) : CalendarProvider {

    private val dataSource = CalendarDataSource(context)

    override suspend fun getTodayEvents(): Result<List<com.github.fziraki.daykit.model.CalendarEvent>, DataError.Local> =
        withContext(Dispatchers.IO) {
            when (val result = dataSource.queryTodayEvents()) {
                is CalendarQueryResult.Success -> Result.Success(
                    result.events.map { it.toDomain() }
                )
                is CalendarQueryResult.PermissionDenied -> Result.Error(DataError.Local.PERMISSION_DENIED)
                is CalendarQueryResult.Error -> Result.Error(DataError.Local.UNKNOWN)
            }
        }
}