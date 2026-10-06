package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.text.style.TextAlign
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppRepository
import com.example.model.AdItem
import com.example.model.AdStatus
import com.example.model.PropertyItem
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityAdsViewScreen(
    repository: AppRepository,
    initialCity: String? = null,
    onOpenTour: (PropertyItem) -> Unit,
    onCreateAdClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allAds by repository.ads.collectAsState()
    val platformAds by repository.platformAds.collectAsState()
    val properties by repository.properties.collectAsState()
    val cities = repository.iranianCities

    var selectedCityName by remember(initialCity) { mutableStateOf(initialCity ?: "ساری") } // Default as requested in prompt: مثلاً ساری

    // Filtered ads by city and active status
    val cityAds = allAds.filter {
        (it.city == selectedCityName || it.city == "سراسری") && it.status == AdStatus.ACTIVE
    }

    // 2.2 — 5-second Auto-rotation Carousel Index
    var currentCarouselIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(cityAds.size, selectedCityName) {
        currentCarouselIndex = 0
        while (cityAds.isNotEmpty()) {
            delay(5000) // 5 seconds per banner
            currentCarouselIndex = (currentCarouselIndex + 1) % cityAds.size
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "تبلیغات شهرهای ایران",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "تورهای ۳۶۰ درجه برگزیده در شهر $selectedCityName",
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
                actions = {
                    IconButton(onClick = onCreateAdClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "ثبت تبلیغ جدید",
                            tint = AccentOrange
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .testTag("city_ads_view_screen"),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2.3 — Dedicated Section at Top: OUR OWN APP ADS
            if (platformAds.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(18.dp))
                            Text(
                                text = "تبلیغات و خدمات ویژه سامانه",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentYellow
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        platformAds.forEach { pad ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = BrandPrimary,
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Image(
                                                painter = painterResource(id = pad.bannerDrawableRes),
                                                contentDescription = null,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pad.propertyTitle,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        pad.platformAdSubtitle?.let { sub ->
                                            Text(
                                                text = sub,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    pad.platformActionText?.let { btnText ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = AccentYellow,
                                            modifier = Modifier.clickable { onCreateAdClick() }
                                        ) {
                                            Text(
                                                text = btnText,
                                                color = Color.Black,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2.2 — City Selector Chips (مثلاً ساری، تهران، شیراز، مشهد، اصفهان، تبریز...)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "انتخاب شهر برای مشاهده بنرها:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val displayedCities = remember(selectedCityName) {
                        val base = listOf("ساری", "تهران", "مشهد", "شیراز", "اصفهان", "کرج", "رشت", "تبریز", "اهواز", "قم", "کرمان")
                        if (selectedCityName !in base) listOf(selectedCityName) + base else base
                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(displayedCities) { city ->
                            FilterChip(
                                selected = selectedCityName == city,
                                onClick = { selectedCityName = city },
                                label = { Text(city) },
                                leadingIcon = if (selectedCityName == city) {
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
                }
            }

            // 2.2 — Banner Carousel (Auto-rotates every 5 seconds)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "بنرهای تبلیغاتی شهر $selectedCityName",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${PersianUtils.toPersianDigits(cityAds.size)} بنر فعال",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentYellow
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (cityAds.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(44.dp))
                                Text(
                                    text = "هنوز تبلیغی برای شهر $selectedCityName ثبت نشده است",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "اولین مشاوری باشید که ملک خود را در شهر $selectedCityName تبلیغ می‌کند!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = onCreateAdClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("ثبت تبلیغ در $selectedCityName")
                                }
                            }
                        }
                    } else {
                        val activeAd = cityAds[currentCarouselIndex % cityAds.size]
                        val prop = properties.find { it.id == activeAd.propertyId } ?: properties.first()

                        // Carousel Banner Card
                        AnimatedContent(
                            targetState = activeAd,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "banner_carousel_transition"
                        ) { ad ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { onOpenTour(prop) }
                                    .testTag("carousel_banner_item"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(230.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = ad.bannerDrawableRes),
                                        contentDescription = ad.propertyTitle,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Gradient Overlay
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Black.copy(alpha = 0.4f),
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.85f)
                                                    )
                                                )
                                            )
                                    )

                                    // Top Badges
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = AccentOrange
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                Text(
                                                    text = "تبلیغ ویژه ${ad.city}",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = BrandSecondary
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.RotateRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                Text(
                                                    text = "تور ۳۶۰ فعال",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    // Bottom Property Info
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomStart)
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = ad.propertyTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "مشاور: ${ad.agentName} • تا تاریخ ${ad.endDateJalali}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = AccentYellow,
                                                modifier = Modifier.clickable { onOpenTour(prop) }
                                            ) {
                                                Text(
                                                    text = "مشاهده تور ۳۶۰°",
                                                    color = Color.Black,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Dot indicators for 5s carousel
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            cityAds.forEachIndexed { idx, _ ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 3.dp)
                                        .size(if (idx == currentCarouselIndex % cityAds.size) 10.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(if (idx == currentCarouselIndex % cityAds.size) AccentOrange else Color.Gray.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }
            }

            // All City Ads List below carousel
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "همه آگهی‌های دارای تور در شهر $selectedCityName",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            items(cityAds) { adItem ->
                val prop = properties.find { it.id == adItem.propertyId } ?: properties.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenTour(prop) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = adItem.bannerDrawableRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = adItem.propertyTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${adItem.city} • اعتبار: ${adItem.endDateJalali}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${PersianUtils.toPersianDigits(adItem.viewCount)} بازدید",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentYellow
                            )
                        }

                        Button(
                            onClick = { onOpenTour(prop) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("بازدید ۳۶۰", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
