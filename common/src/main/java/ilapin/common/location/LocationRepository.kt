package ilapin.common.location

import io.reactivex.Observable

interface LocationRepository {

    fun location(): Observable<Location>
}