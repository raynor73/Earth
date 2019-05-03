package ilapin.common.renderingengine

interface RenderingSettingsRepository {

    fun setClearColor(red: Float, green: Float, blue: Float, alpha: Float)

    fun setAmbientColor(red: Float, green: Float, blue: Float)
}