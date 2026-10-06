package com.example.data

import com.example.R
import com.example.model.ActiveSubscription
import com.example.model.AdItem
import com.example.model.AdStatus
import com.example.model.AppNotification
import com.example.model.CommissionStat
import com.example.model.CommissionStatus
import com.example.model.CustomerInquiry
import com.example.model.NotificationType
import com.example.model.PaymentStatus
import com.example.model.PropertyItem
import com.example.model.PropertyStatus
import com.example.model.ReferralTransaction
import com.example.model.ReferrerTier
import com.example.model.SubscriptionPlan
import com.example.model.TourHotspot
import com.example.model.TourScene
import com.example.model.UserRole
import com.example.model.VisitRequest
import com.example.model.VisitStatus
import com.example.model.WithdrawalRequest
import com.example.model.WithdrawalStatus
import com.example.ui.util.JalaliDateHelper
import com.example.ui.util.PersianUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CityInfo(val name: String, val province: String, val tier: Int) // tier 1: کلانشهر, 2: مراکز استان, 3: سایر

class AppRepository {

    // Current User Session
    private val _currentUserRole = MutableStateFlow(UserRole.AGENT)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _userPhone = MutableStateFlow("۰۹۱۲۳۴۵۶۷۸۹")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userName = MutableStateFlow("مهندس کیان آریا")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _agencyName = MutableStateFlow("املاک مدرن شمیران")
    val agencyName: StateFlow<String> = _agencyName.asStateFlow()

    private val _referralCodeInput = MutableStateFlow("VR-98421") // Agent's referrer code
    val referralCodeInput: StateFlow<String> = _referralCodeInput.asStateFlow()

    // Agent Cover Image (Real Estate Agency Office) & Profile Avatar (Agent Face)
    private val _agentCoverRes = MutableStateFlow(R.drawable.img_tour_sample)
    val agentCoverRes: StateFlow<Int> = _agentCoverRes.asStateFlow()

    private val _agentAvatarRes = MutableStateFlow(R.drawable.img_tour_bedroom)
    val agentAvatarRes: StateFlow<Int> = _agentAvatarRes.asStateFlow()

    // Agent Online / Offline live status
    private val _isAgentOnline = MutableStateFlow(true)
    val isAgentOnline: StateFlow<Boolean> = _isAgentOnline.asStateFlow()

    // Agent's dedicated referral code for introducing colleague agents
    private val _agentReferralCode = MutableStateFlow("VR-AG789")
    val agentReferralCode: StateFlow<String> = _agentReferralCode.asStateFlow()

    // Subscription Plans (New Prices: 1M=1.5M, 2M=2.5M, 3M=4M)
    val availablePlans = listOf(
        SubscriptionPlan(
            id = "plan_1_month",
            title = "پلن ۱ ماهه",
            durationMonths = 1,
            price = 1_500_000L,
            badge = null,
            tourCapacity = "۵ تور مجازی ۳۶۰ درجه",
            features = listOf(
                "۵ تور مجازی ۳۶۰ درجه اختصاصی",
                "کیفیت تصویر Full HD",
                "پشتیبانی آنلاین تیکتی",
                "گزارش هفتگی بازدیدکنندگان"
            )
        ),
        SubscriptionPlan(
            id = "plan_2_month",
            title = "پلن ۲ ماهه",
            durationMonths = 2,
            price = 2_500_000L,
            originalPrice = 3_000_000L,
            discountPercent = 17,
            isPopular = true,
            badge = "محبوب",
            tourCapacity = "۱۵ تور مجازی ۳۶۰ درجه",
            features = listOf(
                "۱۵ تور مجازی ۳۶۰ درجه با کیفیت 4K",
                "هات‌اسپات‌های هوشمند جابجایی بین اتاق‌ها",
                "نشان تاییدیه مشاور برگزیده",
                "پشتیبانی مستقیم"
            )
        ),
        SubscriptionPlan(
            id = "plan_3_month",
            title = "پلن ۳ ماهه",
            durationMonths = 3,
            price = 4_000_000L,
            originalPrice = 4_500_000L,
            discountPercent = 11,
            badge = "به‌صرفه‌ترین",
            tourCapacity = "تورهای نامحدود ۳۶۰ درجه",
            features = listOf(
                "ایجاد تور مجازی ۳۶۰ درجه نامحدود",
                "کیفیت خیره‌کننده 8K پانوراما",
                "یک نوبت اسکن و عکاسی رایگان در محل",
                "برندینگ اختصاصی آژانس و حالت VR",
                "پیشنهاد ویژه و به‌صرفه‌ترین پلن"
            )
        )
    )

    private val _activeSubscription = MutableStateFlow<ActiveSubscription?>(
        ActiveSubscription(
            planId = "plan_2_month",
            planTitle = "پلن ۲ ماهه (محبوب)",
            startDate = System.currentTimeMillis() - (10L * 24 * 3600 * 1000),
            remainingDays = 50,
            isActive = true
        )
    )
    val activeSubscription: StateFlow<ActiveSubscription?> = _activeSubscription.asStateFlow()

    // 2.1 — Iranian Cities List
    val iranianCities = listOf(
        CityInfo("ساری", "مازندران", 2),
        CityInfo("تهران", "تهران", 1),
        CityInfo("مشهد", "خراسان رضوی", 1),
        CityInfo("اصفهان", "اصفهان", 1),
        CityInfo("شیراز", "فارس", 1),
        CityInfo("تبریز", "آذربایجان شرقی", 1),
        CityInfo("کرج", "البرز", 1),
        CityInfo("رشت", "گیلان", 2),
        CityInfo("بابل", "مازندران", 3),
        CityInfo("آمل", "مازندران", 3),
        CityInfo("اهواز", "خوزستان", 1),
        CityInfo("قم", "قم", 2),
        CityInfo("کرمان", "کرمان", 2),
        CityInfo("یزد", "یزد", 2),
        CityInfo("کرمانشاه", "کرمانشاه", 2),
        CityInfo("ارومیه", "آذربایجان غربی", 2),
        CityInfo("گرگان", "گلستان", 2),
        CityInfo("قزوین", "قزوین", 2),
        CityInfo("همدان", "همدان", 2),
        CityInfo("بندرعباس", "هرمزگان", 2),
        CityInfo("زنجان", "زنجان", 2),
        CityInfo("سنندج", "کردستان", 2),
        CityInfo("خرم‌آباد", "لرستان", 2),
        CityInfo("اراک", "مرکزی", 2),
        CityInfo("بوشهر", "بوشهر", 2),
        CityInfo("زاهدان", "سیستان و بلوچستان", 2),
        CityInfo("بجنورد", "خراسان شمالی", 3),
        CityInfo("بیرجند", "خراسان جنوبی", 3),
        CityInfo("ایلام", "ایلام", 3),
        CityInfo("سمنان", "سمنان", 3),
        CityInfo("یاسوج", "کهگیلویه و بویراحمد", 3),
        CityInfo("شهرکرد", "چهارمحال و بختیاری", 3)
    )

