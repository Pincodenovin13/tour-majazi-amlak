package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.SubscriptionPlan
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandGold
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    repository: AppRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeSub by repository.activeSubscription.collectAsState()
    val plans = repository.availablePlans

    var selectedPlanId by remember { mutableStateOf(plans.firstOrNull { it.isPopular }?.id ?: plans.first().id) }
    var showPaymentModal by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentSuccessDialog by remember { mutableStateOf(false) }

    val currentSelectedPlan = plans.first { it.id == selectedPlanId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "طرح‌های اشتراک مشاورین املاک",
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
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "مبلغ قابل پرداخت (${currentSelectedPlan.title}):",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = PersianUtils.formatPrice(currentSelectedPlan.price),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = BrandSecondary
                            )
                        }

                        Button(
                            onClick = { showPaymentModal = true },
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("btn_pay_subscription"),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "پرداخت و فعال‌سازی",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                .testTag("subscription_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Subscription Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BrandPrimary.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandPrimary,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = BrandGold,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "اشتراک فعلی: ${activeSub?.planTitle ?: "غیرفعال"}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${PersianUtils.toPersianDigits(activeSub?.remainingDays ?: 0)} روز از اعتبار شما باقی مانده است.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Description Header
            item {
                Column {
                    Text(
                        text = "پلن مورد نظر خود را انتخاب کنید:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "با خرید هر پلن، املاک شما با نشان تور ۳۶۰ برجسته و بازدیدها چند برابر می‌شود.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3 Subscription Plans (1 ماهه، 3 ماهه، 6 ماهه)
            items(plans) { plan ->
                SubscriptionPlanCard(
                    plan = plan,
                    isSelected = selectedPlanId == plan.id,
                    onSelect = { selectedPlanId = plan.id }
                )
            }

            // Guarantee & Trust Footer
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BrandSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "ضمانت بازگشت وجه و پشتیبانی اختصاصی",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "امکان ارتقای پلن در هر زمان با کسر هزینه باقیمانده پلن قبلی وجود دارد.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Payment Gateway Simulation Dialog
    if (showPaymentModal) {
        AlertDialog(
            onDismissRequest = { if (!isProcessingPayment) showPaymentModal = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = BrandSecondary)
                    Text("درگاه پرداخت اینترنتی امن", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "در حال انتقال به درگاه پرداخت شاپرک جهت پرداخت ${currentSelectedPlan.title}:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("مبلغ تراکنش:", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    PersianUtils.formatPrice(currentSelectedPlan.price),
                                    fontWeight = FontWeight.Bold,
                                    color = BrandSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("پذیرنده:", style = MaterialTheme.typography.bodySmall)
                                Text("تور مجازی املاک ایران", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (isProcessingPayment) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BrandSecondary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("در حال تایید تراکنش و فعال‌سازی...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isProcessingPayment = true
                        // Simulate network call
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            repository.activateSubscription(currentSelectedPlan)
                            isProcessingPayment = false
                            showPaymentModal = false
                            paymentSuccessDialog = true
                        }, 1200)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                    enabled = !isProcessingPayment
                ) {
                    Text("پرداخت اینترنتی")
                }
            },
            dismissButton = {
                if (!isProcessingPayment) {
                    TextButton(onClick = { showPaymentModal = false }) {
                        Text("انصراف")
                    }
                }
            }
        )
    }

    // Success Dialog
    if (paymentSuccessDialog) {
        AlertDialog(
            onDismissRequest = { paymentSuccessDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BrandSecondary)
                    Text("اشتراک با موفقیت فعال شد!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "تبریک! ${currentSelectedPlan.title} به مدت ${PersianUtils.toPersianDigits(currentSelectedPlan.durationMonths * 30)} روز برای حساب مشاور شما فعال گردید. اکنون می‌توانید تورهای مجازی ۳۶۰ خود را ایجاد نمایید.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { paymentSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("ورود به داشبورد")
                }
            }
        )
    }
}

@Composable
private fun SubscriptionPlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (plan.isPopular) BrandGold else BrandPrimary
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        },
        label = "sub_border_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onSelect)
            .testTag("plan_card_${plan.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                if (plan.isPopular) BrandGold.copy(alpha = 0.07f) else BrandPrimary.copy(alpha = 0.05f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Title, Popular badge, and Radio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    plan.badge?.let { badgeText ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentYellow
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = badgeText,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = if (plan.isPopular) BrandGold else BrandPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = PersianUtils.formatPrice(plan.price),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (plan.isPopular) BrandGold else BrandPrimary
                )

                plan.originalPrice?.let { orig ->
                    Text(
                        text = PersianUtils.formatPrice(orig),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Capacity tag
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "ظرفیت: ${plan.tourCapacity}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            // Feature list
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                plan.features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (plan.isPopular) BrandGold else BrandSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
