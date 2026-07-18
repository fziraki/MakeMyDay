package com.github.fziraki.makemyday.myday

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.github.fziraki.makemyday.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SetupMusicRow(
    artistInput: String?,
    onArtistChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    val isGranted = !artistInput.isNullOrEmpty()
    val containerColor = if (isGranted)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    else
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
    val borderColor = if (isGranted)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    else
        MaterialTheme.colorScheme.tertiary
    val focusManager = LocalFocusManager.current

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
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
                    text = stringResource(R.string.music_section_title),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.music_section_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = artistInput ?: "",
                    onValueChange = onArtistChange,
                    placeholder = {
                        Text(
                            stringResource(R.string.music_input_hint),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
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
                                    contentDescription = stringResource(R.string.action_done),
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                            }
                        }
                    }
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.music_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (trailingContent != null) {
                    Spacer(Modifier.height(16.dp))
                    trailingContent()
                }
            }
        }
    }
}
