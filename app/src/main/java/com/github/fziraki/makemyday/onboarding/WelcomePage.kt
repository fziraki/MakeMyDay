package com.github.fziraki.makemyday.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.fziraki.makemyday.R

@Composable
fun WelcomePage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.sun),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Image(
            modifier = Modifier.height(20.dp),
            painter = painterResource(R.drawable.name),
            contentDescription = null
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.welcome_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp))

        Spacer(Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            WelcomeFeatureRow(
                icon = Icons.Outlined.Cloud,
                label = stringResource(R.string.welcome_weather)
            )
            WelcomeFeatureRow(
                icon = Icons.Outlined.CalendarToday,
                label = stringResource(R.string.welcome_calendar)
            )
            WelcomeFeatureRow(
                icon = Icons.Outlined.MusicNote,
                label = stringResource(R.string.welcome_music)
            )
        }
    }
}

@Composable
private fun WelcomeFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}