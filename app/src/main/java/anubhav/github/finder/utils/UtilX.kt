package anubhav.github.finder.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.ImageView
import anubhav.github.finder.R
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormatSymbols
import java.util.Locale

internal object UtilX {

    @JvmStatic
    fun containsMonth(query: CharSequence): Boolean {
        val months = HashSet<String>()
        months.addAll(DateFormatSymbols.getInstance().months)
        months.addAll(DateFormatSymbols.getInstance().shortMonths)
        for (element in months) {
            if (query.contains(element)) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun convertListToString(list: MutableList<String>?): String {
        if (list == null) return ""
        return when {
            list.size == 1 -> list[0] // If the list has only one element, return it as is
            list.size == 2 -> "${list[0]} and ${list[1]}" // If the list has two elements, join with 'and'
            else -> list.dropLast(1).joinToString(", ") + " and ${list.last()}" // For more than two elements, use commas and 'and'
        }
    }

    @JvmStatic
    fun formatUsersCount(counts: Long): String {
        if (counts < 1000) {
            return counts.toString()
        } else if (counts < 1000000) {
            // Format for thousands (e.g., 1.2K)
            val countInThousands = counts.toFloat() / 1000
            return String.format(Locale.getDefault(), "%.1fK", countInThousands)
        } else if (counts < 1000000000) {
            // Format for millions (e.g., 12.3M)
            val countInMillions = counts.toFloat() / 1000000
            return String.format(Locale.getDefault(), "%.1fM", countInMillions)
        } else {
            // Format for billions (e.g., 1.2B)
            val countInBillions = counts.toFloat() / 1000000000
            return String.format(Locale.getDefault(), "%.1fB", countInBillions)
        }
    }

    @JvmStatic
    fun filterNotNullValues(map: Map<String, Any?>): Map<String, Any> {
        val result = LinkedHashMap<String, Any>()
        for ((key, value) in map) {
            if (value == null) continue
            if (value is String) {
                if (value.isNotEmpty()) result[key] = value
            } else {
                result[key] = value
            }
        }
        return result
    }

    @JvmStatic
    fun filterValuesChange(newMap: Map<String, Any?>, oldMap: Map<String, Any?>): Map<String, Any?> {
        val result = LinkedHashMap<String, Any?>()
        newMap.keys.forEach { key ->
            if (newMap.getValue(key) != oldMap.getValue(key)) {
                result[key] = newMap.getValue(key)
            }
        }
        return result
    }

    @JvmStatic
    fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
        when (val value = this[it]) {
            is JSONArray -> {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else -> value
        }
    }

}


fun getBoldText(text: String): SpannableStringBuilder {
    val content = "** $text **"
    val str = SpannableStringBuilder(content)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        str.setSpan(StyleSpan(Typeface.BOLD), 0, content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return str
}

fun String.getStartingLetter(counts: Int = 1): String {
    val re = Regex("[^A-Za-z0-9 ]")
    val results = re.replace(this, "").trim()
    return results.chunked(1).take(counts).joinToString()
}

fun Context.formatNumber(number: String): String {
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryIso = telephonyManager.networkCountryIso.uppercase(Locale.getDefault())
    return PhoneNumberUtils.formatNumber(number, countryIso)
}


fun <T> MutableList<T>.addAllIfNotPresent(elements: MutableList<T>) {
    for (item in elements) {
        addIfNotPresent(item)
    }
}

fun <T> MutableList<T>.addIfNotPresent(element: T) {
    if (!contains(element)) {
        add(element)
    }
}