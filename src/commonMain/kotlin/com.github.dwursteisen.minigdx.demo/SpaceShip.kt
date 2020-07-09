package com.github.dwursteisen.minigdx.demo

import com.curiouscreature.kotlin.math.*
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.github.dwursteisen.minigdx.*
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.render.Camera
import com.github.dwursteisen.minigdx.render.MeshPrimitive
import kotlin.math.max
import kotlin.math.min


class Player(var rotation: Float = 0f) : Component
class Terrain : Component
class Bullet(var fired: Boolean = false, val player: Entity) : Component

@ExperimentalStdlibApi
class RotationSystem : System(EntityQuery(MeshPrimitive::class)) {
    override fun update(delta: Seconds, entity: Entity) {
        entity.findAll(Position::class).forEach {
            it.rotateY(10f * delta)
        }
    }
}

class PlayerControl(private val inputs: InputHandler) : System(EntityQuery(Player::class)) {

    private fun lerp(a: Float, b: Float, f: Float): Float {
        return a + f * (b - a)
    }

    override fun update(delta: Seconds, entity: Entity) {
        entity.get(Position::class).setRotationZ(0f)
        if (inputs.isKeyPressed(Key.ARROW_LEFT)) {
            entity.get(Position::class).translate(50f * delta)
            val player = entity.get(Player::class)
            player.rotation = max(-1f, player.rotation - delta)
        } else if (inputs.isKeyPressed(Key.ARROW_RIGHT)) {
            entity.get(Position::class).translate(-50f * delta)
            val player = entity.get(Player::class)
            player.rotation = min(1f, player.rotation + delta)
        }
        val player = entity.get(Player::class)
        entity.get(Position::class).setRotationZ(player.rotation * 180f)
        player.rotation = lerp(0f, player.rotation, 0.9f)
    }
}

class TerrainMove : System(EntityQuery(Terrain::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        entity.findAll(Position::class).forEach {
            it.transformation *= translation(Float3(0f, 0f, delta * -20f))

            if (it.transformation.position.z < -20f) {
                it.transformation *= translation(Float3(0f, 0f, 200f))
            }
        }
    }
}

class BulletMove(private val inputs: InputHandler) : System(EntityQuery(Bullet::class)) {

    override fun update(delta: Seconds) {
        if (inputs.isKeyJustPressed(Key.SPACE)) {
            entities.firstOrNull { !it.get(Bullet::class).fired }?.run {
                val player = this.get(Bullet::class).player
                this.get(Position::class).setTranslate(player.get(Position::class).translation)
                this.get(Bullet::class).fired = true
            }
        }
        super.update(delta)
    }

    override fun update(delta: Seconds, entity: Entity) {
        val position = entity.get(Position::class)
        if (position.transformation.position.z < 100f) {
            position.translate(z = delta * 120f)
        } else {
            entity.get(Bullet::class).fired = false
        }
    }
}

@ExperimentalStdlibApi
class SpaceshipScreen(private val gameContext: GameContext) : Screen {

    private val spaceship: Scene by gameContext.fileHandler.get("spaceship.protobuf")

    override fun createEntities(engine: Engine) {
        var playerEntity: Entity? = null
        // Create the player model
        spaceship.models["Player"]?.let { player ->
            playerEntity = engine.create {
                player.mesh.primitives.forEach { primitive ->
                    add(
                        MeshPrimitive(
                            primitive = primitive,
                            material = spaceship.materials.values.first { it.id == primitive.materialId }
                        )
                    )
                }
                add(Player())
                add(Position(Mat4.fromColumnMajor(*player.transformation.matrix)))
            }
        }

        spaceship.models["Bullet"]?.let { bullet ->
            val models = bullet.mesh.primitives.map { primitive ->
                MeshPrimitive(
                    primitive = primitive,
                    material = spaceship.materials.values.first { it.id == primitive.materialId }
                )
            }
            (0..100).forEach { _ ->
                engine.create {
                    models.forEach { add(it) }
                    // hack
                    add(Bullet(player = playerEntity!!))
                    add(Position(Mat4.fromColumnMajor(*bullet.transformation.matrix)))
                }
            }
        }

        // Create terrains
        spaceship.models["Terrain"]?.let { terrain ->
            val model = terrain.mesh.primitives.map { primitive ->
                MeshPrimitive(
                    primitive = primitive,
                    material = spaceship.materials.values.first { it.id == primitive.materialId }
                )

            }
            (0..10).forEach { index ->
                engine.create {
                    model.forEach { add(it) }
                    add(Terrain())
                    add(
                        Position(
                            Mat4.fromColumnMajor(*terrain.transformation.matrix) * translation(
                                Float3(
                                    0f,
                                    0f,
                                    index * 20f
                                )
                            )
                        )
                    )
                }
            }
        }

        // Create the camera
        spaceship.perspectiveCameras.values.forEach { camera ->
            camera as PerspectiveCamera
            engine.create {
                add(
                    Camera(
                        projection = perspective(
                            fov = camera.fov,
                            aspect = gameContext.ratio,
                            near = camera.near,
                            far = camera.far
                        )
                    )
                )
                add(
                    Position(
                        transformation = Mat4.fromColumnMajor(
                            *camera.transformation.matrix
                        ), way = -1f
                    )
                )
            }
        }
    }

    override fun createSystems(): List<System> {
        return listOf(
            PlayerControl(gameContext.input),
            TerrainMove(),
            BulletMove(gameContext.input)
        )
    }
}

@ExperimentalStdlibApi
class SpaceShip(gameContext: GameContext) : GameSystem(gameContext, SpaceshipScreen(gameContext))
