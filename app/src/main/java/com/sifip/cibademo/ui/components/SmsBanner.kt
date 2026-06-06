package com.sifip.cibademo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifip.cibademo.ui.theme.AppMuted
import com.sifip.cibademo.ui.theme.AppText
import com.sifip.cibademo.ui.theme.BankPurple

/**
 * Bannière SMS simulée style notification Android : slide-down depuis le
 * haut, expéditeur + heure + aperçu, fond blanc avec ombre. Tap déclenche
 * onTap (équivalent du clic sur le deeplink dans le SMS).
 */
@Composable
fun SmsBanner(
    visible: Boolean,
    sender: String,
    preview: String,
    timestamp: String = "maintenant",
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onTap),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BankPurple),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Sms,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = sender,
                            color = AppText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "  · $timestamp",
                            color = AppMuted,
                            fontSize = 12.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = preview,
                        color = AppText,
                        fontSize = 13.sp,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}
