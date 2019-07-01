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

    private var isDirty = true

    private val _position = Vector3f()
    private val _rotation = Quaternionf()
    private val _scale = Vector3f()

    init {
        _position.set(position)
        _rotation.set(rotation)
        _scale.set(scale)
    }

    var position: Vector3fc
        get() {
            if (isDirty) {
                gameObject?.parent?.let { parent ->
                    val parentTransformation = parent.getComponent(TransformationComponent::class.java) ?: throw IllegalArgumentException("No parent transformation found")
                    calculateFinalTransformation(parentTransformation)
                }
                isDirty = false
            }
            return _position
        }
        set(value) {
            setDirty()
            _position.set(value)
        }

    var rotation: Quaternionfc
        get() {
            if (isDirty) {
                gameObject?.parent?.let { parent ->
                    val parentTransformation = parent.getComponent(TransformationComponent::class.java) ?: throw IllegalArgumentException("No parent transformation found")
                    calculateFinalTransformation(parentTransformation)
                }
                isDirty = false
            }
            return _rotation
        }
        set(value) {
            setDirty()
            _rotation.set(value)
        }

    var scale: Vector3fc
        get() {
            if (isDirty) {
                gameObject?.parent?.let { parent ->
                    val parentTransformation = parent.getComponent(TransformationComponent::class.java) ?: throw IllegalArgumentException("No parent transformation found")
                    calculateFinalTransformation(parentTransformation)
                }
                isDirty = false
            }
            return _scale
        }
        set(value) {
            setDirty()
            _scale.set(value)
        }

    fun setDirty() {
        if (!isDirty) {
            gameObject?.children?.forEach {
                val childTransformation = it.getComponent(TransformationComponent::class.java)
                    ?: throw IllegalArgumentException("No child transformation found")
                childTransformation.setDirty()
            }
        }
        isDirty = true
    }

    private fun calculateFinalTransformation(parentTransformation: TransformationComponent) {
        _rotation.mul(parentTransformation.rotation)
        _scale.mul(parentTransformation.scale)
        _position.rotate(parentTransformation.rotation)
        _position.add(parentTransformation.position)
    }
}