    // Duration options for city banner ads
    data class AdDurationOption(val days: Int, val label: String, val basePrice: Long)
    val adDurationOptions = listOf(
        AdDurationOption(1, "۱ روزه", 80_000L),
        AdDurationOption(3, "۳ روزه", 210_000L),
        AdDurationOption(5, "۵ روزه", 320_000L),
        AdDurationOption(10, "۱۰ روزه", 550_000L),
        AdDurationOption(20, "۲۰ روزه", 950_000L),
        AdDurationOption(30, "۳۰ روزه", 1_280_000L)
    )

    // 2.1 — Tiered Pricing for City Banner Ads (Slider 1 to 30 days)
    // 1 to 10 days: 150,000 / day
    // 11 to 20 days: 130,000 / day
    // 21 to 30 days: 110,000 / day
    // City Multiplier: Tehran = 1.5x, others = 1.0x
    fun calculateAdPrice(city: String, days: Int): Long {
        val dailyRate = when {
            days <= 10 -> 150_000L
            days <= 20 -> 130_000L
            else -> 110_000L
        }
        val isTehran = city == "تهران" || city.contains("تهران")
        val multiplier = if (isTehran) 1.5 else 1.0
        return (days * dailyRate * multiplier).toLong()
    }

    // Properties
    private val _properties = MutableStateFlow<List<PropertyItem>>(createInitialProperties())
    val properties: StateFlow<List<PropertyItem>> = _properties.asStateFlow()

    // 2.4 — City Ads Database
    private val _ads = MutableStateFlow<List<AdItem>>(createInitialAds())
    val ads: StateFlow<List<AdItem>> = _ads.asStateFlow()

    // 2.3 — Our Own App Ads (Managed by admin)
    private val _platformAds = MutableStateFlow<List<AdItem>>(createInitialPlatformAds())
    val platformAds: StateFlow<List<AdItem>> = _platformAds.asStateFlow()

    // 3.1 & 3.2 — Referral & Commission System (Exact Rules)
    private val _commissionStat = MutableStateFlow(
        CommissionStat(
            referralCode = "VR-98421",
            totalEarnings = 1_875_000L,
            availableBalance = 1_175_000L,
            pendingBalance = 375_000L,
            burnedBalance = 300_000L,
            successfulReferralsCount = 6, // مثلاً ۶ از ۹
            targetCount = 9,
            currentTier = ReferrerTier.STANDARD,
            daysRemainingInWindow = 8,
            totalWindowDays = 15,
            transactions = listOf(
                ReferralTransaction(
                    id = "tx_1",
                    title = "پورسانت پلن ۳ ماهه (۴M) — مشاور املاک نیاوران",
                    amount = 400_000L,
                    dateFa = "۱۴۰۳/۰۷/۱۵",
                    isDeposit = true,
                    status = CommissionStatus.PAID,
                    planName = "پلن ۳ ماهه (۴,۰۰۰,۰۰۰ تومان)",
                    planPrice = 4_000_000L,
                    commissionPercent = 10,
                    agentName = "مهندس شایان نیاورانی"
                ),
                ReferralTransaction(
                    id = "tx_2",
                    title = "پورسانت پلن ۲ ماهه (۲.۵M) — مشاور املاک طبرستان ساری",
                    amount = 375_000L,
                    dateFa = "۱۴۰۳/۰۷/۱۴",
                    isDeposit = true,
                    status = CommissionStatus.PAID,
                    planName = "پلن ۲ ماهه (۲,۵۰۰,۰۰۰ تومان)",
                    planPrice = 2_500_000L,
                    commissionPercent = 15,
                    agentName = "مهندس فرید طبرستانی"
                ),
                ReferralTransaction(
                    id = "tx_3",
                    title = "پورسانت پلن ۲ ماهه (۲.۵M) — مشاور املاک البرز کرج",
                    amount = 375_000L,
                    dateFa = "۱۴۰۳/۰۷/۱۲",
                    isDeposit = true,
                    status = CommissionStatus.PENDING,
                    planName = "پلن ۲ ماهه (۲,۵۰۰,۰۰۰ تومان)",
                    planPrice = 2_500_000L,
                    commissionPercent = 15,
                    agentName = "مشاور املاک پایتخت البرز"
                ),
                ReferralTransaction(
                    id = "tx_4",
                    title = "پورسانت پلن ۱ ماهه (۱.۵M) — مشاور املاک ساحل خزر",
                    amount = 300_000L,
                    dateFa = "۱۴۰۳/۰۷/۰۱",
                    isDeposit = true,
                    status = CommissionStatus.BURNED,
                    planName = "پلن ۱ ماهه (۱,۵۰۰,۰۰۰ تومان)",
                    planPrice = 1_500_000L,
                    commissionPercent = 20,
                    agentName = "مشاور املاک ساحل",
                    burnReason = "عدم تمدید اشتراک توسط مشاور — سوخته"
                ),
                ReferralTransaction(
                    id = "tx_5",
                    title = "تسویه بانکی شبا (پایا) — بانک ملت (کسر ۱۰٪ مالیات)",
                    amount = 450_000L,
                    dateFa = "۱۴۰۳/۰۶/۲۸",
                    isDeposit = false,
                    status = CommissionStatus.PAID
                )
            )
        )
    )
    val commissionStat: StateFlow<CommissionStat> = _commissionStat.asStateFlow()

    // 3.3 — Withdrawal Requests (Admin Approval)
    private val _withdrawalRequests = MutableStateFlow<List<WithdrawalRequest>>(createInitialWithdrawals())
    val withdrawalRequests: StateFlow<List<WithdrawalRequest>> = _withdrawalRequests.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<AppNotification>>(createInitialNotifications())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Visit Requests
    private val _visitRequests = MutableStateFlow<List<VisitRequest>>(createInitialVisits())
    val visitRequests: StateFlow<List<VisitRequest>> = _visitRequests.asStateFlow()

