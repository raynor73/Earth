package ilapin.engine3d

interface Scene {

    val camera: CameraComponent

    fun update()

    fun onScreenConfigUpdate(width: Int, height: Int)

    fun onCleared()
}