package ilapin.engine3d

interface Scene {

    val cameras: List<CameraComponent>

    fun update()

    fun onScreenConfigUpdate(width: Int, height: Int)

    fun onCleared()
}