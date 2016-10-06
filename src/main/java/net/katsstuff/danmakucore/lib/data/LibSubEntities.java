/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib.data;

import java.util.HashSet;
import java.util.Set;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibSubEntityName;
import net.katsstuff.danmakucore.misc.IInitNeeded;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("WeakerAccess")
public final class LibSubEntities implements IInitNeeded {

	private static final Set<SubEntityType> CACHE;

	public static final SubEntityType DEFAULT_TYPE;
	public static final SubEntityType FIRE;
	public static final SubEntityType EXPLOSION;
	public static final SubEntityType TELEPORT;

	static {
		if(!DanmakuCore.registriesInitialized || !DanmakuCore.stuffRegistered) {
			throw new IllegalStateException("Forms were queried were they had been registered");
		}

		CACHE = new HashSet<>();

		DEFAULT_TYPE = getSubEntity(LibSubEntityName.DEFAULT);
		FIRE = getSubEntity(LibSubEntityName.FIRE);
		EXPLOSION = getSubEntity(LibSubEntityName.EXPLODE);
		TELEPORT = getSubEntity(LibSubEntityName.TELEPORT);

		CACHE.clear();
	}

	private static SubEntityType getSubEntity(String name) {
		SubEntityType form = DanmakuRegistry.INSTANCE.subEntity.getRegistry().getObject(new ResourceLocation(LibMod.MODID, name));

		//We add the default type first here, which is guaranteed to work.
		// If we ever request something that doesn't exist, we will get the default, and throw an exception because we added it
		if(!CACHE.add(form)) {
			throw new IllegalStateException("Invalid SubEntity requested: " + name);
		}
		else {
			return form;
		}
	}
}
