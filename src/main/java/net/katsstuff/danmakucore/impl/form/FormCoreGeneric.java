/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form;

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.util.ResourceLocation;

public abstract class FormCoreGeneric extends Form implements IRenderForm {

	private final ResourceLocation texture = new ResourceLocation(LibMod.MODID, "textures/entity/danmaku/White.png");;
	private final String name;

	public FormCoreGeneric(String name) {
		setRegistryName(name);
		DanmakuRegistry.INSTANCE.form.register(this);
		this.name = name;
	}

	@Override
	public ResourceLocation getTexture(EntityDanmaku danmaku) {
		return texture;
	}

	@Override
	public IRenderForm getRenderer(EntityDanmaku danmaku) {
		return this;
	}

	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName() + "." + LibMod.MODID +  "." + name;
	}
}
