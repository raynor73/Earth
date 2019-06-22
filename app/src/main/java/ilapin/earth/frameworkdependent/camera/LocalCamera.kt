package ilapin.earth.frameworkdependent.camera

import ilapin.earth.domain.camera.Camera
import ilapin.earth.domain.camera.CameraPreviewSize
import android.hardware.Camera as AndroidCamera

class LocalCamera(private val camera: AndroidCamera) : Camera {

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

    override fun release() {
        camera.release()
    }
}