package ilapin.earth.domain.earth

sealed class OctreeNodeType(val index: Int) {
    object FrontBottomLeft : OctreeNodeType(0)
    object FrontBottomRight : OctreeNodeType(1)
    object FrontTopLeft : OctreeNodeType(2)
    object FrontTopRight : OctreeNodeType(3)
    object BackBottomLeft : OctreeNodeType(4)
    object BackBottomRight : OctreeNodeType(5)
    object BackTopLeft : OctreeNodeType(6)
    object BackTopRight : OctreeNodeType(7)
}