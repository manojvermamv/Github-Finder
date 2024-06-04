package anubhav.github.finder.global

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class SharedPreferenceManager constructor(private val context: Context) {

    val pref: SharedPreferences = context.getSharedPreferences()
    private val editor = pref.edit()
    private val gson = Gson()

    private inline fun <reified T> read(param: PrefParam, defValue: T? = null): T {
        val prefKey = param.key()
        @Suppress("UNCHECKED_CAST")
        val value: Any? = when (defValue) {
            is String -> pref.getString(prefKey, defValue as? String ?: "")
            is Boolean -> pref.getBoolean(prefKey, (defValue as? Any ?: false) as Boolean)
            is Long -> pref.getLong(prefKey, defValue as? Long ?: 0)
            is Int -> pref.getInt(prefKey, defValue as? Int ?: 0)
            is Float -> pref.getFloat(prefKey, defValue as? Float ?: 0f)
            is Set<*> -> pref.getStringSet(prefKey, defValue as? Set<String> ?: setOf<String>())
            is MutableSet<*> -> pref.getStringSet(prefKey, defValue as? MutableSet<String> ?: mutableSetOf<String>())
            else -> {
                val json = pref.getString(prefKey, null)
                if (!json.isNullOrEmpty()) {
                    gson.fromJson(json, object : TypeToken<T>() {}.type)
                    //gson.fromJson(json, object : TypeToken<ArrayList<T>>() {}.type)
                } else defValue
            }
        }
        return (value as T)
    }

    private fun write(param: PrefParam, value: Any, type: Type = value.javaClass) {
        val prefKey = param.key()
        @Suppress("UNCHECKED_CAST")
        when (value) {
            is String -> editor.putString(prefKey, value)
            is Boolean -> editor.putBoolean(prefKey, value)
            is Long -> editor.putLong(prefKey, value)
            is Int -> editor.putInt(prefKey, value)
            is Float -> editor.putFloat(prefKey, value)
            is Set<*> -> editor.putStringSet(prefKey, value as Set<String>)
            is MutableSet<*> -> editor.putStringSet(prefKey, value as MutableSet<String>)
            else -> editor.putString(prefKey, gson.toJson(value, type))
        }.apply()
    }

    inline fun <reified T> liveData(param: PrefParam, default: T) = pref.liveData(param.key(), default)

    fun clear() {
        editor.clear().apply()
        context.getSharedPreferences().edit().clear().apply()
    }

    fun lastRefreshTime(value: Long) = write(PrefParam.PREF_LAST_REFRESH_TIME, value)
    fun lastRefreshTime(): Long = read(PrefParam.PREF_LAST_REFRESH_TIME, 0L)

    fun defaultLanguage(value: String) = write(PrefParam.DEFAULT_LANGUAGE, value)
    fun defaultLanguage(): String = read(PrefParam.DEFAULT_LANGUAGE, "en")

    fun isNewRegistration(value: Boolean) = write(PrefParam.IS_NEW_REGISTRATION, value)
    fun isNewRegistration(): Boolean = read(PrefParam.IS_NEW_REGISTRATION, false)

    fun loggedUID(value: String) = write(PrefParam.LOGGED_IN_UID, value) // this is a uid of backend db (aka, local_id)
    fun loggedUID(): String = read(PrefParam.LOGGED_IN_UID, "")

    fun isLoggedIn(): Boolean = read(PrefParam.LOGGED_IN_UID, "").trim().isNotEmpty()

    fun username(value: String) = write(PrefParam.USERNAME, value)
    fun username(): String = read(PrefParam.USERNAME, "")

    fun googleIdToken(value: String) = write(PrefParam.GOOGLE_ID_TOKEN, value)
    fun googleIdToken(): String = read(PrefParam.GOOGLE_ID_TOKEN, "")

    fun onOffRTL(value: Boolean) = write(PrefParam.KEY_ON_OFF_RTL, value)
    fun onOffRTL(): Boolean = read(PrefParam.KEY_ON_OFF_RTL, false)

    fun isNotificationOn(value: Boolean) = write(PrefParam.KEY_ON_OFF_NOTIFICATION, value)
    fun isNotificationOn(): Boolean = read(PrefParam.KEY_ON_OFF_NOTIFICATION, true)

    fun firebaseUid(value: String) = write(PrefParam.FIREBASE_UID, value)
    fun firebaseUid(): String = read(PrefParam.FIREBASE_UID, "")

    fun firebaseIdToken(value: String) = write(PrefParam.FIREBASE_ID_TOKEN, value)
    fun firebaseIdToken(): String = read(PrefParam.FIREBASE_ID_TOKEN, "")

    // app settings
    //fun defaultConfig(value: DefaultConfig) = write(PrefParam.DEFAULT_CONFIG, value, DefaultConfig::class.java)
    //fun defaultConfig(): DefaultConfig = read(PrefParam.DEFAULT_CONFIG, DefaultConfig())

}

/**
 * shared preferences for global use
 * */
enum class PrefParam {
    PREF_LAST_REFRESH_TIME,
    DEFAULT_LANGUAGE,
    IS_NEW_REGISTRATION,
    LOGGED_IN_UID,
    USERNAME,
    GOOGLE_ID_TOKEN,

    KEY_ON_OFF_RTL,
    KEY_ON_OFF_NOTIFICATION,
    FIREBASE_UID,
    FIREBASE_ID_TOKEN,

    // app settings keys
    DEFAULT_CONFIG;

    fun key() = name.lowercase().trim()
}

private fun Context.getSharedPreferences(): SharedPreferences {
    return getSharedPreferences(packageName + "_preferences", 0)
}