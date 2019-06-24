package ilapin.common.android.renderingengine

import android.opengl.GLES20
import ilapin.engine3d.MaterialComponent

class UniformFillingVisitor(private val renderingEngine: RenderingEngine) {

    //private val bufferFloatArray = FloatArray(4)

    var material: MaterialComponent? = null

    fun visitAmbientShader(shader: AmbientShader) {
        val currentMaterial = material ?: return

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderingEngine.getTextureIdOrFallback(currentMaterial.textureName))
        GLES20.glGetUniformLocation(shader.program, "textureUniform").also { textureHandle ->
            GLES20.glUniform1i(textureHandle, 0)
        }

        GLES20.glGetUniformLocation(shader.program, "ambientColor").also { ambientColorHandle ->
            GLES20.glUniform3f(
                ambientColorHandle,
                renderingEngine.ambientColor.x(),
                renderingEngine.ambientColor.y(),
                renderingEngine.ambientColor.z()
            )
        }
    }

    /*fun visitWireframeShader(shader: WireframeShader) {
        val currentMaterial = material ?: return

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderingEngine.getTextureId(currentMaterial.textureName))
        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        GLES20.glGetUniformLocation(shader.program, "textureUniform").also { textureHandle ->
            GLES20.glUniform1i(textureHandle, 0)
        }

    }*/

    /*GLES20.glGetUniformLocation(shader.program, "colorUniform").also { colorHandle ->
        // Set color for drawing the triangle
        bufferFloatArray[0] = (currentMaterial.color ushr 24) / 255f
        bufferFloatArray[1] = ((currentMaterial.color ushr 16) and 0xff) / 255f
        bufferFloatArray[2] = ((currentMaterial.color ushr 8) and 0xff) / 255f
        bufferFloatArray[3] = (currentMaterial.color and 0xff) / 255f
        GLES20.glUniform4fv(colorHandle, 1, bufferFloatArray, 0)
    }*/
}