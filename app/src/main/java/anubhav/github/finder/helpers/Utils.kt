package anubhav.github.finder.helpers

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*


object Utils {

    fun getLastKnownLocation(context: Context, enabledProvidersOnly: Boolean): Location? {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val providers = manager?.getProviders(enabledProvidersOnly)
        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
            && providers != null)
        {
            for (provider in providers) {
                val utilLocation = manager.getLastKnownLocation(provider)
                if (utilLocation != null) return utilLocation
            }
        }
        return null
    }

    fun getCurrentCityName(context: Context, enabledProvidersOnly: Boolean): String {
        val location = getLastKnownLocation(context, enabledProvidersOnly) ?: return ""
        val latitude = location.latitude
        val longitude = location.longitude

        val addresses: List<Address>?
        var cityName = ""

        try {
            val geocoder = Geocoder(context, Locale.getDefault()) // Use this@ for context
            addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                cityName = address.locality ?: "" // Use safe null operator to handle null city name
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle potential exceptions (e.g., network issues)
        }
        return cityName
    }

    fun openLink(context: Context, url: String) {
        var link = url
        if (!url.startsWith("https://") && !url.startsWith("http://")){
            link = "http://$url"
        }
        val i = Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(link))
        context.startActivity(i)
    }

}