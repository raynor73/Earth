package ilapin.common.android.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import ilapin.common.location.Location
import ilapin.common.location.LocationsRepository
import io.reactivex.Observable

class AndroidLocationsRepository(private val context: Context) : LocationsRepository {

    @SuppressLint("MissingPermission")
    override fun locations(): Observable<Location> {
        return Observable.create { emitter ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val locationRequest = LocationRequest.create().apply {
                interval = 250
                fastestInterval = 250
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (emitter.isDisposed || locationResult == null) {
                        return
                    }
                    locationResult.locations.takeIf { it.size > 0 }?.last()?.let { location ->
                        emitter.onNext(
                            Location(
                                location.latitude,
                                location.longitude,
                                location.time,
                                if (location.hasAltitude()) location.altitude else null,
                                if (location.hasAccuracy()) location.accuracy else null
                            )
                        )
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            emitter.setCancellable { fusedLocationClient.removeLocationUpdates(locationCallback) }
        }
    }
}