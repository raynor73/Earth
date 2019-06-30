package ilapin.engine3d

import org.joml.Vector3f
import org.joml.Vector3fc

class DirectionalLightComponent(color: Vector3fc) : GameObjectComponent() {

    private val _color = Vector3f()

    private val initialDirection: Vector3fc = Vector3f(0f, -1f, 0f)
    private val tmpVector = Vector3f()

    init {
        _color.set(color)
    }

    val color: Vector3fc
        get() = _color

    val direction: Vector3fc
        get() {
            val transform = gameObject?.getComponent(TransformationComponent::class.java) ?: throw IllegalArgumentException("Transformation component not found")
            tmpVector.set(initialDirection)
            tmpVector.rotate(transform.rotation)
            return tmpVector
        }
}