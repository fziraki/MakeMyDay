package com.github.fziraki.daykit.internal.calendar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.ZoneId

internal class CalendarDataSource(
    private val context: Context
) {

    fun queryTodayEvents(): CalendarQueryResult {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return CalendarQueryResult.PermissionDenied
        }

        val today = LocalDate.now()
        val zone = ZoneId.systemDefault()

        val startOfDay = today
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()

        val endOfDay = today
            .plusDays(1)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()

        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon()
            .appendPath(startOfDay.toString())
            .appendPath(endOfDay.toString())
            .build()

        val projection = arrayOf(
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.ALL_DAY
        )

        val events = mutableListOf<CalendarEventEntity>()

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${CalendarContract.Instances.BEGIN} ASC"
        )

        cursor?.use {
            val titleIndex = it.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
            val startIndex = it.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
            val endIndex = it.getColumnIndexOrThrow(CalendarContract.Instances.END)
            val allDayIndex = it.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)

            while (it.moveToNext()) {
                events += CalendarEventEntity(
                    title = it.getString(titleIndex),
                    startTime = it.getLong(startIndex),
                    endTime = it.getLong(endIndex),
                    isAllDay = it.getInt(allDayIndex) == 1
                )
            }
        }

        return CalendarQueryResult.Success(events)
    }
}

internal sealed interface CalendarQueryResult {
    data class Success(val events: List<CalendarEventEntity>) : CalendarQueryResult
    data object PermissionDenied : CalendarQueryResult
    data object Error : CalendarQueryResult
}