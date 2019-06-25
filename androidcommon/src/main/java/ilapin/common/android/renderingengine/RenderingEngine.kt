package ilapin.common.android.renderingengine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLUtils
import ilapin.common.renderingengine.*
import ilapin.engine3d.CameraComponent
import ilapin.engine3d.MeshComponent
import ilapin.engine3d.Scene
import org.joml.Vector3f
import org.joml.Vector3fc

class RenderingEngine(
    private val context: Context,
    private val sceneProvider: () -> Scene?
) : MeshRenderingRepository,
    RenderingSettingsRepository,
    TextureLoadingRepository,
    TextureRepository
{
    private val uniformFillingVisitor = UniformFillingVisitor(this)
    private val meshRenderers = HashMap<MeshComponent, MeshRendererComponent>()
    private val meshRendererCameras = HashMap<CameraComponent, MeshRendererComponent>()

    private val textureIds = HashMap<String, Int>()
    private val textureIdsToDelete = IntArray(1)
    private val textureIdsOut = IntArray(1)

    private val _ambientColor = Vector3f()

    private val ambientShader = AmbientShader(context)
    private val cameraShader = CameraShader(context)

    val ambientColor: Vector3fc
        get() = _ambientColor

    init {
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        GLES20.glFrontFace(GLES20.GL_CCW)
        GLES20.glCullFace(GLES20.GL_BACK)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        createTexture(FALLBACK_TEXTURE_NAME, 1, 1, intArrayOf(0xffff00ff.toInt()))
    }

    override fun setClearColor(red: Float, green: Float, blue: Float, alpha: Float) {
        GLES20.glClearColor(red, green, blue, alpha)
    }

    override fun setAmbientColor(red: Float, green: Float, blue: Float) {
        _ambientColor.set(red, green, blue)
    }

    override fun addMeshToRenderList(camera: CameraComponent, mesh: MeshComponent) {
        val gameObject = mesh.gameObject ?: throw NoParentGameObjectError()
        val meshRendererComponent = MeshRendererComponent(uniformFillingVisitor)
        gameObject.addComponent(meshRendererComponent)
        meshRenderers[mesh] = meshRendererComponent
        meshRendererCameras[camera] = meshRendererComponent
    }

    override fun removeMeshFromRenderList(camera: CameraComponent, mesh: MeshComponent) {
        if (meshRenderers.remove(mesh) == null) {
            throw IllegalArgumentException("Can't find mesh renderer to remove")
        }
        if (meshRendererCameras.remove(camera) == null) {
            throw IllegalArgumentException("Can't find mesh renderer's camera to remove")
        }
    }

    override fun loadTexture(textureName: String) {
        deleteTextureIfExists(textureName)

        GLES20.glGenTextures(1, textureIdsOut, 0)
        textureIds[textureName] = textureIdsOut[0]

        val bitmapStream = context.assets.open(textureName)
        val bitmap = BitmapFactory.decodeStream(bitmapStream)
        bitmapStream.close()

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIdsOut[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    override fun createTexture(textureName: String, width: Int, height: Int, data: IntArray) {
        deleteTextureIfExists(textureName)

        GLES20.glGenTextures(1, textureIdsOut, 0)
        textureIds[textureName] = textureIdsOut[0]

        val bitmap = Bitmap.createBitmap(data, width, height, Bitmap.Config.ARGB_8888)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIdsOut[0])
        /*GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)*/
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    fun createCameraPreviewTexture(textureName: String) {
        deleteTextureIfExists(textureName)

        GLES20.glGenTextures(1, textureIdsOut, 0)
        textureIds[textureName] = textureIdsOut[0]
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIdsOut[0])
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)
    }

    override fun deleteTexture(textureName: String) {
        textureIdsToDelete[0] = getTextureId(textureName)
        GLES20.glDeleteTextures(1, textureIdsToDelete, 0)
    }

    fun getTextureIdOrFallback(textureName: String): Int {
        return textureIds[textureName] ?: getTextureId(FALLBACK_TEXTURE_NAME)
    }

    fun getTextureId(textureName: String): Int {
        return textureIds[textureName] ?: throw IllegalArgumentException("Unknown texture name: $textureName")
    }

    fun render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        sceneProvider.invoke()?.cameras?.forEach { camera ->
            meshRenderers.values.forEach { it.render(camera, ambientShader) }
        }
    }

    fun onScreenConfigUpdate(width: Int, height: Int) {
        sceneProvider.invoke()?.onScreenConfigUpdate(width, height)
    }

    private fun deleteTextureIfExists(textureName: String) {
        textureIds[textureName]?.let {
            textureIdsToDelete[0] = it
            GLES20.glDeleteTextures(1, textureIdsToDelete, 0)
        }
    }

    companion object {

        private const val FALLBACK_TEXTURE_NAME = "fallbackTexture"
    }
}