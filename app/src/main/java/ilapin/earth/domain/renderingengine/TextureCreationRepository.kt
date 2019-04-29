package ilapin.earth.domain.renderingengine

interface TextureCreationRepository {

    fun createTexture(textureName: String, width: Int, height: Int, data: IntArray)
}