    // Customer Inquiries (Requests from buyers/renters to real estate agents)
    private val _customerInquiries = MutableStateFlow<List<CustomerInquiry>>(createInitialCustomerInquiries())
    val customerInquiries: StateFlow<List<CustomerInquiry>> = _customerInquiries.asStateFlow()

    // Stitched Tour Scenes from Real 360 Camera
    private val _createdTourScenes = MutableStateFlow<List<TourScene>>(emptyList())
    val createdTourScenes: StateFlow<List<TourScene>> = _createdTourScenes.asStateFlow()

    fun addStitchedScene(scene: TourScene) {
        _createdTourScenes.update { it + scene }
    }

    fun clearCreatedTourScenes() {
        _createdTourScenes.value = emptyList()
    }

    // Authentication & Roles
    fun setRole(role: UserRole) {
        _currentUserRole.value = role
        if (role == UserRole.REGULAR_USER) {
            _userName.value = "علی حسینی (کاربر عادی)"
        } else {
            _userName.value = "مهندس کیان آریا"
        }
    }

    fun login(phone: String, role: UserRole) {
        _userPhone.value = phone
        _currentUserRole.value = role
    }

    fun updateProfile(name: String, agency: String) {
        _userName.value = name
        _agencyName.value = agency
    }

    fun addProperty(property: PropertyItem) {
        _properties.update { listOf(property) + it }
    }

    fun deleteProperty(propertyId: String) {
        _properties.update { list -> list.filterNot { it.id == propertyId } }
    }

    fun markPropertySold(propertyId: String) {
        _properties.update { list -> list.filterNot { it.id == propertyId } }
        addNotification(
            title = "ملک به عنوان فروخته شده ثبت و حذف شد",
            message = "فایل مورد نظر از سیستم خارج شد و دیگر در لیست عمومی خریداران نمایش داده نمی‌شود.",
            type = NotificationType.GENERAL,
            targetRole = UserRole.AGENT
        )
    }

    fun getPropertyById(propertyId: String): PropertyItem? {
        return _properties.value.find { it.id == propertyId }
    }

    fun updateAgentCover(resId: Int) {
        _agentCoverRes.value = resId
    }

    fun updateAgentAvatar(resId: Int) {
        _agentAvatarRes.value = resId
    }

    fun toggleAgentOnline() {
        _isAgentOnline.value = !_isAgentOnline.value
    }

    fun isAgentActive(): Boolean {
        val sub = _activeSubscription.value ?: return false
        return sub.isActive && sub.remainingDays > 0
    }

    fun isSubscriptionNearExpiry(): Boolean {
        val sub = _activeSubscription.value ?: return false
        return sub.isActive && sub.remainingDays in 1..2
    }

    // Customer Inquiries Notification & Tracking for Agent
    fun notifyAgentOfCustomerInquiry(
        propertyTitle: String,
        channel: String,
        customerInfo: String = "خریدار / متقاضی جدید",
        customerPhone: String = "۰۹۱۲۹۹۹۸۸۷۷"
    ) {
        val newInquiry = CustomerInquiry(
            id = "inq_${System.currentTimeMillis()}",
            customerName = customerInfo,
            customerPhone = customerPhone,
            propertyTitle = propertyTitle,
            propertyId = null,
            channel = channel,
            dateFa = JalaliDateHelper.formatJalaliDateOnly(System.currentTimeMillis()),
            timeFa = JalaliDateHelper.formatTimeOnly(System.currentTimeMillis()),
            details = "درخواست $channel برای ملک $propertyTitle",
            isReviewed = false
        )
        _customerInquiries.update { listOf(newInquiry) + it }

        addNotification(
            title = "مشتری جدید برای ملک شما!",
            message = "$customerInfo برای ملک «$propertyTitle» از طریق «$channel» درخواست ارتباط یا بازدید ثبت کرد. لطفاً بررسی فرمایید.",
            type = NotificationType.VISIT_REQUEST,
            targetRole = UserRole.AGENT
        )
    }

    fun markInquiryReviewed(inquiryId: String) {
        _customerInquiries.update { list ->
            list.map { if (it.id == inquiryId) it.copy(isReviewed = true) else it }
        }
    }

    fun approveInquiryVisit(inquiryId: String) {
        _customerInquiries.update { list ->
            list.map { if (it.id == inquiryId) it.copy(isReviewed = true, isApproved = true) else it }
        }
        addNotification(
            title = "نوبت بازدید تایید شد",
            message = "هماهنگی بازدید برای متقاضی تایید گردید.",
            type = NotificationType.VISIT_REQUEST,
            targetRole = UserRole.AGENT
        )
    }

    // Set remaining days (for simulation of 2-day warning or deactivation)
    fun setSubscriptionRemainingDays(days: Int) {
        val cur = _activeSubscription.value
        if (cur != null) {
            val isActive = days > 0
            _activeSubscription.value = cur.copy(
                remainingDays = days,
                isActive = isActive
            )
            if (days in 1..2) {
                addNotification(
                    title = "هشدار تمدید اشتراک (۲ روز مانده)",
                    message = "تنها $days روز تا انقضای اشتراک مشاور شما باقی مانده است. در صورت عدم تمدید، به حالت دی‌اکتیو تبدیل شده و املاک شما از دید عموم مخفی خواهد شد.",
                    type = NotificationType.SUBSCRIPTION_EXPIRING,
                    targetRole = UserRole.AGENT
                )
            } else if (days <= 0) {
                addNotification(
                    title = "حساب شما دی‌اکتیو (غیرفعال) شد",
                    message = "مهلت اشتراک شما به پایان رسید و به حالت دی‌اکتیو تبدیل شدید. تا زمان فعال‌سازی طرح جدید، هیچ ملکی به مردم نمایش داده نمی‌شود.",
                    type = NotificationType.SUBSCRIPTION_EXPIRING,
                    targetRole = UserRole.AGENT
                )
            }
        }
    }

