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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class FormGeneric extends Form {

	private final ResourceLocation texture = new ResourceLocation(LibMod.MODID, "textures/white.png");

	@SideOnly(Side.CLIENT)
	protected IRenderForm renderer;

	@SuppressWarnings("WeakerAccess")
	public FormGeneric(String name) {
		super(name);
	}

	public FormGeneric() {}

	@Override
	public ResourceLocation getTexture(EntityDanmaku danmaku) {
		return texture;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderForm getRenderer(EntityDanmaku danmaku) {
		if(renderer == null) {
			renderer = createRenderer();
		}
		return renderer;
	}

	@SideOnly(Side.CLIENT)
	protected abstract IRenderForm createRenderer();
}
