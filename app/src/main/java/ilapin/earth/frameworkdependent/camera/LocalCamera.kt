package ilapin.earth.frameworkdependent.camera

import android.graphics.SurfaceTexture
import ilapin.common.android.renderingengine.RenderingEngine
import ilapin.earth.domain.camera.Camera
import ilapin.earth.domain.camera.CameraPreviewSize
import android.hardware.Camera as AndroidCamera

class LocalCamera(
    private val camera: AndroidCamera,
    private val previewTextureName: String,
    private val renderingEngine: RenderingEngine
) : Camera {

    private val surfaceTexture: SurfaceTexture

    @Volatile
    private var _isFrameAvailable: Boolean = false

    init {
        renderingEngine.createCameraPreviewTexture(previewTextureName)
        surfaceTexture = SurfaceTexture(renderingEngine.getTextureId(previewTextureName))
        camera.setPreviewTexture(surfaceTexture)
        surfaceTexture.setOnFrameAvailableListener { _isFrameAvailable = true }
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

    override fun updatePreviewIfFrameAvailable() {
        if (_isFrameAvailable) {
            surfaceTexture.updateTexImage()
            _isFrameAvailable = false
        }
    }

    override fun stopPreview() {
        camera.stopPreview()
    }

    override fun onCleared() {
        camera.release()
        surfaceTexture.release()
        renderingEngine.deleteTexture(previewTextureName)
    }
}