    // Colleague Agent Renewal Commission & Burning Rule
    // If agent is active -> receives commission
    // If agent is deactive -> commission is VOID/BURNED
    fun processColleagueAgentPlanPurchase(
        colleagueName: String,
        plan: SubscriptionPlan
    ) {
        val agentIsCurrentlyActive = isAgentActive()
        val (commissionPercent, commissionAmount) = when (plan.durationMonths) {
            1 -> 20 to 300_000L
            2 -> 15 to 375_000L
            3 -> 10 to 400_000L
            else -> 10 to (plan.price * 10 / 100)
        }

        val curComm = _commissionStat.value
        val dateFa = JalaliDateHelper.formatJalaliDateOnly(System.currentTimeMillis())

        if (agentIsCurrentlyActive) {
            // Active -> gets commission
            val newTx = ReferralTransaction(
                id = "tx_${System.currentTimeMillis()}",
                title = "پورسانت $commissionPercent٪ خرید ${plan.title} توسط همکار ($colleagueName)",
                amount = commissionAmount,
                dateFa = dateFa,
                isDeposit = true,
                status = CommissionStatus.PAID,
                planName = plan.title,
                planPrice = plan.price,
                commissionPercent = commissionPercent,
                agentName = colleagueName
            )
            val newCount = curComm.successfulReferralsCount + 1
            _commissionStat.value = curComm.copy(
                totalEarnings = curComm.totalEarnings + commissionAmount,
                availableBalance = curComm.availableBalance + commissionAmount,
                successfulReferralsCount = newCount,
                transactions = listOf(newTx) + curComm.transactions
            )
            addNotification(
                title = "واریز پورسانت معرفی همکار املاکی!",
                message = "مبلغ ${PersianUtils.formatPrice(commissionAmount)} بابت خرید ${plan.title} توسط همکار به موجودی شما افزوده شد.",
                type = NotificationType.COMMISSION_EARNED,
                targetRole = UserRole.AGENT
            )
        } else {
            // Deactive -> BURNS!
            val burnedTx = ReferralTransaction(
                id = "tx_${System.currentTimeMillis()}",
                title = "پورسانت سوخته خرید ${plan.title} توسط همکار ($colleagueName)",
                amount = commissionAmount,
                dateFa = dateFa,
                isDeposit = true,
                status = CommissionStatus.BURNED,
                planName = plan.title,
                planPrice = plan.price,
                commissionPercent = commissionPercent,
                agentName = colleagueName,
                burnReason = "حساب شما در زمان تمدید همکار غیرفعال (دی‌اکتیو) بود — پورسانت سوخت!"
            )
            _commissionStat.value = curComm.copy(
                burnedBalance = curComm.burnedBalance + commissionAmount,
                transactions = listOf(burnedTx) + curComm.transactions
            )
            addNotification(
                title = "پورسانت معرفی همکار سوخت!",
                message = "همکار شما ($colleagueName) اشتراک ${plan.title} را تمدید کرد، اما به دلیل غیرفعال بودن حساب شما، پورسانت ${PersianUtils.formatPrice(commissionAmount)} سوخت! جهت حفظ پورسانت‌ها طرح خود را فعال نگه دارید.",
                type = NotificationType.SUBSCRIPTION_EXPIRING,
                targetRole = UserRole.AGENT
            )
        }
    }

    // 2.1 — Create Banner Ad
    fun createAd(
        propertyId: String?,
        propertyTitle: String,
        city: String,
        province: String,
        durationDays: Int,
        bannerDrawableRes: Int = R.drawable.img_tour_sample
    ): AdItem {
        val price = calculateAdPrice(city, durationDays)
        val startDateJalali = JalaliDateHelper.formatJalaliDateOnly(System.currentTimeMillis())
        val endDateJalali = JalaliDateHelper.formatJalaliDateOnly(System.currentTimeMillis() + (durationDays.toLong() * 24 * 3600 * 1000))

        val newAd = AdItem(
            id = "ad_${System.currentTimeMillis()}",
            agentId = "agent_current",
            agentName = _userName.value,
            propertyId = propertyId,
            propertyTitle = propertyTitle,
            city = city,
            province = province,
            durationDays = durationDays,
            price = price,
            startDateJalali = startDateJalali,
            endDateJalali = endDateJalali,
            bannerDrawableRes = bannerDrawableRes,
            status = AdStatus.ACTIVE,
            paymentStatus = PaymentStatus.PAID,
            viewCount = 1,
            contactCount = 0
        )
        _ads.update { listOf(newAd) + it }
        addNotification(
            title = "تبلیغ بنری شهری فعال شد",
            message = "تبلیغ بنری شما در شهر $city با موفقیت ثبت و فعال گردید.",
            type = NotificationType.GENERAL,
            targetRole = UserRole.AGENT
        )
        return newAd
    }

    // 2.3 & 7 — Admin Platform Ads Management
    fun addPlatformAd(title: String, subtitle: String, actionText: String) {
        val newAd = AdItem(
            id = "pad_${System.currentTimeMillis()}",
            agentId = "admin_platform",
            agentName = "تور مجازی املاک ایران",
            propertyId = null,
            propertyTitle = title,
            city = "سراسری",
            province = "کل کشور",
            durationDays = 365,
            price = 0,
            startDateJalali = JalaliDateHelper.formatJalaliDateOnly(System.currentTimeMillis()),
            endDateJalali = "دائمی",
            bannerDrawableRes = R.drawable.ic_app_logo,
            status = AdStatus.ACTIVE,
            paymentStatus = PaymentStatus.PAID,
            isPlatformAd = true,
            platformAdSubtitle = subtitle,
            platformActionText = actionText
        )
        _platformAds.update { listOf(newAd) + it }
    }

    fun removePlatformAd(adId: String) {
        _platformAds.update { list -> list.filterNot { it.id == adId } }
    }

