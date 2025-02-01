package net.katsstuff.danmakucore.client

import scala.compiletime.uninitialized
import com.google.common.collect.ImmutableMap
import com.mojang.blaze3d.vertex.{DefaultVertexFormat, VertexFormat, VertexFormatElement}
import net.katsstuff.danmakucore.DanmakuCore
import net.minecraft.client.renderer.{GameRenderer, RenderStateShard, RenderType, ShaderInstance}
import net.minecraftforge.client.event.RegisterShadersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object DanCoreShaders {
  val genericFloatElement =
    new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 1)
  val mat4Element =
    new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 4)

  val danmakuFancyVertexFormat: VertexFormat = new VertexFormat(
    ImmutableMap.of(
      "ModelViewMatrix1",
      mat4Element,
      "ModelViewMatrix2",
      mat4Element,
      "ModelViewMatrix3",
      mat4Element,
      "ModelViewMatrix4",
      mat4Element,
      "MainColor",
      DefaultVertexFormat.ELEMENT_COLOR,
      "SecondaryColor",
      DefaultVertexFormat.ELEMENT_COLOR,
      "CoreSize",
      genericFloatElement,
      "CoreHardness",
      genericFloatElement,
      "EdgeHardness",
      genericFloatElement,
      "EdgeGlow",
      genericFloatElement
    )
  )

  val entireDanmakuVertexFormat = new VertexFormat(
    ImmutableMap.of(
      "Position",
      DefaultVertexFormat.ELEMENT_POSITION,
      //"Normal",
      //DefaultVertexFormat.ELEMENT_NORMAL,
      "MainColor",
      DefaultVertexFormat.ELEMENT_COLOR
      /*
      "SecondaryColor",
      DefaultVertexFormat.ELEMENT_COLOR,
      "CoreSize",
      DanCoreShaders.genericFloatElement,
      "CoreHardness",
      DanCoreShaders.genericFloatElement,
      "EdgeHardness",
      DanCoreShaders.genericFloatElement,
      "EdgeGlow",
      DanCoreShaders.genericFloatElement
       */
    )
  )

  private def accessibleFieldGetter[A, B](clazz: Class[_ <: A], fieldName: String): A => B = {
    val field = clazz.getDeclaredField(fieldName)
    field.setAccessible(true)

    obj => field.get(obj).asInstanceOf[B]
  }

  val renderStateShardClass = classOf[RenderStateShard]

  val danmakuFancyRenderType: RenderType = RenderType.create(
    "danmaku_fancy",
    DanCoreShaders.entireDanmakuVertexFormat,
    VertexFormat.Mode.QUADS,
    256,
    false,
    true,
    RenderType.CompositeState.builder
      .setShaderState(new RenderStateShard.ShaderStateShard(() => danmakuFancy))
      .setTransparencyState(accessibleFieldGetter(renderStateShardClass, "TRANSLUCENT_TRANSPARENCY")(null))
      .setCullState(accessibleFieldGetter(renderStateShardClass, "CULL")(null))
      .createCompositeState(false)
  )

  var danmakuFancy: ShaderInstance = uninitialized

  @SubscribeEvent
  def onRegisterShaders(event: RegisterShadersEvent): Unit =
    event.registerShader(
      new ShaderInstance(event.getResourceProvider, DanmakuCore.resource("danmaku_fancy"), danmakuFancyVertexFormat),
      s => danmakuFancy = s
    )
}
