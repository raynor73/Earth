package ilapin.earth.domain.orientation

import io.reactivex.Observable

interface OrientationRepository {

    fun orientation(): Observable<Orientation>
}