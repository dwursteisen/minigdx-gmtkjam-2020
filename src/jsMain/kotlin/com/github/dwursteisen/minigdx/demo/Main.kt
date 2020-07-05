package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.configuration
import org.w3c.dom.Attr
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.get
import kotlin.browser.document

@ExperimentalStdlibApi
private val factory: Map<String, (GL) -> Game> = mapOf(
    "spaceship" to { gl ->
        SpaceShip(gl)
    },
    "camera" to { gl ->
        CameraDemo(gl)
    }
)

@ExperimentalStdlibApi
fun toDemo(attr: Attr?): ((GL) -> Game)? {
    val value = attr?.value
    return factory[value] ?: return null
}

@ExperimentalStdlibApi
fun main() {
    val canvas = document.getElementsByTagName("canvas")
    println(canvas.length)
    (0 until canvas.length).forEach { index ->
        canvas[index]?.run {
            val factory = toDemo(this.getAttributeNode("demo"))
            if (factory != null) {
                configuration(GLConfiguration(canvas = this as HTMLCanvasElement)).run {
                    factory(it)
                }
            }
        }
    }
}
