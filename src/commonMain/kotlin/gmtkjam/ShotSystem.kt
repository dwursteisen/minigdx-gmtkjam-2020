package gmtkjam

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System

class ShotSystem : System(EntityQuery(Shot::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        entity.shot.ttl -= delta
        if (entity.shot.ttl < 0f) {
            entity.destroy()
        }
    }
}
