package ilapin.earth.frameworkdependent.camera

import android.graphics.SurfaceTexture
import ilapin.common.android.log.L
import ilapin.common.android.renderingengine.RenderingEngine
import ilapin.earth.App.Companion.LOG_TAG
import ilapin.earth.domain.camera.Camera
import ilapin.earth.domain.camera.CameraPreviewSize
import java.util.concurrent.atomic.AtomicInteger
import android.hardware.Camera as AndroidCamera

class LocalCamera(
    private val camera: AndroidCamera,
    private val cameraSensorOrientation: Int,
    private val previewTextureName: String,
    private val renderingEngine: RenderingEngine
) : Camera {

    private val surfaceTexture: SurfaceTexture

    private val numberOfFramesAvailable = AtomicInteger(0)

    init {
        renderingEngine.createCameraPreviewTexture(previewTextureName)
        surfaceTexture = SurfaceTexture(renderingEngine.getTextureId(previewTextureName))
        camera.setPreviewTexture(surfaceTexture)
        setPreviewSize(getSupportedPreviewSizes()[0])
        surfaceTexture.setOnFrameAvailableListener { numberOfFramesAvailable.incrementAndGet() }
    }

    override fun getSupportedPreviewSizes(): List<CameraPreviewSize> {
        return camera.parameters.supportedPreviewSizes.map { CameraPreviewSize(it.width, it.height) }
    }

    override fun setPreviewSize(size: CameraPreviewSize) {
        camera.parameters = camera.parameters.apply { setPreviewSize(size.width, size.height) }
    }

    override fun getPreviewSize(): CameraPreviewSize {
        return camera.parameters.previewSize.let { CameraPreviewSize(it.width, it.height) }
    }

    override fun getSensorOrientation() = cameraSensorOrientation

    override fun startPreview() {
        L.d(LOG_TAG, "startPreview")
        camera.startPreview()
    }

    override fun updatePreviewIfFrameAvailable() {
        while (numberOfFramesAvailable.get() > 0) {
            surfaceTexture.updateTexImage()
            numberOfFramesAvailable.decrementAndGet()
        }
    }

    override fun stopPreview() {
        L.d(LOG_TAG, "stopPreview")
        camera.stopPreview()
    }

    override fun onCleared() {
        camera.release()
        surfaceTexture.release()
        renderingEngine.deleteTexture(previewTextureName)
    }
}