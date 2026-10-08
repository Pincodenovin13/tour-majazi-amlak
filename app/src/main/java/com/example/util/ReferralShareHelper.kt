package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ReferralShareHelper {

    fun getFullReferralLink(referralCode: String): String {
        return "https://tour-majazi.ir/ref/$referralCode"
    }

    fun getShortReferralLink(referralCode: String): String {
        val cleanCode = referralCode.replace("-", "")
        return "tour-majazi.ir/r/$cleanCode"
    }

    fun buildShareMessage(referrerName: String, referralCode: String): String {
        val link = getFullReferralLink(referralCode)
        return """
سلام! من $referrerName هستم.
با لینک زیر در پلتفرم «تور مجازی املاک» ثبت‌نام کن و با کد معرف من از خدمات ویژه و تخفیف بهره‌مند شو:
$link

کد معرف: $referralCode
(لینک کوتاه: ${getShortReferralLink(referralCode)})
""".trimIndent()
    }

    fun copyToClipboard(context: Context, text: String, label: String = "کد معرف") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "$label با موفقیت کپی شد", Toast.LENGTH_SHORT).show()
    }

    fun shareToWhatsApp(context: Context, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(message)}")
                `package` = "com.whatsapp"
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback to web or general chooser
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(message)}"))
            try {
                context.startActivity(browserIntent)
            } catch (e: Exception) {
                shareGeneral(context, message, "ارسال به واتساپ")
            }
        }
    }

    fun shareToTelegram(context: Context, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("tg://msg?text=${Uri.encode(message)}")
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/share/url?url=&text=${Uri.encode(message)}"))
            try {
                context.startActivity(webIntent)
            } catch (e: Exception) {
                shareGeneral(context, message, "ارسال به تلگرام")
            }
        }
    }

    fun shareToBale(context: Context, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                `package` = "ir.nasim"
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            shareGeneral(context, message, "ارسال به بله")
        }
    }

    fun shareToEitaa(context: Context, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                `package` = "ir.eitaa.messenger"
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            shareGeneral(context, message, "ارسال به ایتا")
        }
    }

    fun shareViaSms(context: Context, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:")
                putExtra("sms_body", message)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            shareGeneral(context, message, "ارسال پیامک")
        }
    }

    fun shareGeneral(context: Context, message: String, title: String = "اشتراک‌گذاری لینک دعوت") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }
}
