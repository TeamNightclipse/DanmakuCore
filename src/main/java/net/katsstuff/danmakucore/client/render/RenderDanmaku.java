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

import net.katsstuff.danmakucore.data.OrientedBoundingBox;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

public class RenderDanmaku extends Render<EntityDanmaku> {

	public RenderDanmaku(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	private final List<Form> invalidForms = new ArrayList<>();

	@Override
	public void doRender(EntityDanmaku entity, double x, double y, double z, float entityYaw, float partialTicks) {
		ShotData shotData = entity.getShotData();
		//We don't want to render expired danmaku
		if(entity.ticksExisted <= shotData.end()) {
			GL11.glPushMatrix();
			bindEntityTexture(entity);
			GL11.glTranslated(x, y + shotData.sizeY() / 2, z);
			GlStateManager.disableLighting();

			Form form = shotData.form();
			IRenderForm renderForm = form.getRenderer(entity);
			if(renderForm != null) {
				renderForm.renderForm(entity, x, y, z, entityYaw, partialTicks, renderManager);
			}
			else if(!invalidForms.contains(form)) {
				LogHelper.error("Invalid renderer for " + I18n.format(form.getUnlocalizedName()));
				invalidForms.add(form);
			}

			GlStateManager.enableLighting();
			GL11.glPopMatrix();

			//From RenderManager renderDebugBoundingBox
			if(renderManager.isDebugBoundingBox() && !entity.isInvisible() && !Minecraft.getMinecraft().isReducedDebug()) {
				GlStateManager.pushMatrix();
				GlStateManager.depthMask(false);
				GlStateManager.disableTexture2D();
				GlStateManager.disableLighting();
				GlStateManager.disableCull();
				GlStateManager.disableBlend();
				GL11.glTranslated(x, y + shotData.sizeY() / 2, z);

				OrientedBoundingBox obb = entity.getOrientedBoundingBox();
				AxisAlignedBB aabb = obb.boundingBox();

				GlStateManager.rotate(obb.orientation().toQuaternion());
				RenderGlobal.drawSelectionBoundingBox(aabb.offset(-entity.posX, -entity.posY, -entity.posZ), 0F, 1F, 0F, 1F);

				GlStateManager.enableTexture2D();
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.depthMask(true);
				GlStateManager.popMatrix();
			}

			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDanmaku entity) {
		return entity.getShotData().form().getTexture(entity);
	}
}
