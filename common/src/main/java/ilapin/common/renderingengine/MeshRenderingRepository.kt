package ilapin.common.renderingengine

import ilapin.engine3d.CameraComponent
import ilapin.engine3d.MeshComponent

interface MeshRenderingRepository {

    fun addMeshToRenderList(camera: CameraComponent, mesh: MeshComponent)

    fun removeMeshFromRenderList(camera: CameraComponent, mesh: MeshComponent)
}