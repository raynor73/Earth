package ilapin.earth.frameworkdependent.camera

import ilapin.earth.domain.camera.Camera
import ilapin.earth.domain.camera.CameraRepository
import android.hardware.Camera as AndroidCamera

class LocalCameraRepository : CameraRepository {

    override fun openCamera(): Camera {
        return LocalCamera(AndroidCamera.open())
    }
}