package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf(UserRole.AGENT) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Header Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BrandPrimary.copy(alpha = 0.2f),
                        border = BorderStroke(1.5.dp, AccentYellow),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.RotateRight,
                                contentDescription = null,
                                tint = AccentYellow,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "انتخاب نوع ورود به سامانه",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "لطفاً پنل مورد نظر خود را مشخص فرمایید:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // OPTION 1: پنل مشاورین املاک (Real Estate Agents - Issue 9)
            item {
                RoleOptionCard(
                    title = "پنل مشاورین املاک",
                    subtitle = "ثبت فایل‌های ملکی، ساخت تور ۳۶۰ هوشمند با موبایل، پلن‌های اشتراک و مدیریت بازدیدها",
                    badgeText = "دفاتر و آژانس‌های املاک",
                    badgeColor = AccentOrange,
                    icon = Icons.Default.BusinessCenter,
                    isSelected = selectedRole == UserRole.AGENT,
                    onClick = { selectedRole = UserRole.AGENT },
                    features = listOf(
                        "ساخت نامحدود تورهای مجازی ۳۶۰ درجه",
                        "ثبت تبلیغات بنری شهری با اسلایدر روزانه",
                        "داشبورد آمار بازدید و درخواست‌های مشتریان"
                    ),
                    testTag = "role_agent_card"
                )
            }

            // OPTION 2: پنل مشاورین آزاد و معرفین (Free Referrers - Issue 9)
            item {
                RoleOptionCard(
                    title = "پنل مشاورین آزاد و معرفین",
                    subtitle = "کد معرف اختصاصی، معرفی مشاورین مسکن و دریافت ۲۰٪ پورسانت نقدی با تسویه شبا پایا",
                    badgeText = "کسب درآمد پورسانتی",
                    badgeColor = BrandSecondary,
                    icon = Icons.Default.MonetizationOn,
                    isSelected = selectedRole == UserRole.REGULAR_USER,
                    onClick = { selectedRole = UserRole.REGULAR_USER },
                    features = listOf(
                        "دریافت کد معرف اختصاصی پس از ثبت‌نام",
                        "کسب ۲۰٪ پورسانت از هر پرداخت اشتراک مشاوران",
                        "تسویه ۲۴ ساعته مستقیم به شماره شبای بانکی"
                    ),
                    testTag = "role_user_card"
                )
            }

            // OPTION 3: بازدید عموم (Public Visitors - Issue 9)
            item {
                RoleOptionCard(
                    title = "بازدید عموم",
                    subtitle = "مشاهده رایگان تورهای ۳۶۰ درجه املاک و تبلیغات شهری سراسر کشور بدون نیاز به ثبت‌نام",
                    badgeText = "ورود مستقیم بدون ثبت‌نام",
                    badgeColor = AccentYellow,
                    icon = Icons.Default.Public,
                    isSelected = selectedRole == UserRole.PUBLIC_VISITOR,
                    onClick = { selectedRole = UserRole.PUBLIC_VISITOR },
                    features = listOf(
                        "تماشای رایگان و سه‌بعدی املاک لوکس سراسر ایران",
                        "جستجو و فیلتر فایل‌ها بر اساس استان و شهر",
                        "تماس و چت مستقیم با مشاورین فایل"
                    ),
                    testTag = "role_public_card"
                )
            }
        }

        // Bottom Continue Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            Button(
                onClick = { onRoleSelected(selectedRole) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("continue_role_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (selectedRole) {
                        UserRole.AGENT -> AccentOrange
                        UserRole.REGULAR_USER -> BrandSecondary
                        UserRole.PUBLIC_VISITOR -> Color(0xFF1976D2)
                    }
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (selectedRole == UserRole.PUBLIC_VISITOR) "ورود به بخش عمومی املاک" else "ورود به ${selectedRole.titleFa}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleOptionCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    features: List<String>,
    testTag: String
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) badgeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        label = "border_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) badgeColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        color = badgeColor.copy(alpha = 0.2f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = if (isSelected) badgeColor else Color.Transparent,
                    border = BorderStroke(2.dp, if (isSelected) badgeColor else MaterialTheme.colorScheme.outline),
                    modifier = Modifier.size(24.dp)
                ) {
                    if (isSelected) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}
