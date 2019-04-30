package ilapin.earth.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import ilapin.earth.R
import org.joml.Vector2f
import org.joml.Vector2fc

class GenerateMapDialog : DialogFragment() {

    private lateinit var listener: Listener

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        listener = requireActivity() as Listener
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_generate_map, null, false)

        val mapWidthInputView = view.findViewById<EditText>(R.id.mapWidthInputView)
        val mapHeightInputView = view.findViewById<EditText>(R.id.mapHeightInputView)
        val seedInputView = view.findViewById<EditText>(R.id.seedInputView)
        val noiseScaleInputView = view.findViewById<EditText>(R.id.noiseScaleInputView)
        val octavesInputView = view.findViewById<EditText>(R.id.octavesInputView)
        val persistenceInputView = view.findViewById<EditText>(R.id.persistenceInputView)
        val lacunarityInputView = view.findViewById<EditText>(R.id.lacunarityInputView)
        val offsetXInputView = view.findViewById<EditText>(R.id.offsetXInputView)
        val offsetYInputView = view.findViewById<EditText>(R.id.offsetYInputView)

        arguments?.apply {
            mapWidthInputView.setText(getInt(OLD_MAP_WIDTH_KEY).toString())
            mapHeightInputView.setText(getInt(OLD_MAP_HEIGHT_KEY).toString())
            seedInputView.setText(getInt(OLD_SEED_KEY).toString())
            noiseScaleInputView.setText(getFloat(OLD_NOISE_SCALE_KEY).toString())
            octavesInputView.setText(getInt(OLD_OCTAVES_KEY).toString())
            persistenceInputView.setText(getFloat(OLD_PERSISTENCE_KEY).toString())
            lacunarityInputView.setText(getFloat(OLD_LACUNARITY_KEY).toString())
            offsetXInputView.setText(getFloat(OLD_OFFSET_X_KEY).toString())
            offsetYInputView.setText(getFloat(OLD_OFFSET_Y_KEY).toString())
        }

        return AlertDialog.Builder(context)
            .setTitle(R.string.generate_map_title)
            .setView(view)
            .setPositiveButton(R.string.ok) { _, _ ->
                listener.onMapParamsReceived(
                    mapWidthInputView.text.toString().toInt(),
                    mapHeightInputView.text.toString().toInt(),
                    seedInputView.text.toString().toInt(),
                    noiseScaleInputView.text.toString().toFloat(),
                    octavesInputView.text.toString().toInt(),
                    persistenceInputView.text.toString().toFloat(),
                    lacunarityInputView.text.toString().toFloat(),
                    Vector2f(offsetXInputView.text.toString().toFloat(), offsetYInputView.text.toString().toFloat())
                )
            }
            .create()
    }

    interface Listener {

        fun onMapParamsReceived(
            mapWidth: Int,
            mapHeight: Int,
            seed: Int,
            noiseScale: Float,
            octaves: Int,
            persistence: Float,
            lacunarity: Float,
            offset: Vector2fc
        )
    }

    companion object {

        private const val OLD_MAP_WIDTH_KEY = "GenerateMapDialog.mapWidth"
        private const val OLD_MAP_HEIGHT_KEY = "GenerateMapDialog.mapHeight"
        private const val OLD_SEED_KEY = "GenerateMapDialog.seed"
        private const val OLD_NOISE_SCALE_KEY = "GenerateMapDialog.noiseScale"
        private const val OLD_OCTAVES_KEY = "GenerateMapDialog.octaves"
        private const val OLD_PERSISTENCE_KEY = "GenerateMapDialog.persistence"
        private const val OLD_LACUNARITY_KEY = "GenerateMapDialog.lacunarity"
        private const val OLD_OFFSET_X_KEY = "GenerateMapDialog.offsetX"
        private const val OLD_OFFSET_Y_KEY = "GenerateMapDialog.offsetY"

        fun newInstance(
            mapWidth: Int,
            mapHeight: Int,
            seed: Int,
            noiseScale: Float,
            octaves: Int,
            persistence: Float,
            lacunarity: Float,
            offset: Vector2fc
        ): GenerateMapDialog {
            val fragment = GenerateMapDialog()

            fragment.arguments = Bundle().apply {
                putInt(OLD_MAP_WIDTH_KEY, mapWidth)
                putInt(OLD_MAP_HEIGHT_KEY, mapHeight)
                putInt(OLD_SEED_KEY, seed)
                putFloat(OLD_NOISE_SCALE_KEY, noiseScale)
                putInt(OLD_OCTAVES_KEY, octaves)
                putFloat(OLD_PERSISTENCE_KEY, persistence)
                putFloat(OLD_LACUNARITY_KEY, lacunarity)
                putFloat(OLD_OFFSET_X_KEY, offset.x())
                putFloat(OLD_OFFSET_Y_KEY, offset.y())
            }

            return fragment
        }
    }
}