package ilapin.earth.domain.renderingengine

interface RenderingSettingsRepository {

    fun setClearColor(red: Float, green: Float, blue: Float, alpha: Float)

    fun setAmbientColor(red: Float, green: Float, blue: Float)
}