/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore;

import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.impl.DanmakuVariant.DanmakuVariantCoreGeneric;
import net.katsstuff.danmakucore.impl.form.FormCrystal1;
import net.katsstuff.danmakucore.impl.form.FormCrystal2;
import net.katsstuff.danmakucore.impl.form.FormKunai;
import net.katsstuff.danmakucore.impl.form.FormPellet;
import net.katsstuff.danmakucore.impl.form.FormPointedSphere;
import net.katsstuff.danmakucore.impl.form.FormScale;
import net.katsstuff.danmakucore.impl.form.FormSphere;
import net.katsstuff.danmakucore.impl.form.FormSphereCircle;
import net.katsstuff.danmakucore.impl.form.FormSphereDark;
import net.katsstuff.danmakucore.impl.form.FormStar;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeFallback;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeSpellcard;
import net.katsstuff.danmakucore.impl.spellcard.SpellcardDelusionEnlightenment;
import net.katsstuff.danmakucore.impl.subentity.SubEntityExplosion;
import net.katsstuff.danmakucore.impl.subentity.SubEntityFire;
import net.katsstuff.danmakucore.impl.subentity.SubEntityTeleport;
import net.katsstuff.danmakucore.impl.subentity.SubEntityTypeDefault;
import net.katsstuff.danmakucore.item.ItemDanmaku;
import net.katsstuff.danmakucore.item.ItemSpellcard;
import net.katsstuff.danmakucore.lib.LibColor;
import net.katsstuff.danmakucore.lib.LibDanmakuVariantName;
import net.katsstuff.danmakucore.lib.LibEntityName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibPhaseName;
import net.katsstuff.danmakucore.lib.LibSubEntityName;
import net.katsstuff.danmakucore.lib.data.LibShotData;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {

	public void bakeDanmakuVariant(DanmakuVariant variant) {}

	public void bakeDanmakuForm(Form form) {}

	public void bakeSpellcard(Spellcard type) {}

	public void registerRenderers() {} //NO-OP

	public void bakeRenderModels() {} //NO-OP

	void registerColors() {
		LibColor.registerColor(LibColor.COLOR_VANILLA_WHITE);
		LibColor.registerColor(LibColor.COLOR_VANILLA_ORANGE);
		LibColor.registerColor(LibColor.COLOR_SATURATED_BLUE);
		LibColor.registerColor(LibColor.COLOR_VANILLA_MAGENTA);
		LibColor.registerColor(LibColor.COLOR_VANILLA_LIGHT_BLUE);
		LibColor.registerColor(LibColor.COLOR_VANILLA_YELLOW);
		LibColor.registerColor(LibColor.COLOR_VANILLA_LIME);
		LibColor.registerColor(LibColor.COLOR_VANILLA_PINK);
		LibColor.registerColor(LibColor.COLOR_VANILLA_GRAY);
		LibColor.registerColor(LibColor.COLOR_VANILLA_SILVER);
		LibColor.registerColor(LibColor.COLOR_VANILLA_CYAN);
		LibColor.registerColor(LibColor.COLOR_VANILLA_PURPLE);
		LibColor.registerColor(LibColor.COLOR_VANILLA_BLUE);
		LibColor.registerColor(LibColor.COLOR_VANILLA_BROWN);
		LibColor.registerColor(LibColor.COLOR_VANILLA_GREEN);
		LibColor.registerColor(LibColor.COLOR_VANILLA_RED);
		LibColor.registerColor(LibColor.COLOR_VANILLA_BLACK);

		LibColor.registerColor(LibColor.COLOR_SATURATED_RED);
		LibColor.registerColor(LibColor.COLOR_SATURATED_BLUE);
		LibColor.registerColor(LibColor.COLOR_SATURATED_GREEN);
		LibColor.registerColor(LibColor.COLOR_SATURATED_YELLOW);
		LibColor.registerColor(LibColor.COLOR_SATURATED_MAGENTA);
		LibColor.registerColor(LibColor.COLOR_SATURATED_CYAN);
		LibColor.registerColor(LibColor.COLOR_SATURATED_ORANGE);

		LibColor.registerColor(LibColor.COLOR_WHITE);
	}

	void registerEntities() {
		EntityRegistry.registerModEntity(EntityDanmaku.class, LibEntityName.DANMAKU, 0, LibMod.MODID, 64, 1, true);
		EntityRegistry.registerModEntity(EntitySpellcard.class, LibEntityName.SPELLCARD, 1, LibMod.MODID, 64, 1, true);
	}

	public void registerItemColors() {} //NO-OP

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new ItemDanmaku(),
				new ItemSpellcard()
		);
	}

	@SubscribeEvent
	public static void registerForms(RegistryEvent.Register<Form> event) {
		event.getRegistry().registerAll(
				new FormCrystal1(),
				new FormCrystal2(),
				new FormKunai(),
				new FormPellet(),
				new FormPointedSphere(),
				new FormScale(),
				new FormSphere(),
				new FormSphereCircle(),
				new FormSphereDark(),
				new FormStar()
		);
	}

	@SubscribeEvent
	public static void registerSubEntities(RegistryEvent.Register<SubEntityType> event) {
		event.getRegistry().registerAll(
				new SubEntityTypeDefault(LibSubEntityName.DEFAULT),
				new SubEntityFire(LibSubEntityName.FIRE, 2F),
				new SubEntityExplosion(LibSubEntityName.EXPLODE, 3F),
				new SubEntityTeleport(LibSubEntityName.TELEPORT)
		);
	}

	@SubscribeEvent
	public static void registerSpellcards(RegistryEvent.Register<Spellcard> event) {
		event.getRegistry().registerAll(
				new SpellcardDelusionEnlightenment()
		);
	}

	@SubscribeEvent
	public static void registerPhases(RegistryEvent.Register<PhaseType> event) {
		event.getRegistry().registerAll(
				new PhaseTypeFallback().setRegistryName(LibPhaseName.FALLBACK),
				new PhaseTypeSpellcard().setRegistryName(LibPhaseName.SPELLCARD)
		);
	}

	@SubscribeEvent
	public static void registerVariants(RegistryEvent.Register<DanmakuVariant> event) {
		event.getRegistry().registerAll(
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.DEFAULT, () -> LibShotData.SHOT_MEDIUM, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.CIRCLE, () -> LibShotData.SHOT_CIRCLE, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.CRYSTAL1, () -> LibShotData.SHOT_CRYSTAL1, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.CRYSTAL2, () -> LibShotData.SHOT_CRYSTAL2, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.OVAL, () -> LibShotData.SHOT_OVAL, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.SPHERE_DARK, () -> LibShotData.SHOT_SPHERE_DARK, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.PELLET, () -> LibShotData.SHOT_PELLET, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.STAR_SMALL, () -> LibShotData.SHOT_SMALLSTAR, 0.2D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.STAR, () -> LibShotData.SHOT_STAR, 0.2D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.TINY, () -> LibShotData.SHOT_TINY, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.SMALL, () -> LibShotData.SHOT_SMALL, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.KUNAI, () -> LibShotData.SHOT_KUNAI, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.SCALE, () -> LibShotData.SHOT_SCALE, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.RICE, () -> LibShotData.SHOT_RICE, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.LASER, () -> LibShotData.SHOT_LASER, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.LASER_SHORT, () -> LibShotData.SHOT_LASER_SHORT, 0.4D),
				new DanmakuVariantCoreGeneric(LibDanmakuVariantName.LASER_LONG, () -> LibShotData.SHOT_LASER_LONG, 0.4D)
		);
	}
}
