package ilapin.earth.ui

import android.content.res.Configuration
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import ilapin.earth.R
import ilapin.earth.domain.terrainscene.GenerateMapMessage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GenerateMapDialog.Listener {

    private var renderer: GLSurfaceViewRenderer? = null

    private var mapWidth = 100
    private var mapHeight = 100
    private var noiseScale = 30f
    private var octaves = 4
    private var persistence = 0.5f
    private var lacunarity = 2f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            renderer = GLSurfaceViewRenderer(this)
            val glView = GLSurfaceView(this)
            glView.setEGLContextClientVersion(2)
            glView.setRenderer(renderer)
            glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            containerLayout.addView(glView, 0)
        }
    }

    override fun onMapParamsReceived(
        mapWidth: Int,
        mapHeight: Int,
        noiseScale: Float,
        octaves: Int,
        persistence: Float,
        lacunarity: Float
    ) {
        this.mapWidth = mapWidth
        this.mapHeight = mapHeight
        this.noiseScale = noiseScale
        this.octaves = octaves
        this.persistence = persistence
        this.lacunarity = lacunarity

        renderer?.putMessage(GenerateMapMessage(mapWidth, mapHeight, noiseScale, octaves, persistence, lacunarity))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.generateMapMenuItem -> {
                GenerateMapDialog
                    .newInstance(mapWidth, mapHeight, noiseScale, octaves, persistence, lacunarity)
                    .show(supportFragmentManager, "GenerateMapDialog")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        renderer?.onCleared()
    }
}
