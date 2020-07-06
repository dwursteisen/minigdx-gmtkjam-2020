package com.github.dwursteisen.minigdx.demo

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.perspective
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.render.Camera
import com.github.dwursteisen.minigdx.render.MeshPrimitive

@ExperimentalStdlibApi
class CameraScreen(private val screen: com.github.dwursteisen.minigdx.Screen) : Screen {

    private val spaceship: Scene by fileHandler.get("cameras.protobuf")

    override fun createEntities(engine: Engine) {
        // Create the player model
        spaceship.models.values.forEach { player ->
            engine.create {
                player.mesh.primitives.forEach { primitive ->
                    add(
                        MeshPrimitive(
                            primitive = primitive,
                            material = spaceship.materials.values.first { it.id == primitive.materialId }
                        )
                    )
                }
                val transformation = Mat4.fromColumnMajor(*player.transformation.matrix)
                add(Position(transformation))
            }
        }

        // Create the camera
        val (_, _, camera) = spaceship.perspectiveCameras.values.toList()
        camera as PerspectiveCamera
        engine.create {
            add(
                Camera(
                    projection = perspective(
                        fov = camera.fov,
                        aspect = screen.width / screen.height.toFloat(),
                        near = camera.near,
                        far = camera.far
                    )
                )
            )
            val transformation = Mat4.fromColumnMajor(*camera.transformation.matrix)
            add(
                Position(
                    transformation = transformation,
                    way = -1f
                )
            )
        }
    }

    override fun createSystems(): List<System> {
        return emptyList()
    }
}

@ExperimentalStdlibApi
class CameraDemo(gl: GL) : GameSystem(gl, CameraScreen(gl.screen))
