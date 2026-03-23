package com.alilopez.kt_demohilt.core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    isDarkTheme: Boolean,
    accentColor: Color,
    textColor: Color,
    secondaryTextColor: Color
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            IconButton(
                onClick = {
                    if (isFocused) {
                        focusManager.clearFocus()
                    }
                }
            ) {
                Icon(
                    imageVector = if (isFocused) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Search,
                    contentDescription = if (isFocused) "Dejar de escribir" else "Buscar",
                    tint = secondaryTextColor
                )
            }
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar búsqueda",
                            tint = secondaryTextColor
                        )
                    }

                    IconButton(onClick = {
                        onSearch()
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = accentColor
                        )
                    }
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = accentColor.copy(alpha = if (isDarkTheme) 0.16f else 0.10f),
            unfocusedContainerColor = accentColor.copy(alpha = if (isDarkTheme) 0.10f else 0.06f),
            focusedBorderColor = accentColor.copy(alpha = 0.9f),
            unfocusedBorderColor = accentColor.copy(alpha = 0.35f),
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedLeadingIconColor = accentColor,
            unfocusedLeadingIconColor = secondaryTextColor,
            cursorColor = accentColor
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch()
            focusManager.clearFocus()
        })
    )
}

