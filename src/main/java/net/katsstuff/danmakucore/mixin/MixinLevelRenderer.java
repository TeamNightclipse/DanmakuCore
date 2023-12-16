package net.katsstuff.danmakucore.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.katsstuff.danmakucore.events.AfterEntityRenderEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {

    @Shadow private Frustum cullingFrustum;
    @Shadow private Frustum capturedFrustum;

    private void dispatchAfterEntityRenderEvent(LevelRenderer levelRenderer, PoseStack poseStack, float partialTick,
            Matrix4f projectionMatrix, Frustum frustum, Camera camera) {
        MinecraftForge.EVENT_BUS.post(
                new AfterEntityRenderEvent(levelRenderer, poseStack, partialTick, projectionMatrix, frustum, camera));
    }

    @Inject(method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V",
                    ordinal = 1
            ),
            require = 1
    )
    public void onRenderLevel(PoseStack p_109600_, float p_109601_, long p_109602_, boolean p_109603_, Camera p_109604_,
            GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_109607_, CallbackInfo ci) {
        Frustum frustum;
        if (this.capturedFrustum != null) {
            frustum = this.capturedFrustum;
        } else {
            frustum = this.cullingFrustum;
        }

        dispatchAfterEntityRenderEvent(p_109605_.getMinecraft().levelRenderer, p_109600_, p_109601_, p_109607_, frustum, p_109604_);
    }
}
