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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.UserRole
import com.example.ui.theme.BrandGold
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
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BrandPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.RotateRight,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "خوش آمدید به تور مجازی املاک",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "نوع حساب کاربری خود را انتخاب نمایید",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Card 1: Real Estate Agent / Consultant
            RoleOptionCard(
                title = "مشاور املاک و آژانس مسکن",
                subtitle = "ثبت فایل‌های فروش و اجاره، ایجاد تور ۳۶۰ اختصاصی، اشتراک حرفه‌ای و دسترسی به پنل مدیریت بازدیدها",
                badgeText = "پیشنهادی برای دفاتر املاک",
                badgeColor = BrandPrimary,
                icon = Icons.Default.BusinessCenter,
                isSelected = selectedRole == UserRole.AGENT,
                onClick = { selectedRole = UserRole.AGENT },
                features = listOf(
                    "ساخت نامحدود تورهای مجازی ۳۶۰ درجه",
                    "پنل اختصاصی مدیریت املاک و بازدیدها",
                    "پلن‌های اشتراک ۱، ۳ و ۶ ماهه",
                    "برندسازی اختصاصی آژانس املاک شما"
                ),
                testTag = "role_agent_card"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Card 2: Regular User
            RoleOptionCard(
                title = "کاربر عادی و خریدار / مستاجر",
                subtitle = "بازدید مجازی ۳۶۰ از بهترین املاک بدون نیاز به مراجعه حضوری، دریافت کد معرف و کسب سود پورسانت نقدی",
                badgeText = "کسب درآمد پورسانتی",
                badgeColor = BrandSecondary,
                icon = Icons.Default.Person,
                isSelected = selectedRole == UserRole.REGULAR_USER,
                onClick = { selectedRole = UserRole.REGULAR_USER },
                features = listOf(
                    "تماشای رایگان تورهای ۳۶۰ درجه املاک لوکس",
                    "کد معرف اختصاصی و دریافت ۲۰٪ پورسانت",
                    "امکان تسویه حساب آنی به شماره شبا",
                    "ارتباط مستقیم با مشاورین معتبر"
                ),
                testTag = "role_user_card"
            )
        }

        // Bottom Continue Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Button(
                onClick = { onRoleSelected(selectedRole) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("continue_role_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == UserRole.AGENT) BrandPrimary else BrandSecondary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ادامه به مرحله ورود",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack, // Persian RTL back points forward
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
        targetValue = if (isSelected) badgeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
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
            containerColor = if (isSelected) badgeColor.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                        color = badgeColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeColor.copy(alpha = 0.15f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeColor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = badgeColor)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mini feature bullets
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                features.forEach { feat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = feat,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
