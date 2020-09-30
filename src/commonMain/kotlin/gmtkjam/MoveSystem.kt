package gmtkjam

import com.curiouscreature.kotlin.math.Quaternion
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System

class MoveSystem : System(EntityQuery(Offscreen::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        val position = entity.position

        // save the previous rotation
        val previousRotation = position.quaternion.copy()
        if (!entity.hasComponent(Player::class)) {
            position.setLocalRotation(Quaternion.identity())
                .addLocalRotation(y = entity.offscreen.rotation, delta = 1f)
        }

        position.addLocalTranslation(z = -entity.offscreen.speed, delta = delta)

        val globalPosition = position.transformation.position

        if (globalPosition.x > LIMIT) {
            position.setGlobalTranslation(x = -LIMIT)
        } else if (globalPosition.x < -LIMIT) {
            position.setGlobalTranslation(x = LIMIT)
        }

        if (globalPosition.z > LIMIT) {
            position.setGlobalTranslation(z = -LIMIT)
        } else if (globalPosition.z < -LIMIT) {
            position.setGlobalTranslation(z = LIMIT)
        }

        if (!entity.hasComponent(Player::class)) {
            position.setLocalRotation(previousRotation)
        }
    }

    companion object {
        const val LIMIT = 12f
    }
}