    // 4.3 & 3.2 — Buy Subscription + Exact Referral Commission Rules
    // 1 Month (1.5M) -> 20% = 300,000 Toman
    // 2 Month (2.5M) -> 15% = 375,000 Toman
    // 3 Month (4M) -> 10% = 400,000 Toman
    fun activateSubscription(plan: SubscriptionPlan) {
        _activeSubscription.value = ActiveSubscription(
            planId = plan.id,
            planTitle = plan.title,
            startDate = System.currentTimeMillis(),
            remainingDays = plan.durationMonths * 30,
            isActive = true
        )

        val (commissionPercent, commissionAmount) = when (plan.durationMonths) {
            1 -> 20 to 300_000L
            2 -> 15 to 375_000L
            3 -> 10 to 400_000L
            else -> 10 to (plan.price * 10 / 100)
        }

        val curComm = _commissionStat.value
        val newTx = ReferralTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "پورسانت $commissionPercent٪ خرید ${plan.title} توسط ${_userName.value}",
            amount = commissionAmount,
            dateFa = JalaliDateHelper.formatJalaliDateOnly(System.currentTimeMillis()),
            isDeposit = true,
            status = CommissionStatus.PAID,
            planName = plan.title,
            planPrice = plan.price,
            commissionPercent = commissionPercent,
            agentName = _userName.value
        )

        val newCount = curComm.successfulReferralsCount + 1
        val newTier = when {
            newCount >= 15 -> ReferrerTier.SPECIAL
            newCount >= 9 -> ReferrerTier.ACTIVE
            else -> ReferrerTier.STANDARD
        }

        _commissionStat.value = curComm.copy(
            totalEarnings = curComm.totalEarnings + commissionAmount,
            availableBalance = curComm.availableBalance + commissionAmount,
            successfulReferralsCount = newCount,
            currentTier = newTier,
            transactions = listOf(newTx) + curComm.transactions
        )

        addNotification(
            title = "پورسانت جدید واریز شد!",
            message = "مبلغ ${PersianUtils.formatPrice(commissionAmount)} بابت خرید ${plan.title} مشاور به موجودی شما افزوده شد.",
            type = NotificationType.COMMISSION_EARNED,
            targetRole = UserRole.REGULAR_USER
        )

