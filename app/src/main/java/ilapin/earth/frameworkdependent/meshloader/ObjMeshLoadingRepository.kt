package ilapin.earth.frameworkdependent.meshloader

import android.content.Context
import de.javagl.obj.ObjReader
import de.javagl.obj.ObjUtils
import ilapin.earth.domain.earthscene.MeshLoadingRepository
import ilapin.engine3d.MeshComponent

class ObjMeshLoadingRepository(private val context: Context) : MeshLoadingRepository {

    override fun loadMesh(meshName: String): MeshComponent {
        val inputStream = context.assets.open(meshName)
        val mesh = ObjUtils.triangulate(ObjReader.read(inputStream)).toMesh()
        inputStream.close()
        return mesh
    }
}