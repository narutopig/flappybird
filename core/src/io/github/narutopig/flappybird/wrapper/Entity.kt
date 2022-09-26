package io.github.narutopig.flappybird.wrapper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import java.util.*

class Entity(texture: Texture) : Sprite(texture) {
    var speed: Vector2 = Vector2()
    val uuid: UUID = UUID.randomUUID()
    val timers = mutableMapOf<String, Int>()
    var keepInBounds: Boolean = false

    fun nextPos(): Vector2 {
        return Vector2(x + speed.x, y + speed.y)
    }

    fun nextBBox(): Rectangle {
        return boundingRectangle.setPosition(nextPos())
    }

    fun update() {
        for (entry in timers) {
            if (entry.value > 0) entry.setValue(entry.value - 1)
        }

        if (keepInBounds) {
            if (bottomedge() < 0) {
                if (speed.y < 0) return
            } else if (topedge() > Gdx.graphics.height) {
                if (speed.y > 0) return
            }
        }

        translate(speed.x, speed.y)
    }

    fun setPositionCentered(x: Float, y: Float) {
        setPosition(x - width / 2, y - height / 2)
    }

    fun setPosition(vector: Vector2) {
        setPosition(vector.x, vector.y)
    }

    /**
     * If x, y are meant for rendering when the image is centered at its center,
     * returns the vector that is centered at bottom left
     * (I suck at writing)
     */
    fun centered(x: Float, y: Float): Vector2 {
        return Vector2(x - width / 2, y - height / 2)
    }

    fun leftedge(): Float {
        return boundingRectangle.x
    }

    fun righttedge(): Float {
        return leftedge() + boundingRectangle.width
    }

    fun bottomedge(): Float {
        return boundingRectangle.y
    }

    fun topedge(): Float {
        return bottomedge() + boundingRectangle.height
    }
}