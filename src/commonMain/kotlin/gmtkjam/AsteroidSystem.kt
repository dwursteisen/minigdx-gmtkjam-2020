package gmtkjam

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.Node
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.createFromNode
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.physics.AABBCollisionResolver
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.random.Random

class AsteroidSystem(
    private val gameContext: GameContext,
    private val scene: Scene,
    private val engine: Engine,
    private val asteroids: MutableList<Node>
) : System(EntityQuery(Asteroid::class)) {

    private val shots: List<Entity> by interested(EntityQuery(Shot::class))

    private val players by interested(EntityQuery((Player::class)))

    private val collider = AABBCollisionResolver()

    @ExperimentalStdlibApi
    override fun onGameStart(engine: Engine) {
        (0..3).forEach {
            val asteroid = engine.createFromNode(asteroids.random(), gameContext, scene)
            asteroid.position.setGlobalTranslation(
                x = Random.nextFloat() * 24f - 12f,
                z = Random.nextFloat() * 24f - 12f
            )
            asteroid.add(Asteroid())
            asteroid.add(Offscreen(speed = 5f, rotation = Random.nextFloat() * 360f))

            println(asteroid.componentsType)
        }
    }

    @ExperimentalStdlibApi
    override fun update(delta: Seconds, entity: Entity) {
        entity.position.addLocalRotation(z = 90f, delta = delta)

        shots.filter { collider.collide(it, entity) }
            .forEach { shot ->
                if (entity.asteroid.size > 0) {
                    createNewAsteroid(entity)
                    createNewAsteroid(entity)
                }
                shot.destroy()
                entity.destroy()
            }

        if (players.any { collider.collide(it, entity) }) {
            println("You loose")
        }
    }

    @ExperimentalStdlibApi
    private fun createNewAsteroid(entity: Entity) {
        val asteroid = engine.createFromNode(asteroids.random(), gameContext, scene)
        asteroid.position.setGlobalTranslation(entity.position.translation)
        asteroid.add(Asteroid(size = entity.asteroid.size - 1))
        asteroid.add(Offscreen(speed = 5f, rotation = Random.nextFloat() * 360f))
        asteroid.position.setScale(SCALES[entity.asteroid.size - 1])
    }

    companion object {
        private val SCALES = arrayOf(
            Vector3(0.75f, 0.75f, 0.75f),
            Vector3(0.5f, 0.5f, 0.5f),
            Vector3(0.25f, 0.25f, 0.25f),
            Vector3(0.25f, 0.25f, 0.25f)
        )
    }
}
