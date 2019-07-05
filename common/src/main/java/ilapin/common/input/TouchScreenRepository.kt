package ilapin.common.input

import io.reactivex.Observable

interface TouchScreenRepository {

    fun touchEvents(): Observable<TouchEvent>
}