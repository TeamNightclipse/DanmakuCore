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
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.misc.IInitNeeded;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.util.ResourceLocation;

public final class LibForms implements IInitNeeded {

	private static final Set<Form> CACHE;
	public static final Form SPHERE; //Default
	public static final Form SPHERE_DARK;
	public static final Form CRYSTAL_1;
	public static final Form CRYSTAL_2;
	public static final Form SPHERE_CIRCLE;
	public static final Form PELLET;

	public static final Form SCALE;
	public static final Form STAR;
	public static final Form RICE;
	public static final Form KUNAI;
	public static final Form LASER;

	static {
		if(!DanmakuCore.registriesInitialized || !DanmakuCore.stuffRegistered) {
			throw new IllegalStateException("Forms were queried before they had been registered");
		}

		CACHE = new HashSet<>();

		SPHERE = getForm(LibFormName.DEFAULT); //Default
		SPHERE_DARK = getForm(LibFormName.SPHERE_DARK);
		CRYSTAL_1 = getForm(LibFormName.CRYSTAL1);
		CRYSTAL_2 = getForm(LibFormName.CRYSTAL2);
		SPHERE_CIRCLE = getForm(LibFormName.CIRCLE);
		PELLET = getForm(LibFormName.PELLET);
		STAR = getForm(LibFormName.STAR);
		KUNAI = getForm(LibFormName.KUNAI);

		SCALE = getForm(LibFormName.SCALE);
		RICE = getForm(LibFormName.RICE);
		LASER = getForm(LibFormName.LASER);

		CACHE.clear();
	}

	private static Form getForm(String name) {
		Form form = DanmakuRegistry.INSTANCE.form.getRegistry().getObject(new ResourceLocation(LibMod.MODID, name));

		if (!CACHE.add(form)) {
			throw new IllegalStateException("Invalid Form requested: " + name);
		}
		else {
			return form;
		}
	}
}
