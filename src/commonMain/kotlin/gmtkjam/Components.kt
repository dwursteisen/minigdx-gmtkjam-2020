package gmtkjam

import com.curiouscreature.kotlin.math.Degrees
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity

class Player(var rotation: Degrees = 0f) : Component

class Asteroid(
    var size: Int = 3
) : Component

class Offscreen(
    var speed: Float = 0f,
    var rotation: Degrees = 0f
) : Component

class Shot(
    var ttl: Seconds = 2f
) : Component

val Entity.offscreen
    get(): Offscreen {
        return this.get(Offscreen::class)
    }

val Entity.shot
    get() : Shot {
        return this.get(Shot::class)
    }

val Entity.asteroid
    get() : Asteroid {
        return this.get(Asteroid::class)
    }
