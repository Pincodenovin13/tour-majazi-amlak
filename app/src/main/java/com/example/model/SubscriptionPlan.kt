package com.example.model

data class SubscriptionPlan(
    val id: String,
    val title: String,
    val durationMonths: Int,
    val price: Long,
    val originalPrice: Long? = null,
    val discountPercent: Int = 0,
    val isPopular: Boolean = false,
    val badge: String? = null,
    val tourCapacity: String,
    val features: List<String>
)

data class ActiveSubscription(
    val planId: String,
    val planTitle: String,
    val startDate: Long,
    val remainingDays: Int,
    val isActive: Boolean = true
)
