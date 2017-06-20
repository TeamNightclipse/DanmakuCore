/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.server.commands;

import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;

public class DanPowerCmd extends SetDataCmd {

	@Override
	public String getName() {
		return "power";
	}

	@Override
	public void changeData(String arg, EntityPlayer player) throws CommandException {
		float f = (float)parseDouble(arg, 0D, 4D);

		notifyCommandListener(player, this, translationKey() + ".success", f, player.getName());
		TouhouHelper.changeAndSyncPlayerData(d -> d.setPower(f), player);
	}
}
