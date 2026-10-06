package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.BrandGold
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    role: UserRole,
    onLoginSuccess: (mobile: String, role: UserRole) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phoneNumber by remember { mutableStateOf("09123456789") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(60) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isOtpSent) {
        if (isOtpSent) {
            countdown = 60
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Bar with back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .testTag("login_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward, // RTL back
                        contentDescription = "بازگشت",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (role == UserRole.AGENT) BrandPrimary.copy(alpha = 0.12f) else BrandSecondary.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (role == UserRole.AGENT) Icons.Default.BusinessCenter else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (role == UserRole.AGENT) BrandPrimary else BrandSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "ورود به عنوان ${role.titleFa}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (role == UserRole.AGENT) BrandPrimary else BrandSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = if (!isOtpSent) "ورود با شماره موبایل" else "کد تایید ۵ رقمی",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (!isOtpSent)
                    "برای ورود یا ثبت‌نام در سامانه تور مجازی املاک، شماره همراه خود را وارد کنید."
                else
                    "کد تایید پیامک شده به شماره ${PersianUtils.toPersianDigits(phoneNumber)} را وارد کنید.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Mobile Input
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= 11) {
                        phoneNumber = it
                        errorMessage = null
                    }
                },
                label = { Text("شماره موبایل") },
                placeholder = { Text("۰۹۱۲۳۴۵۶۷۸۹") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = BrandPrimary
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mobile_number_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    focusedLabelColor = BrandPrimary
                ),
                enabled = !isOtpSent
            )

            Spacer(modifier = Modifier.height(16.dp))

            // OTP Input (when sent)
            AnimatedVisibility(visible = isOtpSent) {
                Column {
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = {
                            if (it.length <= 5) {
                                otpCode = it
                                errorMessage = null
                            }
                        },
                        label = { Text("کد تایید ۵ رقمی") },
                        placeholder = { Text("مثلاً ۵۴۳۲۱") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = BrandSecondary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_code_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandSecondary,
                            focusedLabelColor = BrandSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Demo autofill shortcut for fast testing
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
                                Text("ارسال مجدد کد", color = BrandPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Error display
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Bottom Action Button
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (!isOtpSent) {
                        if (phoneNumber.length < 10) {
                            errorMessage = "لطفاً شماره موبایل معتبر وارد کنید"
                        } else {
                            isOtpSent = true
                            otpCode = "54321" // autofill for convenient test
                        }
                    } else {
                        if (otpCode.length < 5) {
                            errorMessage = "لطفاً کد تایید ۵ رقمی را کامل وارد نمایید"
                        } else {
                            isLoading = true
                            onLoginSuccess(phoneNumber, role)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("submit_login_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (role == UserRole.AGENT) BrandPrimary else BrandSecondary
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (!isOtpSent) "ارسال کد تایید پیامکی" else "تایید و ورود به اپلیکیشن",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "با ورود به سامانه، شرایط و قوانین تور مجازی املاک را می‌پذیرید.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
