package com.sifip.cibademo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifip.cibademo.ui.theme.BankPurple
import com.sifip.cibademo.ui.theme.SifipBlue

/** Bandeau supérieur violet pour les écrans bancaires. */
@Composable
fun BankTopBar(
    title: String,
    onHomeClick: (() -> Unit)? = null,
    onPowerClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    TopBar(
        title = title,
        backgroundColor = BankPurple,
        onHomeClick = onHomeClick,
        onPowerClick = onPowerClick,
        modifier = modifier,
    )
}

/** Bandeau supérieur bleu pour les écrans SIFIP (consentement). */
@Composable
fun SifipTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SifipBlue)
            .height(56.dp),
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White,
                )
            }
        }
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 56.dp),
        )
    }
}

@Composable
private fun TopBar(
    title: String,
    backgroundColor: Color,
    onHomeClick: (() -> Unit)?,
    onPowerClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .height(48.dp),
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 96.dp),
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onHomeClick != null) {
                IconButton(onClick = onHomeClick) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Accueil",
                        tint = Color.White,
                    )
                }
            }
            if (onPowerClick != null) {
                IconButton(onClick = onPowerClick) {
                    Icon(
                        imageVector = Icons.Filled.PowerSettingsNew,
                        contentDescription = "Déconnexion",
                        tint = Color.White,
                    )
                }
            }
            if (onHomeClick == null && onPowerClick == null) {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    }
}
