package ilapin.earth.domain.camera

interface Camera {

    fun getSupportedPreviewSizes(): List<CameraPreviewSize>

    fun release()
}