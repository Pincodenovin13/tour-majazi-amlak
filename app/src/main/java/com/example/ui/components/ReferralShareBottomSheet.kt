package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.util.ReferralShareHelper

data class ShareOptionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val brandColor: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralShareBottomSheet(
    referralCode: String,
    referrerName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shareMessage = ReferralShareHelper.buildShareMessage(referrerName, referralCode)
    val fullLink = ReferralShareHelper.getFullReferralLink(referralCode)
    val shortLink = ReferralShareHelper.getShortReferralLink(referralCode)

    val options = listOf(
        ShareOptionItem(
            id = "whatsapp",
            title = "واتساپ",
            subtitle = "WhatsApp",
            icon = Icons.Default.Send,
            brandColor = Color(0xFF25D366),
            onClick = {
                ReferralShareHelper.shareToWhatsApp(context, shareMessage)
                onDismiss()
            }
        ),
        ShareOptionItem(
            id = "telegram",
            title = "تلگرام",
            subtitle = "Telegram",
            icon = Icons.Default.Send,
            brandColor = Color(0xFF0088CC),
            onClick = {
                ReferralShareHelper.shareToTelegram(context, shareMessage)
                onDismiss()
            }
        ),
        ShareOptionItem(
            id = "bale",
            title = "بله",
            subtitle = "Bale Messenger",
            icon = Icons.Default.Message,
            brandColor = Color(0xFF009688),
            onClick = {
                ReferralShareHelper.shareToBale(context, shareMessage)
                onDismiss()
            }
        ),
        ShareOptionItem(
            id = "eitaa",
            title = "ایتا",
            subtitle = "Eitaa",
            icon = Icons.Default.Message,
            brandColor = Color(0xFFE65100),
            onClick = {
                ReferralShareHelper.shareToEitaa(context, shareMessage)
                onDismiss()
            }
        ),
        ShareOptionItem(
            id = "sms",
            title = "پیامک (SMS)",
            subtitle = "ارسال با سیم‌کارت",
            icon = Icons.Default.Message,
            brandColor = Color(0xFF1976D2),
            onClick = {
                ReferralShareHelper.shareViaSms(context, shareMessage)
                onDismiss()
            }
        ),
        ShareOptionItem(
            id = "copy",
            title = "کپی لینک دعوت",
            subtitle = "کپی کامل در حافظه",
            icon = Icons.Default.ContentCopy,
            brandColor = AccentYellow,
            onClick = {
                ReferralShareHelper.copyToClipboard(context, fullLink, "لینک دعوت")
                onDismiss()
            }
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("referral_share_bottom_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AccentOrange.copy(alpha = 0.2f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(22.dp))
                        }
                    }

                    Column {
                        Text(
                            text = "اشتراک‌گذاری لینک دعوت",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ارسال مستقیم لینک دیپ‌لینک به پیام‌رسان‌ها و دوستان",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "بستن", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Deep link preview box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, AccentYellow.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "لینک اختصاصی شما (دیپ‌لینک هوشمند):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = fullLink,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentYellow,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "لینک کوتاه: $shortLink",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "انتخاب روش ارسال:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2-column Grid of Sharing apps
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(options) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { item.onClick() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, item.brandColor.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = item.brandColor.copy(alpha = 0.2f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = item.brandColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
