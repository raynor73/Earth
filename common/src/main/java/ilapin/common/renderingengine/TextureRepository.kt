package ilapin.common.renderingengine

interface TextureRepository {

    fun createTexture(textureName: String, width: Int, height: Int, data: IntArray)

    fun deleteTexture(textureName: String)
}