/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.registry;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.lib.LibDanmakuVariantName;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibPhaseName;
import net.katsstuff.danmakucore.lib.LibSubEntityName;
import net.katsstuff.danmakucore.misc.IInitNeeded;
import net.minecraft.util.ResourceLocation;

public final class DanmakuRegistry implements IInitNeeded {

	public static final DanmakuRegistry INSTANCE = new DanmakuRegistry();
	public final Registry<Form> form;
	public final Registry<SubEntityType> subEntity;
	public final Registry<DanmakuVariant> danmakuVariant;
	public final Registry<Spellcard> spellcard;
	public final Registry<PhaseType> phase;

	private DanmakuRegistry() {
		form = new Registry<Form>(resource("forms"), Form.class, resource(LibFormName.DEFAULT)) {

			@Override
			public Form register(Form value) {
				Form res = super.register(value);
				DanmakuCore.proxy.bakeDanmakuForm(value);
				return res;
			}
		};
		subEntity = new Registry<>(resource("subEntities"), SubEntityType.class, resource(LibSubEntityName.DEFAULT));
		danmakuVariant = new Registry<DanmakuVariant>(resource("danmakuTypes"), DanmakuVariant.class, resource(LibDanmakuVariantName.DEFAULT)) {

			@Override
			public DanmakuVariant register(DanmakuVariant value) {
				DanmakuVariant res = super.register(value);
				DanmakuCore.proxy.bakeDanmakuVariant(value);
				return res;
			}
		};
		spellcard = new Registry<Spellcard>(resource("spellcard"), Spellcard.class, null) { //TODO: Use fallback spellcard

			@Override
			public Spellcard register(Spellcard value) {
				Spellcard res = super.register(value);
				DanmakuCore.proxy.bakeSpellcard(value);
				return res;
			}
		};
		phase = new Registry<>(resource("phase"), PhaseType.class, resource(LibPhaseName.FALLBACK));
	}

	private static ResourceLocation resource(String keyValue) {
		return new ResourceLocation(LibMod.MODID, keyValue);
	}
}
