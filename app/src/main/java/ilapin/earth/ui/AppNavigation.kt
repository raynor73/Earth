package ilapin.earth.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import ilapin.earth.R
import ilapin.earth.ui.common.MessageDialog

class AppNavigation {

    companion object {

        fun showMagneticSensorCalibrationRequired(activity: AppCompatActivity) {
            MessageDialog
                .newInstance(
                    activity.getString(R.string.magnetic_sensor_calibration_required_title),
                    activity.getString(R.string.magnetic_sensor_calibration_required_message),
                    activity.getString(R.string.ok),
                    activity.getString(R.string.magnetic_sensor_calibration_required_how),
                    null
                )
                .show(activity.supportFragmentManager, "MessageDialog")
        }

        fun showWebPage(context: Context, urlString: String) {
            val packageManager = context.packageManager
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlString)
            )
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, R.string.no_browser_error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}