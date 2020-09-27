package gmtkjam

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.Node
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.createFromNode
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.Key

class PlayerSystem(
    private val gameContext: GameContext,
    private val scene: Scene,
    private val engine: Engine,
    private val input: InputHandler,
    private val shot: List<Node>
) : System(EntityQuery(Player::class)) {

    private var coolDown = 0f

    @ExperimentalStdlibApi
    override fun update(delta: Seconds, entity: Entity) {
        if (coolDown > 0f) {
            coolDown -= delta
        }
        if (input.isKeyPressed(Key.SPACE) && coolDown <= 0f) {
            // create new shot
            // direction = rotation z of the model !
            coolDown = 0.2f
            val node = shot.random()
            val newShot = engine.createFromNode(node, gameContext, scene)
            newShot.add(Offscreen())
            newShot.add(Shot())
            newShot.position.setGlobalTranslation(entity.position.translation)
            newShot.position.setLocalRotation(x = 0f, y = entity.get(Player::class).rotation, z = 0f)
            newShot.offscreen.rotation = entity.get(Player::class).rotation
            newShot.offscreen.speed = 10f
            println(newShot.componentsType)
        }

        if (input.isKeyPressed(Key.ARROW_UP)) {
            val offscreen = entity.offscreen
            offscreen.speed += 10f * delta
            offscreen.speed = minOf(offscreen.speed, 50f)
            offscreen.rotation = entity.position.rotation.y
        }


        if (input.isKeyPressed(Key.ARROW_DOWN)) {
            val offscreen = entity.offscreen
            offscreen.speed -= 10f * delta
            offscreen.speed = maxOf(offscreen.speed, 0f)
            offscreen.rotation = entity.position.rotation.y
        }

        if (input.isKeyPressed(Key.ARROW_LEFT)) {
            entity.position.addLocalRotation(y = -180f, delta = delta)
            entity.get(Player::class).rotation -= 180 * delta
        } else if (input.isKeyPressed(Key.ARROW_RIGHT)) {
            entity.position.addLocalRotation(y = 180f, delta = delta)
            entity.get(Player::class).rotation += 180 * delta
        }
    }
}
