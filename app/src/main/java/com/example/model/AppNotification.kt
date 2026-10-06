package com.example.model

enum class NotificationType {
    VISIT_REQUEST,
    COMMISSION_EARNED,
    SUBSCRIPTION_EXPIRING,
    WITHDRAWAL_UPDATE,
    GENERAL
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: NotificationType,
    val isRead: Boolean = false,
    val targetRole: UserRole? = null // null = all users
)

enum class VisitStatus(val titleFa: String) {
    PENDING("در انتظار هماهنگی"),
    APPROVED("تایید شد"),
    REJECTED("لغو شد")
}

data class VisitRequest(
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val requesterName: String,
    val requesterPhone: String,
    val preferredDateJalali: String,
    val preferredTime: String,
    val agentId: String,
    val status: VisitStatus = VisitStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)
