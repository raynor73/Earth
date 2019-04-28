package ilapin.earth.domain.renderingengine

import ilapin.engine3d.MeshComponent

interface MeshRenderingRepository {

    fun addMeshToRenderList(mesh: MeshComponent)
}