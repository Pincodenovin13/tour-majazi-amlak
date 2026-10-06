package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.CommissionStatus
import com.example.model.PropertyItem
import com.example.model.ReferralTransaction
import com.example.model.ReferrerTier
import com.example.model.WithdrawalStatus
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils

@Composable
fun UserDashboardScreen(
    repository: AppRepository,
    onOpenTourViewer: (PropertyItem) -> Unit,
    onCityAdsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val userName by repository.userName.collectAsState()
    val commissionStat by repository.commissionStat.collectAsState()
    val withdrawals by repository.withdrawalRequests.collectAsState()

    var showWithdrawModal by remember { mutableStateOf(false) }
    var withdrawAmountInput by remember { mutableStateOf("500000") }
    var cardNumberInput by remember { mutableStateOf("6037997512345678") }
    var shebaInput by remember { mutableStateOf("IR820120000000012345678901") }
    var descInput by remember { mutableStateOf("درخواست واریز شبانه شبا پایا") }
    var copyFeedback by remember { mutableStateOf(false) }

    var selectedHistoryFilter by remember { mutableStateOf<CommissionStatus?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("user_dashboard"),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BrandSecondary, Color(0xFF1B4D20), Color(0xFF0F2B12))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable(onClick = onProfileClick)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "پروفایل",
                                        tint = AccentYellow,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = userName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "داشبورد سیستم معرف و پورسانت نقدی",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        // City Ads Shortcut Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AccentOrange,
                            modifier = Modifier.clickable(onClick = onCityAdsClick)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "تبلیغات شهرها",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "با معرفی اپلیکیشن به مشاورین املاک، تا ۲۰٪ پورسانت نقدی دریافت کرده و با تسویه شبانه پایا برداشت نمایید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Section: TARGETS, DEADLINES & CURRENT TIER BANNER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentYellow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Row: Tier Badge and Window Days
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(20.dp))
                            Text(
                                text = "سطح کاربری معرف:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (commissionStat.currentTier) {
                                    ReferrerTier.SPECIAL -> AccentYellow
                                    ReferrerTier.ACTIVE -> BrandSecondary
                                    ReferrerTier.STANDARD -> Color(0xFF616161)
                                }
                            ) {
                                Text(
                                    text = commissionStat.currentTier.titleFa,
                                    color = if (commissionStat.currentTier == ReferrerTier.SPECIAL) Color.Black else Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Days Remaining in 15-day window
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentOrange.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "${PersianUtils.toPersianDigits(commissionStat.daysRemainingInWindow)} روز مانده از مهلت",
                                    color = AccentOrange,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Count vs Actual (e.g. ۶ از ۹)
                    val progress = (commissionStat.successfulReferralsCount.toFloat() / commissionStat.targetCount.toFloat()).coerceIn(0f, 1f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تارگت دوره ۱۵ روزه (مشاورین پرداخت‌کننده):",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${PersianUtils.toPersianDigits(commissionStat.successfulReferralsCount)} از ${PersianUtils.toPersianDigits(commissionStat.targetCount)} مشاور",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentYellow
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AccentYellow,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rules explanation
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "• مهلت هر دوره معرف ۱۵ روز است (تارگت: حداقل ۹ مشاور).",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "• در صورت معرفی ۹ مشاور، دوره ۱۵ روز دیگر تمدید می‌شود.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "• در صورت رسیدن به ۱۵ مشاور به بالا، دوره ۳۰ روز تمدید (سطح ویژه) می‌شود.",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentYellow
                            )
                            Text(
                                text = "• در صورت عدم تکمیل ۹ مشاور در ۱۵ روز، ۳ معرف سوخته و مجدداً ۱۵ روز فرصت داده می‌شود.",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentOrange
                            )
                        }
                    }
                }
            }
        }

        // Section: REFERRAL CODE CARD
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "کد معرف یکتای شما برای ثبت در آژانس‌های املاک:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large Code Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(2.dp, AccentYellow),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(commissionStat.referralCode))
                                    copyFeedback = true
                                    Toast.makeText(context, "کد معرف کپی شد", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = commissionStat.referralCode,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentYellow,
                                    letterSpacing = 4.sp
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (copyFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "کپی",
                                        tint = AccentYellow
                                    )
                                    Text(
                                        text = if (copyFeedback) "کپی شد" else "کپی کد",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentYellow
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Share Button
                        Button(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "مشاورین محترم املاک، تورهای ۳۶۰ درجه و واقعیت مجازی بسازید!\nکد معرف تخفیف و ثبت‌نام: ${commissionStat.referralCode}\nسامانه تور مجازی املاک ایران"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "اشتراک‌گذاری کد معرف"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("اشتراک‌گذاری کد معرف با مشاورین و آژانس‌ها", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: EXACT COMMISSION RATES TABLE & BURNING COMMISSION WARNING
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = AccentYellow)
                        Text(
                            text = "جدول دقیق پورسانت پلن‌های اشتراک",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Table rows
                    CommissionRateRow(
                        planName = "پلن ۱ ماهه (۱,۵۰۰,۰۰۰ تومان)",
                        rate = "۲۰٪",
                        amount = "۳۰۰,۰۰۰ تومان",
                        highlightColor = BrandPrimary
                    )
                    CommissionRateRow(
                        planName = "پلن ۲ ماهه (۲,۵۰۰,۰۰۰ تومان - محبوب)",
                        rate = "۱۵٪",
                        amount = "۳۷۵,۰۰۰ تومان",
                        highlightColor = BrandSecondary
                    )
                    CommissionRateRow(
                        planName = "پلن ۳ ماهه (۴,۰۰۰,۰۰۰ تومان - به‌صرفه‌ترین)",
                        rate = "۱۰٪",
                        amount = "۴۰۰,۰۰۰ تومان",
                        highlightColor = AccentYellow
                    )

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // CRITICAL BURNING COMMISSION RULE NOTICE
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentOrange.copy(alpha = 0.12f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                        Column {
                            Text(
                                text = "قانون مهم سوختن پورسانت (Burning Commission):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange
                            )
                            Text(
                                text = "در صورتی که اشتراک مشاور معرفی‌شده منقضی شود و تمدید نگردد، پورسانت معرف سوخته (باطل) می‌شود. استمرار دریافت پورسانت، منوط به فعال بودن اشتراک مشاور است.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Section: FINANCIAL STATS & WITHDRAWABLE BALANCE
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "آمار مالی و موجودی کیف پول",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2x2 Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Stat 1: درآمد کل
                    UserStatCard(
                        title = "درآمد کل کسب‌شده",
                        value = PersianUtils.formatPrice(commissionStat.totalEarnings),
                        icon = Icons.Default.TrendingUp,
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f)
                    )

                    // Stat 2: موجودی قابل برداشت
                    UserStatCard(
                        title = "موجودی قابل برداشت",
                        value = PersianUtils.formatPrice(commissionStat.availableBalance),
                        icon = Icons.Default.Payments,
                        color = AccentYellow,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Stat 3: پورسانت‌های در انتظار
                    UserStatCard(
                        title = "در انتظار تایید",
                        value = PersianUtils.formatPrice(commissionStat.pendingBalance),
                        icon = Icons.Default.HourglassEmpty,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f)
                    )

                    // Stat 4: پورسانت سوخته
                    UserStatCard(
                        title = "پورسانت سوخته",
                        value = PersianUtils.formatPrice(commissionStat.burnedBalance),
                        icon = Icons.Default.Cancel,
                        color = AccentOrange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Button: "درخواست تسویه و برداشت" (Minimum 500,000 Toman, Nightly Sheba)
                Button(
                    onClick = { showWithdrawModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_user_withdraw")
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "درخواست تسویه و واریز شبانه (شبا)",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Section: COMMISSION HISTORY WITH STATUS (پرداخت شده / در انتظار / سوخته)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تاریخچه و وضعیت پورسانت‌ها",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${PersianUtils.toPersianDigits(commissionStat.transactions.size)} تراکنش",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Filter chips: All, پرداخت شده, در انتظار, سوخته
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedHistoryFilter == null,
                        onClick = { selectedHistoryFilter = null },
                        label = { Text("همه") }
                    )
                    FilterChip(
                        selected = selectedHistoryFilter == CommissionStatus.PAID,
                        onClick = { selectedHistoryFilter = if (selectedHistoryFilter == CommissionStatus.PAID) null else CommissionStatus.PAID },
                        label = { Text("پرداخت شده") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandSecondary)
                    )
                    FilterChip(
                        selected = selectedHistoryFilter == CommissionStatus.PENDING,
                        onClick = { selectedHistoryFilter = if (selectedHistoryFilter == CommissionStatus.PENDING) null else CommissionStatus.PENDING },
                        label = { Text("در انتظار") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF1976D2))
                    )
                    FilterChip(
                        selected = selectedHistoryFilter == CommissionStatus.BURNED,
                        onClick = { selectedHistoryFilter = if (selectedHistoryFilter == CommissionStatus.BURNED) null else CommissionStatus.BURNED },
                        label = { Text("سوخته") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentOrange)
                    )
                }
            }
        }

        val filteredTransactions = commissionStat.transactions.filter {
            selectedHistoryFilter == null || it.status == selectedHistoryFilter
        }

        items(filteredTransactions, key = { it.id }) { tx ->
            CommissionHistoryCard(tx = tx)
        }

        // Section: Withdrawal Requests List
        if (withdrawals.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "درخواست‌های تسویه شبانه ارسال شده",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(withdrawals) { w ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مبلغ خالص: ${PersianUtils.formatPrice(w.amount)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (w.status) {
                                    WithdrawalStatus.APPROVED -> BrandSecondary.copy(alpha = 0.2f)
                                    WithdrawalStatus.PENDING -> AccentYellow.copy(alpha = 0.2f)
                                    WithdrawalStatus.REJECTED -> Color.Red.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = w.status.titleFa,
                                    color = when (w.status) {
                                        WithdrawalStatus.APPROVED -> BrandSecondary
                                        WithdrawalStatus.PENDING -> AccentYellow
                                        WithdrawalStatus.REJECTED -> Color.Red
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "شبا: ${w.shebaNumber} • ثبت: ${w.requestDateJalali}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        w.adminNote?.let { note ->
                            Text(
                                text = "پاسخ مدیریت: $note",
                                style = MaterialTheme.typography.bodySmall,
                                color = AccentYellow
                            )
                        }
                    }
                }
            }
        }
    }

    // 3.1 — Withdrawal Form Dialog (Minimum 500,000 Toman, Sheba IR, 10% Tax Deduction, Nightly Payout)
    if (showWithdrawModal) {
        val enteredAmount = withdrawAmountInput.toLongOrNull() ?: 0L
        val taxAmount = (enteredAmount * 0.10).toLong()
        val netPayout = (enteredAmount - taxAmount).coerceAtLeast(0L)
        val isShebaValid = shebaInput.trim().startsWith("IR", ignoreCase = true) && shebaInput.trim().length >= 16
        val isAmountValid = enteredAmount >= 500_000L && enteredAmount <= commissionStat.availableBalance

        AlertDialog(
            onDismissRequest = { showWithdrawModal = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = AccentYellow)
                    Text("درخواست تسویه پورسانت (واریز شبانه)", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "موجودی قابل برداشت: ${PersianUtils.formatPrice(commissionStat.availableBalance)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandSecondary
                    )

                    OutlinedTextField(
                        value = withdrawAmountInput,
                        onValueChange = { withdrawAmountInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("مبلغ درخواستی (تومان) - حداقل ۵۰۰,۰۰۰") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = enteredAmount < 500_000L || enteredAmount > commissionStat.availableBalance
                    )

                    // Live Net Payout & Tax Breakdown
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("کسر ۱۰٪ مالیات قانونی:", style = MaterialTheme.typography.labelSmall)
                                Text("- ${PersianUtils.formatPrice(taxAmount)}", color = AccentOrange, style = MaterialTheme.typography.labelSmall)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("مبلغ دریافتی خالص شما:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                Text(PersianUtils.formatPrice(netPayout), color = AccentYellow, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = shebaInput,
                        onValueChange = { shebaInput = it },
                        label = { Text("شماره شبا بانکی (الزامی با IR)") },
                        placeholder = { Text("IR820120000000012345678901") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = !isShebaValid
                    )

                    OutlinedTextField(
                        value = cardNumberInput,
                        onValueChange = { cardNumberInput = it },
                        label = { Text("شماره کارت بانکی (جهت تطبیق نام)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text(
                        text = "قوانین تسویه: حداقل مبلغ ۵۰۰,۰۰۰ تومان • فقط شماره شبا معتبر • تسویه به صورت واریز شبانه (پایا) صورت می‌پذیرد.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isAmountValid) {
                            Toast.makeText(context, "مبلغ وارد شده باید حداقل ۵۰۰,۰۰۰ تومان و حداکثر سقف موجودی باشد.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!isShebaValid) {
                            Toast.makeText(context, "شماره شبا باید با IR آغاز شده و معتبر باشد.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val success = repository.submitWithdrawalRequest(enteredAmount, cardNumberInput, shebaInput, descInput)
                        if (success) {
                            Toast.makeText(context, "درخواست برداشت ثبت شد و در نوبت واریز شبانه قرار گرفت.", Toast.LENGTH_LONG).show()
                            showWithdrawModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                    enabled = isAmountValid && isShebaValid
                ) {
                    Text("ثبت و واریز شبانه", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawModal = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
private fun CommissionRateRow(
    planName: String,
    rate: String,
    amount: String,
    highlightColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = planName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(text = "درصد پورسانت: $rate", style = MaterialTheme.typography.labelSmall, color = highlightColor)
        }
        Text(text = amount, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = highlightColor)
    }
}

@Composable
private fun CommissionHistoryCard(tx: ReferralTransaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                        color = when (tx.status) {
                            CommissionStatus.PAID -> BrandSecondary.copy(alpha = 0.2f)
                            CommissionStatus.PENDING -> Color(0xFF1976D2).copy(alpha = 0.2f)
                            CommissionStatus.BURNED -> AccentOrange.copy(alpha = 0.2f)
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (tx.status) {
                                    CommissionStatus.PAID -> Icons.Default.CheckCircle
                                    CommissionStatus.PENDING -> Icons.Default.HourglassEmpty
                                    CommissionStatus.BURNED -> Icons.Default.Cancel
                                },
                                contentDescription = null,
                                tint = when (tx.status) {
                                    CommissionStatus.PAID -> BrandSecondary
                                    CommissionStatus.PENDING -> Color(0xFF1976D2)
                                    CommissionStatus.BURNED -> AccentOrange
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = tx.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "تاریخ: ${tx.dateFa}" + if (tx.agentName != null) " • مشاور: ${tx.agentName}" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (tx.status) {
                            CommissionStatus.PAID -> BrandSecondary
                            CommissionStatus.PENDING -> Color(0xFF1976D2)
                            CommissionStatus.BURNED -> AccentOrange
                        }
                    ) {
                        Text(
                            text = tx.status.titleFa,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = (if (tx.isDeposit) "+ " else "- ") + PersianUtils.formatPrice(tx.amount),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (tx.status) {
                            CommissionStatus.PAID -> BrandSecondary
                            CommissionStatus.PENDING -> Color(0xFF64B5F6)
                            CommissionStatus.BURNED -> AccentOrange
                        }
                    )
                }
            }

            // Burn reason explanation if burned
            tx.burnReason?.let { reason ->
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AccentOrange.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "علت ابطال: $reason",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UserStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
