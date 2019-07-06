package ilapin.common.math

import ilapin.common.rx.BaseObserver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.joml.Vector3f
import org.joml.Vector3fc

class VectorSmoother(windowSize: Int) : BaseObserver<Vector3fc>(), Disposable {

    private val tmpVector = Vector3f()

    private val vectorSubject = PublishSubject.create<Vector3fc>()

    private val xStatistics = DescriptiveStatistics(windowSize)
    private val yStatistics = DescriptiveStatistics(windowSize)
    private val zStatistics = DescriptiveStatistics(windowSize)

    private var disposable: Disposable? = null

    val smoothedVector: Observable<Vector3fc>
        get() = vectorSubject

    override fun onSubscribe(d: Disposable) {
        if (disposable != null) {
            throw IllegalStateException("Trying to subscribe more than once")
        }
        disposable = d
    }

    override fun onNext(t: Vector3fc) {
        xStatistics.addValue(t.x().toDouble())
        yStatistics.addValue(t.y().toDouble())
        zStatistics.addValue(t.z().toDouble())

        tmpVector.x = xStatistics.mean.toFloat()
        tmpVector.y = yStatistics.mean.toFloat()
        tmpVector.z = zStatistics.mean.toFloat()

        vectorSubject.onNext(tmpVector)
    }

    override fun isDisposed(): Boolean {
        return disposable?.isDisposed ?: false
    }

    override fun dispose() {
        disposable?.dispose()
    }
}