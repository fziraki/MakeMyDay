package com.github.fziraki.daykit.internal.calendar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.github.fziraki.daykit.model.CalendarEvent
import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import java.time.LocalDate
import java.time.ZoneId

internal class CalendarDataSource(
    private val context: Context
) {

    fun queryTodayEvents(): Result<List<CalendarEvent>, DataError.Local> {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.Error(DataError.Local.PERMISSION_DENIED)
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

        val events = mutableListOf<CalendarEvent>()

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
                events += CalendarEvent(
                    title = it.getString(titleIndex) ?: "",
                    startTime = it.getLong(startIndex),
                    endTime = it.getLong(endIndex),
                    isAllDay = it.getInt(allDayIndex) == 1
                )
            }
        }

        return Result.Success(events)
    }
}