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

    private val finalPosition = Vector3f()
    private val finalRotation = Quaternionf()
    private val finalScale = Vector3f()

    init {
        _position.set(position)
        _rotation.set(rotation)
        _scale.set(scale)
    }

    var position: Vector3fc
        get() {
            calculateFinalTransformation()
            return finalPosition
        }
        set(value) {
            setDirty()
            _position.set(value)
        }

    var rotation: Quaternionfc
        get() {
            calculateFinalTransformation()
            return finalRotation
        }
        set(value) {
            setDirty()
            _rotation.set(value)
        }

    var scale: Vector3fc
        get() {
            calculateFinalTransformation()
            return finalScale
        }
        set(value) {
            setDirty()
            _scale.set(value)
        }

    fun setDirty() {
        gameObject?.children?.forEach {
            val childTransformation = it.getComponent(TransformationComponent::class.java)
                ?: throw IllegalArgumentException("No child transformation found")
            childTransformation.setDirty()
        }
        isDirty = true
    }

    private fun calculateFinalTransformation() {
        if (isDirty) {
            val parent = gameObject?.parent
            if (parent != null) {
                val parentTransformation = parent.getComponent(TransformationComponent::class.java)
                    ?: throw IllegalArgumentException("No parent transformation found")

                _rotation.mul(parentTransformation.rotation, finalRotation)

                _scale.mul(parentTransformation.scale, finalScale)

                _position.rotate(parentTransformation.rotation, finalPosition)
                finalPosition.add(parentTransformation.position)
            } else {
                finalPosition.set(_position)
                finalRotation.set(_rotation)
                finalScale.set(_scale)
            }
            isDirty = false
        }
    }
}