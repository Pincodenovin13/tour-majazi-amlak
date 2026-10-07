package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import coil.compose.AsyncImage
import com.example.R
import com.example.data.AppRepository
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandGold
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary

// Data Model: Fantasy Avatar Presets (Men & Women)
data class FantasyAvatarPreset(
    val id: String,
    val name: String,
    val roleTitle: String,
    val isMale: Boolean,
    val primaryColor: Color,
    val secondaryColor: Color,
    val icon: ImageVector,
    val badge: String
)

// Data Model: Agency Cover / Property / Lobby / Logo Presets
data class AgencyCoverPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val gradientColors: List<Color>,
    val icon: ImageVector,
    val tag: String,
    val drawableFallback: Int = R.drawable.img_tour_sample
)

object AgentBrandingData {
    val fantasyAvatars = listOf(
        // Men Avatars (چهره فانتزی مرد)
        FantasyAvatarPreset(
            id = "male_formal",
            name = "مهندس کیان آریا",
            roleTitle = "مدیر ارشد آژانس • کت و شلوار دیپلماتیک",
            isMale = true,
            primaryColor = Color(0xFF1565C0),
            secondaryColor = Color(0xFF0D47A1),
            icon = Icons.Default.BusinessCenter,
            badge = "رسمی دیپلمات"
        ),
        FantasyAvatarPreset(
            id = "male_young",
            name = "مشاور سهراب سپهری",
            roleTitle = "کارشناس فروش برج‌ها • تیپ مدرن و پویا",
            isMale = true,
            primaryColor = Color(0xFFFF5722),
            secondaryColor = Color(0xFFD84315),
            icon = Icons.Default.Person,
            badge = "مشاور پرانرژی"
        ),
        FantasyAvatarPreset(
            id = "male_architect",
            name = "مهندس کامران بهرامی",
            roleTitle = "کارشناس ارزیابی و سازه • استایل مهندسی",
            isMale = true,
            primaryColor = Color(0xFF00897B),
            secondaryColor = Color(0xFF004D40),
            icon = Icons.Default.Foundation,
            badge = "کارشناس سازه"
        ),
        FantasyAvatarPreset(
            id = "male_senior",
            name = "حاج رضا اعتمادی",
            roleTitle = "پیشکسوت معاملات ملکی • با ۳۰ سال سابقه",
            isMale = true,
            primaryColor = Color(0xFF4E342E),
            secondaryColor = Color(0xFF3E2723),
            icon = Icons.Default.WorkspacePremium,
            badge = "۳۰ سال سابقه"
        ),

        // Women Avatars (چهره فانتزی زن)
        FantasyAvatarPreset(
            id = "female_manager",
            name = "مهندس مریم پارسا",
            roleTitle = "مدیر معاملات لوکس • پوشش رسمی سازمانی",
            isMale = false,
            primaryColor = Color(0xFF8E24AA),
            secondaryColor = Color(0xFF4A148C),
            icon = Icons.Default.Face,
            badge = "مدیر قراردادها"
        ),
        FantasyAvatarPreset(
            id = "female_sales",
            name = "خانم سارا رادمنش",
            roleTitle = "کارشناس فروش پنت‌هاوس • سبک نوین و شیک",
            isMale = false,
            primaryColor = Color(0xFFD81B60),
            secondaryColor = Color(0xFF880E4F),
            icon = Icons.Default.Star,
            badge = "مشاور برتر ماه"
        ),
        FantasyAvatarPreset(
            id = "female_interior",
            name = "مهندس نسترن صادقی",
            roleTitle = "طراح دکوراسیون و تور ۳۶۰ • استایل خلاق",
            isMale = false,
            primaryColor = Color(0xFF00ACC1),
            secondaryColor = Color(0xFF006064),
            icon = Icons.Default.Weekend,
            badge = "معماری داخلی"
        ),
        FantasyAvatarPreset(
            id = "female_formal",
            name = "خانم بهاره یوسفی",
            roleTitle = "حقوقدان و مشاور ملکی • استایل کلاسیک",
            isMale = false,
            primaryColor = Color(0xFF5E35B1),
            secondaryColor = Color(0xFF311B92),
            icon = Icons.Default.Apartment,
            badge = "مشاور بین‌الملل"
        )
    )

