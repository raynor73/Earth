package ilapin.engine3d

import org.joml.Matrix4f
import org.joml.Matrix4fc

class OrthoCameraComponent : CameraComponent() {

    private val projectionMatrix = Matrix4f()

    var config: Config? = null
        set(value) {
            if (value != null) {
                projectionMatrix.identity().ortho(
                    value.left,
                    value.right,
                    value.bottom,
                    value.top,
                    value.zNear,
                    value.zFar
                )
            } else {
                projectionMatrix.identity()
            }
            field = value
        }


    init {
        projectionMatrix.identity()
    }

    override fun getViewProjectionMatrix(): Matrix4fc? = projectionMatrix

    data class Config(
        val left: Float,
        val right: Float,
        val bottom: Float,
        val top: Float,
        val zNear: Float,
        val zFar: Float
    )
}