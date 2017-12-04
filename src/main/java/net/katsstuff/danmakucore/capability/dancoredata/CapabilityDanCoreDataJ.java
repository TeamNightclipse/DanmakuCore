/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability.dancoredata;

import net.katsstuff.danmakucore.capability.dancoredata.IDanmakuCoreData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityDanCoreDataJ {

	@CapabilityInject(IDanmakuCoreData.class)
	public static Capability<IDanmakuCoreData> CORE_DATA;
}
