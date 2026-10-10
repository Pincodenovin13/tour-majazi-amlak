package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppRepository
import com.example.model.PropertyItem
import com.example.ui.components.AgentAvatarView
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.util.PersianUtils

/**
 * Masks phone number like: ۰۹۱۲***۶۷۸۹
 */
fun maskPhoneNumber(phone: String): String {
    val digits = phone.filter { it.isDigit() || it in '۰'..'۹' }
    return if (digits.length >= 8) {
        val start = digits.take(4)
        val end = digits.takeLast(4)
        "$start***$end"
    } else {
        "۰۹۱۲***۶۷۸۹"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentPublicProfileScreen(
    agentName: String,
    agentPhone: String = "۰۹۱۲۳۴۵۶۷۸۹",
    agencyName: String = "املاک مدرن شمیران",
    city: String = "ساری",
    bannerViewCount: Int = 0,
    repository: AppRepository,
    onOpenTour: (PropertyItem) -> Unit,
    onBackClick: () -> Unit,
    onRequireLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allProperties by repository.properties.collectAsState()
    val isAgentOnline by repository.isAgentOnline.collectAsState()
    val favoriteIds by repository.favoritePropertyIds.collectAsState()
    val isLoggedIn by repository.isLoggedIn.collectAsState()

    var isFavoriteAgent by remember { mutableStateOf(false) }
    var showGuestDialog by remember { mutableStateOf(false) }

    // Active 360 tours for this agent
    val agentTours = remember(allProperties, agentName) {
        allProperties.filter {
            it.agentName == agentName || agentName.contains(it.agentName) || it.agentName.contains(agentName)
        }.ifEmpty { allProperties }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "پروفایل مشاور املاک",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (!isLoggedIn) {
                                showGuestDialog = true
                            } else {
                                isFavoriteAgent = !isFavoriteAgent
                                Toast.makeText(
                                    context,
                                    if (isFavoriteAgent) "مشاور به علاقه‌مندی‌ها افزوده شد" else "مشاور از علاقه‌مندی‌ها حذف شد",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavoriteAgent) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "ذخیره در علاقه‌مندی‌ها",
                            tint = if (isFavoriteAgent) AccentYellow else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("agent_public_profile_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Agent Header Info
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Large Circular Avatar with Online/Offline Dot
                            Box {
                                AgentAvatarView(
                                    repository = repository,
                                    sizeDp = 84,
                                    borderWidthDp = 3f
                                )
                                // Online/Offline status dot (green / red)
                                Surface(
                                    shape = CircleShape,
                                    color = if (isAgentOnline) Color(0xFF4CAF50) else Color(0xFFE53935),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, DarkSurface),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.BottomEnd)
                                ) {}
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = agentName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "تایید هویت",
                                        tint = BrandSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = agencyName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AccentYellow,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Masked Phone Number
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = maskPhoneNumber(agentPhone),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // City & Online status tag
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isAgentOnline) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (isAgentOnline) "آنلاین و پاسخگو" else "آفلاین",
                                            color = if (isAgentOnline) Color(0xFF4CAF50) else Color(0xFFE53935),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = "مستقر در $city",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats: Total tours and view counts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceCard,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.RotateRight, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "${PersianUtils.toPersianDigits(agentTours.size)} فایل ۳۶۰°",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = AccentYellow
                                        )
                                    }
                                    Text("تورهای فعال مشاور", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceCard,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "${PersianUtils.toPersianDigits(bannerViewCount + 128)} بازدید",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = AccentOrange
                                        )
                                    }
                                    Text("کل بازدیدهای ثبت‌شده", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4 Contact Buttons: واتساپ, تلگرام, بله, تماس تلفنی
                        Text(
                            text = "راه‌های ارتباط مستقیم با مشاور:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Call button
                            Button(
                                onClick = {
                                    if (!isLoggedIn) {
                                        showGuestDialog = true
                                    } else {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$agentPhone"))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تماس", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            // WhatsApp button
                            Button(
                                onClick = {
                                    if (!isLoggedIn) {
                                        showGuestDialog = true
                                    } else {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=989123456789"))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("واتساپ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            // Telegram button
                            Button(
                                onClick = {
                                    if (!isLoggedIn) {
                                        showGuestDialog = true
                                    } else {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/amlak_modern"))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("تلگرام", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            // Bale (بله) messenger
                            Button(
                                onClick = {
                                    if (!isLoggedIn) {
                                        showGuestDialog = true
                                    } else {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ble.ir/amlak_modern"))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("بله", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bookmark / Save button
                        OutlinedButton(
                            onClick = {
                                if (!isLoggedIn) {
                                    showGuestDialog = true
                                } else {
                                    isFavoriteAgent = !isFavoriteAgent
                                    Toast.makeText(
                                        context,
                                        if (isFavoriteAgent) "مشاور ذخیره شد" else "از ذخیره‌شده‌ها حذف شد",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (isFavoriteAgent) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = AccentYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isFavoriteAgent) "ذخیره شده در علاقه‌مندی‌ها" else "ذخیره در علاقه‌مندی‌ها",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Section 2: Agent's 360 Tours
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تورهای این مشاور",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentYellow.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${PersianUtils.toPersianDigits(agentTours.size)} فایل تور ۳۶۰°",
                            color = AccentYellow,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Grid of Tour Cards
            items(agentTours, key = { it.id }) { prop ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenTour(prop) }
                        .testTag("agent_tour_card_${prop.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            Image(
                                painter = painterResource(id = prop.primaryImageRes),
                                contentDescription = prop.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // 360 Tour badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandSecondary,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.RotateRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Text("تور ۳۶۰ فعال", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = prop.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${prop.city}، ${prop.neighborhood} • ${PersianUtils.toPersianDigits(prop.areaSqMeters)} متر • ${PersianUtils.toPersianDigits(prop.rooms)} خوابه",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = PersianUtils.formatPrice(prop.price),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange
                                )

                                Button(
                                    onClick = { onOpenTour(prop) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color.Black),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ورود به تور ۳۶۰°", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Guest Registration Dialog
    if (showGuestDialog) {
        AlertDialog(
            onDismissRequest = { showGuestDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(text = "نیاز به ثبت‌نام و ورود", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "برای این کار لطفاً ثبت‌نام کنید تا بتوانید مستقیماً با مشاور تماس گرفته و فایل‌ها را ذخیره کنید.",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGuestDialog = false
                        onRequireLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color.Black)
                ) {
                    Text("ثبت‌نام / ورود", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGuestDialog = false }) {
                    Text("انصراف", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }
}
