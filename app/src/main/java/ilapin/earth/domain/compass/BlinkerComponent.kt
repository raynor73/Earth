package ilapin.earth.domain.compass

import ilapin.common.android.renderingengine.MeshRendererComponent
import ilapin.common.time.TimeRepository
import ilapin.engine3d.GameObjectComponent

class BlinkerComponent(private val timeRepository: TimeRepository, private val period: Long) : GameObjectComponent() {

    private var prevTimestamp: Long? = null
    private var elapsedTime = 0L

    private var isVisible = true
    private var isBlinking = false

    fun startBlinking() {
        if (isBlinking) {
            return
        }

        isBlinking = true
        prevTimestamp = null
        elapsedTime = 0
    }

    fun stopBlinking() {
        if (!isBlinking) {
            return
        }

        isBlinking = false
        isVisible = true
        gameObject?.getComponent(MeshRendererComponent::class.java)?.isEnabled = isVisible
    }

    override fun update() {
        super.update()

        if (!isBlinking) {
            return
        }

        val currentTimestamp = timeRepository.getTimestamp()
        prevTimestamp?.let {
            elapsedTime += currentTimestamp - it
            if (elapsedTime / 1000000 >= period) {
                toggle()
                elapsedTime = 0
            }
        }
        prevTimestamp = currentTimestamp
    }

    private fun toggle() {
        isVisible = !isVisible
        gameObject?.getComponent(MeshRendererComponent::class.java)?.isEnabled = isVisible
    }
}