package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.UserRole
import com.example.ui.components.ProvinceCitySelector
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandGold
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    role: UserRole,
    repository: AppRepository,
    initialReferralCode: String = "",
    initialReferrerName: String = "",
    isReferralLocked: Boolean = false,
    onLoginSuccess: (mobile: String, role: UserRole) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: ثبت‌نام کامل, 1: ورود سریع با پیامک

    // Common fields
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("09123456789") }
    var selectedProvince by remember { mutableStateOf("مازندران") }
    var selectedCity by remember { mutableStateOf("ساری") }
    var referralCodeInput by remember(initialReferralCode) { mutableStateOf(initialReferralCode) }

    // Agent-specific fields (Issue 7)
    var agencyName by remember { mutableStateOf("") }
    var whatsappNumber by remember { mutableStateOf("09123456789") }
    var telegramId by remember { mutableStateOf("") }

    // Quick OTP Login fields
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(60) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Success dialog for registration showing referral code
    var showRegistrationSuccessDialog by remember { mutableStateOf(false) }
    var generatedReferralCode by remember { mutableStateOf("") }

    LaunchedEffect(isOtpSent) {
        if (isOtpSent) {
            countdown = 60
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Bar with back button & role tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .testTag("login_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "بازگشت",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (role == UserRole.AGENT) BrandPrimary.copy(alpha = 0.15f) else BrandSecondary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, if (role == UserRole.AGENT) BrandPrimary else BrandSecondary)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (role == UserRole.AGENT) Icons.Default.BusinessCenter else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (role == UserRole.AGENT) AccentYellow else BrandSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = role.titleFa,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (role == UserRole.AGENT) AccentYellow else BrandSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Screen Header Title
            Text(
                text = if (selectedTabIndex == 0) "ثبت‌نام کامل در سامانه" else "ورود سریع با پیامک",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = if (role == UserRole.AGENT)
                    "عضویت مشاورین املاک جهت ساخت تورهای ۳۶۰ درجه و مدیریت فایل‌ها"
                else
                    "عضویت کاربران و مشاورین آزاد با کد معرف و دریافت پورسانت نقدی",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Tabs: Registration vs Quick OTP Login
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AccentOrange
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        errorMessage = null
                    },
                    text = {
                        Text(
                            text = "فرم ثبت‌نام جدید",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 0) AccentOrange else Color.White
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                        errorMessage = null
                    },
                    text = {
                        Text(
                            text = "ورود سریع با پیامک",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 1) AccentOrange else Color.White
                        )
                    }
                )
            }
        }

        // TAB 0: COMPLETE REGISTRATION FORM (Issue 7)
        if (selectedTabIndex == 0) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "اطلاعات هویتی و ارتباطی:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentYellow
                        )

                        // 1. Full Name
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("نام و نام خانوادگی") },
                            placeholder = { Text("مثلاً کیان آریا") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AccentYellow) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_fullname_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // 2. Mobile Number
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("شماره موبایل") },
                            placeholder = { Text("۰۹۱۲۳۴۵۶۷۸۹") },
                            leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AccentOrange) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // If AGENT: Agency Name, WhatsApp, Telegram
                        if (role == UserRole.AGENT) {
                            OutlinedTextField(
                                value = agencyName,
                                onValueChange = { agencyName = it },
                                label = { Text("نام آژانس املاک") },
                                placeholder = { Text("مثلاً املاک مدرن شمیران") },
                                leadingIcon = { Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = BrandSecondary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("reg_agency_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = whatsappNumber,
                                onValueChange = { whatsappNumber = it },
                                label = { Text("شماره واتساپ") },
                                placeholder = { Text("۰۹۱۲۳۴۵۶۷۸۹") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF25D366)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth().testTag("reg_whatsapp_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = telegramId,
                                onValueChange = { telegramId = it },
                                label = { Text("آیدی تلگرام (اختیاری)") },
                                placeholder = { Text("@amlak_modern") },
                                leadingIcon = { Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF0088CC)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("reg_telegram_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Locked Referral Banner (Issues 4 & 8)
                        if (isReferralLocked && referralCodeInput.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BrandSecondary.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, BrandSecondary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = null,
                                        tint = AccentYellow,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "🎁 شما توسط ${if (initialReferrerName.isNotBlank()) initialReferrerName else "معرف رسمی"} دعوت شده‌اید و پس از ثبت‌نام، ۲۰٪ تخفیف ویژه دریافت می‌کنید",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            lineHeight = 20.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "کد معرف قفل شده و قابل تغییر نمی‌باشد.",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AccentYellow
                                        )
                                    }
                                }
                            }
                        }

                        // Referral Code (Locked if via link, editable otherwise)
                        OutlinedTextField(
                            value = referralCodeInput,
                            onValueChange = {
                                if (!isReferralLocked) referralCodeInput = it.uppercase()
                            },
                            enabled = !isReferralLocked,
                            label = { Text(if (isReferralLocked) "کد معرف (قفل شده)" else "کد معرفی (اختیاری)") },
                            placeholder = { Text("مثلاً VR-98421") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isReferralLocked) Icons.Default.Lock else Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = if (isReferralLocked) Color.Gray else AccentOrange
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_referral_code_input"),
                            shape = RoundedCornerShape(12.dp),
                            supportingText = {
                                Text(
                                    text = when {
                                        isReferralLocked -> "شما توسط ${if (initialReferrerName.isNotBlank()) initialReferrerName else "معرف شما"} دعوت شده‌اید."
                                        role == UserRole.AGENT -> "اگر کد معرفی دارید، وارد کنید (پاداش نقدی به معرف تعلق می‌گیرد)."
                                        else -> "اگر کد معرفی دارید، وارد کنید (یا خالی بگذارید)."
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isReferralLocked) AccentYellow else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }

            // PROVINCE AND CITY SELECTOR (Issue 7 & 10)
            item {
                ProvinceCitySelector(
                    selectedProvince = selectedProvince,
                    selectedCity = selectedCity,
                    onSelect = { prov, cty ->
                        selectedProvince = prov
                        selectedCity = cty
                    }
                )
            }

            // Error display
            errorMessage?.let { err ->
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Submit Registration Button
            item {
                Button(
                    onClick = {
                        if (fullName.trim().isEmpty()) {
                            errorMessage = "لطفاً نام و نام خانوادگی خود را وارد نمایید."
                            return@Button
                        }
                        if (phoneNumber.trim().length < 10) {
                            errorMessage = "لطفاً شماره موبایل معتبر ۱۱ رقمی وارد نمایید."
                            return@Button
                        }
                        if (role == UserRole.AGENT && agencyName.trim().isEmpty()) {
                            agencyName = "املاک مستقل $fullName"
                        }

                        // Register and generate referral code
                        val code = repository.registerUser(
                            name = fullName.trim(),
                            phone = phoneNumber.trim(),
                            role = role,
                            province = selectedProvince,
                            city = selectedCity,
                            agency = agencyName.trim(),
                            whatsapp = whatsappNumber.trim(),
                            telegram = telegramId.trim(),
                            referralCodeEntered = referralCodeInput.trim()
                        )

                        generatedReferralCode = code
                        showRegistrationSuccessDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_submit_registration")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تکمیل ثبت‌نام و ورود به پنل",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // TAB 1: QUICK OTP LOGIN
        if (selectedTabIndex == 1) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("شماره همراه") },
                            placeholder = { Text("۰۹۱۲۳۴۵۶۷۸۹") },
                            leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AccentOrange) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("quick_phone_input"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isOtpSent
                        )

                        AnimatedVisibility(visible = isOtpSent) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { otpCode = it },
                                    label = { Text("کد تایید ۵ رقمی") },
                                    placeholder = { Text("۵۴۳۲۱") },
                                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = AccentYellow) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().testTag("quick_otp_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { otpCode = "54321" },
                                        color = BrandGold.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "کد تستی (۵۴۳۲۱)",
                                            color = BrandGold,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }

                                    if (countdown > 0) {
                                        Text(
                                            text = "ارسال مجدد تا ${PersianUtils.toPersianDigits(countdown)} ثانیه دیگر",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        TextButton(onClick = {
                                            countdown = 60
                                            otpCode = ""
                                        }) {
                                            Text("ارسال مجدد کد", color = AccentOrange, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        errorMessage?.let { err ->
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = err,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (!isOtpSent) {
                                    if (phoneNumber.length < 10) {
                                        errorMessage = "لطفاً شماره موبایل معتبر وارد کنید"
                                        return@Button
                                    }
                                    isOtpSent = true
                                    errorMessage = null
                                } else {
                                    if (otpCode.length < 4) {
                                        errorMessage = "کد تایید معتبر نیست (کد تستی: ۵۴۳۲۱)"
                                        return@Button
                                    }
                                    onLoginSuccess(phoneNumber, role)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text(
                                text = if (!isOtpSent) "دریافت کد تایید پیامکی" else "تایید و ورود",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // REGISTRATION SUCCESS DIALOG (Displaying Personal Referral Code - Issue 7)
    if (showRegistrationSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showRegistrationSuccessDialog = false
                onLoginSuccess(phoneNumber, role)
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BrandSecondary.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandSecondary)
                        }
                    }
                    Text("ثبت‌نام با موفقیت انجام شد!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "حساب کاربری شما با مشخصات کامل ثبت گردید و دسترسی به پنل اختصاصی فعال شد.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, AccentYellow.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "کد معرف اختصاصی شما:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = generatedReferralCode,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentYellow
                            )
                            Text(
                                text = "این کد را به دوستان و همکاران خود بدهید تا از خریدهای آن‌ها ۲۰٪ پورسانت نقدی دریافت کنید.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRegistrationSuccessDialog = false
                        onLoginSuccess(phoneNumber, role)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ورود به پنل کاربری", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
