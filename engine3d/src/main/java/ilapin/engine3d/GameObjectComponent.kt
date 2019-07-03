package ilapin.engine3d

abstract class GameObjectComponent {

    var isEnabled = true

    var gameObject: GameObject? = null

    open fun update() {}
}
