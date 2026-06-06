package com.sifip.cibademo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sifip.cibademo.R

/** Logo BMOI Groupe BCP (PNG officiel dans drawable). */
@Composable
fun BankLogo(
    modifier: Modifier = Modifier,
    width: Dp = 96.dp,
    height: Dp = 96.dp,
) {
    Box(modifier = modifier.width(width).height(height)) {
        Image(
            painter = painterResource(id = R.drawable.bmoi_logo),
            contentDescription = "BMOI Groupe BCP",
            contentScale = ContentScale.Fit,
        )
    }
}

/** Logo SIFIP (PNG officiel dans drawable). */
@Composable
fun SifipLogo(
    modifier: Modifier = Modifier,
    width: Dp = 96.dp,
    height: Dp = 96.dp,
) {
    Box(modifier = modifier.width(width).height(height)) {
        Image(
            painter = painterResource(id = R.drawable.sifip_logo),
            contentDescription = "SIFIP",
            contentScale = ContentScale.Fit,
        )
    }
}
