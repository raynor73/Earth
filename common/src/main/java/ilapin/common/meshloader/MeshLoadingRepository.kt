package ilapin.common.meshloader

import ilapin.engine3d.MeshComponent

interface MeshLoadingRepository {

    fun loadMesh(meshName: String): MeshComponent
}