package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.WorkspacePremium
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.UserRole
import com.example.ui.theme.BrandGold
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: AppRepository,
    onSubscriptionClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAdminPanelClick: () -> Unit,
    onSwitchRoleClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentRole by repository.currentUserRole.collectAsState()
    val userName by repository.userName.collectAsState()
    val agencyName by repository.agencyName.collectAsState()
    val userPhone by repository.userPhone.collectAsState()
    val activeSub by repository.activeSubscription.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editNameInput by remember { mutableStateOf(userName) }
    var editAgencyInput by remember { mutableStateOf(agencyName) }
    var showCameraGuideDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "پروفایل و تنظیمات کاربری",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
                    containerColor = BrandPrimary,
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
                .testTag("profile_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandPrimary,
                            modifier = Modifier.size(76.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (currentRole == UserRole.AGENT) agencyName else "حساب کاربری معرفی املاک",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = PersianUtils.toPersianDigits(userPhone),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                editNameInput = userName
                                editAgencyInput = agencyName
                                showEditDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ویرایش اطلاعات حساب", color = BrandPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Subscription Section (for Agent)
            if (currentRole == UserRole.AGENT) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onSubscriptionClick),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandGold.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandGold.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = BrandGold)
                                Column {
                                    Text(
                                        text = "اشتراک: ${activeSub?.planTitle ?: "غیرفعال"}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${PersianUtils.toPersianDigits(activeSub?.remainingDays ?: 0)} روز باقی مانده",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text("مدیریت پلن >", color = BrandGold, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Actions & Settings List
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        ProfileMenuItem(
                            icon = Icons.Default.Notifications,
                            title = "مرکز اعلان‌ها و پیام‌ها",
                            subtitle = "مشاهده درخواست‌های مشتریان، پیام‌های بازدید و هشدارهای سیستم",
                            onClick = onNotificationClick
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.CameraAlt,
                            title = "راهنمای عکاسی و ساخت تور ۳۶۰",
                            subtitle = "نکات عکسبرداری پانوراما با موبایل یا دوربین ۳۶۰",
                            onClick = { showCameraGuideDialog = true }
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.SwitchAccount,
                            title = "تغییر نقش کاربری",
                            subtitle = "جابجایی بین مشاور املاک و کاربر عادی",
                            onClick = onSwitchRoleClick
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Headphones,
                            title = "پشتیبانی و تماس با ما",
                            subtitle = "تلفن پشتیبانی: ۰۲۱-۸۸۸۸۴۴۲۲ (شنبه تا چهارشنبه)",
                            onClick = {
                                Toast.makeText(context, "ارتباط با واحد پشتیبانی برقرار شد", Toast.LENGTH_SHORT).show()
                            }
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Security,
                            title = "حریم خصوصی و شرایط خدمات",
                            subtitle = "نسخه ۱.۰.۰ سامانه تور مجازی املاک",
                            onClick = {
                                Toast.makeText(context, "سامانه ثبت شده در مرکز ملی رسانه‌های دیجیتال", Toast.LENGTH_SHORT).show()
                            }
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Logout,
                            title = "خروج از حساب کاربری",
                            subtitle = "بازگشت به صفحه ورود",
                            iconColor = Color.Red,
                            onClick = onLogoutClick
                        )
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("ویرایش اطلاعات حساب") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editNameInput,
                        onValueChange = { editNameInput = it },
                        label = { Text("نام و نام خانوادگی") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAgencyInput,
                        onValueChange = { editAgencyInput = it },
                        label = { Text("نام آژانس / دفتر املاک") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updateProfile(editNameInput, editAgencyInput)
                        Toast.makeText(context, "اطلاعات با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("ذخیره تغییرات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // 360 Camera Guide Dialog
    if (showCameraGuideDialog) {
        AlertDialog(
            onDismissRequest = { showCameraGuideDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.RotateRight, contentDescription = null, tint = BrandPrimary)
                    Text("راهنمای ساخت تور مجازی ۳۶۰°", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("۱. تمام چراغ‌های واحد را روشن کرده و پرده‌ها را باز بگذارید تا نور طبیعی کامل وارد شود.")
                    Text("۲. دوربین یا گوشی را در مرکز هر اتاق و در ارتفاع ۱.۴ تا ۱.۵ متر (سطح دید چشم نشسته) قرار دهید.")
                    Text("۳. برای اتصال فضاها به یکدیگر از هات‌اسپات‌های هدایت‌کننده بین درگاه‌ها استفاده نمایید.")
                    Text("۴. در صورت داشتن پلن طلایی، عکاسی ۳۶۰ با دوربین صنعتی به صورت رایگان توسط تیم ما انجام می‌شود.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCameraGuideDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("متوجه شدم")
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = BrandPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.12f),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (iconColor == Color.Red) Color.Red else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
        )
    }
}
