package ilapin.earth.domain.earth

import org.joml.Vector3f

class OctreeLodResolver(private val levelSize: Float, private val maxLevel: Int) {

    //private val worldPosition = Vector3f()
    private val position = Vector3f()
    private val _path = ArrayList<OctreeNodeType>(maxLevel + 1)

    val path: List<OctreeNodeType>
        get() = _path

    init {
        for (i: Int in 0..maxLevel) {
            _path[i] = OctreeNodeType.FrontBottomLeft
        }
    }


}