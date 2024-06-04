package anubhav.github.finder.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


fun Activity.showKeyboard() {
    var view = currentFocus
    if (view == null) {
        view = findViewById(android.R.id.content)
    }
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    //inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
}

fun Activity.hideKeyboard() {
    var view = this.currentFocus
    if (view == null) {
        view = findViewById(android.R.id.content)
    }
    val inputManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(view!!.windowToken, 0)
}

fun hideKeyboard(currentFocus: View) {
    val inputMethodManager = currentFocus.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    inputMethodManager.toggleSoftInput(0, 0)
}

fun Context.hideKeyboard(currentFocus: View? = null) {
    with(currentFocus ?: View(this)) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        inputMethodManager.toggleSoftInput(0, 0)
    }
}

fun Context.hideKeyboard(windowToken: IBinder? = null) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    inputMethodManager.toggleSoftInput(0, 0)
}

// In setStatusBarLight only light colors allowed
fun Activity.setStatusBarLight(@ColorRes backgroundColor: Int = android.R.color.white) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //  set status text dark
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    window.statusBarColor = ContextCompat.getColor(this, backgroundColor) // set status background color
}

// In setStatusBarDark only dark colors allowed
fun Activity.setStatusBarDark(@ColorRes backgroundColor: Int = android.R.color.black) {
    window.statusBarColor = ContextCompat.getColor(this, backgroundColor) // set status background color
    val decorView: View = window.decorView
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //  set status text light
        decorView.systemUiVisibility = decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
}

// In setStatusBarLight only light colors allowed
fun Window.setStatusBarLight(context: Context, @ColorRes backgroundColor: Int = android.R.color.white) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //  set status text dark
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    statusBarColor = ContextCompat.getColor(context, backgroundColor) // set status background color
}

// In setStatusBarDark only dark colors allowed
fun Window.setStatusBarDark(context: Context, @ColorRes backgroundColor: Int = android.R.color.black) {
    statusBarColor = ContextCompat.getColor(context, backgroundColor) // set status background color
    val mDecorView: View = decorView
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //  set status text light
        mDecorView.systemUiVisibility = mDecorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
}