        addNotification(
            title = "اشتراک با موفقیت فعال شد",
            message = "${plan.title} با موفقیت تایید شد. دسترسی به امکانات ویژه برقرار است.",
            type = NotificationType.GENERAL,
            targetRole = UserRole.AGENT
        )
    }

    // 3.1 & 3.3 — Request Withdrawal (Exact Rules: Min 500,000 Toman, Only Sheba IR, 10% Tax Deducted, Nightly Processing)
    fun submitWithdrawalRequest(amount: Long, cardNumber: String, shebaNumber: String, desc: String): Boolean {
        val current = _commissionStat.value
        if (amount < 500_000L || amount > current.availableBalance) return false
        val cleanSheba = shebaNumber.trim().uppercase()
        if (!cleanSheba.startsWith("IR") || cleanSheba.length < 16) return false

        val taxAmount = (amount * 0.10).toLong()
        val netAmount = amount - taxAmount

        val newRequest = WithdrawalRequest(
            id = "req_${System.currentTimeMillis()}",
            userId = "user_normal",
            userName = _userName.value,
            userPhone = _userPhone.value,
            amount = netAmount,
            cardNumber = cardNumber,
            shebaNumber = cleanSheba,
            description = "$desc (مبلغ کل درخواستی: ${PersianUtils.formatPrice(amount)} — ۱۰٪ مالیات کسر شد: ${PersianUtils.formatPrice(taxAmount)})",
            requestDateJalali = JalaliDateHelper.formatJalaliDateTime(System.currentTimeMillis()),
            status = WithdrawalStatus.PENDING
        )

        _commissionStat.value = current.copy(
            availableBalance = current.availableBalance - amount,
            pendingBalance = current.pendingBalance + netAmount
        )

        _withdrawalRequests.update { listOf(newRequest) + it }

        addNotification(
            title = "درخواست تسویه ثبت شد (واریز شبانه شبا)",
            message = "درخواست برداشت ${PersianUtils.formatPrice(amount)} ثبت شد. مبلغ خالص ${PersianUtils.formatPrice(netAmount)} (پس از کسر ۱۰٪ مالیات) در نوبت شبانه شبا پایا واریز می‌گردد.",
            type = NotificationType.WITHDRAWAL_UPDATE,
            targetRole = UserRole.REGULAR_USER
        )
        return true
    }

    // 3.3 & 7 — Admin Approve/Reject Withdrawal
    fun approveWithdrawal(requestId: String) {
        _withdrawalRequests.update { list ->
            list.map { req ->
                if (req.id == requestId) req.copy(status = WithdrawalStatus.APPROVED, adminNote = "تایید و واریز شد از طریق پایا")
                else req
            }
        }
        val req = _withdrawalRequests.value.find { it.id == requestId }
        if (req != null) {
            val cur = _commissionStat.value
            _commissionStat.value = cur.copy(
                pendingBalance = (cur.pendingBalance - req.amount).coerceAtLeast(0)
            )
            addNotification(
                title = "تسویه حساب تایید شد",
                message = "مبلغ ${PersianUtils.formatPrice(req.amount)} به شماره شبای شما واریز گردید.",
                type = NotificationType.WITHDRAWAL_UPDATE,
                targetRole = UserRole.REGULAR_USER
            )
        }
    }

    fun rejectWithdrawal(requestId: String, reason: String = "اطلاعات حساب مغایرت دارد") {
        _withdrawalRequests.update { list ->
            list.map { req ->
                if (req.id == requestId) req.copy(status = WithdrawalStatus.REJECTED, adminNote = reason)
                else req
            }
        }
        val req = _withdrawalRequests.value.find { it.id == requestId }
        if (req != null) {
            val cur = _commissionStat.value
            _commissionStat.value = cur.copy(
                availableBalance = cur.availableBalance + req.amount,
                pendingBalance = (cur.pendingBalance - req.amount).coerceAtLeast(0)
            )
            addNotification(
                title = "درخواست برداشت رد شد",
                message = "درخواست تسویه شما به دلیل: $reason رد شد و مبلغ به کیف پول بازگشت.",
                type = NotificationType.WITHDRAWAL_UPDATE,
                targetRole = UserRole.REGULAR_USER
            )
        }
    }

    // Section 5 & 6 — In-person Visit Requests
    fun requestVisit(propertyId: String, propertyTitle: String, preferredDateJalali: String, preferredTime: String) {
        val newVisit = VisitRequest(
            id = "visit_${System.currentTimeMillis()}",
            propertyId = propertyId,
            propertyTitle = propertyTitle,
            requesterName = _userName.value,
            requesterPhone = _userPhone.value,
            preferredDateJalali = preferredDateJalali,
            preferredTime = preferredTime,
            agentId = "agent_current",
            status = VisitStatus.PENDING
        )
        _visitRequests.update { listOf(newVisit) + it }
        addNotification(
            title = "درخواست بازدید حضوری جدید",
            message = "کاربر ${_userName.value} برای ملک «$propertyTitle» در تاریخ $preferredDateJalali ساعت $preferredTime درخواست بازدید داده است.",
            type = NotificationType.VISIT_REQUEST,
            targetRole = UserRole.AGENT
        )
    }

    fun answerVisitRequest(visitId: String, approve: Boolean) {
        _visitRequests.update { list ->
            list.map { v ->
                if (v.id == visitId) v.copy(status = if (approve) VisitStatus.APPROVED else VisitStatus.REJECTED)
                else v
            }
        }
        val visit = _visitRequests.value.find { it.id == visitId }
        addNotification(
            title = if (approve) "بازدید حضوری تایید شد" else "بازدید حضوری لغو شد",
            message = if (approve) "مشاور املاک زمان بازدید ملک «${visit?.propertyTitle}» را تایید کرد." else "متاسفانه مشاور املاک امکان هماهنگی بازدید را در این زمان نداشت.",
            type = NotificationType.VISIT_REQUEST,
            targetRole = UserRole.REGULAR_USER
        )
    }

    // Notifications
    fun addNotification(title: String, message: String, type: NotificationType, targetRole: UserRole?) {
        val notif = AppNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            type = type,
            isRead = false,
            targetRole = targetRole
        )
        _notifications.update { listOf(notif) + it }
    }

    fun markAllNotificationsAsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    // Initial Data
    private fun createInitialProperties(): List<PropertyItem> {
        val scenesProp1 = listOf(
            TourScene(
                id = "scene_living",
                name = "سالن پذیرایی و نشیمن ۳۶۰",
                drawableResId = R.drawable.img_tour_sample,
                hotspots = listOf(
                    TourHotspot("hs_1", "ورود به اتاق خواب مستر", 0.65f, 0.52f, "scene_bed"),
                    TourHotspot("hs_2", "کفپوش چوب بلوط و سیستم تهویه", 0.32f, 0.70f, infoText = "کف چوب طبیعی فرآوری شده با گرمایش از کف و چیلر مرکزی"),
                    TourHotspot("hs_3", "چشم‌انداز پانوراما شهر", 0.48f, 0.40f, infoText = "پنجره‌های تمام قد ترمال‌بریک با شیشه‌های سه جداره آکوستیک")
                )
            ),
            TourScene(
                id = "scene_bed",
                name = "اتاق خواب مستر رویال",
                drawableResId = R.drawable.img_tour_bedroom,
                hotspots = listOf(
                    TourHotspot("hs_bed_1", "بازگشت به سالن پذیرایی", 0.20f, 0.50f, "scene_living"),
                    TourHotspot("hs_bed_2", "کلوزت روم و کمد دیواری", 0.78f, 0.48f, infoText = "کمد دیواری‌های سفارشی های‌گلاس با نورپردازی هوشمند سنسوریک")
                )
            )
        )

        val scenesProp2 = listOf(
            TourScene(
                id = "scene_living_2",
                name = "سالن پنت‌هاوس",
                drawableResId = R.drawable.img_tour_sample,
                hotspots = listOf(
                    TourHotspot("hs_p2_1", "ورود به سوئیت", 0.70f, 0.50f, "scene_bed_2")
                )
            ),
            TourScene(
                id = "scene_bed_2",
                name = "اتاق مستر با بالکن",
                drawableResId = R.drawable.img_tour_bedroom,
                hotspots = listOf(
                    TourHotspot("hs_p2_back", "بازگشت به سالن", 0.30f, 0.50f, "scene_living_2")
                )
            )
        )

        return listOf(
            PropertyItem(
                id = "prop_1",
                title = "آپارتمان مدرن ۲۲۰ متری سعادت‌آباد",
                propertyType = "آپارتمان",
                transactionType = "فروش",
                price = 28_500_000_000L,
                areaSqMeters = 220,
                rooms = 3,
                city = "تهران",
                neighborhood = "سعادت‌آباد (میدان کاج)",
                address = "سعادت‌آباد، خیابان صرافها، کوچه سرو، پلاک ۲۴",
                description = "سازه‌ای لوکس و مهندسی‌ساز از آرشیتکت بنام، نورگیر بی‌نظیر جنوب و غرب، متریال تماماً وارداتی، ۲ پارکینگ سندی، لابی مجلل به همراه لابی‌من ۲۴ ساعته، روف‌گاردن ۴ فصل با ویوی ۳۶۰ درجه کل تهران.",
                features = listOf("۲ پارکینگ سندی", "استخر و سونا", "سیستم BMS هوشمند", "روف گاردن", "لابی و لابی‌من ۲۴ ساعته", "آسانسور ۱۴ نفره ویتور"),
                scenes = scenesProp1,
                viewsCount = 540,
                inquiriesCount = 22,
                status = PropertyStatus.ACTIVE_TOUR,
                isFeatured = true
            ),
            PropertyItem(
                id = "prop_2",
                title = "ویلا تریپلکس ۱۰۰۰ متری خزرشهر ساری",
                propertyType = "ویلا",
                transactionType = "فروش",
                price = 45_000_000_000L,
                areaSqMeters = 600,
                rooms = 5,
                city = "ساری",
                neighborhood = "خزرشهر شمالی",
                address = "ساری، جاده فرح‌آباد، مجتمع خزرشهر شمالی، کوچه صدف",
                description = "ویلای ساحلی رویایی با دسترسی اختصاصی به دریا، استخر ۴ فصل آبگرم روباز، سونا خشک و بخار، بیلیارد، محوطه‌سازی نخل و چمن کاری شده، سند ۶ دانگ تک برگ.",
                features = listOf("دسترسی ساحلی اختصاصی", "استخر روباز آبگرم", "جکوزی ۸ نفره", "محوطه‌سازی و آلاچیق", "سند تک برگ"),
                scenes = scenesProp1,
                viewsCount = 980,
                inquiriesCount = 38,
                status = PropertyStatus.ACTIVE_TOUR,
                isFeatured = true
            ),
            PropertyItem(
                id = "prop_3",
                title = "پنت‌هاوس ۳۵۰ متری فرمانیه",
                propertyType = "آپارتمان",
                transactionType = "فروش",
                price = 56_000_000_000L,
                areaSqMeters = 350,
                rooms = 4,
                city = "تهران",
                neighborhood = "فرمانیه شرقی",
                address = "فرمانیه، تقاطع سنبل، برج باغ رویال",
                description = "پنت‌هاوس استثنایی با سقف ۴ متری، سالن فلت یکدست، آشپزخانه فول‌فرنیش بوش آلمان، مستر روم ۵۰ متری مشرف به کوهستان، ۳ پارکینگ باکس.",
                features = listOf("۳ پارکینگ باکس", "استخر اختصاصی در پنت‌هاوس", "آسانسور اختصاصی با کد", "سوئیت مجزا سرایداری"),
                scenes = scenesProp2,
                viewsCount = 760,
                inquiriesCount = 19,
                status = PropertyStatus.ACTIVE_TOUR,
                isFeatured = true
            ),
            PropertyItem(
                id = "prop_4",
                title = "آپارتمان ۱۶۰ متری خیابان فرهنگ ساری",
                propertyType = "آپارتمان",
                transactionType = "رهن و اجاره",
                price = 1_800_000_000L,
                areaSqMeters = 160,
                rooms = 3,
                city = "ساری",
                neighborhood = "خیابان فرهنگ",
                address = "ساری، خیابان فرهنگ، کوچه حافظ، مجتمع ارکیده",
                description = "واحد فول بازسازی شده با کابینت‌های نئوکلاسیک، سالن بزرگ با پنجره‌های سراسری، آسانسور ایتالیایی، پارکینگ همکف سندی.",
                features = listOf("پارکینگ بدون مزاحم", "کابینت نئوکلاسیک", "سیستم سرمایش اسپلیت", "آسانسور", "انباری"),
                scenes = scenesProp2,
                viewsCount = 310,
                inquiriesCount = 14,
                status = PropertyStatus.ACTIVE_TOUR,
                isFeatured = false
            )
        )
    }

    private fun createInitialAds(): List<AdItem> {
        return listOf(
            AdItem(
                id = "ad_1",
                agentId = "agent_1",
                agentName = "املاک کاسپین ساری",
                propertyId = "prop_2",
                propertyTitle = "ویلا تریپلکس ۱۰۰۰ متری ساحلی خزرشهر",
                city = "ساری",
                province = "مازندران",
                durationDays = 30,
                price = 1_280_000L,
                startDateJalali = "۱۴۰۳/۰۷/۰۱",
                endDateJalali = "۱۴۰۳/۰۸/۰۱",
                bannerDrawableRes = R.drawable.img_tour_sample,
                status = AdStatus.ACTIVE,
                paymentStatus = PaymentStatus.PAID,
                viewCount = 1840,
                contactCount = 42
            ),
            AdItem(
                id = "ad_2",
                agentId = "agent_2",
                agentName = "املاک شمیران تهران",
                propertyId = "prop_1",
                propertyTitle = "آپارتمان مدرن ۲۲۰ متری سعادت‌آباد",
                city = "تهران",
                province = "تهران",
                durationDays = 20,
                price = 950_000L,
                startDateJalali = "۱۴۰۳/۰۷/۰۵",
                endDateJalali = "۱۴۰۳/۰۷/۲۵",
                bannerDrawableRes = R.drawable.img_tour_sample,
                status = AdStatus.ACTIVE,
                paymentStatus = PaymentStatus.PAID,
                viewCount = 2410,
                contactCount = 68
            ),
            AdItem(
                id = "ad_3",
                agentId = "agent_3",
                agentName = "املاک پایتخت مازندران",
                propertyId = "prop_4",
                propertyTitle = "واحد ۱۶۰ متری خیابان فرهنگ ساری",
                city = "ساری",
                province = "مازندران",
                durationDays = 10,
                price = 550_000L,
                startDateJalali = "۱۴۰۳/۰۷/۱۰",
                endDateJalali = "۱۴۰۳/۰۷/۲۰",
                bannerDrawableRes = R.drawable.img_tour_bedroom,
                status = AdStatus.ACTIVE,
                paymentStatus = PaymentStatus.PAID,
                viewCount = 890,
                contactCount = 27
            )
        )
    }

    private fun createInitialPlatformAds(): List<AdItem> {
        return listOf(
            AdItem(
                id = "pad_1",
                agentId = "admin_platform",
                agentName = "تیم فنی تور مجازی املاک",
                propertyId = null,
                propertyTitle = "عکاسی و اسکن ۳۶۰ درجه رایگان در سراسر کشور",
                city = "سراسری",
                province = "کل کشور",
                durationDays = 365,
                price = 0,
                startDateJalali = "۱۴۰۳/۰۱/۰۱",
                endDateJalali = "دائمی",
                bannerDrawableRes = R.drawable.ic_app_logo,
                status = AdStatus.ACTIVE,
                paymentStatus = PaymentStatus.PAID,
                isPlatformAd = true,
                platformAdSubtitle = "با ثبت اشتراک ۳ ماهه، یک نوبت عکاسی پانورامای صنعتی رایگان دریافت کنید!",
                platformActionText = "اطلاعات بیشتر"
            ),
            AdItem(
                id = "pad_2",
                agentId = "admin_platform",
                agentName = "باشگاه مشتریان و همکاران",
                propertyId = null,
                propertyTitle = "کسب پورسانت ۱۰٪ مادام‌العمر با معرفی مشاورین املاک",
                city = "سراسری",
                province = "کل کشور",
                durationDays = 365,
                price = 0,
                startDateJalali = "۱۴۰۳/۰۱/۰۱",
                endDateJalali = "دائمی",
                bannerDrawableRes = R.drawable.ic_app_logo,
                status = AdStatus.ACTIVE,
                paymentStatus = PaymentStatus.PAID,
                isPlatformAd = true,
                platformAdSubtitle = "کد معرف خود را با دفاتر املاک به اشتراک بگذارید و نقداً تسویه کنید.",
                platformActionText = "دریافت کد معرف"
            )
        )
    }

    private fun createInitialWithdrawals(): List<WithdrawalRequest> {
        return listOf(
            WithdrawalRequest(
                id = "w_1",
                userId = "user_normal",
                userName = "علی حسینی",
                userPhone = "۰۹۱۲۳۴۵۶۷۸۹",
                amount = 500_000L,
                cardNumber = "۶۰۳۷-۹۹۷۵-۱۲۳۴-۵۶۷۸",
                shebaNumber = "IR820120000000012345678901",
                description = "درخواست تسویه پورسانت معرفی مشاور املاک فرمانیه",
                requestDateJalali = "۱۴۰۳/۰۷/۱۵ — ۱۱:۲۰",
                status = WithdrawalStatus.PENDING
            ),
            WithdrawalRequest(
                id = "w_2",
                userId = "user_normal_2",
                userName = "مریم صادقی",
                userPhone = "۰۹۳۵۱۲۳۴۵۶۷",
                amount = 750_000L,
                cardNumber = "۵۰۲۲-۲۹۱۰-۸۸۸۸-۴۳۲۱",
                shebaNumber = "IR150560000000088887777666",
                description = "تسویه پورسانت معرف",
                requestDateJalali = "۱۴۰۳/۰۷/۱۲ — ۱۶:۴۵",
                status = WithdrawalStatus.APPROVED,
                adminNote = "واریز شد با شماره پیگیری ۹۸۲۳۴ بانک سامان"
            )
        )
    }

    private fun createInitialNotifications(): List<AppNotification> {
        return listOf(
            AppNotification(
                id = "n_1",
                title = "پورسانت جدید ثبت شد",
                message = "مبلغ ۷۵,۰۰۰ تومان به عنوان پورسانت اشتراک مشاور املاک به حساب شما افزوده شد.",
                timestamp = System.currentTimeMillis() - 3600 * 1000,
                type = NotificationType.COMMISSION_EARNED
            ),
            AppNotification(
                id = "n_2",
                title = "درخواست بازدید حضوری ملک",
                message = "یک درخواست بازدید برای ملک ۲۲۰ متری سعادت‌آباد ثبت شد.",
                timestamp = System.currentTimeMillis() - 7200 * 1000,
                type = NotificationType.VISIT_REQUEST
            )
        )
    }

    private fun createInitialVisits(): List<VisitRequest> {
        return listOf(
            VisitRequest(
                id = "v_1",
                propertyId = "prop_1",
                propertyTitle = "آپارتمان مدرن ۲۲۰ متری سعادت‌آباد",
                requesterName = "آقای بهرامی",
                requesterPhone = "۰۹۱۲۹۹۹۸۸۷۷",
                preferredDateJalali = "۱۴۰۳/۰۷/۲۰",
                preferredTime = "۱۷:۰۰",
                agentId = "agent_current",
                status = VisitStatus.PENDING
            )
        )
    }

    fun getAgentsForCity(cityName: String): List<CityAgentItem> {
        val baseProps = _properties.value.filter { it.city == cityName }
        return listOf(
            CityAgentItem(
                id = "ag_1_${cityName}",
                name = "مهندس علیرضا سلیمانی",
                agencyName = "املاک مدرن $cityName",
                phone = "۰۹۱۱۱۵۱۱۲۳۴",
                city = cityName,
                activeToursCount = if (baseProps.isNotEmpty()) baseProps.size + 2 else 5,
                badge = "مشاور ویژه و برگزیده",
                bannerRes = R.drawable.img_tour_sample,
                rating = "۴.۹"
            ),
            CityAgentItem(
                id = "ag_2_${cityName}",
                name = "مهندس مسعود حسینی",
                agencyName = "املاک آینده‌ساز $cityName",
                phone = "۰۹۱۲۳۴۵۶۷۸۹",
                city = cityName,
                activeToursCount = 3,
                badge = "تاییدیه هویت طلایی",
                bannerRes = R.drawable.img_tour_bedroom,
                rating = "۴.۸"
            ),
            CityAgentItem(
                id = "ag_3_${cityName}",
                name = "مهندس آرش کریمی",
                agencyName = "گروه مهندسین و املاک پارس $cityName",
                phone = "۰۹۳۵۰۰۰۱۱۲۲",
                city = cityName,
                activeToursCount = 6,
                badge = "عضو رسمی اتحادیه",
                bannerRes = R.drawable.img_tour_sample,
                rating = "۴.۷"
            )
        )
    }

    private fun createInitialCustomerInquiries(): List<CustomerInquiry> {
        return listOf(
            CustomerInquiry(
                id = "inq_1",
                customerName = "آقای بهرامی",
                customerPhone = "۰۹۱۲۹۹۹۸۸۷۷",
                propertyTitle = "آپارتمان مدرن ۲۲۰ متری سعادت‌آباد",
                propertyId = "prop_1",
                channel = "درخواست بازدید حضوری (ساعت ۱۸:۰۰)",
                dateFa = "۱۴۰۳/۰۷/۲۵",
                timeFa = "۱۸:۰۰",
                details = "هماهنگی جهت بازدید به همراه خانواده",
                isReviewed = false,
                isApproved = false
            ),
            CustomerInquiry(
                id = "inq_2",
                customerName = "خانم مهندس صادقی",
                customerPhone = "۰۹۳۵۱۲۳۴۵۶۷",
                propertyTitle = "ویلا تریپلکس ۱۰۰۰ متری خزرشهر ساری",
                propertyId = "prop_2",
                channel = "تماس تلفنی مستقیم",
                dateFa = "۱۴۰۳/۰۷/۲۴",
                timeFa = "۱۶:۲۰",
                details = "استعلام قیمت نهایی و شرایط رهن ویلا",
                isReviewed = false,
                isApproved = false
            ),
            CustomerInquiry(
                id = "inq_3",
                customerName = "دکتر رضوی",
                customerPhone = "۰۹۱۱۱۵۱۲۳۴۵",
                propertyTitle = "پنت‌هاوس ۳۵۰ متری فرمانیه",
                propertyId = "prop_3",
                channel = "پیام در واتساپ",
                dateFa = "۱۴۰۳/۰۷/۲۳",
                timeFa = "۱۱:۴۵",
                details = "درخواست ارسال اطلاعات تکمیلی و ویدیو ۳۶۰",
                isReviewed = true,
                isApproved = true
            )
        )
    }

    companion object {
        val instance by lazy { AppRepository() }
    }
}

data class CityAgentItem(
    val id: String,
    val name: String,
    val agencyName: String,
    val phone: String,
    val city: String,
    val activeToursCount: Int,
    val badge: String,
    val bannerRes: Int,
    val rating: String
)
