package anubhav.github.finder.delegates

import android.text.Editable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

interface RxJavaHandler {
    fun registerLifecycleOwner(owner: LifecycleOwner)
    fun Disposable.addToDisposable(): Boolean
    fun <T> PublishSubject<T>.debounceOnMain(delay: Long = 1000, subscribe: (T) -> Unit)
}


class RxJavaHandlerImpl: RxJavaHandler, LifecycleEventObserver {


    private val disposables by lazy { CompositeDisposable() }

    override fun Disposable.addToDisposable(): Boolean = disposables.add(this)

    override fun registerLifecycleOwner(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> println("User created the screen")
            Lifecycle.Event.ON_START -> println("User started the screen")
            Lifecycle.Event.ON_RESUME -> println("User resumed(opened) the screen")
            Lifecycle.Event.ON_PAUSE -> println("User paused(leaves) the screen")
            Lifecycle.Event.ON_STOP -> println("User stopped the screen")
            Lifecycle.Event.ON_DESTROY -> {
                println("User destroyed the screen")
                disposables.clear()
            }
            else -> Unit
        }
    }

    /**
     * Rx Java Implementation
     * */
    override fun <T> PublishSubject<T>.debounceOnMain(delay: Long, subscribe: (T) -> Unit) {
        try {
            debounce(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { subscribe(it) }
                .addToDisposable()
        } catch (_: RuntimeException) {
        }
    }

}


/**
 * Helper Functions
 * */

fun Editable?.addRxTextChange(): Observable<String> {
    val flowable: Observable<String> = Observable.create { observable ->
        if (!isNullOrBlank()) {
            observable.onNext(trim().toString())
        }
    }
    return flowable
}