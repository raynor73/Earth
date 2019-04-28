package ilapin.engine3d

import org.joml.Vector2f
import org.joml.Vector2fc
import org.joml.Vector3f
import org.joml.Vector3fc

class MeshComponent(
    vertices: List<Vector3fc>,
    normals: List<Vector3fc>,
    uvs: List<Vector2f>,
    indices: List<Int>
) : GameObjectComponent() {

    private val _vertices = ArrayList<Vector3f>()
    private val _normals = ArrayList<Vector3f>()
    private val _uvs = ArrayList<Vector2f>()
    private val _indices = ArrayList<Int>()

    init {
        vertices.forEach { vertex -> _vertices += Vector3f(vertex) }
        normals.forEach { normal -> _normals += Vector3f(normal) }
        uvs.forEach { uv -> _uvs += Vector2f(uv) }
        _indices += indices
    }

    val vertices: List<Vector3fc> = _vertices
    val normals: List<Vector3fc> = _normals
    val uvs: List<Vector2fc> = _uvs
    val indices: List<Int> = _indices
}