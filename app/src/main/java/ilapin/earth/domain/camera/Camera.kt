package ilapin.earth.domain.camera

interface Camera {

    fun getSupportedPreviewSizes(): List<CameraPreviewSize>

    fun setPreviewSize(size: CameraPreviewSize)

    fun startPreview()

    fun updatePreviewIfFrameAvailable()

    fun stopPreview()

    fun onCleared()
}