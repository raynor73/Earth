package ilapin.common.android.renderingengine

import android.content.Context
import ilapin.common.renderingengine.DisplayMetricsRepository

class AndroidDisplayMetricsRepository(private val context: Context) : DisplayMetricsRepository {

    override fun getPixelDensityFactor(): Float {
        return context.resources.displayMetrics.density
    }
}