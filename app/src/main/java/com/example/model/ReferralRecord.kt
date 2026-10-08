package com.example.model

enum class ReferralPlanDuration(val months: Int, val titleFa: String, val commissionPercent: Int) {
    ONE_MONTH(1, "پلن ۱ ماهه", 20),
    TWO_MONTHS(2, "پلن ۲ ماهه", 15),
    THREE_MONTHS(3, "پلن ۳ ماهه", 10);

    companion object {
        fun fromMonths(months: Int): ReferralPlanDuration = when (months) {
            1 -> ONE_MONTH
            2 -> TWO_MONTHS
            3 -> THREE_MONTHS
            else -> ONE_MONTH
        }
    }
}

enum class ReferralStatus(val titleFa: String) {
    TRIAL("دوره آزمایشی"),
    ACTIVE("طرح فعال"),
    EXPIRED("منقضی شده")
}

data class ReferralRecord(
    val id: String,
    val referrerCode: String,
    val referrerName: String,
    val referredAgentName: String,
    val referredAgentPhone: String,
    val referredCity: String = "ساری",
    val registeredDateJalali: String,
    val planChosen: ReferralPlanDuration = ReferralPlanDuration.ONE_MONTH,
    val status: ReferralStatus = ReferralStatus.ACTIVE,
    val initialBonusToman: Long = 200_000L,
    val totalPurchasedAmountToman: Long = 1_500_000L,
    val totalCommissionEarnedToman: Long = 300_000L
)
