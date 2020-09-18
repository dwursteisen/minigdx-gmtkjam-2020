package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.configuration
import com.github.dwursteisen.minigdx.game.GameSystem
import gmtkjam.FridayNightJam
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window


@ExperimentalStdlibApi
class FridayNightJamGame(gameContext: GameContext) : GameSystem(gameContext, FridayNightJam(gameContext))


val loadingProgress = { progress: Percent ->
    println(progress)
    val percent = progress.toFloat()
    val tag = document.getElementById("loading")
    val result = if(percent < 0.25f) {
        "\uD83C\uDD7E\uD83C\uDD7E\uD83C\uDD7E\uD83C\uDD7E"
    } else if(percent < 0.5f) {
        "\uD83C\uDD7E\uD83C\uDD7E❇️❇️"
    } else if(percent < 0.75f) {
        "\uD83C\uDD7E❇️❇️❇️"
    } else {
        "❇️❇️❇️❇️"
    }
    tag?.textContent = result
}

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
        gameName = "Friday Night",
        loadingListener = loadingProgress
    )
    ).execute { FridayNightJamGame(it) }
}
