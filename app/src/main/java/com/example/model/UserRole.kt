package com.example.model

enum class UserRole(val titleFa: String, val subtitleFa: String) {
    AGENT(
        titleFa = "مشاور املاک",
        subtitleFa = "ثبت فایل، ساخت تور ۳۶۰، ارتقای اشتراک و مدیریت بازدیدها"
    ),
    REGULAR_USER(
        titleFa = "کاربر عادی",
        subtitleFa = "بازدید مجازی ۳۶۰ درجه، اشتراک کد معرف و کسب درآمد پورسانت"
    )
}
