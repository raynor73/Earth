package ilapin.engine3d

import org.joml.Matrix4fc

abstract class CameraComponent : GameObjectComponent() {

    abstract fun getViewProjectionMatrix(): Matrix4fc?
}