package ilapin.engine3d

import org.joml.Vector3f
import org.joml.Vector3fc

class DirectionalLightComponent(
    color: Vector3fc,
    direction: Vector3fc
) : GameObjectComponent() {

    private val _color = Vector3f()
    private val _direction = Vector3f()

    init {
        _color.set(color)
        _direction.set(direction)
    }

    val color: Vector3fc
        get() = _color

    val direction: Vector3fc
        get() = _direction
}