package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppRepository
import com.example.data.IranProvincesData
import com.example.data.Province
import com.example.model.PropertyItem
import com.example.model.PropertyStatus
import com.example.model.UserRole
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val drawableRes: Int,
    val badge: String
)

enum class OnboardingPath(val title: String, val role: UserRole) {
    BUYER_RENTER("من خریدار / مستأجر هستم", UserRole.REGULAR_USER),
    REFERRER("من معرف مشاورین هستم", UserRole.REGULAR_USER),
    AGENT("من مشاور املاک هستم", UserRole.AGENT)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainDashboardScreen(
    repository: AppRepository,
    onRolePathSelected: (UserRole, isReferrer: Boolean) -> Unit,
    onCitySelected: (cityName: String) -> Unit,
    onOpenTourViewer: (PropertyItem) -> Unit,
    onAdminPanelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val provinces = IranProvincesData.provinces

    val properties by repository.properties.collectAsState()
    val isAgentOnline by repository.isAgentOnline.collectAsState()

    // Default to Mazandaran and Sari
    var selectedProvince by remember { mutableStateOf(provinces.find { it.id == "mazandaran" } ?: provinces.first()) }
    var selectedCity by remember { mutableStateOf(selectedProvince.cities.firstOrNull() ?: "ساری") }
    var showProvinceSelectorSheet by remember { mutableStateOf(false) }
    var provinceSearchQuery by remember { mutableStateOf("") }

    // Main Tab: 0 = بازدید عموم (خریدار و مستأجر), 1 = بخش مشاورین و همکاران
    var mainTabIndex by remember { mutableIntStateOf(0) }

    // Transaction filter in public view: "همه", "فروش", "رهن و اجاره"
    var transactionFilter by remember { mutableStateOf("همه") }

    // Booking in-person visit modal
    var propertyForVisitBooking by remember { mutableStateOf<PropertyItem?>(null) }
    var visitDateInput by remember { mutableStateOf("۱۴۰۳/۰۷/۲۵") }
    var visitTimeInput by remember { mutableStateOf("۱۸:۰۰") }

    // Admin Access Dialog
    var showAdminAccessDialog by remember { mutableStateOf(false) }

    // Active Onboarding Carousel Sheet
    var activeOnboardingPath by remember { mutableStateOf<OnboardingPath?>(null) }

    // Filter properties for selected city and transaction type
    // Only show active tours
    val cityProperties = remember(properties, selectedCity, transactionFilter) {
        val inCity = properties.filter {
            it.status == PropertyStatus.ACTIVE_TOUR && (it.city == selectedCity || selectedCity.isEmpty())
        }
        val pool = if (inCity.isNotEmpty()) inCity else properties.filter { it.status == PropertyStatus.ACTIVE_TOUR }
        when (transactionFilter) {
            "فروش" -> pool.filter { it.transactionType == "فروش" }
            "رهن و اجاره" -> pool.filter { it.transactionType == "رهن و اجاره" }
            else -> pool
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_app_logo),
                                    contentDescription = "لوگو",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "تور مجازی املاک ایران",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "پلتفرم بازدید ۳۶۰ درجه و واقعیت مجازی",
                                style = MaterialTheme.typography.bodySmall,
                                color = AccentYellow
                            )
                        }
                    }
                },
                actions = {
                    // Separate Admin Panel Button for App Owner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AccentOrange.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showAdminAccessDialog = true }
                            .testTag("btn_owner_admin_panel")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = "پنل صاحب اپلیکیشن",
                                tint = AccentOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "پنل صاحب اپ",
                                color = AccentOrange,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .testTag("main_dashboard_screen")
        ) {
            // Main Top Tabs: "بازدید عموم (خریدار و مستأجر)" vs "بخش همکاران و مشاورین"
            TabRow(
                selectedTabIndex = mainTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AccentYellow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = mainTabIndex == 0,
                    onClick = { mainTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(
                                text = "بازدید عموم (خریدار / مستأجر)",
                                fontWeight = if (mainTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                )

                Tab(
                    selected = mainTabIndex == 1,
                    onClick = { mainTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.BusinessCenter, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(
                                text = "مشاورین املاک و معرفین",
                                fontWeight = if (mainTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                )
            }

            // Tab Content
            when (mainTabIndex) {
                // ==================== TAB 0: PUBLIC BUYER / RENTER VIEW ====================
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Guidance Banner
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(BrandPrimary, Color(0xFF0F3A7D))
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = AccentYellow.copy(alpha = 0.2f),
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.RotateRight,
                                                contentDescription = null,
                                                tint = AccentYellow,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "بازدید مجازی ۳۶۰ درجه رایگان ملک",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "شهر خود را انتخاب کنید، ملک‌های فروش یا اجاره را با تور ۳۶۰ مشاهده کرده و مستقیم با مشاور در تماس باشید.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.85f),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }

                        // PROVINCE SELECTOR FIELD (COMPLETE & SLEEK)
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.LocationCity, contentDescription = null, tint = AccentYellow)
                                        Text(
                                            text = "انتخاب استان و شهر:",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "۳۲ استان ایران",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentYellow,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Sleek Province Card Field
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { showProvinceSelectorSheet = true }
                                        .testTag("province_picker_field"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentYellow.copy(alpha = 0.7f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = AccentYellow.copy(alpha = 0.15f),
                                                modifier = Modifier.size(38.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.LocationCity,
                                                        contentDescription = null,
                                                        tint = AccentYellow,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }

                                            Column {
                                                Text(
                                                    text = "استان انتخابی شما:",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "استان ${selectedProvince.name} (مرکز: ${selectedProvince.center})",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    "تغییر استان",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = AccentYellow,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AccentYellow)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // CITIES OF SELECTED PROVINCE
                                Text(
                                    text = "شهرهای استان ${selectedProvince.name}:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(selectedProvince.cities) { city ->
                                        val isSelected = selectedCity == city
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedCity = city },
                                            label = {
                                                Text(
                                                    text = if (city == selectedProvince.center) "$city (مرکز)" else city,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            leadingIcon = if (isSelected) {
                                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                            } else null,
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AccentOrange,
                                                selectedLabelColor = Color.White,
                                                selectedLeadingIconColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // TRANSACTION TYPE TABS: "همه", "فروش (خرید)", "رهن و اجاره"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    listOf("همه", "فروش", "رهن و اجاره").forEach { type ->
                                        val isSel = transactionFilter == type
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { transactionFilter = type },
                                            label = {
                                                Text(
                                                    text = when (type) {
                                                        "فروش" -> "ملک‌های خرید (فروش)"
                                                        "رهن و اجاره" -> "رهن و اجاره"
                                                        else -> "همه فایل‌ها"
                                                    },
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = BrandSecondary,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Section Header for Properties of City
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "ملک‌های دارای تور ۳۶۰ در شهر $selectedCity",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "ثبت‌شده توسط مشاورین رسمی املاک",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = BrandSecondary.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "${PersianUtils.toPersianDigits(cityProperties.size)} ملک آماده بازدید",
                                            color = BrandSecondary,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // List of Properties in City
                        items(cityProperties, key = { it.id }) { property ->
                            PublicPropertyCard(
                                property = property,
                                isAgentOnline = isAgentOnline,
                                onOpenTour = { onOpenTourViewer(property) },
                                onRequestVisit = {
                                    propertyForVisitBooking = property
                                },
                                onCall = { phone ->
                                    repository.notifyAgentOfCustomerInquiry(property.title, "تماس تلفنی")
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                    context.startActivity(dialIntent)
                                },
                                onSocialChannel = { channelName, uriStr ->
                                    repository.notifyAgentOfCustomerInquiry(property.title, channelName)
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uriStr)))
                                }
                            )
                        }

                        // CTA Button to View City Ads
                        item {
                            Button(
                                onClick = { onCitySelected(selectedCity) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .height(52.dp)
                                    .testTag("btn_view_city_ads_cta"),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "مشاهده تمام بنرهای تبلیغاتی شهر $selectedCity",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                // ==================== TAB 1: REAL ESTATE AGENT & REFERRER PATHS ====================
                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "مسیرهای همکاری و پنل همکاران",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "اگر مشاور املاک یا علاقه‌مند به کسب درآمد از معرفی دفاتر املاک هستید، مسیر خود را انتخاب نمایید:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Path 1: مشاور املاک
                        item {
                            PathOptionCard(
                                title = "من مشاور املاک هستم",
                                subtitle = "عکسبرداری هوشمند ۳۶۰ با دوربین، ساخت تور، فعال‌سازی اشتراک، تبلیغات بنری و جذب مشتریان خریدار و مستأجر",
                                badge = "پنل آژانس املاک",
                                badgeColor = BrandSecondary,
                                icon = Icons.Default.BusinessCenter,
                                onClick = { activeOnboardingPath = OnboardingPath.AGENT },
                                testTag = "path_card_agent"
                            )
                        }

                        // Path 2: معرف مشاورین
                        item {
                            PathOptionCard(
                                title = "من معرف مشاورین هستم",
                                subtitle = "کسب پورسانت نقدی ۱۰ الی ۲۰ درصد از خرید اشتراک دفاتر املاک، با تسویه حساب ۲۴ ساعته مستقیم به شبا",
                                badge = "درآمد و پورسانت",
                                badgeColor = AccentYellow,
                                badgeTextColor = Color.Black,
                                icon = Icons.Default.MonetizationOn,
                                onClick = { activeOnboardingPath = OnboardingPath.REFERRER },
                                testTag = "path_card_referrer"
                            )
                        }

                        // Information Box on Colleague Referral & Commission
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = AccentYellow)
                                        Text("قوانین پورسانت و تسویه حساب", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "• طرح ۱ ماهه (۱.۵ میلیون): ۲۰٪ پورسانت = ۳۰۰,۰۰۰ تومان\n• طرح ۲ ماهه (۲.۵ میلیون): ۱۵٪ پورسانت = ۳۷۵,۰۰۰ تومان\n• طرح ۳ ماهه (۴ میلیون): ۱۰٪ پورسانت = ۴۰۰,۰۰۰ تومان\n• تسویه حساب کامل طی ۲۴ ساعت از طریق سامانه بانکی پایا به شماره شبا واریز می‌گردد.\n• شرط دریافت پورسانت، داشتن اشتراک فعال در زمان تمدید می‌باشد.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Province Picker Bottom Sheet with Search (32 Provinces)
    if (showProvinceSelectorSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProvinceSelectorSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.LocationCity, contentDescription = null, tint = AccentYellow)
                        Text(
                            text = "انتخاب استان از بین ۳۲ استان کشور",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = { showProvinceSelectorSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "بستن")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = provinceSearchQuery,
                    onValueChange = { provinceSearchQuery = it },
                    placeholder = { Text("جستجوی نام استان یا شهر...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                val filteredProvinces = remember(provinceSearchQuery, provinces) {
                    if (provinceSearchQuery.isBlank()) provinces
                    else provinces.filter { prov ->
                        prov.name.contains(provinceSearchQuery, ignoreCase = true) ||
                                prov.center.contains(provinceSearchQuery, ignoreCase = true) ||
                                prov.cities.any { it.contains(provinceSearchQuery, ignoreCase = true) }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProvinces, key = { it.id }) { prov ->
                        val isProvSelected = selectedProvince.id == prov.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedProvince = prov
                                    selectedCity = prov.cities.firstOrNull() ?: prov.center
                                    showProvinceSelectorSheet = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isProvSelected) AccentYellow.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.background
                            ),
                            border = if (isProvSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AccentYellow) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "استان ${prov.name}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isProvSelected) AccentYellow else Color.White
                                    )
                                    Text(
                                        text = "مرکز: ${prov.center} • ${PersianUtils.toPersianDigits(prov.cities.size)} شهر تابعه",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (isProvSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AccentYellow)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Onboarding Carousel for selected path
    activeOnboardingPath?.let { path ->
        OnboardingCarouselBottomSheet(
            path = path,
            onDismiss = { activeOnboardingPath = null },
            onFinish = {
                val isReferrer = path == OnboardingPath.REFERRER
                onRolePathSelected(path.role, isReferrer)
                activeOnboardingPath = null
            }
        )
    }

    // Dialog for In-person Visit Appointment (Sends real notification to agent!)
    propertyForVisitBooking?.let { prop ->
        AlertDialog(
            onDismissRequest = { propertyForVisitBooking = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AccentOrange)
                    Text("درخواست بازدید حضوری ملک", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "برای هماهنگی بازدید از «${prop.title}»، تاریخ و ساعت پیشنهادی خود را وارد کنید:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = visitDateInput,
                        onValueChange = { visitDateInput = it },
                        label = { Text("تاریخ پیشنهادی (شمسی)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = visitTimeInput,
                        onValueChange = { visitTimeInput = it },
                        label = { Text("ساعت پیشنهادی (مثلاً ۱۸:۰۰)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "با ثبت این فرم، نوتیفیکیشن فوری به مشاور املاک ارسال می‌شود تا با شما هماهنگ نماید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.requestVisit(prop.id, prop.title, visitDateInput, visitTimeInput)
                        repository.notifyAgentOfCustomerInquiry(prop.title, "درخواست بازدید حضوری (ساعت $visitTimeInput)")
                        Toast.makeText(context, "درخواست بازدید ثبت شد و نوتیفیکیشن برای مشاور ارسال گردید", Toast.LENGTH_LONG).show()
                        propertyForVisitBooking = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                ) {
                    Text("ثبت و ارسال به مشاور")
                }
            },
            dismissButton = {
                TextButton(onClick = { propertyForVisitBooking = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Admin Access Dialog (Separating Admin Panel for App Owner)
    if (showAdminAccessDialog) {
        AlertDialog(
            onDismissRequest = { showAdminAccessDialog = false },
            icon = {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(36.dp))
            },
            title = {
                Text(
                    text = "ورود به پنل مدیریت کل (صاحب اپلیکیشن)",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "این بخش منحصراً برای مدیریت کل، ارسال نوتیفیکیشن به کاربران و مشاورین، بررسی واریزی‌های ۲۴ ساعته و تنظیمات سامانه است.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAdminAccessDialog = false
                        onAdminPanelClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                ) {
                    Text("ورود به پنل مدیریت")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminAccessDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
private fun PublicPropertyCard(
    property: PropertyItem,
    isAgentOnline: Boolean,
    onOpenTour: () -> Unit,
    onRequestVisit: () -> Unit,
    onCall: (String) -> Unit,
    onSocialChannel: (channelName: String, url: String) -> Unit
) {
    val agentPhone = "09123456789"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Top Image with Tour Badge & Transaction Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable(onClick = onOpenTour)
            ) {
                val thumbRes = property.scenes.firstOrNull()?.drawableResId ?: R.drawable.img_tour_sample
                Image(
                    painter = painterResource(id = thumbRes),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // Top badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tour 360 Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.RotateRight, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(16.dp))
                            Text("تور ۳۶۰ فعال", color = AccentYellow, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Transaction Type
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (property.transactionType == "فروش") BrandSecondary else AccentOrange
                    ) {
                        Text(
                            text = property.transactionType,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Price at bottom of image
                Surface(
                    shape = RoundedCornerShape(topStart = 12.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 0.dp, end = 0.dp)
                ) {
                    Text(
                        text = PersianUtils.formatPrice(property.price),
                        color = BrandSecondary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            // Property Details
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(15.dp))
                    Text(
                        text = "${property.city}، ${property.neighborhood}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Specs: Metraj & Rooms
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "متراژ: ${PersianUtils.toPersianDigits(property.areaSqMeters)} متر",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${PersianUtils.toPersianDigits(property.rooms)} خوابه",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${PersianUtils.toPersianDigits(property.viewsCount)} بازدید",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentYellow,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                // Real Estate Agent Identity + Live Online/Offline Status Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box {
                            Surface(
                                shape = CircleShape,
                                color = BrandPrimary,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                            // Online/Offline Indicator Dot
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(11.dp)
                                    .background(if (isAgentOnline) Color(0xFF4CAF50) else Color.Gray, CircleShape)
                                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                        }

                        Column {
                            Text(
                                text = "مشاور: مهندس کیان آریا",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (isAgentOnline) "آنلاین - پاسخگوی سریع" else "آفلاین - پاسخ با تاخیر",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isAgentOnline) Color(0xFF4CAF50) else Color.Gray
                                )
                            }
                        }
                    }

                    // Direct Call Button
                    Button(
                        onClick = { onCall(agentPhone) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تماس با مشاور", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Action Button: "مشاهده تور ۳۶۰ درجه"
                Button(
                    onClick = onOpenTour,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.RotateRight, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مشاهده تور مجازی ۳۶۰ درجه ملک",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Bar: In-person Visit + WhatsApp + Telegram + Bale
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Appointment / Visit Request
                    Button(
                        onClick = onRequestVisit,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(36.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("هماهنگی بازدید", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }

                    // WhatsApp
                    Button(
                        onClick = {
                            val msg = "سلام، در رابطه با آگهی «${property.title}» در سامانه تور ۳۶۰ سوال داشتم."
                            val url = "https://api.whatsapp.com/send?phone=989123456789&text=${Uri.encode(msg)}"
                            onSocialChannel("واتساپ", url)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Text("واتساپ", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }

                    // Telegram
                    Button(
                        onClick = {
                            onSocialChannel("تلگرام", "https://t.me/AmlakKianAria")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Text("تلگرام", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }

                    // Bale
                    Button(
                        onClick = {
                            onSocialChannel("بله", "https://ble.ir/amlak_tour")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A389)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .height(36.dp)
                    ) {
                        Text("بله", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PathOptionCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    badgeTextColor: Color = Color.White,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = badgeColor.copy(alpha = 0.15f),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(26.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeColor
                    ) {
                        Text(
                            text = badge,
                            color = badgeTextColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowBack, // In RTL layout ArrowBack points forward
                contentDescription = null,
                tint = AccentYellow,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// 3-Slide Swipeable Onboarding Carousel
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun OnboardingCarouselBottomSheet(
    path: OnboardingPath,
    onDismiss: () -> Unit,
    onFinish: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val slides = remember(path) {
        when (path) {
            OnboardingPath.BUYER_RENTER -> listOf(
                OnboardingSlide(
                    title = "بازدید مجازی ۳۶۰ درجه رایگان",
                    description = "بدون نیاز به مراجعه حضوری و صرف ساعت‌ها وقت در ترافیک، درون تمام اتاق‌ها و بخش‌های ملک قدم بزنید.",
                    icon = Icons.Default.Visibility,
                    drawableRes = R.drawable.img_tour_sample,
                    badge = "کیفیت 4K و 8K"
                ),
                OnboardingSlide(
                    title = "ارتباط مستقیم با مشاورین معتبر",
                    description = "از طریق واتساپ، تلگرام، بله یا تماس تلفنی مستقیماً با آژانس و مشاور مربوطه گفتگو کنید و نوبت بازدید هماهنگ نمایید.",
                    icon = Icons.Default.Person,
                    drawableRes = R.drawable.img_tour_bedroom,
                    badge = "۴ راه ارتباطی"
                ),
                OnboardingSlide(
                    title = "تبلیغات و املاک تفکیک‌شده استانی",
                    description = "در ۳۲ استان کشور، فایل‌های دارای تور مجازی را بر اساس شهر و محله فیلتر کرده و بهترین گزینه را بیابید.",
                    icon = Icons.Default.LocationCity,
                    drawableRes = R.drawable.img_tour_sample,
                    badge = "سراسر ایران"
                )
            )

            OnboardingPath.REFERRER -> listOf(
                OnboardingSlide(
                    title = "سیستم درآمدزایی و کد معرف اختصاصی",
                    description = "کد معرف یکتای خود را دریافت کرده و آن را به دفاتر املاک، همکاران و مشاورین مسکن در شهر خود معرفی کنید.",
                    icon = Icons.Default.MonetizationOn,
                    drawableRes = R.drawable.ic_app_logo,
                    badge = "کد معرف یکتا"
                ),
                OnboardingSlide(
                    title = "پورسانت نقدی ۱۰ الی ۲۰ درصد",
                    description = "به ازای خرید هر پلن اشتراک توسط مشاورین معرفی‌شده شما، تا ۴۰۰,۰۰۰ تومان پورسانت نقدی دریافت کنید.",
                    icon = Icons.Default.WorkspacePremium,
                    drawableRes = R.drawable.img_tour_sample,
                    badge = "تسویه نقدی"
                ),
                OnboardingSlide(
                    title = "تسویه حساب آسان به شماره شبا",
                    description = "درخواست برداشت خود را در داشبورد ثبت کنید تا مستقیماً از طریق سیستم بانکی پایا به حساب شما واریز گردد.",
                    icon = Icons.Default.CheckCircle,
                    drawableRes = R.drawable.img_tour_bedroom,
                    badge = "واریز ۲۴ ساعته"
                )
            )

            OnboardingPath.AGENT -> listOf(
                OnboardingSlide(
                    title = "عکسبرداری هوشمند ۳۶۰ با دوربین گوشی",
                    description = "با ماژول بومی CameraX و تراز ژیروسکوپ، بدون تجهیزات گران‌قیمت تصاویر پانورامای باکیفیت ثبت و متصل کنید.",
                    icon = Icons.Default.RotateRight,
                    drawableRes = R.drawable.img_tour_sample,
                    badge = "ماژول CameraX"
                ),
                OnboardingSlide(
                    title = "ساخت سریع تور مجازی ملک",
                    description = "اتاق‌ها را با هات‌اسپات‌های تعاملی به هم متصل کرده و لینک تور را در واتساپ و سایت به خریداران ارائه دهید.",
                    icon = Icons.Default.Home,
                    drawableRes = R.drawable.img_tour_bedroom,
                    badge = "تعاملی و جذاب"
                ),
                OnboardingSlide(
                    title = "تبلیغات بنری در صفحه اول شهرها",
                    description = "با رزرو بنر در شهر هدف خود، ملک شما در اسلایدر اختصاصی برای هزاران خریدار همان منطقه نمایش داده می‌شود.",
                    icon = Icons.Default.BusinessCenter,
                    drawableRes = R.drawable.img_tour_sample,
                    badge = "جذب مشتری حداکثری"
                )
            )
        }
    }

    val pagerState = rememberPagerState(pageCount = { slides.size })

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Title & Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentYellow.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = path.title,
                        color = AccentYellow,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                TextButton(onClick = onFinish) {
                    Text(
                        text = "رد کردن",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Swipeable HorizontalPager Carousel
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = slide.drawableRes),
                            contentDescription = slide.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = slide.badge,
                                color = AccentYellow,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = slide.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(slides.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AccentYellow else Color.Gray.copy(alpha = 0.4f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val isLastPage = pagerState.currentPage == slides.size - 1

            Button(
                onClick = {
                    if (isLastPage) {
                        onFinish()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isLastPage) "ادامه به مرحله ورود و ثبت‌نام" else "ادامه",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
