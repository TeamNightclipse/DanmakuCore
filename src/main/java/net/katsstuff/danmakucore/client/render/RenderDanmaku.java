/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class RenderDanmaku extends Render<EntityDanmaku> {

	public RenderDanmaku(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	private final List<Form> invalidForms = new ArrayList<>();

	@Override
	public void doRender(EntityDanmaku entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GL11.glPushMatrix();
		bindEntityTexture(entity);
		GL11.glTranslated(x, y, z);
		GlStateManager.disableLighting();

		Form form = entity.getShotData().form();
		IRenderForm renderForm = form.getRenderer(entity);
		if(renderForm != null) {
			renderForm.renderForm(entity, x, y, z, entityYaw, partialTicks, renderManager);
		}
		else if(!invalidForms.contains(form)) {
			LogHelper.error("Invalid renderer for " + I18n.format(form.getUnlocalizedName()));
			invalidForms.add(form);
		}

		GL11.glPopMatrix();
		GlStateManager.enableLighting();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDanmaku entity) {
		return entity.getShotData().form().getTexture(entity);
	}
}
