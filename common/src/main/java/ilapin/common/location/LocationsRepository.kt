package ilapin.common.location

import io.reactivex.Observable

interface LocationsRepository {

    fun locations(): Observable<Location>
}