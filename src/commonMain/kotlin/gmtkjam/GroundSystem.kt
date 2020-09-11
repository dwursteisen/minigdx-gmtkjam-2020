package gmtkjam

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.states.State
import com.github.dwursteisen.minigdx.ecs.systems.StateMachineSystem
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.math.lerp

class GroundSystem : StateMachineSystem(Ground::class) {

    class Idle(val system: GroundSystem) : State() {
        override fun configure() {
            onEvent(StartRunning::class) {
                Run(system)
            }
        }

        override fun update(delta: Seconds, entity: Entity): State? {
            val translation = entity.get(Origin::class).origin.translation
            val position = entity.get(Position::class).translation
            val origin = Vector3(translation.x, translation.y, translation.z)
            entity.get(Position::class).setTranslate(
                x = lerp(origin.x, position.x),
                z = lerp(origin.z, position.z)
            )
            return null
        }
    }

    class Run(val system: GroundSystem) : State() {

        override fun configure() {
            onEvent(StopRunning::class) {
                Idle(system)
            }
        }

        override fun update(delta: Seconds, entity: Entity): State? {
            entity.get(Position::class).translate(z = 5f * delta)
            return null
        }
    }

    override fun initialState(entity: Entity): State {
        return Idle(this)
    }
}
