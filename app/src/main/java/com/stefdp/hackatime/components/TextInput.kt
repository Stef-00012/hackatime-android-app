package com.stefdp.hackatime.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.hackatime.R
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String? = null,
    placeholder: String?= null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    onPasswordToggle: (
        visible: Boolean
    ) -> Unit = { },
    sideButtonIcon: Painter? = null,
    onSideButtonPress: () -> Unit = {},
    sideButtonContentDescription: String? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        selectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        ),
        cursorColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
        focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent
    )
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth(),
//            .then(modifier),
        singleLine = singleLine,
        label = if (label != null) {
            {
                Text(
                    text = label,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        } else null,
        placeholder = if (placeholder != null) {
            {
                Text(
                    text = placeholder,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        } else null,
        enabled = enabled,
        colors = colors,
        shape = RoundedCornerShape(8.dp),
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else VisualTransformation.None,
        trailingIcon = if (isPassword || sideButtonIcon != null) {
            {
                IconButton(
                    modifier = Modifier.padding(end = 4.dp),
                    onClick = if (isPassword) {
                        {
                            passwordVisible = !passwordVisible
                            onPasswordToggle(passwordVisible)
                        }
                    } else onSideButtonPress
                ) {
                    Icon(
                        painter = if (isPassword) {
                            if (passwordVisible) {
                                painterResource(R.drawable.visibility_off)
                            } else {
                                painterResource(R.drawable.visibility)
                            }
                        } else {
                            /*
                                This is safe because this button only appears if the
                                (isPassword || sideButtonIcon != null) condition passes,
                                and this "else" is only reached if "isPassword" is false,
                                which means "sideButtonIcon" must be non-null
                            */
                            sideButtonIcon as Painter
                        },
                        contentDescription = if (isPassword) {
                            if (passwordVisible) "Hide Password" else "Show Password"
                        } else sideButtonContentDescription ?: "Side Button",
                        modifier = Modifier.requiredSize(28.dp)
                    )
                }
            }
        } else null
    )
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun TextInputPreview() {
    HackatimeStatsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = Color.Transparent
            ) {
                TextInput(
                    value = TextFieldValue(""),
                    onValueChange = { },
//                    label = "Label",
                    isPassword = true,
                    placeholder = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                )
            }
            
        }
    }
}