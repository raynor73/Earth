package ilapin.earth.domain.camera

interface Camera {

    fun getSupportedPreviewSizes(): List<CameraPreviewSize>

    fun setPreviewSize(size: CameraPreviewSize)

    fun startPreview()

    fun stopPreview()

    fun onCleared()
}