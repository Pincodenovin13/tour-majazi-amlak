package com.example.model

enum class PropertyStatus(val titleFa: String) {
    ACTIVE_TOUR("تور فعال ۳۶۰"),
    PROCESSING("در حال پردازش"),
    DRAFT("پیش‌نویس")
}

data class PropertyItem(
    val id: String,
    val title: String,
    val propertyType: String, // آپارتمان، ویلا، دفتر کار، تجاری
    val transactionType: String, // فروش، رهن و اجاره
    val price: Long,
    val areaSqMeters: Int,
    val rooms: Int,
    val city: String,
    val neighborhood: String,
    val address: String,
    val description: String,
    val features: List<String> = emptyList(),
    val scenes: List<TourScene> = emptyList(),
    val viewsCount: Int = 0,
    val inquiriesCount: Int = 0,
    val status: PropertyStatus = PropertyStatus.ACTIVE_TOUR,
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
