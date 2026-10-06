package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppRepository
import com.example.model.PropertyItem
import com.example.model.PropertyStatus
import com.example.model.SubscriptionPlan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.ColorAds
import com.example.ui.theme.ColorAnalytics
import com.example.ui.theme.ColorProfile
import com.example.ui.theme.ColorProperties
import com.example.ui.theme.ColorSubscription
import com.example.ui.theme.ColorTour
import com.example.ui.util.JalaliDateHelper
import com.example.ui.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDashboardScreen(
    repository: AppRepository,
    onCreateTourClick: () -> Unit,
    onMyPropertiesClick: () -> Unit,
    onSubscriptionClick: () -> Unit,
    onCityAdsClick: () -> Unit,
    onAnalyticsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onCapture360Click: () -> Unit = {},
    onProfileClick: () -> Unit,
    onOpenTourViewer: (PropertyItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val userName by repository.userName.collectAsState()
    val agencyName by repository.agencyName.collectAsState()
    val activeSubscription by repository.activeSubscription.collectAsState()
    val properties by repository.properties.collectAsState()
    val ads by repository.ads.collectAsState()
    val commissionStat by repository.commissionStat.collectAsState()

    val agentCoverRes by repository.agentCoverRes.collectAsState()
    val agentAvatarRes by repository.agentAvatarRes.collectAsState()
    val isAgentOnline by repository.isAgentOnline.collectAsState()
    val agentReferralCode by repository.agentReferralCode.collectAsState()

    val isAgentActive = repository.isAgentActive()
    val isNearExpiry = repository.isSubscriptionNearExpiry()
    val remainingDays = activeSubscription?.remainingDays ?: 0

    val totalViews = properties.sumOf { it.viewsCount } + ads.sumOf { it.viewCount }

    // Modals
    var showCoverChangeDialog by remember { mutableStateOf(false) }
    var showAvatarChangeDialog by remember { mutableStateOf(false) }
    var propertyToDelete by remember { mutableStateOf<PropertyItem?>(null) }
    var showWithdrawModal by remember { mutableStateOf(false) }
    var withdrawAmountInput by remember { mutableStateOf(commissionStat.availableBalance.coerceAtLeast(500_000L).toString()) }
    var shebaInput by remember { mutableStateOf("IR820120000000012345678901") }
    var cardInput by remember { mutableStateOf("6037997512345678") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("agent_dashboard"),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TOP HEADER BANNER WITH AGENCY COVER IMAGE & AGENT AVATAR
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                // Agency Cover Image
                Image(
                    painter = painterResource(id = agentCoverRes),
                    contentDescription = "سردر دفتر املاک",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showCoverChangeDialog = true }
                )

                // Dark Gradient for legibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )

                // Button to change Cover Photo
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .clickable { showCoverChangeDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(14.dp))
                        Text("تغییر عکس سردر املاک", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }

                // Agent Info & Profile Face Avatar in Bottom Portion
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Face Avatar
                    Box(modifier = Modifier.size(72.dp)) {
                        Image(
                            painter = painterResource(id = agentAvatarRes),
                            contentDescription = "عکس چهره مشاور",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, AccentYellow, CircleShape)
                                .clickable { showAvatarChangeDialog = true }
                        )

                        // Camera overlay badge
                        Surface(
                            shape = CircleShape,
                            color = BrandPrimary,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .border(1.dp, Color.White, CircleShape)
                                .clickable { showAvatarChangeDialog = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(13.dp))
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Online / Offline live switcher button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isAgentOnline) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isAgentOnline) Color(0xFF4CAF50) else Color.Gray),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { repository.toggleAgentOnline() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(if (isAgentOnline) Color(0xFF4CAF50) else Color.Gray, CircleShape)
                                    )
                                    Text(
                                        text = if (isAgentOnline) "آنلاین" else "آفلاین",
                                        color = if (isAgentOnline) Color(0xFF4CAF50) else Color.Gray,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = agencyName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // 2. SUBSCRIPTION STATUS: ACTIVE vs DEACTIVE BANNER + 2-DAY EXPIRY WARNING
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (!isAgentActive) {
                    // DEACTIVE ALERT BANNER
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A1010)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE53935))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(24.dp))
                                Text(
                                    text = "وضعیت حساب: دی‌اکتیو (غیرفعال)",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "به دلیل اتمام یا نداشتن اشتراک فعال، املاک شما از دید عموم مخفی شده و هیچ فایلی به مردم ارائه نمی‌شود. همچنین در صورت تمدید همکاران معرفی‌شده، پورسانت شما خواهد سوخت! برای اکتیو شدن، یکی از طرح‌ها را فعال فرمایید:",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onSubscriptionClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("خرید طرح و اکتیو شدن", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                } else {
                    // ACTIVE STATUS CARD
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(20.dp))
                                    Text(
                                        text = "وضعیت حساب: اکتیو (فعال)",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = BrandSecondary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AccentYellow.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${PersianUtils.toPersianDigits(remainingDays)} روز اعتبار باقی مانده",
                                        color = AccentYellow,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // 2-DAY EXPIRY WARNING (CRITICAL RULE)
                            if (isNearExpiry) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AccentOrange.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = AccentOrange)
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "هشدار تمدید اشتراک (تنها ۲ روز باقی مانده!)",
                                                fontWeight = FontWeight.Bold,
                                                color = AccentOrange,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "برای جلوگیری از دی‌اکتیو شدن و تعلیق نمایش املاک به عموم، اقدام به تمدید فرمایید.",
                                                color = Color.White.copy(alpha = 0.85f),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                        Button(
                                            onClick = onSubscriptionClick,
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("تمدید", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2.5 QUICK 360 CAPTURE BANNER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onCapture360Click() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandPrimary),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentYellow)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AccentYellow,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
                            }
                        }
                        Column {
                            Text(
                                text = "عکاسی هوشمند ۳۶۰° (CameraX + Gyro)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "تراز ۲° خودکار، ره‌گیری ژیروسکوپ و دوخت مستقیم با OpenCV",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentYellow
                    ) {
                        Text(
                            text = "شروع",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 3. MAIN ACTION GRID (3x2 BUTTONS)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "منوی اختصاصی مشاور املاک",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AgentGridButton(
                        title = "ساخت تور جدید",
                        subtitle = "عکسبرداری هوشمند ۳۶۰",
                        icon = Icons.Default.CameraAlt,
                        color = ColorTour,
                        modifier = Modifier.weight(1f),
                        onClick = onCreateTourClick,
                        testTag = "btn_grid_create_tour"
                    )

                    AgentGridButton(
                        title = "املاک من",
                        subtitle = "${PersianUtils.toPersianDigits(properties.size)} فایل ثبت شده",
                        icon = Icons.Default.Home,
                        color = ColorProperties,
                        modifier = Modifier.weight(1f),
                        onClick = onMyPropertiesClick,
                        testTag = "btn_grid_my_properties"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AgentGridButton(
                        title = "اشتراک من",
                        subtitle = "طرح‌های ۱، ۲ و ۳ ماهه",
                        icon = Icons.Default.CreditCard,
                        color = ColorSubscription,
                        modifier = Modifier.weight(1f),
                        onClick = onSubscriptionClick,
                        testTag = "btn_grid_subscription"
                    )

                    AgentGridButton(
                        title = "تبلیغات شهری",
                        subtitle = "بنر با اسلایدر ۱ تا ۳۰ روز",
                        icon = Icons.Default.Campaign,
                        color = ColorAds,
                        modifier = Modifier.weight(1f),
                        onClick = onCityAdsClick,
                        badge = "تعرفه دقیق",
                        testTag = "btn_grid_city_ads"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AgentGridButton(
                        title = "آمار بازدید",
                        subtitle = "${PersianUtils.toPersianDigits(totalViews)} بازدید کل",
                        icon = Icons.Default.Insights,
                        color = ColorAnalytics,
                        modifier = Modifier.weight(1f),
                        onClick = onAnalyticsClick,
                        testTag = "btn_grid_analytics"
                    )

                    AgentGridButton(
                        title = "پروفایل مشاور",
                        subtitle = "تنظیمات آژانس و چهره",
                        icon = Icons.Default.Person,
                        color = ColorProfile,
                        modifier = Modifier.weight(1f),
                        onClick = onProfileClick,
                        testTag = "btn_grid_profile"
                    )
                }
            }
        }

        // 4. COLLEAGUE REFERRAL SYSTEM & COMMISSION BURNING RULES
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = AccentYellow)
                            Text(
                                text = "معرفی همکاران مشاور و کسب پورسانت",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AccentYellow
                        ) {
                            Text(
                                text = "واریز ۲۴ ساعته",
                                color = Color.Black,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "با کد معرف اختصاصی خود همکاران دفاتر دیگر را دعوت کنید و از هر خرید اشتراک پورسانت نقدی بگیرید:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Referral Code Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("کد معرف اختصاصی شما:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(agentReferralCode, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AccentYellow)
                            }

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(agentReferralCode))
                                    Toast.makeText(context, "کد معرف کپی شد", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("کپی کد", color = Color.Black, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Commission Table: 1M=20%, 2M=15%, 3M=10%
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("جدول پورسانت خرید همکاران:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = AccentYellow)
                            Text("• پلن ۱ ماهه (۱.۵ میلیون): ۲۰٪ پورسانت = ۳۰۰,۰۰۰ تومان", style = MaterialTheme.typography.labelSmall, color = Color.White)
                            Text("• پلن ۲ ماهه (۲.۵ میلیون): ۱۵٪ پورسانت = ۳۷۵,۰۰۰ تومان", style = MaterialTheme.typography.labelSmall, color = Color.White)
                            Text("• پلن ۳ ماهه (۴ میلیون): ۱۰٪ پورسانت = ۴۰۰,۰۰۰ تومان", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // CRITICAL BURNING RULE INFO
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF3E2723),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF7043)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFF7043))
                            Text(
                                text = "قانون سوختن پورسانت: مشاور فقط در صورتی پورسانت را دریافت و برداشت می‌کند که خود دارای طرح فعال باشد. در صورت دی‌اکتیو بودن، پورسانت تمدید همکاران می‌سوزد!",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Earnings Balance & 24h Payout Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("موجودی پورسانت قابل برداشت:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = PersianUtils.formatPrice(commissionStat.availableBalance),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandSecondary
                            )
                        }

                        Button(
                            onClick = { showWithdrawModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تسویه کامل (واریز ۲۴ ساعته)", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // 5. PROPERTIES LIST WITH INSTANT DELETE BUTTON FOR SOLD PROPERTIES
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "املاک و تورهای ثبت شده شما",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ملکی که فروخته شد را با دکمه حذف خارج کنید تا به خریداران نشان داده نشود",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onCreateTourClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("+ ثبت فایل جدید", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Properties list items
        items(properties, key = { it.id }) { property ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val thumbRes = property.scenes.firstOrNull()?.drawableResId ?: R.drawable.img_tour_sample
                        Image(
                            painter = painterResource(id = thumbRes),
                            contentDescription = property.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenTourViewer(property) }
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = property.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "${property.city}، ${property.neighborhood} • ${property.transactionType}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = PersianUtils.formatPrice(property.price),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // View 360 Tour Button
                        Button(
                            onClick = { onOpenTourViewer(property) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مشاهده تور ۳۶۰", style = MaterialTheme.typography.labelSmall)
                        }

                        // INSTANT DELETE / SOLD BUTTON (AS REQUESTED)
                        Button(
                            onClick = { propertyToDelete = property },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("حذف (فروخته شد)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: Change Agency Cover Photo
    if (showCoverChangeDialog) {
        AlertDialog(
            onDismissRequest = { showCoverChangeDialog = false },
            title = { Text("انتخاب عکس سردر و دفتر املاک") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("یکی از تصاویر نمونه دفتر املاک را برای قاب بالای داشبورد خود انتخاب کنید:")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            R.drawable.img_tour_sample to "نمای سالن ۱",
                            R.drawable.img_tour_bedroom to "نمای اتاق",
                            R.drawable.ic_app_logo to "لوگوی اختصاصی"
                        ).forEach { (res, label) ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        repository.updateAgentCover(res)
                                        showCoverChangeDialog = false
                                        Toast.makeText(context, "عکس سردر املاک به‌روزرسانی شد", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(6.dp)) {
                                    Image(painter = painterResource(id = res), contentDescription = null, modifier = Modifier.size(50.dp), contentScale = ContentScale.Crop)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(label, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCoverChangeDialog = false }) { Text("بستن") }
            }
        )
    }

    // Modal: Change Agent Avatar Photo
    if (showAvatarChangeDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarChangeDialog = false },
            title = { Text("انتخاب عکس چهره مشاور املاک") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("عکس پرسنلی یا نمایه خود را انتخاب فرمایید:")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            R.drawable.img_tour_bedroom to "عکس رسمی ۱",
                            R.drawable.img_tour_sample to "عکس رسمی ۲",
                            R.drawable.ic_app_logo to "آیکون هویت"
                        ).forEach { (res, label) ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        repository.updateAgentAvatar(res)
                                        showAvatarChangeDialog = false
                                        Toast.makeText(context, "عکس چهره با موفقیت تغییر یافت", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(6.dp)) {
                                    Image(painter = painterResource(id = res), contentDescription = null, modifier = Modifier.size(50.dp), contentScale = ContentScale.Crop)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(label, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAvatarChangeDialog = false }) { Text("بستن") }
            }
        )
    }

    // Delete Sold Property Dialog
    propertyToDelete?.let { prop ->
        AlertDialog(
            onDismissRequest = { propertyToDelete = null },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("حذف ملک فروخته شده") },
            text = {
                Text("آیا ملک «${prop.title}» به فروش یا اجاره رفته است؟ با تایید، این ملک بلافاصله از سیستم حذف شده و دیگر به هیچ کاربری در بازدید عموم نشان داده نمی‌شود.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.markPropertySold(prop.id)
                        Toast.makeText(context, "ملک به عنوان فروخته شده ثبت شد و از دید عموم حذف گردید", Toast.LENGTH_LONG).show()
                        propertyToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("تایید حذف و فروش")
                }
            },
            dismissButton = {
                TextButton(onClick = { propertyToDelete = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Withdrawal Modal (24h Payout)
    if (showWithdrawModal) {
        AlertDialog(
            onDismissRequest = { showWithdrawModal = false },
            title = { Text("درخواست تسویه حساب (واریز ۲۴ ساعته)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "مبلغ مورد نظر جهت تسویه حساب را وارد نمایید. پس از تایید، وجه ظرف ۲۴ ساعت از طریق سامانه پایا به شماره شبا واریز می‌گردد.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = withdrawAmountInput,
                        onValueChange = { withdrawAmountInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("مبلغ درخواستی (تومان)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = shebaInput,
                        onValueChange = { shebaInput = it },
                        label = { Text("شماره شبا (با IR)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cardInput,
                        onValueChange = { cardInput = it },
                        label = { Text("شماره کارت بانکی") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawAmountInput.toLongOrNull() ?: 0L
                        val success = repository.submitWithdrawalRequest(
                            amount = amount,
                            cardNumber = cardInput,
                            shebaNumber = shebaInput,
                            desc = "تسویه پورسانت معرفی همکاران املاک (واریز ۲۴ ساعته)"
                        )
                        if (success) {
                            Toast.makeText(context, "درخواست تسویه ثبت شد. طی ۲۴ ساعت واریز می‌گردد.", Toast.LENGTH_LONG).show()
                            showWithdrawModal = false
                        } else {
                            Toast.makeText(context, "خطا: حداقل مبلغ ۵۰۰,۰۰۰ تومان و شماره شبا با IR معتبر الزامی است.", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary)
                ) {
                    Text("ثبت درخواست واریز")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawModal = false }) { Text("انصراف") }
            }
        )
    }
}

@Composable
private fun AgentGridButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    badge: String? = null,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.15f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                    }
                }

                if (badge != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = color
                    ) {
                        Text(
                            text = badge,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
