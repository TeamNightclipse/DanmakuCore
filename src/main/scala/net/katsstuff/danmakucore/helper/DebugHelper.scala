/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper

import java.lang.reflect.Field

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.debug.{DebugRenderer, DebugRendererPathfinding}
import net.minecraft.pathfinding.Path
import net.minecraftforge.fml.relauncher.{ReflectionHelper, Side, SideOnly}

object DebugHelper {
  @SideOnly(Side.CLIENT)
  private var pathTarget: Field = _

  @SideOnly(Side.CLIENT)
  def renderPath(path: Path, entityId: Int): Unit = {
    if (pathTarget == null) {
      pathTarget = ReflectionHelper.findField(classOf[Path], "target")
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
