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
        val noiseScaleInputView = view.findViewById<EditText>(R.id.noiseScaleInputView)

        arguments?.apply {
            mapWidthInputView.setText(getInt(OLD_MAP_WIDTH_KEY).toString())
            mapHeightInputView.setText(getInt(OLD_MAP_HEIGHT_KEY).toString())
            noiseScaleInputView.setText(getFloat(OLD_NOISE_SCALE_KEY).toString())
        }

        return AlertDialog.Builder(context)
            .setTitle(R.string.generate_map_title)
            .setView(view)
            .setPositiveButton(R.string.ok) { dialog, which ->
                listener.onMapParamsReceived(
                    mapWidthInputView.text.toString().toInt(),
                    mapHeightInputView.text.toString().toInt(),
                    noiseScaleInputView.text.toString().toFloat()
                )
            }
            .create()
    }

    interface Listener {

        fun onMapParamsReceived(mapWidth: Int, mapHeight: Int, noiseScale: Float)
    }

    companion object {

        private const val OLD_MAP_WIDTH_KEY = "GenerateMapDialog.mapWidth"
        private const val OLD_MAP_HEIGHT_KEY = "GenerateMapDialog.mapHeight"
        private const val OLD_NOISE_SCALE_KEY = "GenerateMapDialog.noiseScale"

        fun newInstance(mapWidth: Int, mapHeight: Int, noiseScale: Float): GenerateMapDialog {
            val fragment = GenerateMapDialog()

            fragment.arguments = Bundle().apply {
                putInt(OLD_MAP_WIDTH_KEY, mapWidth)
                putInt(OLD_MAP_HEIGHT_KEY, mapHeight)
                putFloat(OLD_NOISE_SCALE_KEY, noiseScale)
            }

            return fragment
        }
    }
}