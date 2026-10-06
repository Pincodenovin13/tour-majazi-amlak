package com.example.model

enum class AdStatus(val titleFa: String) {
    ACTIVE("فعال"),
    EXPIRED("منقضی شده"),
    PENDING("در انتظار تایید")
}

enum class PaymentStatus(val titleFa: String) {
    PAID("پرداخت شده"),
    UNPAID("پرداخت نشده")
}

data class AdItem(
    val id: String,
    val agentId: String,
    val agentName: String,
    val propertyId: String?,
    val propertyTitle: String,
    val city: String,
    val province: String,
    val durationDays: Int, // 1, 3, 5, 10, 20, 30
    val price: Long,
    val startDateJalali: String,
    val endDateJalali: String,
    val bannerDrawableRes: Int,
    val status: AdStatus = AdStatus.ACTIVE,
    val paymentStatus: PaymentStatus = PaymentStatus.PAID,
    val viewCount: Int = 0,
    val contactCount: Int = 0,
    val isPlatformAd: Boolean = false, // Our own app ads
    val platformAdSubtitle: String? = null,
    val platformActionText: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
