package ilapin.engine3d

class GameObject {

    var isEnabled = true

    private var _parent: GameObject? = null
    private val children = HashSet<GameObject>()
    private val components = HashSet<GameObjectComponent>()

    val parent: GameObject?
        get() = _parent

    fun addChild(child: GameObject) {
        children += child
        child._parent = this
    }

    fun removeChild(child: GameObject) {
        children -= child
        child._parent = null
    }

    fun addComponent(component: GameObjectComponent) {
        components += component
        component.gameObject = this
    }

    fun removeComponent(component: GameObjectComponent) {
        components -= component
        component.gameObject = null
    }

    fun update() {
        if (isEnabled) {
            children.forEach { it.update() }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : GameObjectComponent> getComponent(clazz: Class<T>): T? {
        for (component in components) {
            if (component.javaClass == clazz) {
                return component as T
            }
        }

        return null
    }
}