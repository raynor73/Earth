package ilapin.earth.domain.camera

interface Camera {

    fun getSupportedPreviewSizes(): List<CameraPreviewSize>

    fun getSensorOrientation(): Int

    fun setPreviewSize(size: CameraPreviewSize)

    fun getPreviewSize(): CameraPreviewSize

    fun startPreview()

    fun updatePreviewIfFrameAvailable()

    fun stopPreview()

    fun onCleared()
}