package ilapin.earth.domain.earthscene

import ilapin.engine3d.MeshComponent

interface MeshLoadingRepository {

    fun loadMesh(meshName: String): MeshComponent
}