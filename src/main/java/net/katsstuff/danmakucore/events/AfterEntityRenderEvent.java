package net.katsstuff.danmakucore.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraftforge.eventbus.api.Event;

public class AfterEntityRenderEvent extends Event {

    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final float partialTick;
    private final Matrix4f projectionMatrix;
    private final Frustum frustum;
    private final Camera camera;

    public AfterEntityRenderEvent(LevelRenderer levelRenderer, PoseStack poseStack, float partialTick, Matrix4f projectionMatrix,
            Frustum frustum, Camera camera) {
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.partialTick = partialTick;
        this.projectionMatrix = projectionMatrix;
        this.frustum = frustum;
        this.camera = camera;
    }

    public LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Frustum getFrustum() {
        return frustum;
    }

    public Camera getCamera() {
        return camera;
    }
}
