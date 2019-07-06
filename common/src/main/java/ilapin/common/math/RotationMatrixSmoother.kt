package ilapin.common.math

import ilapin.common.rx.BaseObserver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Vector3f
import org.joml.Vector3fc
import java.util.*

class RotationMatrixSmoother(private val windowSize: Int) : BaseObserver<Matrix4fc>(), Disposable {

    private val rotationMatrixSubject = PublishSubject.create<Matrix4fc>()

    private val matrices = LinkedList<Matrix4f>()

    private val tmpMatrix = Matrix4f()
    private val xAxis = Vector3f()
    private val yAxis = Vector3f()
    private val zAxis = Vector3f()

    private var disposable: Disposable? = null

    val smoothedRotationMatrix: Observable<Matrix4fc>
        get() = rotationMatrixSubject

    override fun onSubscribe(d: Disposable) {
        if (disposable != null) {
            throw IllegalStateException("Trying to subscribe more than once")
        }
        disposable = d
    }

    override fun onNext(t: Matrix4fc) {
        matrices += Matrix4f(t)
        if (matrices.size > windowSize) {
            matrices.removeFirst()
        }
        val result = matrices.fold(
            PlaneVectors(
                Vector3f(),
                Vector3f()
            )
        ) { accumulator, matrix ->
            xAxis.set(1f, 0f, 0f)
            yAxis.set(0f, 1f, 0f)
            xAxis.mulDirection(matrix)
            yAxis.mulDirection(matrix)
            PlaneVectors(xAxis.add(accumulator.x), yAxis.add(accumulator.y))
        }
        xAxis.set(result.x)
        yAxis.set(result.y)
        xAxis.div(matrices.size.toFloat())
        yAxis.div(matrices.size.toFloat())
        xAxis.cross(yAxis, zAxis)
        xAxis.normalize()
        yAxis.normalize()
        zAxis.normalize()

        tmpMatrix.identity()

        tmpMatrix.m00(xAxis.x)
        tmpMatrix.m10(xAxis.y)
        tmpMatrix.m20(xAxis.z)

        tmpMatrix.m01(yAxis.x)
        tmpMatrix.m11(yAxis.y)
        tmpMatrix.m21(yAxis.z)

        tmpMatrix.m02(zAxis.x)
        tmpMatrix.m12(zAxis.y)
        tmpMatrix.m22(zAxis.z)

        rotationMatrixSubject.onNext(tmpMatrix.invert())
    }

    override fun isDisposed(): Boolean {
        return disposable?.isDisposed ?: false
    }

    override fun dispose() {
        disposable?.dispose()
    }

    private class PlaneVectors(x: Vector3fc, y: Vector3fc) {
        private val _x = Vector3f()
        private val _y = Vector3f()

        val x: Vector3fc
            get() = _x

        val y: Vector3fc
            get() = _y

        init {
            _x.set(x)
            _y.set(y)
        }
    }
}