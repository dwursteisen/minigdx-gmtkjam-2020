package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.configuration
import com.github.dwursteisen.minigdx.game.GameSystem
import gmtkjam.GmtkJamScreen
import org.w3c.dom.Attr
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.get
import kotlin.browser.document


@ExperimentalStdlibApi
class GmtkJam(gameContext: GameContext) : GameSystem(gameContext, GmtkJamScreen(gameContext))

@ExperimentalStdlibApi
fun main() {
    val canvas = document.getElementsByTagName("canvas")[0]!!
    configuration(GLConfiguration(canvas = canvas as HTMLCanvasElement)).execute { GmtkJam(it) }
}
