package io.github.narutopig.flappybird.scenes

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.narutopig.flappybird.wrapper.Util

class StartScene(startFunc: () -> Unit) : ApplicationAdapter() {
    // graphics stuff
    private lateinit var batch: SpriteBatch
    private lateinit var viewport: Viewport
    private lateinit var camera: OrthographicCamera
    private lateinit var font: BitmapFont
    private var startFunc: () -> Unit
    private var width: Int = 0
    private var height: Int = 0
    private var frames = 0

    init {
        this.startFunc = startFunc
    }

    override fun create() {
        font = BitmapFont(Gdx.files.internal("comicsans.fnt"), false)

        // display
        Gdx.graphics.setWindowedMode(1280, 720)
        Gdx.graphics.setResizable(false)
        width = Gdx.graphics.width
        height = Gdx.graphics.height
        batch = SpriteBatch()
        camera = OrthographicCamera()
        camera.setToOrtho(false, 1280f, 720f)
        viewport = FitViewport(1280f, 720f, camera)
    }

    override fun render() {
        frames++
        Gdx.graphics.setTitle("Flappy Bird | ${Gdx.graphics.framesPerSecond} FPS")
        // keyboard
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            startFunc()
        }

        // drawing
        ScreenUtils.clear(Color.SKY)
        viewport.update(1280, 720, true)
        batch.transformMatrix = camera.view
        batch.projectionMatrix = camera.projection
        camera.update()

        batch.begin()

        val str = "Press Space to Start"
        if (frames >= 30) font.data.setScale(1.25f)
        if (frames > 60) {
            font.data.setScale(1f)
            frames = 0
        }
        font.draw(batch, str, (width - Util.stringWidth(font, str)) / 2, height / 2f)

        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }
}