    val agencyCoverPresets = listOf(
        // طرح لابی مجلل
        AgencyCoverPreset(
            id = "lobby_marble",
            title = "طرح لابی مجلل و سنگ مرمر",
            subtitle = "ورودی باشکوه با لوستر کریستال و مبلمان سلطنتی",
            gradientColors = listOf(Color(0xFF263238), Color(0xFF37474F), Color(0xFF102027)),
            icon = Icons.Default.Weekend,
            tag = "لابی سلطنتی",
            drawableFallback = R.drawable.img_tour_sample
        ),
        // طرح برج‌های مدرن
        AgencyCoverPreset(
            id = "tower_modern",
            title = "طرح برج‌های مسکونی مدرن",
            subtitle = "معماری شیشه‌ای های‌تک با روف‌گاردن و هلی‌پد",
            gradientColors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF01579B)),
            icon = Icons.Default.LocationCity,
            tag = "برج‌های لوکس",
            drawableFallback = R.drawable.img_tour_bedroom
        ),
        // طرح آپارتمان نئوکلاسیک
        AgencyCoverPreset(
            id = "apartment_classic",
            title = "طرح آپارتمان نئوکلاسیک",
            subtitle = "نمای رومی شکیل با ستون‌های سنگی و بالکن‌های تراش‌خورده",
            gradientColors = listOf(Color(0xFF4E342E), Color(0xFF6D4C41), Color(0xFF3E2723)),
            icon = Icons.Default.Apartment,
            tag = "آپارتمان نئوکلاسیک",
            drawableFallback = R.drawable.img_tour_sample
        ),
        // طرح ویلای مدرن
        AgencyCoverPreset(
            id = "villa_luxury",
            title = "طرح ویلای مدرن استخردار",
            subtitle = "محوطه سبز اختصاصی با آبنما و پنجره‌های قدی سرتاسری",
            gradientColors = listOf(Color(0xFF004D40), Color(0xFF00796B), Color(0xFF00332C)),
            icon = Icons.Default.HomeWork,
            tag = "ویلا باغ لاکچری",
            drawableFallback = R.drawable.img_tour_sample
        ),
        // طرح لوگوی رسمی و زرین املاک
        AgencyCoverPreset(
            id = "logo_gold",
            title = "طرح لوگوی زرین املاک و کلید",
            subtitle = "نشان رسمی املاک با سقف و کلید طلایی و پرستیژ صنفی",
            gradientColors = listOf(Color(0xFFE65100), Color(0xFFFF8F00), Color(0xFFBF360C)),
            icon = Icons.Default.VpnKey,
            tag = "نشان زرین املاک",
            drawableFallback = R.drawable.ic_app_logo
        ),
        // طرح پنت‌هاوس شهری با دید پانوراما
        AgencyCoverPreset(
            id = "penthouse_skyline",
            title = "طرح پنت‌هاوس اسکای‌لاین",
            subtitle = "دید خیره‌کننده ۳۶۰ درجه به افق شهر و آسمان شب",
            gradientColors = listOf(Color(0xFF311B92), Color(0xFF512DA8), Color(0xFF1A237E)),
            icon = Icons.Default.Foundation,
            tag = "پنت‌هاوس اسکای‌لاین",
            drawableFallback = R.drawable.img_tour_bedroom
        )
    )
}

/**
 * Visual Representation of the Agent Avatar (supports Gallery Uri, Fantasy Presets, or fallback drawable)
 */
