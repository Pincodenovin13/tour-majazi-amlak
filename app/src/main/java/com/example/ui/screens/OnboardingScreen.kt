package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import kotlinx.coroutines.launch

data class OnboardingSlideData(
    val title: String,
    val subtitle: String,
    val tag: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val isInteractiveSlide2: Boolean = false
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val slides = listOf(
        // Slide 1: خوش‌آمدگویی
        OnboardingSlideData(
            title = "به تور مجازی املاک خوش آمدید",
            subtitle = "پلتفرم بازدید ۳۶۰ درجه و واقعیت مجازی املاک در سراسر ایران",
            tag = "سامانه سراسری املاک کشور",
            icon = Icons.Default.RotateRight,
            primaryColor = Color(0xFF0D47A1),
            secondaryColor = Color(0xFF1976D2)
        ),
        // Slide 2: تور مجازی ۳۶۰ درجه چیست؟
        OnboardingSlideData(
            title = "تور مجازی ۳۶۰ درجه چیست؟",
            subtitle = "با یک لمس، در ملک قدم بزنید و همه اتاق‌ها، سالن، حمام، تراس و روف‌گاردن را ببینید",
            tag = "پنت‌هاوس مدرن ۱۲۰ متری • پیش‌نمایش متحرک",
            icon = Icons.Default.Home,
            primaryColor = Color(0xFF1B5E20),
            secondaryColor = Color(0xFF2E7D32),
            isInteractiveSlide2 = true
        ),
        // Slide 3: برای مشاورین املاک
        OnboardingSlideData(
            title = "برای مشاورین املاک",
            subtitle = "با گوشی خودت تور ۳۶۰ بساز، در شهرت تبلیغ کن و مشتری‌های بیشتری جذب کن. دیگر نیازی نیست مشتری را به ملک ببری",
            tag = "ابزار هوشمند آژانس‌ها و دفاتر املاک",
            icon = Icons.Default.CameraAlt,
            primaryColor = Color(0xFFE65100),
            secondaryColor = Color(0xFFFF6F00)
        ),
        // Slide 4: برای خریداران و مستاجران
        OnboardingSlideData(
            title = "برای خریداران و مستاجران",
            subtitle = "قبل از بازدید حضوری، تور مجازی ملک را ببین و بهترین انتخاب را داشته باش. بدون رفت و آمد، بدون اتلاف وقت",
            tag = "بازدید سه‌بعدی و هوشمندانه ملک",
            icon = Icons.Default.People,
            primaryColor = Color(0xFF4A148C),
            secondaryColor = Color(0xFF7B1FA2)
        ),
        // Slide 5: کسب درآمد با معرفی
        OnboardingSlideData(
            title = "کسب درآمد با معرفی",
            subtitle = "مشاورین املاک را به ما معرفی کن و تا ۲۰٪ پورسانت نقدی از هر اشتراک آنها دریافت کن",
            tag = "سیستم درآمدزایی و پورسانت نقدی",
            icon = Icons.Default.MonetizationOn,
            primaryColor = Color(0xFFBF360C),
            secondaryColor = Color(0xFFD84315)
        )
    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { slides.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == slides.size - 1

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = systemBarsPadding.calculateTopPadding(),
                bottom = systemBarsPadding.calculateBottomPadding()
            )
            .testTag("onboarding_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar: Skip button (رد کردن)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandPrimary.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, AccentYellow.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RotateRight,
                            contentDescription = null,
                            tint = AccentYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "راهنمای تور مجازی املاک",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentYellow,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                TextButton(
                    onClick = onFinish,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = "رد کردن",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val slide = slides[page]
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Hero Graphic for this slide
                    if (slide.isInteractiveSlide2) {
                        AnimatedApartmentHeroGraphic()
                    } else {
                        StandardSlideHeroGraphic(slide = slide)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Category Pill Tag
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = slide.secondaryColor.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, slide.secondaryColor.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = slide.tag,
                            color = AccentYellow,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slide Title
                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Slide Subtitle / Explanatory Text
                    Text(
                        text = slide.subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom Section: Dots and Continue Button
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Pager Dot Indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(slides.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            val width by animateFloatAsState(
                                targetValue = if (isSelected) 28f else 8f,
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                label = "dot_width"
                            )
                            Box(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(width.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) AccentYellow else Color.White.copy(alpha = 0.25f)
                                    )
                                    .clickable {
                                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                    }
                            )
                        }
                    }

                    // Continue / Start Button
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
                            .height(54.dp)
                            .testTag("onboarding_continue_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLastPage) AccentOrange else AccentYellow
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isLastPage) "شروع استفاده از سامانه" else "ادامه",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isLastPage) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = if (isLastPage) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StandardSlideHeroGraphic(slide: OnboardingSlideData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.5.dp, slide.secondaryColor.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            slide.secondaryColor.copy(alpha = 0.7f),
                            slide.primaryColor.copy(alpha = 0.9f),
                            Color(0xFF0F1522)
                        ),
                        radius = 450f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Decorative background circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = size.minDimension * 0.42f,
                    center = centerOffset,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = size.minDimension * 0.54f,
                    center = centerOffset,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Central Glowing Icon
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.45f),
                border = BorderStroke(2.dp, AccentYellow),
                shadowElevation = 14.dp,
                modifier = Modifier.size(105.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = slide.icon,
                        contentDescription = null,
                        tint = AccentYellow,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedApartmentHeroGraphic() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF102027)),
        border = BorderStroke(1.5.dp, AccentYellow.copy(alpha = 0.6f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E3A5F),
                            Color(0xFF0D1B2A)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentOrange
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.RotateRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text("تور زنده ۳۶۰°", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "پنت‌هاوس ۱۲۰ متر ۲ خواب",
                            color = AccentYellow,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Interactive Room Hotspot Map (Animated layout representation)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoomPill(title = "سالن پذیرایی", subtitle = "پنجره‌های قدی", icon = Icons.Default.Weekend, isSelected = true, modifier = Modifier.weight(1f))
                    RoomPill(title = "اتاق خواب مستر", subtitle = "حمام شیشه‌ای", icon = Icons.Default.Home, isSelected = false, modifier = Modifier.weight(1f))
                    RoomPill(title = "روف‌گاردن و تراس", subtitle = "ویو ابدی شهر", icon = Icons.Default.Navigation, isSelected = false, modifier = Modifier.weight(1f))
                }

                // Directional Hotspot Indicators
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, BrandSecondary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(16.dp))
                            Text("فلش‌های راهنمای گردش بین اتاق‌ها", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                        Text("کیفیت 4K", color = AccentYellow, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomPill(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) BrandPrimary.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, if (isSelected) AccentYellow else Color.White.copy(alpha = 0.15f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AccentYellow else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = if (isSelected) AccentYellow else Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
