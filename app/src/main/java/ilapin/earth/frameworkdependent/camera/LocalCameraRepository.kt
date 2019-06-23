package ilapin.earth.frameworkdependent.camera

import ilapin.common.android.renderingengine.RenderingEngine
import ilapin.earth.domain.camera.Camera
import ilapin.earth.domain.camera.CameraRepository
import android.hardware.Camera as AndroidCamera

class LocalCameraRepository(
    private val previewTextureName: String,
    private val renderingEngine: RenderingEngine
) : CameraRepository {

    override fun openCamera(): Camera {
        return LocalCamera(AndroidCamera.open(), previewTextureName, renderingEngine)
    }
}