@Composable
fun AgentAvatarView(
    repository: AppRepository,
    modifier: Modifier = Modifier,
    sizeDp: Int = 72,
    borderWidthDp: Float = 2.5f,
    onClick: (() -> Unit)? = null
) {
    val avatarUri by repository.agentAvatarUri.collectAsState()
    val avatarPresetId by repository.agentAvatarPresetId.collectAsState()
    val fallbackDrawable by repository.agentAvatarRes.collectAsState()

    val currentPreset = remember(avatarPresetId) {
        AgentBrandingData.fantasyAvatars.find { it.id == avatarPresetId }
    }

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .border(borderWidthDp.dp, AccentYellow, CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUri != null) {
            AsyncImage(
                model = avatarUri,
                contentDescription = "عکس چهره مشاور",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (currentPreset != null) {
            // Fantasy Avatar Illustrated Presentation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(currentPreset.primaryColor, currentPreset.secondaryColor)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = currentPreset.icon,
                        contentDescription = currentPreset.name,
                        tint = Color.White,
                        modifier = Modifier.size((sizeDp * 0.45f).dp)
                    )
                    Text(
                        text = if (currentPreset.isMale) "مرد" else "زن",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = (sizeDp * 0.14f).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Image(
                painter = painterResource(id = fallbackDrawable),
                contentDescription = "عکس چهره مشاور",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Visual Representation of the Agency Cover/Logo/Lobby (supports Gallery Uri, Presets, or fallback drawable)
 */
@Composable
fun AgentCoverView(
    repository: AppRepository,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val coverUri by repository.agentCoverUri.collectAsState()
    val coverPresetId by repository.agentCoverPresetId.collectAsState()
    val fallbackDrawable by repository.agentCoverRes.collectAsState()

    val currentCoverPreset = remember(coverPresetId) {
        AgentBrandingData.agencyCoverPresets.find { it.id == coverPresetId }
    }

    Box(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        if (coverUri != null) {
            AsyncImage(
                model = coverUri,
                contentDescription = "سردر و لوگوی املاک",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (currentCoverPreset != null) {
            // Preset Real Estate / Lobby / Apartment Art
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(currentCoverPreset.gradientColors))
            ) {
                // Background subtle fallback texture
                Image(
                    painter = painterResource(id = currentCoverPreset.drawableFallback),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    alpha = 0.35f
                )

                // Artistic Centered Presentation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, AccentYellow.copy(alpha = 0.6f)),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = currentCoverPreset.icon,
                                contentDescription = null,
                                tint = AccentYellow,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentCoverPreset.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentOrange.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = currentCoverPreset.tag,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        } else {
            Image(
                painter = painterResource(id = fallbackDrawable),
                contentDescription = "سردر املاک",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Modal Dialog: Select Agent Face / Avatar (Gallery Picker + Fantasy Men & Women Presets)
 */
@Composable
fun AgentAvatarPickerDialog(
    repository: AppRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedGenderTabIndex by remember { mutableIntStateOf(0) } // 0: مرد, 1: زن

    val avatarUri by repository.agentAvatarUri.collectAsState()
    val avatarPresetId by repository.agentAvatarPresetId.collectAsState()

    // Gallery Picker Launcher
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            repository.setAgentAvatarUri(uri.toString())
            Toast.makeText(context, "عکس چهره با موفقیت از گالری انتخاب شد", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = BrandSecondary.copy(alpha = 0.2f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = BrandSecondary)
                    }
                }
                Column {
                    Text(
                        text = "انتخاب چهره و نمایه مشاور",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "عکس از گالری یا چهره‌های فانتزی نمونه مرد و زن",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. ACTION: Pick from User's Device Gallery
                Button(
                    onClick = {
                        galleryPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_pick_avatar_gallery")
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "انتخاب عکس چهره از گالری گوشی",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                if (avatarUri != null) {
                    OutlinedButton(
                        onClick = {
                            repository.setAgentAvatarUri(null)
                            repository.setAgentAvatarPreset("male_formal")
                            Toast.makeText(context, "عکس به حالت نمونه پیش‌فرض بازگشت", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                        border = BorderStroke(1.dp, Color(0xFFFF5252)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حذف عکس گالری و بازگشت به آواتار")
                    }
                }

                HorizontalDividerCustom()

                // 2. FANTASY AVATARS CATEGORY TABS (Men vs Women)
                Text(
                    text = "یا انتخاب از نمونه چهره‌های فانتزی مشاوران:",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentYellow
                )

                TabRow(
                    selectedTabIndex = selectedGenderTabIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedGenderTabIndex]),
                            color = AccentYellow
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedGenderTabIndex == 0,
                        onClick = { selectedGenderTabIndex = 0 },
                        text = {
                            Text(
                                "چهره‌های فانتزی مرد (۴ مدل)",
                                fontWeight = if (selectedGenderTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedGenderTabIndex == 0) AccentYellow else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedGenderTabIndex == 1,
                        onClick = { selectedGenderTabIndex = 1 },
                        text = {
                            Text(
                                "چهره‌های فانتزی زن (۴ مدل)",
                                fontWeight = if (selectedGenderTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedGenderTabIndex == 1) AccentYellow else Color.White
                            )
                        }
                    )
                }

                // Grid of 4 Avatars based on Gender
                val filteredAvatars = AgentBrandingData.fantasyAvatars.filter {
                    if (selectedGenderTabIndex == 0) it.isMale else !it.isMale
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    items(filteredAvatars, key = { it.id }) { preset ->
                        val isSelected = avatarPresetId == preset.id && avatarUri == null
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) AccentYellow else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    repository.setAgentAvatarPreset(preset.id)
                                    Toast.makeText(context, "چهره «${preset.name}» انتخاب شد", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Avatar circle icon
                                Surface(
                                    shape = CircleShape,
                                    color = preset.primaryColor,
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = preset.icon,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = preset.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isSelected) AccentYellow else Color.Black.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = preset.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

/**
 * Modal Dialog: Select Agency Logo, Property Designs & Lobby (Gallery Picker + 6 Luxury Presets)
 */
@Composable
fun AgentCoverPickerDialog(
    repository: AppRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coverUri by repository.agentCoverUri.collectAsState()
    val coverPresetId by repository.agentCoverPresetId.collectAsState()

    // Gallery Picker Launcher
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            repository.setAgentCoverUri(uri.toString())
            Toast.makeText(context, "لوگو و سردر املاک با موفقیت از گالری انتخاب شد", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = AccentOrange.copy(alpha = 0.2f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Apartment, contentDescription = null, tint = AccentOrange)
                    }
                }
                Column {
                    Text(
                        text = "انتخاب لوگو، سردر و طرح املاک",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "آپلود لوگوی آژانس یا طرح‌های لابی، آپارتمان و ملک",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. ACTION: Pick from User's Device Gallery
                Button(
                    onClick = {
                        galleryPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_pick_cover_gallery")
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "انتخاب لوگو و عکس سردر از گالری گوشی",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                if (coverUri != null) {
                    OutlinedButton(
                        onClick = {
                            repository.setAgentCoverUri(null)
                            repository.setAgentCoverPreset("lobby_marble")
                            Toast.makeText(context, "طرح به حالت پیش‌فرض لابی بازگشت", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                        border = BorderStroke(1.dp, Color(0xFFFF5252)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حذف عکس گالری و بازگشت به طرح‌های نمونه")
                    }
                }

                HorizontalDividerCustom()

                // 2. PRESET DESIGNS: LOBBY, APARTMENT, TOWER, VILLA, GOLDEN LOGO
                Text(
                    text = "یا انتخاب از طرح‌های آماده ملک، آپارتمان و لابی:",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentYellow
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    items(AgentBrandingData.agencyCoverPresets, key = { it.id }) { preset ->
                        val isSelected = coverPresetId == preset.id && coverUri == null
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) AccentOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    repository.setAgentCoverPreset(preset.id)
                                    Toast.makeText(context, "طرح «${preset.title}» اعمال شد", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = preset.gradientColors.first(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = preset.icon,
                                            contentDescription = null,
                                            tint = AccentYellow,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = preset.title,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isSelected) AccentOrange else Color.Black.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = preset.tag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
private fun HorizontalDividerCustom() {
    androidx.compose.material3.HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
