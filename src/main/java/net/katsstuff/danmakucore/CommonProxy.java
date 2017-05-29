/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore;

import java.util.Arrays;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.client.particle.GlowTexture;
import net.katsstuff.danmakucore.client.particle.IGlowParticle;
import net.katsstuff.danmakucore.danmodel.DanModelReader;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.EntityFallingData;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.entity.living.boss.EntityDanmakuBoss;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.impl.danmakuvariant.DanmakuVariantGeneric;
import net.katsstuff.danmakucore.impl.form.FormControl;
import net.katsstuff.danmakucore.impl.form.FormCrystal1;
import net.katsstuff.danmakucore.impl.form.FormCrystal2;
import net.katsstuff.danmakucore.impl.form.FormFire;
import net.katsstuff.danmakucore.impl.form.FormKunai;
import net.katsstuff.danmakucore.impl.form.FormLaser;
import net.katsstuff.danmakucore.impl.form.FormPellet;
import net.katsstuff.danmakucore.impl.form.FormPointedSphere;
import net.katsstuff.danmakucore.impl.form.FormScale;
import net.katsstuff.danmakucore.impl.form.FormSphere;
import net.katsstuff.danmakucore.impl.form.FormSphereCircle;
import net.katsstuff.danmakucore.impl.form.FormSphereDark;
import net.katsstuff.danmakucore.impl.form.FormStar;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeFallback;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeShapeCircle;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeShapeRing;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeShapeWide;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeSpellcard;
import net.katsstuff.danmakucore.impl.spellcard.SpellcardDelusionEnlightenment;
import net.katsstuff.danmakucore.impl.subentity.SubEntityChild;
import net.katsstuff.danmakucore.impl.subentity.SubEntityExplosion;
import net.katsstuff.danmakucore.impl.subentity.SubEntityFire;
import net.katsstuff.danmakucore.impl.subentity.SubEntityTeleport;
import net.katsstuff.danmakucore.impl.subentity.SubEntityTypeDefault;
import net.katsstuff.danmakucore.item.ItemDanmaku;
import net.katsstuff.danmakucore.item.ItemSpellcard;
import net.katsstuff.danmakucore.lib.LibColor;
import net.katsstuff.danmakucore.lib.LibDanmakuVariantName;
import net.katsstuff.danmakucore.lib.LibEntityName;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibPhaseName;
import net.katsstuff.danmakucore.lib.LibRegistryName;
import net.katsstuff.danmakucore.lib.LibSubEntityName;
import net.katsstuff.danmakucore.lib.data.LibShotData;
import net.katsstuff.danmakucore.network.SpellcardInfoPacket;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;

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
		EntityRegistry.registerModEntity(EntityFallingData.class, LibEntityName.FALLING_DATA, 2, LibMod.MODID, 40, 1, true);
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
				new FormStar(),
				new FormControl(),
				new FormFire(),
				new FormLaser(),
				DanModelReader.createForm(new ResourceLocation(LibMod.MODID, "models/form/heart"), LibFormName.HEART).get(),
				DanModelReader.createForm(new ResourceLocation(LibMod.MODID, "models/form/note1"), LibFormName.NOTE1).get()
		);
	}

	@SubscribeEvent
	public static void registerSubEntities(RegistryEvent.Register<SubEntityType> event) {
		event.getRegistry().registerAll(
				new SubEntityTypeDefault(LibSubEntityName.DEFAULT),
				new SubEntityFire(LibSubEntityName.FIRE, 2F),
				new SubEntityExplosion(LibSubEntityName.EXPLODE, 3F),
				new SubEntityTeleport(LibSubEntityName.TELEPORT),
				new SubEntityChild(LibSubEntityName.CHILD)
		);
	}

	@SubscribeEvent
	public static void registerSpellcards(RegistryEvent.Register<Spellcard> event) {
		Spellcard[] toRegister = {
				new SpellcardDelusionEnlightenment()
		};

		event.getRegistry().registerAll(toRegister);

		Arrays.stream(toRegister).forEach(Spellcard::bakeModel);
	}

	@SubscribeEvent
	public static void registerPhases(RegistryEvent.Register<PhaseType> event) {
		event.getRegistry().registerAll(
				new PhaseTypeFallback().setRegistryName(LibPhaseName.FALLBACK),
				new PhaseTypeSpellcard().setRegistryName(LibPhaseName.SPELLCARD),
				new PhaseTypeShapeCircle().setRegistryName(LibPhaseName.SHAPE_CIRCLE),
				new PhaseTypeShapeRing().setRegistryName(LibPhaseName.SHAPE_RING),
				new PhaseTypeShapeWide().setRegistryName(LibPhaseName.SHAPE_WIDE)
		);
	}

	@SubscribeEvent
	public static void registerVariants(RegistryEvent.Register<DanmakuVariant> event) {
		event.getRegistry().registerAll(
				new DanmakuVariantGeneric(LibDanmakuVariantName.DEFAULT, () -> LibShotData.SHOT_MEDIUM, 0.3D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.CIRCLE, () -> LibShotData.SHOT_CIRCLE, 0.3D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.CRYSTAL1, () -> LibShotData.SHOT_CRYSTAL1, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.CRYSTAL2, () -> LibShotData.SHOT_CRYSTAL2, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.OVAL, () -> LibShotData.SHOT_OVAL, 0.2D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.SPHERE_DARK, () -> LibShotData.SHOT_SPHERE_DARK, 0.3D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.PELLET, () -> LibShotData.SHOT_PELLET, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.STAR_SMALL, () -> LibShotData.SHOT_SMALLSTAR, 0.3D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.STAR, () -> LibShotData.SHOT_STAR, 0.2D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.TINY, () -> LibShotData.SHOT_TINY, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.SMALL, () -> LibShotData.SHOT_SMALL, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.KUNAI, () -> LibShotData.SHOT_KUNAI, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.SCALE, () -> LibShotData.SHOT_SCALE, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.RICE, () -> LibShotData.SHOT_RICE, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.POINTED_LASER, () -> LibShotData.SHOT_POINTED_LASER, 0.35D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.POINTED_LASER_SHORT, () -> LibShotData.SHOT_POINTED_LASER_SHORT, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.POINTED_LASER_LONG, () -> LibShotData.SHOT_POINTED_LASER_LONG, 0.3D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.FIRE, () -> LibShotData.SHOT_FIRE, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.LASER, () -> LibShotData.SHOT_LASER, 0D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.HEART, () -> LibShotData.SHOT_HEART, 0.4D),
				new DanmakuVariantGeneric(LibDanmakuVariantName.NOTE1, () -> LibShotData.SHOT_NOTE1, 0.4D)
		);
	}

	@SubscribeEvent
	public static void createRegistries(RegistryEvent.NewRegistry event) {
		createRegistry(LibRegistryName.FORMS, Form.class, resource(LibFormName.DEFAULT));
		createRegistry(LibRegistryName.SUB_ENTITIES, SubEntityType.class, resource(LibSubEntityName.DEFAULT));
		createRegistry(LibRegistryName.VARIANTS, DanmakuVariant.class, resource(LibDanmakuVariantName.DEFAULT));
		createRegistry(LibRegistryName.SPELLCARDS, Spellcard.class, null);
		createRegistry(LibRegistryName.PHASES, PhaseType.class, resource(LibPhaseName.FALLBACK));
	}

	/**
	 * Add a {@link EntityDanmakuBoss} to the boss bar render handler.
	 */
	public void addDanmakuBoss(EntityDanmakuBoss boss) {}

	/**
	 * Removes a {@link EntityDanmakuBoss} from the boss bar render handler.
	 */
	public void removeDanmakuBoss(EntityDanmakuBoss boss) {}

	/**
	 * Adds a spellcard name to the spellcard renderer
	 */
	public void handleSpellcardInfo(SpellcardInfoPacket.Message packet) {}

	public void createParticleGlow(World world, Vector3 pos, Vector3 motion, float r, float g, float b, float scale, int lifetime,
			GlowTexture type) {}

	public void createChargeSphere(Entity entity, int amount, double offset, double divSpeed, float r, float g, float b, int lifetime) {}

	public <T extends IGlowParticle> void addParticle(T particle) {}

	@SuppressWarnings("deprecation") //We need the default value
	private static <I extends IForgeRegistryEntry<I>> FMLControlledNamespacedRegistry<I> createRegistry(ResourceLocation name, Class<I> clazz,
			@Nullable ResourceLocation defaultValue) {
		return PersistentRegistryManager.createRegistry(name, clazz, defaultValue, 0, Short.MAX_VALUE, false, null, null, null);
	}

	private static ResourceLocation resource(String path) {
		return new ResourceLocation(LibMod.MODID, path);
	}
}
