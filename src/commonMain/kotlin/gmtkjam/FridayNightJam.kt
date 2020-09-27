package gmtkjam

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.Node
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.createFromNode
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.Screen


@ExperimentalStdlibApi
class FridayNightJam(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("asteroid.protobuf")

    private val log = gameContext.logger

    private val asteroids = mutableListOf<Node>()
    private val shot = mutableListOf<Node>()

    override fun createEntities(engine: Engine) {
        log.info("CREATE_ENTITIES") { "Create Entities" }
        scene.children.forEach {
            if (it.name == "player") {
                val entity = engine.createFromNode(it, gameContext, scene)
                log.info("CREATE_ENTITIES") { "Create Player" }
                entity.add(Player())
                entity.add(Offscreen())
            } else if (it.name.startsWith("asteroid")) {
                log.info("CREATE_ENTITIES") { "Create Asteroid" }
                asteroids.add(it)
            } else if (it.name == "shot") {
                log.info("CREATE_ENTITIES") { "Saving shot Node" }
                shot.add(it)
            } else {
                // Create cameras ...
                engine.createFromNode(it, gameContext, scene)
            }
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(
            PlayerSystem(gameContext, scene, engine, gameContext.input, shot),
            AsteroidSystem(gameContext, scene, engine, asteroids),
            MoveSystem()
        ) + super.createSystems(engine)
    }
}
