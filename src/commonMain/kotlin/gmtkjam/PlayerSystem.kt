package gmtkjam

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.physics.AABBCollisionResolver
import com.github.dwursteisen.minigdx.ecs.states.State
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.StateMachineSystem
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.math.lerp
import kotlin.math.max
import kotlin.math.min

class PlayerSystem(val inputHandler: InputHandler) : StateMachineSystem(Player::class) {

    private val pics by interested(EntityQuery(Pic::class))

    private val collider: AABBCollisionResolver = AABBCollisionResolver()

    class Idle(val system: PlayerSystem) : State() {

        override fun onEnter(entity: Entity) {
            entity.get(Player::class).lane = 0
        }

        override fun update(delta: Seconds, entity: Entity): State? {
            val translation = entity.get(Origin::class).origin.translation
            val position = entity.get(Position::class).translation
            val origin = Vector3(translation.x, translation.y, translation.z)
            if (origin.dist(position) < 2f) {

                if (system.inputHandler.isKeyJustPressed(Key.SPACE)) {
                    return Run(system)
                }
            } else {
                entity.get(Position::class).setTranslate(
                    x = lerp(origin.x, position.x, deltaTime = delta, step = 0.95f),
                    z = lerp(origin.z, position.z, deltaTime = delta, step = 0.95f)
                )
            }

            return null
        }
    }

    class Run(val system: PlayerSystem) : State() {
        override fun onEnter(entity: Entity) {
            emitEvents(StartRunning())
            entity.get(Player::class).lane = 0
            super.onEnter(entity)
        }

        override fun update(delta: Seconds, entity: Entity): State? {
            system.pics.forEach {
                if (system.collider.collide(entity, it)) {
                    return Idle(system)
                }
            }

            if (system.inputHandler.isKeyJustPressed(Key.ARROW_LEFT)) {
                entity.get(Player::class).lane = max(-3, entity.get(Player::class).lane - 1)
            } else if (system.inputHandler.isKeyJustPressed(Key.ARROW_RIGHT)) {
                entity.get(Player::class).lane = min(3, entity.get(Player::class).lane + 1)
            }

            val translation = entity.get(Origin::class).origin.translation
            val position = entity.position.translation
            val origin = Vector3(translation.x, translation.y, translation.z)

            val lane = entity.get(Player::class).lane.toFloat()
            entity.position.setTranslate(
                x = lerp(origin.x + lane, position.x, step = 0.70f),
                z = lerp(origin.z, position.z, step = 0.70f)
            )
            return null
        }

        override fun onExit(entity: Entity) {
            emitEvents(StopRunning())
        }
    }

    override fun initialState(entity: Entity): State {
        return Idle(this)
    }
}
