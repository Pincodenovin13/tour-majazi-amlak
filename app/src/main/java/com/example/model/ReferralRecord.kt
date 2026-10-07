package com.example.model

data class ReferralRecord(
    val id: String,
    val referrerCode: String,
    val referrerName: String,
    val referredAgentName: String,
    val referredAgentPhone: String,
    val registeredDateJalali: String,
    val initialBonusToman: Long = 200_000L,
    val totalPurchasedAmountToman: Long = 0L,
    val totalCommissionEarnedToman: Long = 200_000L
)
