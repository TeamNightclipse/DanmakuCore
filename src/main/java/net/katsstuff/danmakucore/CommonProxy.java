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
import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.impl.DanmakuVariant.DanmakuVariantCoreGeneric;
import net.katsstuff.danmakucore.impl.form.FormCoreGeneric;
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
import net.katsstuff.danmakucore.lib.LibDanmakuVariantName;
import net.katsstuff.danmakucore.lib.LibEntityName;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibPhaseName;
import net.katsstuff.danmakucore.lib.LibSubEntityName;
import net.katsstuff.danmakucore.lib.data.LibShotData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

	public void bakeDanmakuVariant(DanmakuVariant variant) {}

	public void bakeDanmakuForm(Form form) {}

	public void bakeSpellcard(Spellcard type) {}

	void registerStuff() {
		//Forms
		new FormCrystal1();
		new FormCrystal2();
		new FormKunai();
		new FormPellet();
		new FormPointedSphere();
		new FormScale();
		new FormSphere();
		new FormSphereCircle();
		new FormSphereDark();
		new FormStar();

		//SubEntities
		new SubEntityTypeDefault(LibSubEntityName.DEFAULT);
		new SubEntityFire(LibSubEntityName.FIRE, 2F);
		new SubEntityExplosion(LibSubEntityName.EXPLODE, 3F);
		new SubEntityTeleport(LibSubEntityName.TELEPORT);

		//Spellcard(s)
		new SpellcardDelusionEnlightenment();

		//Phases
		GameRegistry.register(new PhaseTypeFallback().setRegistryName(LibPhaseName.FALLBACK));
		GameRegistry.register(new PhaseTypeSpellcard().setRegistryName(LibPhaseName.SPELLCARD));
	}

	//We do variants for them selves as they are kind of just named ShotData with some extra data, and as such MUST come after everything else
	void registerVariants() {
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.DEFAULT, LibShotData.SHOT_MEDIUM, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.CIRCLE, LibShotData.SHOT_CIRCLE, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.CRYSTAL1, LibShotData.SHOT_CRYSTAL1, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.CRYSTAL2, LibShotData.SHOT_CRYSTAL2, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.OVAL, LibShotData.SHOT_OVAL, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.SPHERE_DARK, LibShotData.SHOT_SPHERE_DARK, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.PELLET, LibShotData.SHOT_PELLET, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.STAR_SMALL, LibShotData.SHOT_SMALLSTAR, 0.2D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.STAR, LibShotData.SHOT_STAR, 0.2D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.TINY, LibShotData.SHOT_TINY, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.SMALL, LibShotData.SHOT_SMALL, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.KUNAI, LibShotData.SHOT_KUNAI, 0.4D);

		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.SCALE, LibShotData.SHOT_SCALE, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.RICE, LibShotData.SHOT_RICE, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.LASER, LibShotData.SHOT_LASER, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.LASER_SHORT, LibShotData.SHOT_LASER_SHORT, 0.4D);
		new DanmakuVariantCoreGeneric(LibDanmakuVariantName.LASER_LONG, LibShotData.SHOT_LASER_LONG, 0.4D);
	}

	public void registerRenderers() {} //NO-OP

	public void registerModels() {} //NO-OP

	void registerColors() {
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_WHITE);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_ORANGE);
		LibShotData.registerColor(LibShotData.COLOR_SATURATED_BLUE);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_MAGENTA);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_LIGHT_BLUE);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_YELLOW);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_LIME);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_PINK);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_GRAY);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_SILVER);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_CYAN);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_PURPLE);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_BLUE);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_BROWN);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_GREEN);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_RED);
		LibShotData.registerColor(LibShotData.COLOR_VANILLA_BLACK);

		LibShotData.registerColor(LibShotData.COLOR_SATURATED_RED);
		LibShotData.registerColor(LibShotData.COLOR_SATURATED_BLUE);
		LibShotData.registerColor(LibShotData.COLOR_SATURATED_GREEN);
		LibShotData.registerColor(LibShotData.COLOR_SATURATED_YELLOW);
		LibShotData.registerColor(LibShotData.COLOR_SATURATED_MAGENTA);
		LibShotData.registerColor(LibShotData.COLOR_SATURATED_CYAN);
		LibShotData.registerColor(LibShotData.COLOR_SATURATED_ORANGE);

		LibShotData.registerColor(LibShotData.COLOR_WHITE);
	}

	void registerEntities() {
		EntityRegistry.registerModEntity(EntityDanmaku.class, LibEntityName.DANMAKU, 0, LibMod.MODID, 64, 1, true);
		EntityRegistry.registerModEntity(EntitySpellcard.class, LibEntityName.SPELLCARD, 1, LibMod.MODID, 64, 1, true);
	}

	public void registerItemColors() {} //NO-OP
}
