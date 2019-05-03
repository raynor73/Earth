package ilapin.common.orientation

import io.reactivex.Observable

interface OrientationRepository {

    fun orientation(): Observable<Orientation>
}