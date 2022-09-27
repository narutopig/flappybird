package io.github.narutopig.flappybird

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import io.github.narutopig.flappybird.scenes.GameScene
import io.github.narutopig.flappybird.scenes.StartScene
import kotlin.system.exitProcess

class Main : ApplicationAdapter() {
    private val scenes = mutableMapOf<String, ApplicationAdapter>()
    private var currScene = "start"

    init {
        scenes["start"] = StartScene { start() }
        scenes["game"] = GameScene()
    }

    private fun start() {
        currScene = "game"
    }

    override fun create() {
        scenes.forEach { (_, u) -> u.create() }
    }

    override fun render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            exitProcess(0)
        }
        scenes[currScene]!!.render()
    }

    override fun dispose() {
        scenes[currScene]!!.dispose()
    }
}