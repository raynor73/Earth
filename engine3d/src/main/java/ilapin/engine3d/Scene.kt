package ilapin.engine3d

interface Scene {

    val camera: PerspectiveCameraComponent

    fun onCleared()
}