package ilapin.earth.domain.celestial_sphere

import ilapin.common.input.TouchEvent
import ilapin.common.rx.BaseObserver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class ScrollController : Disposable {

    private var prevTouchEvent: TouchEvent? = null

    private var disposable: Disposable? = null

    private val scrollEventSubject = PublishSubject.create<ScrollEvent>()

    val scrollEvent: Observable<ScrollEvent> = scrollEventSubject

    val touchEventsObserver = object : BaseObserver<TouchEvent>() {

        override fun onSubscribe(d: Disposable) {
            if (disposable != null) {
                throw IllegalStateException("Trying to subscribe more than once")
            }
            disposable = d
        }

        override fun onNext(t: TouchEvent) {
            if (t.action == TouchEvent.Action.CANCEL || t.action == TouchEvent.Action.UP) {
                prevTouchEvent = null
                return
            }

            prevTouchEvent?.let {
                scrollEventSubject.onNext(ScrollEvent(t.x - it.x, t.y - it.y))
            }
            prevTouchEvent = t
        }
    }

    override fun isDisposed(): Boolean {
        return disposable?.isDisposed ?: false
    }

    override fun dispose() {
        disposable?.dispose()
    }
}