package gmtkjam

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.StateMachineComponent

class Player(var lane: Int = 0) : StateMachineComponent()

class Pic : Component

class Origin(var origin: Mat4) : Component

class Ground : StateMachineComponent()
