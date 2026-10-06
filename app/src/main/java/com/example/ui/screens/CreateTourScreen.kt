package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppRepository
import com.example.model.PropertyItem
import com.example.model.PropertyStatus
import com.example.model.TourHotspot
import com.example.model.TourScene
import com.example.ui.components.Tour360Viewer
import com.example.ui.theme.BrandGold
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateTourScreen(
    repository: AppRepository,
    onTourCreated: () -> Unit,
    onLaunchCamera360: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("آپارتمان نوساز ۲۰۰ متری ولنجک") }
    var transactionType by remember { mutableStateOf("فروش") }
    var propertyType by remember { mutableStateOf("آپارتمان") }
    var priceInput by remember { mutableStateOf("25000000000") }
    var areaInput by remember { mutableStateOf("200") }
    var roomsCount by remember { mutableStateOf(3) }
    var city by remember { mutableStateOf("تهران") }
    var neighborhood by remember { mutableStateOf("ولنجک") }
    var address by remember { mutableStateOf("ولنجک، خیابان چهاردهم، برج نگین") }
    var description by remember { mutableStateOf("واحد غرق در نور با چشم‌انداز ابدی کوهستان توچال، متریال درجه یک اروپایی، سیستم تمام هوشمند صوتی و نوری، ۲ پارکینگ سندی، لابی مجلل.") }

    val availableFeatures = listOf(
        "پارکینگ اختصاصی", "آسانسور", "انباری", "استخر و سونا", "روف گاردن",
        "سیستم هوشمند BMS", "لابی‌من ۲۴ ساعته", "بالکن بزرگ", "سالن ورزش Gym"
    )
    val selectedFeatures = remember {
        mutableStateListOf("پارکینگ اختصاصی", "آسانسور", "انباری", "سیستم هوشمند BMS", "لابی‌من ۲۴ ساعته")
    }

    val createdScenes by repository.createdTourScenes.collectAsState()
    val defaultScenes = remember {
        listOf(
            TourScene(
                id = "sc_1",
                name = "سالن پذیرایی ۳۶۰°",
                drawableResId = R.drawable.img_tour_sample,
                hotspots = listOf(
                    TourHotspot("hs1", "انتقال به اتاق مستر", 0.6f, 0.5f, "sc_2"),
                    TourHotspot("hs2", "مشخصات پنجره و متریال", 0.35f, 0.6f, infoText = "شیشه‌های ۳ جداره عایق صدا")
                )
            ),
            TourScene(
                id = "sc_2",
                name = "اتاق خواب مستر ۳۶۰°",
                drawableResId = R.drawable.img_tour_bedroom,
                hotspots = listOf(
                    TourHotspot("hs3", "بازگشت به سالن", 0.25f, 0.5f, "sc_1")
                )
            )
        )
    }
    val scenes = remember(createdScenes) {
        if (createdScenes.isNotEmpty()) createdScenes + defaultScenes else defaultScenes
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ساخت تور مجازی ملک جدید",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward, // RTL back
                            contentDescription = "بازگشت"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandPrimary,
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
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                Toast.makeText(context, "لطفاً عنوان ملک را وارد کنید", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val price = priceInput.toLongOrNull() ?: 10_000_000_000L
                            val area = areaInput.toIntOrNull() ?: 100

                            val newProperty = PropertyItem(
                                id = "prop_${System.currentTimeMillis()}",
                                title = title,
                                propertyType = propertyType,
                                transactionType = transactionType,
                                price = price,
                                areaSqMeters = area,
                                rooms = roomsCount,
                                city = city,
                                neighborhood = neighborhood,
                                address = address,
                                description = description,
                                features = selectedFeatures.toList(),
                                scenes = scenes,
                                viewsCount = 1,
                                inquiriesCount = 0,
                                status = PropertyStatus.ACTIVE_TOUR,
                                isFeatured = true
                            )
                            repository.addProperty(newProperty)
                            repository.clearCreatedTourScenes()
                            Toast.makeText(context, "تور مجازی ۳۶۰ درجه با موفقیت ایجاد شد!", Toast.LENGTH_LONG).show()
                            onTourCreated()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("submit_create_tour_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RotateRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ثبت نهایی و انتشار تور ۳۶۰°",
                            style = MaterialTheme.typography.titleMedium,
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
                .testTag("create_tour_form"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Section 1: Transaction & Property Type
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "۱. مشخصات کلی و نوع معامله",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Transaction Type Segmented Control
                        TabRow(
                            selectedTabIndex = if (transactionType == "فروش") 0 else 1,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                        ) {
                            Tab(
                                selected = transactionType == "فروش",
                                onClick = { transactionType = "فروش" },
                                text = { Text("فروش نقدی", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = transactionType == "رهن و اجاره",
                                onClick = { transactionType = "رهن و اجاره" },
                                text = { Text("رهن و اجاره", fontWeight = FontWeight.Bold) }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "نوع کاربری ملک:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("آپارتمان", "ویلا", "دفتر کار", "تجاری", "زمین و کلنگی").forEach { type ->
                                FilterChip(
                                    selected = propertyType == type,
                                    onClick = { propertyType = type },
                                    label = { Text(type) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Property Information Form
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "۲. اطلاعات پایه ملک",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )

                        // Title
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("عنوان آگهی") },
                            placeholder = { Text("مثال: آپارتمان ۲۰۰ متری نوساز ولنجک") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("property_title_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Price
                        Column {
                            OutlinedTextField(
                                value = priceInput,
                                onValueChange = { priceInput = it.filter { ch -> ch.isDigit() } },
                                label = { Text("قیمت (تومان)") },
                                placeholder = { Text("مثال: 25000000000") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("property_price_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            val parsedPrice = priceInput.toLongOrNull() ?: 0L
                            if (parsedPrice > 0) {
                                Text(
                                    text = "معادل: " + PersianUtils.formatPrice(parsedPrice),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandSecondary,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }

                        // Area & Rooms Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = areaInput,
                                onValueChange = { areaInput = it.filter { ch -> ch.isDigit() } },
                                label = { Text("متراژ (متر)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = roomsCount.toString(),
                                onValueChange = { roomsCount = it.toIntOrNull() ?: 1 },
                                label = { Text("تعداد اتاق") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // City & Neighborhood
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("شهر") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = neighborhood,
                                onValueChange = { neighborhood = it },
                                label = { Text("محله / منطقه") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Address
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("آدرس دقیق") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandPrimary)
                            }
                        )

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("توضیحات تکمیلی") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3
                        )
                    }
                }
            }

            // Section 3: Amenities & Features
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "۳. امکانات و تجهیزات ملک",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableFeatures.forEach { feature ->
                                val isChecked = selectedFeatures.contains(feature)
                                FilterChip(
                                    selected = isChecked,
                                    onClick = {
                                        if (isChecked) selectedFeatures.remove(feature)
                                        else selectedFeatures.add(feature)
                                    },
                                    label = { Text(feature) },
                                    leadingIcon = if (isChecked) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandSecondary,
                                        selectedLabelColor = Color.White,
                                        selectedLeadingIconColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Section 4: 360 Virtual Tour Scenes & Preview
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "۴. فضاهای تور مجازی ۳۶۰ درجه",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandSecondary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "۲ فضا آماده",
                                    color = BrandSecondary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "پیش‌نمایش تعاملی تور ۳۶۰ ثبت شده (برای امتحان انگشت خود را روی تصویر بکشید):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interactive Viewer
                        Tour360Viewer(
                            scenes = scenes,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Module A: Launch Native 360 Camera
                        Button(
                            onClick = onLaunchCamera360,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "عکسبرداری زنده ۳۶۰ درجه با دوربین (ماژول CameraX)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
