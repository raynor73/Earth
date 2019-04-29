package ilapin.engine3d

import org.joml.Vector2f
import org.joml.Vector3f

class MeshFactory {

    companion object {

        fun createVerticalPlane(gridCellSize: Float, gridSize: Int): MeshComponent {
            val numberOfVerticesInStripe = (gridSize + 1) * 2
            val primaryStripeIndicesPattern = arrayOf(0, 1, 3, 3, 2, 0)
            val intermediateStripeIndicesPatter = arrayOf(
                primaryStripeIndicesPattern[0] + 1,
                primaryStripeIndicesPattern[1] + numberOfVerticesInStripe - 1,
                primaryStripeIndicesPattern[2] + numberOfVerticesInStripe - 1,
                primaryStripeIndicesPattern[3] + numberOfVerticesInStripe - 1,
                primaryStripeIndicesPattern[4] + 1,
                primaryStripeIndicesPattern[5] + 1
            )

            val vertices = ArrayList<Vector3f>()
            val normals = ArrayList<Vector3f>()
            val uvs = ArrayList<Vector2f>()
            val indices = ArrayList<Int>()

            val planeSize = gridCellSize * gridSize
            val planeHalfSize = planeSize / 2

            val numberOfBaseRows = Math.ceil((gridSize / 2f).toDouble()).toInt()
            var y = planeHalfSize
            var v = 0f
            for (baseRow: Int in 0 until numberOfBaseRows) {
                var x = -planeHalfSize
                var u = 0f
                val baseIndex = vertices.size

                for (column: Int in 0..gridSize) {
                    // Build intermediate stripe
                    if (baseRow > 0 && column > 0) {
                        intermediateStripeIndicesPatter.forEach { index ->
                            indices.add(index + (column - 1) * 2 + baseIndex - numberOfVerticesInStripe)
                        }
                    }

                    // Build primary stripe
                    vertices.add(Vector3f(x, y, 0f))
                    normals.add(Vector3f(0f, 0f, -1f))
                    uvs.add(Vector2f(u, v))

                    vertices.add(Vector3f(x, y - gridCellSize, 0f))
                    normals.add(Vector3f(0f, 0f, -1f))
                    uvs.add(Vector2f(u, v + 1))

                    if (column > 0) {
                        primaryStripeIndicesPattern.forEach { index ->
                            indices.add(index + (column - 1) * 2 + baseIndex)
                        }
                    }

                    x += gridCellSize
                    u += 1
                }

                y -= 2 * gridCellSize
                v += 2
            }

            return MeshComponent(vertices, normals, uvs, indices)
        }

        fun createHorizontalPlane(gridCellSize: Float, gridSize: Int): MeshComponent {
            val numberOfVerticesInStripe = (gridSize + 1) * 2
            val primaryStripeIndicesPattern = arrayOf(0, 1, 3, 3, 2, 0)
            val intermediateStripeIndicesPatter = arrayOf(
                primaryStripeIndicesPattern[0] + 1,
                primaryStripeIndicesPattern[1] + numberOfVerticesInStripe - 1,
                primaryStripeIndicesPattern[2] + numberOfVerticesInStripe - 1,
                primaryStripeIndicesPattern[3] + numberOfVerticesInStripe - 1,
                primaryStripeIndicesPattern[4] + 1,
                primaryStripeIndicesPattern[5] + 1
            )

            val vertices = ArrayList<Vector3f>()
            val normals = ArrayList<Vector3f>()
            val uvs = ArrayList<Vector2f>()
            val indices = ArrayList<Int>()

            val planeSize = gridCellSize * gridSize
            val planeHalfSize = planeSize / 2

            val numberOfBaseRows = Math.ceil((gridSize / 2f).toDouble()).toInt()
            var y = planeHalfSize
            var v = 0f
            for (baseRow: Int in 0 until numberOfBaseRows) {
                var x = -planeHalfSize
                var u = 0f
                val baseIndex = vertices.size

                for (column: Int in 0..gridSize) {
                    // Build intermediate stripe
                    if (baseRow > 0 && column > 0) {
                        intermediateStripeIndicesPatter.forEach { index ->
                            indices.add(index + (column - 1) * 2 + baseIndex - numberOfVerticesInStripe)
                        }
                    }

                    // Build primary stripe
                    vertices.add(Vector3f(x, 0f, -y))
                    normals.add(Vector3f(0f, 0f, -1f))
                    uvs.add(Vector2f(u, v))

                    vertices.add(Vector3f(x, 0f, -(y - gridCellSize)))
                    normals.add(Vector3f(0f, 0f, -1f))
                    uvs.add(Vector2f(u, v + 1))

                    if (column > 0) {
                        primaryStripeIndicesPattern.forEach { index ->
                            indices.add(index + (column - 1) * 2 + baseIndex)
                        }
                    }

                    x += gridCellSize
                    u += 1
                }

                y -= 2 * gridCellSize
                v += 2
            }

            return MeshComponent(vertices, normals, uvs, indices)
        }

        fun createVerticalQuad(): MeshComponent {
            return MeshComponent(
                listOf(Vector3f(1f, 1f, 0f), Vector3f(-1f, 1f, 0f), Vector3f(-1f, -1f, 0f), Vector3f(1f, -1f, 0f)),
                listOf(Vector3f(0f, 0f, -1f), Vector3f(0f, 0f, -1f), Vector3f(0f, 0f, -1f), Vector3f(0f, 0f, -1f)),
                listOf(Vector2f(1f, 0f), Vector2f(0f, 0f), Vector2f(0f, 1f), Vector2f(1f, 1f)),
                listOf(0, 1, 2, 2, 3, 0)
            )
        }
    }
}