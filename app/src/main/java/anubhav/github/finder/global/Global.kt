package anubhav.github.finder.global

import android.app.Application
import android.util.Log
import anubhav.github.finder.data.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.BoxStoreBuilder
import io.objectbox.exception.DbSchemaException

object Global {
    fun getBoxStore(application: Application): BoxStore {
        return try {
            MyObjectBox.builder().androidContext(application).build()
        } catch (e: DbSchemaException) {
            Log.wtf("Global", "getBoxStore Error: ${e.localizedMessage}")
            BoxStore.deleteAllFiles(application, BoxStoreBuilder.DEFAULT_NAME)
            MyObjectBox.builder().androidContext(application).build()
        }
    }
}