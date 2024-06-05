package anubhav.github.finder.data

import android.content.Context
import anubhav.github.finder.R.string as stringRes

enum class SearchOrder {
    LOCATION,
    NAME
}

fun Context?.searchOrderName(searchOrder: SearchOrder) = this?.let {
    when (searchOrder) {
        SearchOrder.LOCATION -> getString(stringRes.location)
        SearchOrder.NAME -> getString(stringRes.name)
    }
} ?: ""