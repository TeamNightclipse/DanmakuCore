/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.server.commands;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.katsstuff.danmakucore.capability.IDanmakuCoreData;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.misc.ThrowFunction;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.server.command.CommandTreeBase;

public class DanmakuCoreCmd extends CommandTreeBase {

	public DanmakuCoreCmd() {
		ThrowFunction<String, Integer, CommandException> parseInt = arg -> parseInt(arg, 0);
		ThrowFunction<String, Float, CommandException> parseFloat = arg -> (float)parseDouble(arg, 0D, 4D);
		Function<BiConsumer<IDanmakuCoreData, Integer>, BiConsumer<EntityPlayer, Integer>> processInt = processer();
		Function<BiConsumer<IDanmakuCoreData, Float>, BiConsumer<EntityPlayer, Float>> processFloat = processer();

		addSubcommand(new SetDataCmd<>("lives", parseInt, processInt.apply(IDanmakuCoreData::setLives)));
		addSubcommand(new SetDataCmd<>("bombs", parseInt, processInt.apply(IDanmakuCoreData::setBombs)));
		addSubcommand(new SetDataCmd<>("power", parseFloat, processFloat.apply(IDanmakuCoreData::setPower)));
		addSubcommand(new SetDataCmd<>("score", parseInt, processInt.apply(IDanmakuCoreData::setScore)));
	}

	private static <T> Function<BiConsumer<IDanmakuCoreData, T>, BiConsumer<EntityPlayer, T>> processer() {
		return f1 -> (p, i) -> TouhouHelper.changeAndSyncPlayerData(d -> f1.accept(d, i), p);
	}

	@Override
	public String getName() {
		return "danmakucore";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.danmakucore.usage";
	}
}
