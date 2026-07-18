package com.github.fziraki.makemyday.myday

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.EditLocationAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.fziraki.makemyday.R
import com.github.fziraki.makemyday.myday.model.CalendarEventUi
import com.github.fziraki.makemyday.myday.model.TrackUi
import com.github.fziraki.makemyday.myday.model.WeatherUi
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDayScreen(
    onNavigateToLocationSearch: () -> Unit,
    themeMode: String,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyDayViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val calendarAsked by viewModel.isCalendarAsked
        .collectAsStateWithLifecycle(initialValue = false)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.onAction(MyDayAction.RetryCalendar)
    }

    val showLightIcon = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Image(
                                modifier = Modifier.height(32.dp).padding(bottom = 4.dp),
                                painter = painterResource(R.drawable.sun),
                                contentDescription = null,
                                colorFilter = if (showLightIcon)
                                    ColorFilter.tint(color =MaterialTheme.colorScheme.tertiary)
                                else
                                    ColorFilter.tint(color =MaterialTheme.colorScheme.secondary)
                            )
                            Image(
                                modifier = Modifier.height(16.dp),
                                painter = painterResource(R.drawable.name),
                                contentDescription = null,
                                colorFilter = if (showLightIcon)
                                    ColorFilter.tint(color =MaterialTheme.colorScheme.secondary)
                                else
                                    ColorFilter.tint(color =MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(
                        onClick = {
                            onToggleTheme()
                        }
                    ) {
                        Icon(
                            imageVector = if (showLightIcon)
                                Icons.Default.LightMode
                            else
                                Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = if (showLightIcon)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                TodayCard()
            }

            item {
                if (state.locationNotSet) {
                    WeatherNotSetCard(onTap = onNavigateToLocationSearch)
                }else {
                    WeatherCard(state.weather, onEditLocation = onNavigateToLocationSearch)
                }
            }

            item {
                SectionLabel(stringResource(R.string.section_todays_events))
                if (state.calendarPermissionDenied) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                val permanentlyDenied =
                                    calendarAsked &&
                                            !ActivityCompat.shouldShowRequestPermissionRationale(
                                                context as Activity,
                                                Manifest.permission.READ_CALENDAR
                                            ) &&
                                            ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.READ_CALENDAR
                                            ) != PackageManager.PERMISSION_GRANTED

                                if (permanentlyDenied) {

                                    context.startActivity(
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts(
                                                "package",
                                                context.packageName,
                                                null
                                            )
                                        }
                                    )

                                } else {

                                    viewModel.markCalendarAsked()
                                    permissionLauncher.launch(Manifest.permission.READ_CALENDAR)
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.calendar_access_needed),
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    stringResource(R.string.calendar_tap_to_grant),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                } else if (state.calendarError) {
                    Text(
                        text = stringResource(R.string.calendar_error_retry),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable { viewModel.onAction(MyDayAction.RetryCalendar) }
                    )
                } else if (state.events.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_events_today),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.events.forEach { EventRow(it) }
                    }
                }
            }

            item {
                SetupMusicRow(
                    artistInput = state.inputArtist,
                    onArtistChange = { viewModel.onAction(MyDayAction.OnArtistChange(it)) },
                    onDone = { viewModel.onAction(MyDayAction.OnDone) },
                    trailingContent = {
                        Button(
                            onClick = { viewModel.onAction(MyDayAction.OnGetTrackClick) },
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Text(stringResource(R.string.get_recommendation))
                        }
                    }
                )
            }

            when(val result = state.musicUiState){
                is MusicUiState.Error -> {
                    item {
                        Text(
                            text = result.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                MusicUiState.Idle -> {

                }
                MusicUiState.Loading -> {

                }
                is MusicUiState.Success -> {
                    item {
                        DiscoverCard(
                            track = result.track,
                            isPlaying = isPlaying,
                            onPlayPause = {
                                viewModel.onAction(MyDayAction.OnPlayPause(it))
                            },
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun TodayCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = todayLabel(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = greeting(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

}


private fun todayLabel(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
    return today.format(formatter)
}

@Composable
private fun greeting(): String {
    val hour = LocalTime.now().hour
    return when {
        hour < 12 -> stringResource(R.string.greeting_morning)
        hour < 18 -> stringResource(R.string.greeting_afternoon)
        hour < 21 -> stringResource(R.string.greeting_evening)
        else -> stringResource(R.string.greeting_night)
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun WeatherCard(weather: WeatherUi?, modifier: Modifier = Modifier, onEditLocation: () -> Unit = {}) {

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ),
        border = BorderStroke(width = 0.5.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (weather == null) {
                Text(
                    stringResource(R.string.weather_unavailable),
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.weather_temp_condition, weather.tempC.toInt(), weather.condition), fontWeight = FontWeight.Medium)
                    Text(
                        stringResource(R.string.weather_feels_like, weather.city, weather.tempC.toInt()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        onEditLocation()
                    },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.tertiary,
            ) {
                Icon(
                    imageVector = Icons.Outlined.EditLocationAlt,
                    contentDescription = stringResource(R.string.change_city),
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun WeatherNotSetCard(onTap: () -> Unit, modifier: Modifier = Modifier) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTap() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
        ),
        border = BorderStroke(width = 0.5.dp, color = MaterialTheme.colorScheme.tertiary)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Cloud,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = stringResource(R.string.weather_not_set),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.tap_to_set_city),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun EventRow(event: CalendarEventUi, modifier: Modifier = Modifier) {
    val time = formatEventTime(event, stringResource(R.string.event_all_day))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(36.dp)
                .background(
                    MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.small
                )
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(event.title ?: stringResource(R.string.event_no_title), fontWeight = FontWeight.Medium)
            Text(
                time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DiscoverCard(
    track: TrackUi,
    isPlaying: Boolean,
    onPlayPause: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionLabel(stringResource(R.string.listen_to_preview))
        Card(
            modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                IconButton(
                    onClick = {
                        onPlayPause(track.source)
                    }
                ) {
                    Icon(
                        imageVector = if (isPlaying)
                            Icons.Default.Pause
                        else
                            Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, fontWeight = FontWeight.Medium)
                Text(
                    stringResource(R.string.track_based_on_taste, track.artist.orEmpty()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

}

private fun formatEventTime(event: CalendarEventUi, allDayLabel: String): String {
    if (event.isAllDay) return allDayLabel
    val zone = ZoneId.systemDefault()
    val start = Instant.ofEpochMilli(event.startTime)
        .atZone(zone)
        .toLocalTime()
    val end = Instant.ofEpochMilli(event.endTime)
        .atZone(zone)
        .toLocalTime()
    return "${start.hour}:${start.minute.toString().padStart(2, '0')} - ${end.hour}:${end.minute.toString().padStart(2, '0')}"
}