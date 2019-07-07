package ilapin.common.android.ui

import android.support.v7.app.AppCompatActivity
import io.reactivex.subjects.BehaviorSubject

abstract class RxLifecycleEventsActivity : AppCompatActivity() {

    protected val lifecycleEvents = BehaviorSubject.create<Event>()

    override fun onResume() {
        super.onResume()

        lifecycleEvents.onNext(Event.ON_RESUME)
    }

    override fun onPause() {
        super.onPause()

        lifecycleEvents.onNext(Event.ON_PAUSE)
    }

    enum class Event {
        ON_RESUME, ON_PAUSE
    }
}