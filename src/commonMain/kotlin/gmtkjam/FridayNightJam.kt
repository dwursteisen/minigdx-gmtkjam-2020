package gmtkjam

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.model.Box
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.api.toMat4
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.createFromNode
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.Screen


@ExperimentalStdlibApi
class FridayNightJam(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("assets.protobuf")

    private val log = gameContext.logger

    override fun createEntities(engine: Engine) {
        log.info("CREATE_ENTITIES") { "Create Entities" }
        scene.children.forEach {
            val entity = engine.createFromNode(it, gameContext, scene)
            if (it.name == "player") {
                log.info("CREATE_ENTITIES") { "Create Player" }
                entity.add(Player())
            } else if (it.name.startsWith("pic")) {
                log.info("CREATE_ENTITIES") { "Create Pic" }
                entity.add(Pic())
                entity.add(Ground())
            } else if (!it.name.startsWith("Camera")) {
                log.info("CREATE_ENTITIES") { "Create Ground" }
                entity.add(Ground())
            }
            entity.add(Origin(it.transformation.toMat4()))
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(
            PlayerSystem(gameContext.input),
            GroundSystem()
        ) + super.createSystems(engine)
    }
}
