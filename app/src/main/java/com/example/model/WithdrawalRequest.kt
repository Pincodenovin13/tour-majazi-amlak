package com.example.model

enum class WithdrawalStatus(val titleFa: String) {
    PENDING("در حال بررسی"),
    APPROVED("تایید و واریز شد"),
    REJECTED("رد شده")
}

data class WithdrawalRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val amount: Long,
    val cardNumber: String,
    val shebaNumber: String,
    val description: String,
    val requestDateJalali: String,
    val status: WithdrawalStatus = WithdrawalStatus.PENDING,
    val adminNote: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
