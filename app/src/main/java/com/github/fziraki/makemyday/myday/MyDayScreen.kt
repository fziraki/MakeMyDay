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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.EditLocationAlt
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.fziraki.daykit.model.CalendarEvent
import com.github.fziraki.daykit.model.Track
import com.github.fziraki.daykit.model.WeatherInfo
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
    viewModel: MyDayViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val calendarAsked by viewModel.isCalendarAsked
        .collectAsState(initial = false)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.onAction(MyDayAction.RetryCalendar)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {

            TopAppBar(
                title = {
                    Column {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
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
            item { Spacer(Modifier.height(4.dp)) }


            item {
                if (state.locationNotSet) {
                    WeatherNotSetCard(onTap = onNavigateToLocationSearch)
                }else {
                    WeatherCard(state.weather, onEditLocation = onNavigateToLocationSearch)
                }
            }

            item {
                SectionLabel("Today's events")
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
                                    "Calendar access needed",
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    "Tap to grant permission",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                } else if (state.calendarError) {
                    Text(
                        text = "Could not load events. Tap to retry.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable { viewModel.onAction(MyDayAction.RetryCalendar) }
                    )
                } else if (state.events.isEmpty()) {
                    Text(
                        text = "No events today",
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
                    onDone = {
                        viewModel.onAction(MyDayAction.OnDone)
                    },
                    onGetTrackClicked = {
                        viewModel.onAction(MyDayAction.OnGetTrackClick)
                    }
                )
            }

            when(val result = state.musicUiState){
                is MusicUiState.Error -> {

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
private fun SetupMusicRow(
    artistInput: String?,
    onArtistChange: (String) -> Unit,
    onDone: () -> Unit,
    onGetTrackClicked: () -> Unit
) {

    val containerColor = if (!artistInput.isNullOrEmpty())
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    else
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)

    val borderColor = if (!artistInput.isNullOrEmpty())
        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    else
        MaterialTheme.colorScheme.tertiary

    val focusManager = LocalFocusManager.current

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Music",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "An artist you love",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = artistInput?:"",
                    onValueChange = onArtistChange,
                    placeholder = {
                        Text(
                            "e.g. Arctic Monkeys",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onDone()
                        }
                    ),
                    trailingIcon = {
                        if (!artistInput.isNullOrBlank()) {
                            IconButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    onDone()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                            }
                        }
                    }
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "We'll suggest music in a similar style.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        onGetTrackClicked()
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "Get Recommendation"
                    )
                }
            }
        }
    }
}

private fun todayLabel(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
    return today.format(formatter)
}

private fun greeting(): String {
    val hour = LocalTime.now().hour
    return when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun WeatherCard(weather: WeatherInfo?, onEditLocation: () -> Unit = {}) {

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    "Weather unavailable",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${weather.tempC}°C, ${weather.condition}", fontWeight = FontWeight.Medium)
                    Text(
                        "${weather.city} · feels like ${weather.tempC}°C",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier.size(36.dp).clickable {
                    onEditLocation()
                },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.tertiary,
            ) {
                Icon(
                    imageVector = Icons.Outlined.EditLocationAlt,
                    contentDescription = "Change city",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun WeatherNotSetCard(onTap: () -> Unit) {

    Card(
        modifier = Modifier
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
                    text = "Weather not set up",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Tap to set your city",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun EventRow(event: CalendarEvent) {
    val time = formatEventTime(event)
    Row(verticalAlignment = Alignment.CenterVertically) {
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
            Text(event.title ?: "No title", fontWeight = FontWeight.Medium)
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
    track: Track,
    isPlaying: Boolean,
    onPlayPause: (String) -> Unit
) {
    SectionLabel("Listen to the preview:")
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
                    "${track.artist} · based on your taste",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatEventTime(event: CalendarEvent): String {
    if (event.isAllDay) return "All day"
    val zone = ZoneId.systemDefault()
    val start = Instant.ofEpochMilli(event.startTime)
        .atZone(zone)
        .toLocalTime()
    val end = Instant.ofEpochMilli(event.endTime)
        .atZone(zone)
        .toLocalTime()
    return "${start.hour}:${start.minute.toString().padStart(2, '0')} - ${end.hour}:${end.minute.toString().padStart(2, '0')}"
}