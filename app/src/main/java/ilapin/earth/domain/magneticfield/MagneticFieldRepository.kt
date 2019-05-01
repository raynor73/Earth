package ilapin.earth.domain.magneticfield

import io.reactivex.Observable

interface MagneticFieldRepository {

    fun magneticField(): Observable<MagneticField>
}