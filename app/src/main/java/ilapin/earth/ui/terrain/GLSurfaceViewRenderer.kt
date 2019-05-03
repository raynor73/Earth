package ilapin.earth.ui.terrain

import android.content.Context
import ilapin.common.android.renderingengine.BaseGLSurfaceRenderer
import ilapin.common.messagequeue.MessageQueue
import ilapin.earth.domain.terrain.GenerateMapMessage
import ilapin.earth.domain.terrain.TerrainScene
import ilapin.common.android.time.LocalTimeRepository
import ilapin.engine3d.Scene
import io.reactivex.disposables.Disposable

class GLSurfaceViewRenderer(
    context: Context
) : BaseGLSurfaceRenderer(context) {

    private var subscription: Disposable? = null

    override fun createScene(messageQueue: MessageQueue): Scene {
        val scene = TerrainScene(
            renderingEngine,
            renderingEngine,
            renderingEngine,
            LocalTimeRepository()
        )
        subscription = messageQueue.messages().subscribe { message ->
            when (message) {
                is GenerateMapMessage -> {
                    scene.mapGenerator.also {
                        it.drawMode = message.drawMode
                        it.mapWidth = message.mapWidth
                        it.mapHeight = message.mapHeight
                        it.seed = message.seed
                        it.noiseScale = message.noiseScale
                        it.octaves = message.octaves
                        it.persistence = message.persistence
                        it.lacunarity = message.lacunarity
                        it.offset.set(message.offset)
                        it.generateMap()
                    }
                }
            }
        }
        return scene
    }

    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
    }
}