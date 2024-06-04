package anubhav.github.finder.global

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import io.objectbox.BoxStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import anubhav.github.finder.R

class MyApp : Application() {

    companion object {
        @JvmStatic
        lateinit var appContext: Context
            private set
        lateinit var ioScope: CoroutineScope
            private set
        lateinit var boxStore: BoxStore
            private set
        lateinit var preference: SharedPreferenceManager
            private set

        @JvmStatic
        lateinit var appInstance: MyApp
            private set
        @JvmStatic
        lateinit var sharedPrefs: SharedPreferences
            private set
    }


    var iconPack: IconPack? = null
    var iconPackLoader: IconPackLoader? = null
    private var notificationManager: NotificationManagerCompat? = null

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        boxStore = Global.getBoxStore(this)
        preference = SharedPreferenceManager(this)

        appInstance = this
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(appContext)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        notificationManager = NotificationManagerCompat.from(appContext)
        loadIconPack()

        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    fun clearDatabase() {
        boxStore.removeAllObjects()
        SharedPreferenceManager(this).clear()
    }



    @SuppressLint("MissingPermission")
    fun sendNotification(title: CharSequence?, text: CharSequence?) {
        val channel = NotificationChannel(
            "0",
            "General",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager!!.createNotificationChannel(channel)
        val notifBuilder = NotificationCompat.Builder(appContext, "0")
            .setSmallIcon(R.drawable.baseline_hub_24)
            .setContentTitle(title)
            .setContentText(text)
        notificationManager!!.notify(0, notifBuilder.build())
    }

    @SuppressLint("MissingPermission")
    fun <T> GetSharedPref(key: Int, def: T): T? {
        val keyStr = appContext.getString(key)
        try {
            if (def is String) return sharedPrefs.getString(
                keyStr,
                def as String
            ) as T? else if (def is Int) return sharedPrefs.getString(
                keyStr,
                def.toString()
            )!!
                .toInt() as T else if (def is Boolean) return sharedPrefs.getBoolean(
                keyStr,
                (def as Boolean)!!
            ) as T
        } catch (e: Exception) {
            Log.e("GtihubFinder", "Error getting pref " + keyStr + " (" + e.message + ")")
        }
        return def
    }

    private fun loadIconPack() {
        iconPackLoader = IconPackLoader(baseContext)
        iconPack = createDefaultIconPack(iconPackLoader!!)
    }

}