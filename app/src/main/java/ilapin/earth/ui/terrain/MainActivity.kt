package ilapin.earth.ui.terrain

import android.content.res.Configuration
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import ilapin.earth.R
import ilapin.earth.domain.terrain.GenerateMapMessage
import ilapin.earth.domain.terrain.MapGenerator
import kotlinx.android.synthetic.main.activity_main.*
import org.joml.Vector2f
import org.joml.Vector2fc

class MainActivity : AppCompatActivity(), GenerateMapDialog.Listener {

    private var renderer: GLSurfaceViewRenderer? = null

    private var drawMode = MapGenerator.DrawMode.COLOR_MAP
    private var mapWidth = 100
    private var mapHeight = 100
    private var seed = 21
    private var noiseScale = 27.6f
    private var octaves = 4
    private var persistence = 0.5f
    private var lacunarity = 1.87f
    private val offset = Vector2f(13.4f, 6.26f)

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

            sendGenerateMapMessage()

            increaseButton.setOnClickListener {
                noiseScale += 1f
                sendGenerateMapMessage()
            }
            decreaseButton.setOnClickListener {
                noiseScale -= 1f
                sendGenerateMapMessage()
            }
        }

        ArrayAdapter.createFromResource(this, R.array.draw_modes, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            drawModeSelectorView.adapter = adapter
            drawModeSelectorView.setSelection(1)
            drawModeSelectorView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    drawMode = when (position) {
                        1 -> MapGenerator.DrawMode.COLOR_MAP
                        else -> MapGenerator.DrawMode.NOISE_MAP
                    }
                    sendGenerateMapMessage()
                }
            }
        }
    }

    override fun onMapParamsReceived(
        mapWidth: Int,
        mapHeight: Int,
        seed: Int,
        noiseScale: Float,
        octaves: Int,
        persistence: Float,
        lacunarity: Float,
        offset: Vector2fc
    ) {
        this.mapWidth = mapWidth
        this.mapHeight = mapHeight
        this.seed = seed
        this.noiseScale = noiseScale
        this.octaves = octaves
        this.persistence = persistence
        this.lacunarity = lacunarity
        this.offset.set(offset)

        sendGenerateMapMessage()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.generateMapMenuItem -> {
                GenerateMapDialog.newInstance(
                    mapWidth,
                    mapHeight,
                    seed,
                    noiseScale,
                    octaves,
                    persistence,
                    lacunarity,
                    offset
                )
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

    private fun sendGenerateMapMessage() {
        renderer?.putMessage(GenerateMapMessage(
            drawMode, mapWidth, mapHeight, seed, noiseScale, octaves, persistence, lacunarity, offset
        ))
    }
}
