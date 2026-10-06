package com.example.model

data class CustomerInquiry(
    val id: String,
    val customerName: String,
    val customerPhone: String,
    val propertyTitle: String,
    val propertyId: String?,
    val channel: String, // "درخواست بازدید حضوری", "تماس تلفنی", "واتساپ", "تلگرام", "بله"
    val dateFa: String,
    val timeFa: String,
    val details: String,
    val isReviewed: Boolean = false,
    val isApproved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
