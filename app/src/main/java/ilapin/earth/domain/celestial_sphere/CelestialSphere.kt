package ilapin.earth.domain.celestial_sphere

import ilapin.common.location.Location
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import org.joml.Quaternionf
import org.joml.Quaternionfc
import org.joml.Vector3f

class CelestialSphere {

    private val modelRotationSubject = BehaviorSubject.createDefault<Quaternionfc>(Quaternionf().identity())

    private var lastLocation: Location? = null

    val modelRotation: Observable<Quaternionfc> = modelRotationSubject

    val locationObserver = object : Observer<Location> {

        override fun onComplete() {
            // do nothing
        }

        override fun onSubscribe(d: Disposable) {
            // do nothing
        }

        override fun onNext(t: Location) {
            lastLocation = t

        }

        override fun onError(e: Throwable) {
            // do nothing
        }
    }

    fun convert(celestialLocation: CelestialLocation): Vector3f? {
        return lastLocation?.let {
            Vector3f()
        }
    }
}