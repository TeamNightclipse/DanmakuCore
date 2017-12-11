/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.shader

import java.io.BufferedInputStream

import scala.util.Try

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.lwjgl.BufferUtils

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.{IResource, IResourceManager}
import net.minecraft.client.shader.{ShaderGroup, ShaderLinkHelper}
import net.minecraft.util.ResourceLocation

object ShaderHelper {

  def loadShaderGroup(resource: ResourceLocation): Try[ShaderGroup] = {
    if (ShaderLinkHelper.getStaticShaderLinkHelper == null) {
      ShaderLinkHelper.setNewStaticShaderLinkHelper()
    }

    val mc = Minecraft.getMinecraft
    Try(new ShaderGroup(mc.renderEngine, mc.getResourceManager, mc.getFramebuffer, resource))
  }
}
