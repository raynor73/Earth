package ilapin.earth.domain.celestial_sphere

import ilapin.common.location.Location
import ilapin.common.rx.BaseObserver
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.joml.Quaternionf
import org.joml.Quaternionfc
import org.joml.Vector3f

class CelestialSphere {

    private val modelRotationSubject = BehaviorSubject.createDefault<Quaternionfc>(Quaternionf().identity())

    private var lastLocation: Location? = null

    val modelRotation: Observable<Quaternionfc> = modelRotationSubject

    val locationObserver = object : BaseObserver<Location>() {

        override fun onNext(t: Location) {
            lastLocation = t

        }
    }

    fun convert(celestialLocation: CelestialLocation): Vector3f? {
        return lastLocation?.let {
            Vector3f()
        }
    }
}