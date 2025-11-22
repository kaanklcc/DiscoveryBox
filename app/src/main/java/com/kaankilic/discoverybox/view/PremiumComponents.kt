package com.kaankilic.discoverybox.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaankilic.discoverybox.R

@Composable
fun PremiumBadge(isPremium: Boolean) {
    if (isPremium) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = "Premium",
                tint = Color(0xFFFCD34D),
                modifier = Modifier.size(16.dp)
            )
            Text(
                "Premium",
                color = Color(0xFFFCD34D),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TokenCounter(remainingTokens: Int, isPremium: Boolean) {
    if (isPremium && remainingTokens >= 0) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painterResource(R.drawable.crown),
                contentDescription = "Tokens",
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(16.dp)
            )
            Text(
                "$remainingTokens",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LockedFeatureButton(
    text: String,
    isLocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isLocked,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isLocked) Color.Gray else Color(0xFFFBBF24),
            disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
        )
    ) {
        if (isLocked) {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(text)
    }
}

@Composable
fun UpgradeToPremiumDialog(
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit,
    title: String = "Premium'a Geç",
    message: String = "Daha fazla hikaye oluşturmak için premium üyelik gereklidir."
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onUpgrade,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFBBF24)
                )
            ) {
                Text("Premium'a Geç")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun PremiumFeatureCard(
    icon: String,
    title: String,
    description: String,
    isUnlocked: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isUnlocked) Color.White.copy(alpha = 0.1f) 
                else Color.Gray.copy(alpha = 0.1f)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (isUnlocked) icon else "🔒",
            fontSize = 32.sp,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.6f)
            )
            Text(
                description,
                fontSize = 13.sp,
                color = if (isUnlocked) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.5f)
            )
        }
    }
}