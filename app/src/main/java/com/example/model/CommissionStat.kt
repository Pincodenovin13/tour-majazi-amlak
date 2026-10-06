package com.example.model

enum class CommissionStatus(val titleFa: String) {
    PAID("پرداخت شده"),
    PENDING("در انتظار"),
    BURNED("سوخته")
}

enum class ReferrerTier(val titleFa: String, val subtitleFa: String) {
    STANDARD("عادی", "کمتر از ۹ مشاور"),
    ACTIVE("فعال", "۹ الی ۱۴ مشاور فعال"),
    SPECIAL("ویژه", "۱۵ مشاور به بالا")
}

data class ReferralTransaction(
    val id: String,
    val title: String,
    val amount: Long,
    val dateFa: String,
    val isDeposit: Boolean = true, // true = سود پورسانت, false = برداشت نقدی
    val status: CommissionStatus = CommissionStatus.PAID,
    val planName: String? = null,
    val planPrice: Long? = null,
    val commissionPercent: Int? = null,
    val agentName: String? = null,
    val burnReason: String? = null
)

data class CommissionStat(
    val referralCode: String = "VR-98421",
    val totalEarnings: Long = 1_875_000L,
    val availableBalance: Long = 1_175_000L,
    val pendingBalance: Long = 375_000L,
    val burnedBalance: Long = 300_000L,
    val successfulReferralsCount: Int = 6, // مثلاً ۶ از ۹
    val targetCount: Int = 9,
    val currentTier: ReferrerTier = ReferrerTier.STANDARD,
    val daysRemainingInWindow: Int = 8,
    val totalWindowDays: Int = 15,
    val transactions: List<ReferralTransaction> = listOf()
)
