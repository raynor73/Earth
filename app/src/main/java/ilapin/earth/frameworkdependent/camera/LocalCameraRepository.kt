package ilapin.earth.frameworkdependent.camera

import android.hardware.Camera.CameraInfo
import ilapin.common.android.renderingengine.RenderingEngine
import ilapin.earth.domain.camera.Camera
import ilapin.earth.domain.camera.CameraRepository
import android.hardware.Camera as AndroidCamera

class LocalCameraRepository(
    private val previewTextureName: String,
    private val renderingEngine: RenderingEngine
) : CameraRepository {

    override fun openCamera(): Camera {
        val id = findCameraId()
        val androidCamera = AndroidCamera.open(id)
        return LocalCamera(
            androidCamera,
            getSensorOrientation(id),
            previewTextureName,
            renderingEngine
        )
    }

    private fun findCameraId(): Int {
        val numberOfCameras = AndroidCamera.getNumberOfCameras()
        val cameraInfo = CameraInfo()
        for (i in 0 until numberOfCameras) {
            AndroidCamera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                return i
            }
        }
        throw RuntimeException("No back-facing camera found")
    }

    private fun getSensorOrientation(cameraId: Int): Int {
        val cameraInfo = CameraInfo()
        AndroidCamera.getCameraInfo(cameraId, cameraInfo)
        return cameraInfo.orientation
    }
}