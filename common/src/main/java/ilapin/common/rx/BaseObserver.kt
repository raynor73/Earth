package ilapin.common.rx

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class BaseObserver<T> : Observer<T> {

    override fun onComplete() {
        // do nothing
    }

    override fun onSubscribe(d: Disposable) {
        // do nothing
    }

    override fun onNext(t: T) {
        // do nothing
    }

    override fun onError(e: Throwable) {
        // do nothing
    }
}