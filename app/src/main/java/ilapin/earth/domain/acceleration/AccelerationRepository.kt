package ilapin.earth.domain.acceleration

import io.reactivex.Observable

interface AccelerationRepository {

    fun acceleration(): Observable<Acceleration>
}