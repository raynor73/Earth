package ilapin.common.renderingengine

import ilapin.engine3d.CameraComponent
import ilapin.engine3d.DirectionalLightComponent

interface LightsRenderingRepository {

    fun addDirectionalLight(camera: CameraComponent, light: DirectionalLightComponent)

    fun removeDirectionalLight(camera: CameraComponent, light: DirectionalLightComponent)
}