package ilapin.earth.frameworkdependent.meshloader

import de.javagl.obj.Obj
import ilapin.earth.domain.meshloader.NoNormalIndices
import ilapin.earth.domain.meshloader.NoTextureCoordinateIndices
import ilapin.earth.domain.meshloader.NotTriangularFace
import ilapin.engine3d.MeshComponent
import org.joml.Vector2f
import org.joml.Vector3f

private class ObjIndex(
    val vertexIndex: Int,
    val uvIndex: Int,
    val normalIndex: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObjIndex

        if (vertexIndex != other.vertexIndex) return false
        if (uvIndex != other.uvIndex) return false
        if (normalIndex != other.normalIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertexIndex
        result = 31 * result + uvIndex
        result = 31 * result + normalIndex
        return result
    }
}

fun Obj.toMesh(): MeshComponent {
    val vertices = ArrayList<Vector3f>()
    val uvs = ArrayList<Vector2f>()
    val normals = ArrayList<Vector3f>()
    val indices = ArrayList<Int>()

    val indexMap = HashMap<ObjIndex, Int>()

    var currentMeshIndex = 0
    for (i in 0 until numFaces) {
        val face = getFace(i)

        if (face.numVertices != 3) {
            throw NotTriangularFace("Face has ${face.numVertices} vertices, but should have 3")
        }

        for (j in 0 until face.numVertices) {
            if (!face.containsNormalIndices()) {
                throw NoNormalIndices()
            }
            if (!face.containsTexCoordIndices()) {
                throw NoTextureCoordinateIndices()
            }

            val objVertexIndex = face.getVertexIndex(j)
            val objUvIndex = face.getTexCoordIndex(j)
            val objNormalIndex = face.getNormalIndex(j)

            val objVertex = getVertex(objVertexIndex)
            val objUv = getTexCoord(objUvIndex)
            val objNormal = getNormal(objNormalIndex)

            val objIndex = ObjIndex(objVertexIndex, objUvIndex, objNormalIndex)

            val meshIndex = indexMap[objIndex]
            if (meshIndex != null) {
                indices += meshIndex
            } else {
                vertices += Vector3f(objVertex.x, objVertex.y, objVertex.z)
                uvs += Vector2f(objUv.x, objUv.y)
                normals += Vector3f(objNormal.x, objNormal.y, objNormal.z)
                indexMap[objIndex] = currentMeshIndex
                indices += currentMeshIndex
                currentMeshIndex++
            }
        }
    }

    return MeshComponent(vertices, normals, uvs, indices)
}
