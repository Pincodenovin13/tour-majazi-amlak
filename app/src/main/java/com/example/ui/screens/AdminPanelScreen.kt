package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.NotificationType
import com.example.model.UserRole
import com.example.model.WithdrawalStatus
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    repository: AppRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val withdrawals by repository.withdrawalRequests.collectAsState()
    val properties by repository.properties.collectAsState()
    val ads by repository.ads.collectAsState()
    val platformAds by repository.platformAds.collectAsState()
    val activeSubscription by repository.activeSubscription.collectAsState()

    val isAgentActive = repository.isAgentActive()
    val isNearExpiry = repository.isSubscriptionNearExpiry()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("ارسال نوتیفیکیشن", "مدیریت مشاورین", "تسویه‌ها (۲۴ ساعته)", "املاک و بنرها", "آمار سامانه")

    // Push Notification Form
    var notifTargetRole by remember { mutableStateOf<UserRole?>(null) } // null = All, AGENT, REGULAR_USER
    var notifTitle by remember { mutableStateOf("") }
    var notifMessage by remember { mutableStateOf("") }

    // Platform Ad Dialog
    var showAddPlatformAdModal by remember { mutableStateOf(false) }
    var newAdTitle by remember { mutableStateOf("") }
    var newAdSubtitle by remember { mutableStateOf("") }
    var newAdAction by remember { mutableStateOf("مشاهده جزئیات") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = AccentYellow)
                        Column {
                            Text(
                                text = "پنل اختصاصی صاحب اپلیکیشن",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "مدیریت کل، ارسال اعلان‌ها و نظارت بر تراکنش‌ها",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentYellow
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "بازگشت"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .testTag("admin_panel_screen")
        ) {
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 16.dp
            ) {
                tabTitles.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTabIndex == idx,
                        onClick = { selectedTabIndex = idx },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == idx) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == idx) AccentYellow else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                // Tab 0: SEND NOTIFICATIONS (TO AGENTS, REFERRERS, OR ALL)
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "ارسال اعلان و نوتیفیکیشن هدفمند",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "می‌توانید پیام دلخواه خود را به تفکیک برای مشاورین املاک، معرف‌ها یا همه کاربران ارسال کنید:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Target Selector Chips
                        item {
                            Text("انتخاب گیرندگان اعلان:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = notifTargetRole == null,
                                    onClick = { notifTargetRole = null },
                                    label = { Text("همه کاربران") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )

                                FilterChip(
                                    selected = notifTargetRole == UserRole.AGENT,
                                    onClick = { notifTargetRole = UserRole.AGENT },
                                    label = { Text("مشاورین املاک") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )

                                FilterChip(
                                    selected = notifTargetRole == UserRole.REGULAR_USER,
                                    onClick = { notifTargetRole = UserRole.REGULAR_USER },
                                    label = { Text("معرف‌ها و عموم") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandSecondary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = notifTitle,
                                onValueChange = { notifTitle = it },
                                label = { Text("عنوان اعلان") },
                                placeholder = { Text("مثال: تخفیف ۲۰ درصدی تمدید اشتراک") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = notifMessage,
                                onValueChange = { notifMessage = it },
                                label = { Text("متن کامل پیام اعلان") },
                                placeholder = { Text("متن ارسالی جهت نمایش در گوشی کاربران...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    if (notifTitle.isNotBlank() && notifMessage.isNotBlank()) {
                                        val targetLabel = when (notifTargetRole) {
                                            UserRole.AGENT -> "مشاورین املاک"
                                            UserRole.REGULAR_USER -> "معرف‌ها"
                                            else -> "تمامی کاربران"
                                        }
                                        repository.addNotification(
                                            title = notifTitle,
                                            message = notifMessage,
                                            type = NotificationType.GENERAL,
                                            targetRole = notifTargetRole
                                        )
                                        Toast.makeText(context, "اعلان با موفقیت برای $targetLabel ارسال شد", Toast.LENGTH_LONG).show()
                                        notifTitle = ""
                                        notifMessage = ""
                                    } else {
                                        Toast.makeText(context, "لطفاً عنوان و متن پیام را کامل کنید", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ارسال فوری نوتیفیکیشن", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Tab 1: MANAGE AGENTS & SUBSCRIPTION STATUS (ACTIVE vs DEACTIVE & 2-DAY EXPIRY)
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "مدیریت وضعیت اشتراک و فعالیت مشاور املاک",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "در این بخش می‌توانید وضعیت اکتیو یا دی‌اکتیو بودن مشاور را کنترل و تمدید نمایید:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Current Subscription Status Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isAgentActive) BrandSecondary else Color(0xFFE53935)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("مشاور: مهندس کیان آریا (املاک مدرن)", fontWeight = FontWeight.Bold)
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isAgentActive) BrandSecondary else Color(0xFFE53935)
                                        ) {
                                            Text(
                                                text = if (isAgentActive) "اکتیو (فعال)" else "دی‌اکتیو (غیرفعال)",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "پلن فعلی: ${activeSubscription?.planTitle ?: "ندارد"} • روزهای باقی‌مانده: ${PersianUtils.toPersianDigits(activeSubscription?.remainingDays ?: 0)} روز",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AccentYellow
                                    )

                                    if (isNearExpiry) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "هشدار تمدید ۲ روزه فعال است!",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AccentOrange,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text("عملیات مدیریتی و تستی اشتراک:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Activate 30 days
                                        Button(
                                            onClick = {
                                                repository.setSubscriptionRemainingDays(30)
                                                Toast.makeText(context, "اشتراک مشاور به ۳۰ روز اکتیو تغییر یافت", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("اکتیو ۳۰ روزه", style = MaterialTheme.typography.labelSmall)
                                        }

                                        // Set 2 days (trigger 2-day warning)
                                        Button(
                                            onClick = {
                                                repository.setSubscriptionRemainingDays(2)
                                                Toast.makeText(context, "هشدار ۲ روز مانده به پایان اشتراک فعال شد", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("تنظیم ۲ روز (هشدار)", style = MaterialTheme.typography.labelSmall)
                                        }

                                        // Deactivate (0 days)
                                        Button(
                                            onClick = {
                                                repository.setSubscriptionRemainingDays(0)
                                                Toast.makeText(context, "مشاور دی‌اکتیو شد (فایل‌ها مخفی شدند)", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("دی‌اکتیو کردن", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }

                        // Simulation of Colleague Referral
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("تست قانون سوختن پورسانت معرفی همکار:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "اگر مشاور اکتیو باشد، پورسانت واریز می‌شود؛ اگر دی‌اکتیو باشد، پورسانت می‌سوزد!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            val plan = repository.availablePlans[1] // 2-month plan
                                            repository.processColleagueAgentPlanPurchase("مشاور همکار (املاک پایتخت)", plan)
                                            Toast.makeText(context, "تست تمدید همکار ثبت شد. وضعیت پورسانت در داشبورد ثبت گردید.", Toast.LENGTH_LONG).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("شبیه‌سازی خرید پلن توسط همکار", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 2: WITHDRAWALS & 24H PAYOUT APPROVAL
                2 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "درخواست‌های تسویه حساب پورسانت (واریز ۲۴ ساعته پایا)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "پس از واریز به شماره شبا، بر روی تایید کلیک کنید تا به کاربر اطلاع‌رسانی شود:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        items(withdrawals, key = { it.id }) { req ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(req.userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = when (req.status) {
                                                WithdrawalStatus.APPROVED -> BrandSecondary.copy(alpha = 0.2f)
                                                WithdrawalStatus.PENDING -> AccentOrange.copy(alpha = 0.2f)
                                                WithdrawalStatus.REJECTED -> Color.Red.copy(alpha = 0.2f)
                                            }
                                        ) {
                                            Text(
                                                text = req.status.titleFa,
                                                color = when (req.status) {
                                                    WithdrawalStatus.APPROVED -> BrandSecondary
                                                    WithdrawalStatus.PENDING -> AccentOrange
                                                    WithdrawalStatus.REJECTED -> Color.Red
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("مبلغ خالص واریزی: ${PersianUtils.formatPrice(req.amount)}", fontWeight = FontWeight.Bold, color = BrandSecondary)
                                    Text("شماره شبا: ${req.shebaNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("تاریخ درخواست: ${req.requestDateJalali}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

                                    if (req.status == WithdrawalStatus.PENDING) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    repository.approveWithdrawal(req.id)
                                                    Toast.makeText(context, "درخواست تایید و پیامک واریز ۲۴ ساعته ارسال شد", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تایید واریز پایا")
                                            }

                                            Button(
                                                onClick = {
                                                    repository.rejectWithdrawal(req.id, "شماره شبا با مشخصات کارت مغایرت دارد")
                                                    Toast.makeText(context, "درخواست رد شد و وجه به کیف پول بازگشت", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("رد درخواست")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 3: PROPERTIES & CITY BANNER ADS
                3 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("املاک ثبت شده در سراسر کشور:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }

                        items(properties) { prop ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(prop.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                        Text("شهر: ${prop.city} • ${prop.transactionType} • ${PersianUtils.formatPrice(prop.price)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    IconButton(
                                        onClick = {
                                            repository.deleteProperty(prop.id)
                                            Toast.makeText(context, "ملک حذف شد", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.DeleteForever, contentDescription = "حذف ملک", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("تبلیغات بنری شهری فعال در استان‌ها:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }

                        items(ads) { ad ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(ad.propertyTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                        Text("شهر: ${ad.city} • اعتبار: ${ad.endDateJalali} (${PersianUtils.toPersianDigits(ad.durationDays)} روزه)", style = MaterialTheme.typography.bodySmall, color = AccentYellow)
                                    }
                                    Text(PersianUtils.formatPrice(ad.price), fontWeight = FontWeight.Bold, color = BrandSecondary)
                                }
                            }
                        }
                    }
                }

                // Tab 4: SYSTEM METRICS & STATS
                4 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("آمار و شاخص‌های پلتفرم تور مجازی املاک ایران", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AdminStatBox(
                                    title = "مشاورین فعال",
                                    value = "۱۲۸ دفتر املاک",
                                    color = BrandSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatBox(
                                    title = "کل تورهای ۳۶۰",
                                    value = "${PersianUtils.toPersianDigits(properties.size + 420)} فایل",
                                    color = AccentYellow,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AdminStatBox(
                                    title = "تسویه‌های ۲۴ ساعته",
                                    value = "${withdrawals.size} فقره",
                                    color = AccentOrange,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatBox(
                                    title = "بازدیدکنندگان ماهانه",
                                    value = "۱۸,۴۵۰ نفر",
                                    color = BrandPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
