/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.shape

import net.katsstuff.danmakucore.data.{Quat, Vector3}
import net.katsstuff.danmakucore.shape.{Shape, ShapeResult}

class ShapeCombined(shapes: Shape*) extends Shape {

  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult = {
    val ret   = shapes.map(s => s.draw(pos, orientation, tick)).toSet
    val done  = ret.forall(_.isDone)
    val drawn = ret.flatMap(_.spawnedDanmaku)
    ShapeResult(done, drawn)
  }
}
