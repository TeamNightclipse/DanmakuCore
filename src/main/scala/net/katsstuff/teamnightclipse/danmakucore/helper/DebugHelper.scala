/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.helper

import java.lang.reflect.Field

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.debug.{DebugRenderer, DebugRendererPathfinding}
import net.minecraft.pathfinding.Path
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException
import net.minecraftforge.fml.relauncher.{ReflectionHelper, Side, SideOnly}

object DebugHelper {
  @SideOnly(Side.CLIENT)
  private var pathTarget: Field = _

  private var triedToFindPathTarget = false

  @SideOnly(Side.CLIENT)
  def renderPath(path: Path, entityId: Int): Unit = {
    if (pathTarget == null && !triedToFindPathTarget) {
      pathTarget = try {
        ReflectionHelper.findField(classOf[Path], "target", "field_186314_d")
      } catch {
        case _: UnableToFindFieldException => null
      }
      triedToFindPathTarget = true
    }
    try pathTarget.set(path, path.getFinalPathPoint)
    catch {
      case e: IllegalAccessException =>
        LogHelper.error("Could not access debug helper", e)
    }

    Minecraft.getMinecraft.debugRenderer.pathfinding
      .asInstanceOf[DebugRendererPathfinding]
      .addPath(entityId, path, 0.1F)
  }

  @SideOnly(Side.CLIENT)
  def setPathfinding(enabled: Boolean): Unit = {
    ReflectionHelper.setPrivateValue(
      classOf[DebugRenderer],
      Minecraft.getMinecraft.debugRenderer,
      true,
      "pathfindingEnabled"
    )
  }
}
