package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.configuration
import com.github.dwursteisen.minigdx.game.GameSystem
import gmtkjam.FridayNightJam
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window


@ExperimentalStdlibApi
class FridayNightJamGame(gameContext: GameContext) : GameSystem(gameContext, FridayNightJam(gameContext))

@ExperimentalStdlibApi
fun main() {
    val canvas = document.getElementsByTagName("canvas")[0]!!
    var rootPath = window.location.protocol + "//" + window.location.host + window.location.pathname
    // make it itchio compatible
    rootPath = rootPath.replace("index.html", "")
    configuration(GLConfiguration(
        canvas = canvas as HTMLCanvasElement,
        rootPath = rootPath,
        debug = true,
        gameName = "Friday Night"
    )
    ).execute { FridayNightJamGame(it) }
}
