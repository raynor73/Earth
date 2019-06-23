package ilapin.earth.frameworkdependent.camera

import ilapin.common.android.renderingengine.RenderingEngine
import ilapin.earth.domain.camera.Camera
import ilapin.earth.domain.camera.CameraPreviewSize
import android.hardware.Camera as AndroidCamera

class LocalCamera(
    private val camera: AndroidCamera,
    private val previewTextureName: String,
    private val renderingEngine: RenderingEngine
) : Camera {

    init {
        renderingEngine.createCameraPreviewTexture(previewTextureName)
    }

    override fun getSupportedPreviewSizes(): List<CameraPreviewSize> {
        return camera.parameters.supportedPreviewSizes.map { CameraPreviewSize(it.width, it.height) }
    }

    override fun setPreviewSize(size: CameraPreviewSize) {
        camera.parameters = camera.parameters.apply { setPreviewSize(size.width, size.height) }
    }

    override fun startPreview() {
        camera.startPreview()
    }

    override fun stopPreview() {
        camera.stopPreview()
    }

    override fun onCleared() {
        camera.release()
        renderingEngine.deleteTexture(previewTextureName)
    }
}