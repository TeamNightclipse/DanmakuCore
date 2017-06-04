/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.handler;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerChangeHandler {

	private static Map<EntityPlayer, double[]> changes = new WeakHashMap<>();

	private static final int PREV_X = 0;
	private static final int PREV_Y = 1;
	private static final int PREV_Z = 2;
	private static final int X = 3;
	private static final int Y = 4;
	private static final int Z = 5;

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if(event.phase == TickEvent.Phase.START && !player.world.isRemote) {
			double[] change = changes.get(player);
			if(change == null) {
				change = new double[] {player.posX, player.posY, player.posZ, player.posX, player.posY, player.posZ};
			}
			else {
				change[PREV_X] = change[X];
				change[PREV_Y] = change[Y];
				change[PREV_Z] = change[Z];
				change[X] = player.posX;
				change[Y] = player.posY;
				change[Z] = player.posZ;
			}

			changes.put(player, change);
		}
	}

	public static double[] getPlayerChange(EntityPlayer player) {
		double[] change = changes.get(player);
		if(change == null) {
			return new double[] {0D, 0D, 0D};
		}
		else {
			return new double[] {
					change[X] - change[PREV_X],
					change[Y] - change[PREV_Y],
					change[Z] - change[PREV_Z],
			};
		}
	}
}
