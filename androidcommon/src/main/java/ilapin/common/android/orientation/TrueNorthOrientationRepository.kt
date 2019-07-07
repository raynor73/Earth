package ilapin.common.android.orientation

import android.hardware.GeomagneticField
import ilapin.common.location.Location
import ilapin.common.location.LocationsRepository
import ilapin.common.orientation.Orientation
import ilapin.common.orientation.OrientationRepository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.joml.Matrix4f

class TrueNorthOrientationRepository(
    private val orientationRepository: OrientationRepository,
    private val locationsRepository: LocationsRepository
) : OrientationRepository {

    override fun orientation(): Observable<Orientation> {
        return Observable.combineLatest(
            orientationRepository.orientation(),
            locationsRepository.locations(),
            BiFunction<Orientation, Location, Orientation> { originalOrientation, location ->
                val altitude = location.altitude
                if (altitude != null) {
                    val geomagneticField = GeomagneticField(
                        location.latitude.toFloat(),
                        location.longitude.toFloat(),
                        altitude.toFloat(),
                        location.timestamp
                    )
                    val rotationMatrix = Matrix4f(originalOrientation.rotationMatrix)
                    rotationMatrix.rotateY(-Math.toRadians(geomagneticField.declination.toDouble()).toFloat())
                    Orientation(
                        originalOrientation.inclinationMatrix,
                        rotationMatrix,
                        originalOrientation.azimuth,
                        originalOrientation.pitch,
                        originalOrientation.roll,
                        originalOrientation.magneticFieldAccuracy,
                        originalOrientation.timestamp
                    )
                } else {
                    Orientation(
                        originalOrientation.inclinationMatrix,
                        originalOrientation.rotationMatrix,
                        originalOrientation.azimuth,
                        originalOrientation.pitch,
                        originalOrientation.roll,
                        originalOrientation.magneticFieldAccuracy,
                        originalOrientation.timestamp
                    )
                }
            }
        )
    }
}