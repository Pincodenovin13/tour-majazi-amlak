package com.example.model

enum class UserRole(val titleFa: String, val subtitleFa: String) {
    AGENT(
        titleFa = "پنل مشاورین املاک",
        subtitleFa = "ثبت فایل، ساخت تور ۳۶۰، ارتقای اشتراک و مدیریت بازدیدها"
    ),
    REGULAR_USER(
        titleFa = "پنل مشاورین آزاد و معرفین",
        subtitleFa = "کد معرف اختصاصی، معرفی مشاورین و دریافت ۲۰٪ پورسانت نقدی"
    ),
    PUBLIC_VISITOR(
        titleFa = "بازدید عموم",
        subtitleFa = "مشاهده رایگان تورهای ۳۶۰ درجه املاک و تبلیغات شهری سراسر کشور"
    )
}
