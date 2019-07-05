package ilapin.common.input

class TouchEvent(
    val action: Action,
    val x: Int,
    val y: Int
) {
    enum class Action {
        DOWN, UP, CANCEL, MOVE
    }
}