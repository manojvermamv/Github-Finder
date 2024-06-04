package anubhav.github.finder.global

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

abstract class SharedPreferenceLiveData<T>(private val sharedPrefs: SharedPreferences, val key: String, private val defValue: T) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == this.key || key == null) {
                // Note that we get here on every preference write, even if the value has not changed
                updateIfChanged()
            }
        }

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        updateIfChanged()
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }

    /** Update the live data value, but only if the value has changed. */
    private fun updateIfChanged() = with(getValueFromPreferences(key, defValue)) {
        if (value != this) value = this
    }
}

inline fun <reified T> SharedPreferences.liveData(key: String, default: T): SharedPreferenceLiveData<T> {
    @Suppress("UNCHECKED_CAST")
    return object : SharedPreferenceLiveData<T>(this, key, default) {
        override fun getValueFromPreferences(key: String, defValue: T): T {
            return when (defValue) {
                is String -> getString(key, defValue) as T
                is Int -> getInt(key, defValue) as T
                is Long -> getLong(key, defValue) as T
                is Boolean -> getBoolean(key, defValue) as T
                is Float -> getFloat(key, defValue) as T
                is Set<*> -> getStringSet(key, defValue as Set<String>) as T
                is MutableSet<*> -> getStringSet(key, defValue as MutableSet<String>) as T
                else -> {
                    val json = getString(key, null)
                    if (!json.isNullOrEmpty()) {
                        Gson().fromJson(json, object : TypeToken<T>() {}.type)
                        //gson.fromJson(json, object : TypeToken<ArrayList<T>>() {}.type)
                    } else defValue
                }
            }
        }
    }
}