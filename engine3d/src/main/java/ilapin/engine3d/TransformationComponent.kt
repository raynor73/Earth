package ilapin.engine3d

import org.joml.Quaternionf
import org.joml.Quaternionfc
import org.joml.Vector3f
import org.joml.Vector3fc

class TransformationComponent(
    position: Vector3fc,
    rotation: Quaternionfc,
    scale: Vector3fc
) : GameObjectComponent() {

    private val _position = Vector3f()
    private val _rotation = Quaternionf()
    private val _scale = Vector3f()

    init {
        _position.set(position)
        _rotation.set(rotation)
        _scale.set(scale)
    }

    var position: Vector3fc
        get() = _position
        set(value) {
            _position.set(value)
        }

    var rotation: Quaternionfc
        get() = _rotation
        set(value) {
            _rotation.set(value)
        }

    var scale: Vector3fc
        get() = _scale
        set(value) {
            _scale.set(value)
        }
}