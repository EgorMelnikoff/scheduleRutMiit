package com.egormelnikoff.schedulerutmiit.app.widget.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.theme.Black
import com.egormelnikoff.schedulerutmiit.ui.theme.White
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeBlue


@SuppressLint("RestrictedApi")
@Composable
fun Empty(
    title: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = ColorProvider(
                    day = Black,
                    night = White
                ),
            )
        )
        onClick?.let {
            Spacer(
                modifier = GlanceModifier.height(8.dp)
            )
            Button(
                modifier = GlanceModifier.cornerRadius(12.dp),
                text = LocalContext.current.getString(R.string.update),
                onClick = it,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ColorProvider(
                        lightThemeBlue,
                        darkThemeBlue
                    ),
                    contentColor = androidx.glance.unit.ColorProvider(
                        White
                    )
                )
            )
        }
    }
}