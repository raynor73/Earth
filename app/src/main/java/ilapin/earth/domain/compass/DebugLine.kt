package ilapin.earth.domain.compass

import ilapin.common.renderingengine.MeshRenderingRepository
import ilapin.engine3d.*
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector3fc

class DebugLine(
    start: Vector3fc,
    end: Vector3fc,
    camera: CameraComponent,
    meshRenderingRepository: MeshRenderingRepository,
    textureName: String
) : GameObject() {

    init {
        val relativeEnd = Vector3f()
        end.sub(start, relativeEnd)
        val mesh = MeshComponent(
            listOf(Vector3f(), relativeEnd),
            listOf(Vector3f(), Vector3f()), // can be any value, not used because of unlit shader
            listOf(Vector2f(), Vector2f()),
            listOf(0, 1)
        )
        val material = MaterialComponent(textureName, isDoubleSided = true, isWireframe = true, isUnlit = true)
        val transform = TransformationComponent(start, Quaternionf().identity(), Vector3f(1f, 1f, 1f))

        addComponent(mesh)
        addComponent(material)
        addComponent(transform)

        meshRenderingRepository.addMeshToRenderList(camera, mesh)
    }
}