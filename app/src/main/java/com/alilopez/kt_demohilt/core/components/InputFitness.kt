package com.alilopez.kt_demohilt.core.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputFitness(
    value: String?,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null
) {
    val isDarkTheme = isSystemInDarkTheme()

    // Colores basados en el tema
    val backgroundColor = if (isDarkTheme) Color(0xFF1a3222) else Color.White
    val borderColor = if (isDarkTheme) Color(0xFF346544) else Color(0xFFE2E8F0) // slate-200
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A) // slate-900
    val placeholderColor = if (isDarkTheme) Color(0xFF5e8c6d) else Color(0xFF94A3B8) // slate-400
    val focusedBorderColor = Color(0xFF10B981) // primary green color

    OutlinedTextField(
        value = value ?: "",
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        readOnly = readOnly,
        placeholder = {
            Text(
                text = placeholder,
                color = placeholderColor,
                fontSize = 16.sp
            )
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = placeholderColor
                )
            }
        },
        trailingIcon = trailingIcon?.let {
            {
                if (onTrailingIconClick != null) {
                    IconButton(onClick = onTrailingIconClick) {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = placeholderColor
                        )
                    }
                } else {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = placeholderColor
                    )
                }
            }
        },
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            disabledContainerColor = backgroundColor,
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = borderColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            cursorColor = focusedBorderColor
        ),
        singleLine = true
    )
}
