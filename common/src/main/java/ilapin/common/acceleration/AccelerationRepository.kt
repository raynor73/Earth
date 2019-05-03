package ilapin.common.acceleration

import io.reactivex.Observable

interface AccelerationRepository {

    fun acceleration(): Observable<Acceleration>
}