package ilapin.common.magneticfield

import io.reactivex.Observable

interface MagneticFieldRepository {

    fun magneticField(): Observable<MagneticField>
}