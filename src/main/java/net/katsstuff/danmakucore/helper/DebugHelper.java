/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.DebugRendererPathfinding;
import net.minecraft.pathfinding.Path;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DebugHelper {

	@SideOnly(Side.CLIENT)
	private static Field pathTarget;

	@SideOnly(Side.CLIENT)
	public static void renderPath(Path path, int entityId) {
		if(pathTarget == null) {
			pathTarget = ReflectionHelper.findField(Path.class, "target");
			pathTarget.setAccessible(true);
		}

		try {
			pathTarget.set(path, path.getFinalPathPoint());
		}
		catch(IllegalAccessException e) {
			LogHelper.error("Could not access debug helper", e);
		}
		((DebugRendererPathfinding)Minecraft.getMinecraft().debugRenderer.pathfinding).addPath(entityId, path, 0.1F);
	}

	@SideOnly(Side.CLIENT)
	public static void setPathfinding(boolean enabled) {
		ReflectionHelper.setPrivateValue(DebugRenderer.class, Minecraft.getMinecraft().debugRenderer, true, "pathfindingEnabled");
		LogHelper.info(ReflectionHelper.getPrivateValue(DebugRenderer.class, Minecraft.getMinecraft().debugRenderer, "pathfindingEnabled"));
	}
}
