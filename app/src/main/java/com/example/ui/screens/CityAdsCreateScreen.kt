package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppRepository
import com.example.data.CityInfo
import com.example.model.PropertyItem
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityAdsCreateScreen(
    repository: AppRepository,
    onAdCreated: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cities = repository.iranianCities
    val properties by repository.properties.collectAsState()
    val durationOptions = repository.adDurationOptions

    var selectedCity by remember { mutableStateOf(cities.first()) }
    var cityExpanded by remember { mutableStateOf(false) }

    var selectedProperty by remember { mutableStateOf(properties.firstOrNull()) }
    var propertyExpanded by remember { mutableStateOf(false) }

    var selectedDurationDays by remember { mutableIntStateOf(10) }
    val calculatedPrice = remember(selectedCity, selectedDurationDays) {
        repository.calculateAdPrice(selectedCity.name, selectedDurationDays)
    }

    var selectedBannerDrawable by remember { mutableIntStateOf(R.drawable.img_tour_sample) }
    var isImageUploaded by remember { mutableStateOf(true) }
    var imageSizeKb by remember { mutableIntStateOf(342) } // Under 500 KB limit

    var showZarinPalDialog by remember { mutableStateOf(false) }
    var isPaymentProcessing by remember { mutableStateOf(false) }
    var isSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ثبت تبلیغات بنری شهری",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "نمایش بنر ملک در صفحه اول شهر انتخابی",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentYellow
                        )
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
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "هزینه تبلیغ (${selectedCity.name} - ${PersianUtils.toPersianDigits(selectedDurationDays)} روز):",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = PersianUtils.formatPrice(calculatedPrice),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AccentYellow
                        )
                    }

                    Button(
                        onClick = { showZarinPalDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("btn_pay_ad")
                    ) {
                        Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "پرداخت و ثبت تبلیغ",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .testTag("city_ads_create_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2.1 — Step 1: Dropdown "انتخاب شهر"
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.LocationCity, contentDescription = null, tint = AccentYellow)
                            Text(
                                text = "۱. انتخاب شهر هدف تبلیغات",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = cityExpanded,
                            onExpandedChange = { cityExpanded = !cityExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = "${selectedCity.name} (${selectedCity.province})",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = cityExpanded,
                                onDismissRequest = { cityExpanded = false }
                            ) {
                                cities.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text("${city.name} — استان ${city.province}") },
                                        onClick = {
                                            selectedCity = city
                                            cityExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "بنر شما فقط به کاربران علاقه‌مند در شهر ${selectedCity.name} نمایش داده خواهد شد.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2.1 — Step 2: Dropdown "انتخاب ملک"
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.HomeWork, contentDescription = null, tint = BrandSecondary)
                            Text(
                                text = "۲. انتخاب ملک دارای تور ۳۶۰",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = propertyExpanded,
                            onExpandedChange = { propertyExpanded = !propertyExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedProperty?.title ?: "ملکی ثبت نشده است",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = propertyExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = propertyExpanded,
                                onDismissRequest = { propertyExpanded = false }
                            ) {
                                properties.forEach { prop ->
                                    DropdownMenuItem(
                                        text = { Text(prop.title) },
                                        onClick = {
                                            selectedProperty = prop
                                            propertyExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2.1 — Step 3: Duration Slider (1 to 30 days) with Tiered Pricing & City Multiplier
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = AccentOrange)
                            Text(
                                text = "۳. مدت زمان نمایش تبلیغ (اسلایدر ۱ الی ۳۰ روز)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Selected duration highlight
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مدت زمان انتخابی:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AccentOrange.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange)
                            ) {
                                Text(
                                    text = "${PersianUtils.toPersianDigits(selectedDurationDays)} روز",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // The Slider (1 to 30 days)
                        Slider(
                            value = selectedDurationDays.toFloat(),
                            onValueChange = { selectedDurationDays = kotlin.math.round(it).toInt().coerceIn(1, 30) },
                            valueRange = 1f..30f,
                            steps = 28,
                            colors = SliderDefaults.colors(
                                thumbColor = AccentOrange,
                                activeTrackColor = AccentOrange,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // 1 day and 30 days labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "۱ روز",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "۱۵ روز",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "۳۰ روز",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Tiered Pricing Info
                        val dailyRate = when {
                            selectedDurationDays <= 10 -> 150_000L
                            selectedDurationDays <= 20 -> 130_000L
                            else -> 110_000L
                        }
                        val isTehran = selectedCity.name == "تهران" || selectedCity.province == "تهران"

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Tiered schedule summary
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.25f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("جدول تعرفه روزانه (محاسبه دقیق هر روز):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentYellow)
                                        Text("• ۱ تا ۱۰ روز: ۱۵۰,۰۰۰ تومان / روز", style = MaterialTheme.typography.labelSmall, color = if (selectedDurationDays <= 10) AccentYellow else Color.White.copy(alpha = 0.8f))
                                        Text("• ۱۱ تا ۲۰ روز: ۱۳۰,۰۰۰ تومان / روز (مثلاً ۱۱ روز = ۱,۴۳۰,۰۰۰ تومان)", style = MaterialTheme.typography.labelSmall, color = if (selectedDurationDays in 11..20) AccentYellow else Color.White.copy(alpha = 0.8f))
                                        Text("• ۲۱ تا ۳۰ روز: ۱۱۰,۰۰۰ تومان / روز (مثلاً ۲۱ روز = ۲,۳۱۰,۰۰۰ تومان)", style = MaterialTheme.typography.labelSmall, color = if (selectedDurationDays in 21..30) AccentYellow else Color.White.copy(alpha = 0.8f))
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "نرخ روزانه پله فعلی:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${PersianUtils.formatPrice(dailyRate)} / روز",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentYellow
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ضریب شهر (${selectedCity.name}):",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (isTehran) "۱.۵ برابر (کلانشهر تهران)" else "۱.۰ برابر (استاندارد)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTehran) AccentYellow else Color.White
                                    )
                                }

                                androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                // Live Total Price Below Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "مبلغ کل قابل پرداخت:",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = if (isTehran) {
                                                "${PersianUtils.toPersianDigits(selectedDurationDays)} روز × ${PersianUtils.formatPrice(dailyRate)} × ۱.۵"
                                            } else {
                                                "${PersianUtils.toPersianDigits(selectedDurationDays)} روز × ${PersianUtils.formatPrice(dailyRate)}"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = PersianUtils.formatPrice(calculatedPrice),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentOrange
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2.1 — Step 4: Banner Upload & Dimensions/Size Validation
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AccentYellow)
                            Text(
                                text = "۴. تصویر بنر تبلیغاتی",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Specs info
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("• حداکثر حجم مجاز: ۵۰۰ کیلوبایت (فشرده‌سازی خودکار)", style = MaterialTheme.typography.bodySmall)
                                Text("• ابعاد پیشنهادی استاندارد: ۱۰۸۰ × ۷۲۰ پیکسل (افقی ۳:۲)", style = MaterialTheme.typography.bodySmall)
                                Text("• فرمت مجاز: JPG یا PNG", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Banner Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource(id = selectedBannerDrawable),
                                contentDescription = "پیش‌نمایش بنر",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay tag
                            Surface(
                                color = Color.Black.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "حجم: ${PersianUtils.toPersianDigits(imageSizeKb)} KB (تایید شده)",
                                    color = BrandSecondary,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Switch Image Option
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "انتخاب تصویر نمونه دیگر:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        selectedBannerDrawable = R.drawable.img_tour_sample
                                        imageSizeKb = 340
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedBannerDrawable == R.drawable.img_tour_sample) AccentYellow else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("سالن پذیرایی", color = if (selectedBannerDrawable == R.drawable.img_tour_sample) Color.Black else Color.White)
                                }

                                Button(
                                    onClick = {
                                        selectedBannerDrawable = R.drawable.img_tour_bedroom
                                        imageSizeKb = 295
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedBannerDrawable == R.drawable.img_tour_bedroom) AccentYellow else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("اتاق مستر", color = if (selectedBannerDrawable == R.drawable.img_tour_bedroom) Color.Black else Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 2.1 — ZarinPal Payment Gateway Simulation Dialog
    if (showZarinPalDialog) {
        AlertDialog(
            onDismissRequest = { if (!isPaymentProcessing) showZarinPalDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = AccentOrange)
                    Text("درگاه پرداخت اینترنتی زرین‌پال", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "پرداخت هزینه تبلیغ بنری در شهر ${selectedCity.name} به مدت ${PersianUtils.toPersianDigits(selectedDurationDays)} روز:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("پذیرنده:", style = MaterialTheme.typography.bodySmall)
                                Text("تور مجازی املاک (تبلیغات شهری)", fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("مبلغ قابل پرداخت:", style = MaterialTheme.typography.bodySmall)
                                Text(PersianUtils.formatPrice(calculatedPrice), fontWeight = FontWeight.Bold, color = AccentOrange)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("شماره ترمینال:", style = MaterialTheme.typography.bodySmall)
                                Text("ZP-89410329", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    if (isPaymentProcessing) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AccentOrange)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("در حال تایید پرداخت و انتشار بنر...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isPaymentProcessing = true
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            repository.createAd(
                                propertyId = selectedProperty?.id,
                                propertyTitle = selectedProperty?.title ?: "ملک بدون عنوان",
                                city = selectedCity.name,
                                province = selectedCity.province,
                                durationDays = selectedDurationDays,
                                bannerDrawableRes = selectedBannerDrawable
                            )
                            isPaymentProcessing = false
                            showZarinPalDialog = false
                            isSuccessDialog = true
                        }, 1300)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    enabled = !isPaymentProcessing
                ) {
                    Text("پرداخت با کارت بانکی")
                }
            },
            dismissButton = {
                if (!isPaymentProcessing) {
                    TextButton(onClick = { showZarinPalDialog = false }) {
                        Text("انصراف")
                    }
                }
            }
        )
    }

    // Success Dialog
    if (isSuccessDialog) {
        AlertDialog(
            onDismissRequest = { isSuccessDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BrandSecondary)
                    Text("تبلیغ با موفقیت منتشر شد!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "تبلیغ بنری شما در شهر ${selectedCity.name} با موفقیت فعال شد. کلیه کاربرانی که شهر ${selectedCity.name} را انتخاب کنند این بنر را در اسلایدر مشاهده خواهند کرد.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isSuccessDialog = false
                        onAdCreated()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("مشاهده تبلیغات")
                }
            }
        )
    }
}
