package io.github.narutopig.flappybird.wrapper

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout

// mostly useless rn
object Util {
    fun stringWidth(font: BitmapFont, string: String): Float {
        return GlyphLayout(font, string).width
    }
}