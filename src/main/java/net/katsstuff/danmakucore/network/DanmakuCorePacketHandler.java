/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network;

import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class DanmakuCorePacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LibMod.MODID);
	private static int id = 0;

	public static void init() {
		INSTANCE.registerMessage(CoreDataPacket.Handler.class, CoreDataPacket.Message.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(SpellcardInfoPacket.Handler.class, SpellcardInfoPacket.Message.class, id++, Side.CLIENT);
	}
}
