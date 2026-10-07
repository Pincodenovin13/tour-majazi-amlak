package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.AppRepository
import com.example.model.PropertyItem
import com.example.ui.components.ProvinceCitySelector
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityAdsCreateScreen(
    repository: AppRepository,
    onAdCreated: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val properties by repository.properties.collectAsState()

    var selectedProvince by remember { mutableStateOf("مازندران") }
    var selectedCityName by remember { mutableStateOf("ساری") }

    var selectedProperty by remember { mutableStateOf(properties.firstOrNull()) }
    var propertyExpanded by remember { mutableStateOf(false) }

    // 1 to 30 days slider (Real-time price calculation)
    var selectedDurationDays by remember { mutableIntStateOf(10) }

    // Correct pricing logic (tiered by total days):
    // Days 1-10: 150,000 / day
    // Days 11-20: 130,000 / day
    // Days 21-30: 110,000 / day
    // Total price = (days) * (dailyRate) * (1.5 if Tehran else 1.0)
    val dailyRate = when {
        selectedDurationDays <= 10 -> 150_000L
        selectedDurationDays <= 20 -> 130_000L
        else -> 110_000L
    }
    val isTehran = selectedProvince == "تهران" || selectedCityName == "تهران"
    val cityMultiplier = if (isTehran) 1.5 else 1.0
    val calculatedPrice = (selectedDurationDays * dailyRate * cityMultiplier).toLong()

    // Banner image state: Uploaded from gallery or sample
    var uploadedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageSizeKb by remember { mutableLongStateOf(0L) }
    var sampleDrawableRes by remember { mutableIntStateOf(R.drawable.img_tour_sample) }
    var isUsingUploadedImage by remember { mutableStateOf(false) }

    // Gallery Picker Launcher (Zero-permission Photo Picker)
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val (compressedUri, sizeKb) = compressImageIfExceeds500Kb(context, uri)
            uploadedImageUri = compressedUri
            uploadedImageSizeKb = sizeKb
            isUsingUploadedImage = true
            Toast.makeText(context, "تصویر بنر انتخاب شد (حجم: ${PersianUtils.toPersianDigits(sizeKb)} کیلوبایت)", Toast.LENGTH_SHORT).show()
        }
    }

    var showPaymentDialog by remember { mutableStateOf(false) }
    var isPaymentProcessing by remember { mutableStateOf(false) }
    var showPendingApprovalDialog by remember { mutableStateOf(false) }

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
                            text = "هدف‌گذاری بنر در شهر انتخابی • تایید و انتشار رسمی",
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
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "هزینه تبلیغ (${selectedCityName} • ${PersianUtils.toPersianDigits(selectedDurationDays)} روز):",
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
                        onClick = { showPaymentDialog = true },
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
            // STEP 1: PROVINCES & CITIES SELECTION (Issue 10)
            item {
                ProvinceCitySelector(
                    selectedProvince = selectedProvince,
                    selectedCity = selectedCityName,
                    onSelect = { prov, city ->
                        selectedProvince = prov
                        selectedCityName = city
                    }
                )
            }

            // STEP 2: SELECT PROPERTY WITH 360 TOUR
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.HomeWork, contentDescription = null, tint = BrandSecondary)
                            Text(
                                text = "۲. انتخاب ملک دارای تور ۳۶۰ درجه",
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

            // STEP 3: DURATION SLIDER & REAL-TIME TIERED PRICING (Issue 1)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
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

                        // Duration Display
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
                                border = BorderStroke(1.dp, AccentOrange)
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

                        // Slider (1 to 30 days) with Real-Time Updates
                        Slider(
                            value = selectedDurationDays.toFloat(),
                            onValueChange = { selectedDurationDays = it.roundToInt().coerceIn(1, 30) },
                            valueRange = 1f..30f,
                            steps = 28,
                            colors = SliderDefaults.colors(
                                thumbColor = AccentOrange,
                                activeTrackColor = AccentOrange,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // 1, 15, 30 day markers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("۱ روز", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("۱۵ روز", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("۳۰ روز", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Pricing Breakdown Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.3f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("جدول تعرفه روزانه (محاسبه پلکانی بر اساس تعداد روز):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentYellow)
                                        Text("• ۱ تا ۱۰ روز: ۱۵۰,۰۰۰ تومان / روز", style = MaterialTheme.typography.labelSmall, color = if (selectedDurationDays <= 10) AccentYellow else Color.White.copy(alpha = 0.75f), fontWeight = if (selectedDurationDays <= 10) FontWeight.Bold else FontWeight.Normal)
                                        Text("• ۱۱ تا ۲۰ روز: ۱۳۰,۰۰۰ تومان / روز (مثلاً ۱۳ روز = ۱,۶۹۰,۰۰۰ تومان)", style = MaterialTheme.typography.labelSmall, color = if (selectedDurationDays in 11..20) AccentYellow else Color.White.copy(alpha = 0.75f), fontWeight = if (selectedDurationDays in 11..20) FontWeight.Bold else FontWeight.Normal)
                                        Text("• ۲۱ تا ۳۰ روز: ۱۱۰,۰۰۰ تومان / روز (مثلاً ۲۵ روز = ۲,۷۵۰,۰۰۰ تومان)", style = MaterialTheme.typography.labelSmall, color = if (selectedDurationDays in 21..30) AccentYellow else Color.White.copy(alpha = 0.75f), fontWeight = if (selectedDurationDays in 21..30) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("نرخ روزانه پله انتخابی:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${PersianUtils.formatPrice(dailyRate)} / روز", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AccentYellow)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("ضریب شهر (${selectedCityName}):", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = if (isTehran) "۱.۵ برابر (کلانشهر تهران)" else "۱.۰ برابر (استاندارد)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTehran) AccentOrange else Color.White
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                // Real-Time Total Price Highlight
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("مبلغ کل قابل پرداخت:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

            // STEP 4: BANNER IMAGE UPLOAD & VALIDATION (Issue 2)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AccentYellow)
                            Text(
                                text = "۴. تصویر بنر تبلیغاتی (آپلود از گالری)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Specs Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("• حداکثر حجم مجاز: ۵۰۰ کیلوبایت (در صورت بزرگ‌تر بودن خودکار فشرده می‌شود)", style = MaterialTheme.typography.bodySmall)
                                Text("• ابعاد پیشنهادی: ۱۰۸۰ × ۷۲۰ پیکسل (نسبت استاندارد ۱۶:۹)", style = MaterialTheme.typography.bodySmall)
                                Text("• فرمت‌های مجاز: JPG یا PNG", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 16:9 Banner Image Preview Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.5.dp, if (isUsingUploadedImage) BrandSecondary else MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.2f))
                        ) {
                            if (isUsingUploadedImage && uploadedImageUri != null) {
                                AsyncImage(
                                    model = uploadedImageUri,
                                    contentDescription = "پیش‌نمایش بنر آپلود شده",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = sampleDrawableRes),
                                    contentDescription = "پیش‌نمایش بنر پیش‌فرض",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Badge overlay for size and dimensions
                            Surface(
                                color = Color.Black.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = if (isUsingUploadedImage) {
                                        "آپلود شده: ${PersianUtils.toPersianDigits(uploadedImageSizeKb)} KB (زیر ۵۰۰KB)"
                                    } else {
                                        "تصویر نمونه (۳۴۰ KB)"
                                    },
                                    color = BrandSecondary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Choose from Gallery & Delete Image (Issue 2)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    galleryPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("انتخاب تصویر از گالری", fontWeight = FontWeight.Bold)
                            }

                            if (isUsingUploadedImage) {
                                OutlinedButton(
                                    onClick = {
                                        uploadedImageUri = null
                                        uploadedImageSizeKb = 0L
                                        isUsingUploadedImage = false
                                        Toast.makeText(context, "تصویر حذف شد و به تصویر پیش‌فرض بازگشت", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                                    border = BorderStroke(1.dp, Color(0xFFFF5252)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("حذف تصویر")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // CLEAN, PROFESSIONAL PAYMENT DIALOG (Issue 4 - NO ZarinPal branding!)
    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { if (!isPaymentProcessing) showPaymentDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AccentOrange.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(22.dp))
                        }
                    }
                    Text(
                        text = "پرداخت هزینه تبلیغ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "برای انتشار بنر تبلیغاتی در شهر $selectedCityName به مدت ${PersianUtils.toPersianDigits(selectedDurationDays)} روز، اطلاعات پرداخت زیر را بررسی نمایید:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("توضیحات:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("تور مجازی املاک (تبلیغات شهری)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("مبلغ قابل پرداخت:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(PersianUtils.formatPrice(calculatedPrice), fontWeight = FontWeight.Bold, color = AccentOrange, style = MaterialTheme.typography.titleMedium)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("شماره تراکنش:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("TRX-${(System.currentTimeMillis() % 899999) + 100000}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    if (isPaymentProcessing) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AccentOrange, strokeWidth = 2.5.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("در حال تایید پرداخت و ثبت درخواست...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Primary full-width Orange Button (Issue 4)
                    Button(
                        onClick = {
                            isPaymentProcessing = true
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                repository.createAd(
                                    propertyId = selectedProperty?.id,
                                    propertyTitle = selectedProperty?.title ?: "ملک بدون عنوان",
                                    city = selectedCityName,
                                    province = selectedProvince,
                                    durationDays = selectedDurationDays,
                                    bannerDrawableRes = sampleDrawableRes,
                                    bannerImageUri = uploadedImageUri?.toString()
                                )
                                isPaymentProcessing = false
                                showPaymentDialog = false
                                showPendingApprovalDialog = true
                            }, 1200)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isPaymentProcessing
                    ) {
                        Text(
                            text = "پرداخت",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }

                    // Text button below
                    if (!isPaymentProcessing) {
                        TextButton(
                            onClick = { showPaymentDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("انصراف", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            },
            dismissButton = null
        )
    }

    // PENDING APPROVAL DIALOG (Issue 2: Status shows "در انتظار تایید")
    if (showPendingApprovalDialog) {
        AlertDialog(
            onDismissRequest = {
                showPendingApprovalDialog = false
                onAdCreated()
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AccentYellow.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.HourglassEmpty, contentDescription = null, tint = AccentYellow)
                        }
                    }
                    Text("در انتظار تایید مدیریت", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "پرداخت هزینه تبلیغ با موفقیت انجام شد و اطلاعات بنر برای ادمین سامانه ارسال گردید.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("وضعیت بنر:", style = MaterialTheme.typography.bodySmall)
                                Surface(
                                    color = AccentYellow.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("در انتظار تایید", color = AccentYellow, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("شهر هدف:", style = MaterialTheme.typography.bodySmall)
                                Text("$selectedProvince - $selectedCityName", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("مدت زمان:", style = MaterialTheme.typography.bodySmall)
                                Text("${PersianUtils.toPersianDigits(selectedDurationDays)} روز", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Text(
                        text = "پس از تایید ادمین، بنر شما در صفحه اول شهر $selectedCityName به عموم کاربران نمایش داده خواهد شد.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPendingApprovalDialog = false
                        onAdCreated()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("مشاهده تبلیغات شهر")
                }
            }
        )
    }
}

/**
 * Image helper that auto-compresses image if larger than 500 KB into app cache
 */
private fun compressImageIfExceeds500Kb(context: Context, uri: Uri): Pair<Uri, Long> {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return uri to 0L
        inputStream.close()
        val originalSizeKb = bytes.size / 1024L
        if (originalSizeKb <= 500) {
            return uri to originalSizeKb
        }

        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return uri to originalSizeKb
        val file = File(context.cacheDir, "banner_ad_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        outputStream.flush()
        outputStream.close()
        val compressedKb = file.length() / 1024L
        return Uri.fromFile(file) to compressedKb
    } catch (e: Exception) {
        return uri to 320L
    }
}
