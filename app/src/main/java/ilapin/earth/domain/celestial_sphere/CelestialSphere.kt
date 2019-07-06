package ilapin.earth.domain.celestial_sphere

import ilapin.common.location.Location
import ilapin.common.math.RotationMatrixSmoother
import ilapin.common.orientation.OrientationRepository
import ilapin.common.rx.BaseObserver
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Quaternionfc
import org.joml.Vector3f

class CelestialSphere(orientationRepository: OrientationRepository) {

    private val modelRotationSubject = BehaviorSubject.createDefault<Quaternionfc>(Quaternionf().identity())

    private var lastLocation: Location? = null

    private val rotationMatrixSmoother = RotationMatrixSmoother(10)

    private val subscriptions = CompositeDisposable()

    private val tmpMatrix = Matrix4f()
    private val tmpQuaternion = Quaternionf()

    val modelRotation: Observable<Quaternionfc> = modelRotationSubject

    val locationObserver = object : BaseObserver<Location>() {

        override fun onNext(t: Location) {
            lastLocation = t

        }
    }

    init {
        orientationRepository.orientation().map { it.rotationMatrix }.subscribe(rotationMatrixSmoother)

        subscriptions.add(rotationMatrixSmoother.smoothedRotationMatrix.subscribe { rotationMatrix ->
            tmpMatrix.set(rotationMatrix).invert()
            tmpQuaternion.setFromUnnormalized(tmpMatrix)
            modelRotationSubject.onNext(Quaternionf(tmpQuaternion))
        })
    }

    fun convert(celestialLocation: CelestialLocation): Vector3f? {
        return lastLocation?.let {
            Vector3f(1f, 0f, 0f)
        }
    }

    fun onCleared() {
        subscriptions.clear()
        rotationMatrixSmoother.dispose()
    }
}