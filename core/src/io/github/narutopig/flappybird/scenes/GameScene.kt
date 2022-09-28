package io.github.narutopig.flappybird.scenes

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.narutopig.flappybird.wrapper.Entity
import io.github.narutopig.flappybird.wrapper.Util
import java.util.*
import kotlin.math.min
import kotlin.math.sqrt

class GameScene : ApplicationAdapter() {
    // graphics stuff
    private lateinit var batch: SpriteBatch
    private lateinit var viewport: Viewport
    private lateinit var camera: OrthographicCamera
    private lateinit var pipeTexture: Texture
    private lateinit var font: BitmapFont

    // entities
    private lateinit var flappy: Entity
    private lateinit var pipe1pos: Vector2
    private lateinit var pipe2pos: Vector2
    private var pipe1speed = Vector2(-8f, 0f)
    private var pipe2speed = Vector2(-8f, 0f)
    private val entities = mutableMapOf<UUID, Entity>()

    // constants
    private val gap = 320
    private val gravity = -8f

    // game vars
    private var score = 0
    private var pipe1point = false
    private var pipe2point = false
    private var gameOver = false
    private var width: Int = 0
    private var height: Int = 0

    private fun add(entity: Entity) {
        entities[entity.uuid] = entity
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

        pipeTexture = Texture("pipe.png")
        flappy = Entity(Texture("dt.png"))
        flappy.keepInBounds = true
        flappy.setPositionCentered(48f, height / 2f)
        flappy.timers["jump"] = 0
        flappy.speed = Vector2(0f, gravity)

        pipe1pos = Vector2(width * 1f, height / 2f)
        pipe2pos = Vector2(width * 1.5f, height / 2f)

        add(flappy)
    }

    override fun render() {
        Gdx.graphics.setTitle("Flappy Bird | ${Gdx.graphics.framesPerSecond} FPS")
        // keyboard
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            flappy.speed.y = -gravity * 0.8f
            flappy.timers["jump"] = 30
        }

        if (flappy.timers["jump"] == 0) {
            flappy.speed.y = gravity
        }

        // drawing
        ScreenUtils.clear(Color.SKY)
        viewport.update(1280, 720, true)
        batch.transformMatrix = camera.view
        batch.projectionMatrix = camera.projection
        camera.update()

        batch.begin()

        if (!gameOver) {
            entities.forEach { e ->
                e.value.update()
            }

            pipe1pos = pipe1pos.add(pipe1speed)
            pipe2pos = pipe2pos.add(pipe2speed)
        }

        if (pipe1pos.x <= flappy.righttedge() && !pipe1point) {
            score++
            pipe1point = true
        }

        if (pipe1pos.x + pipeTexture.width <= 0) {
            pipe1pos.x = width * 1.0f
            pipe1pos.y = randHeight()
            pipe1speed = Vector2(-speedScaled(), 0f)
            pipe1point = false
        }

        if (pipe2pos.x <= flappy.righttedge() && !pipe2point) {
            score++
            pipe2point = true
        }

        if (pipe2pos.x + pipeTexture.width <= 0) {
            pipe2pos.x = width * 1.0f
            pipe2pos.y = randHeight()
            pipe2speed = Vector2(-speedScaled(), 0f)
            pipe2point = false
        }

        val fbbox = flappy.boundingRectangle
        val pipe1top = Rectangle(pipe1pos.x, pipe1pos.y + gapScaled() / 2, pipeTexture.width * 1.0f, pipeTexture.height * 1.0f)
        val pipe1bottom = Rectangle(
            pipe1pos.x,
            pipe1pos.y - gapScaled() / 2 - pipeTexture.height,
            pipeTexture.width * 1.0f,
            pipeTexture.height * 1.0f
        )
        val pipe2top = Rectangle(pipe2pos.x, pipe2pos.y + gapScaled() / 2, pipeTexture.width * 1.0f, pipeTexture.height * 1.0f)
        val pipe2bottom = Rectangle(
            pipe2pos.x,
            pipe2pos.y - gapScaled() / 2 - pipeTexture.height,
            pipeTexture.width * 1.0f,
            pipeTexture.height * 1.0f
        )

        entities.forEach { e ->
            e.value.draw(batch)
        }

        println(gapScaled())

        batch.draw(pipeTexture, pipe1pos.x, pipe1pos.y + gapScaled() / 2)
        batch.draw(pipeTexture, pipe1pos.x, pipe1pos.y - gapScaled() / 2 - pipeTexture.height)
        batch.draw(pipeTexture, pipe2pos.x, pipe2pos.y + gapScaled() / 2)
        batch.draw(pipeTexture, pipe2pos.x, pipe2pos.y - gapScaled() / 2 - pipeTexture.height)

        if (fbbox.overlaps(pipe1top) || fbbox.overlaps(pipe1bottom) || fbbox.overlaps(pipe2top) || fbbox.overlaps(
                pipe2bottom
            ) || fbbox.y <= 0
        ) {
            gameOver = true
        }

        val str = "Score: $score"
        font.draw(batch, str, width / 2f - Util.stringWidth(font, str) / 2, height - 16f)

        val go = "Game Over!"
        if (gameOver) {
            font.draw(batch, go, width / 2f - Util.stringWidth(font, go) / 2, height / 2f)
        }

        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        entities.forEach { (_, u) -> u.texture.dispose() }
    }

    private fun randHeight(): Float {
        return ((height - gapScaled()) / 2 + Math.random() * gapScaled()).toFloat()
    }

    private fun speedScaled(): Float {
        return (sqrt(score.toDouble()) + 8).toFloat()
    }

    private fun gapScaled(): Int {
        return min((-2.4 * score + gap).toInt(), 160)
    }
}