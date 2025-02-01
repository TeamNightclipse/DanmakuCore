package net.katsstuff.danmakucore.client

import com.google.common.collect.ImmutableMap
import com.mojang.blaze3d.vertex.{DefaultVertexFormat, VertexFormat}

object DanCoreVertexFormat {

  val positionNormal: VertexFormat = new VertexFormat(
    ImmutableMap.of(
      "Position",
      DefaultVertexFormat.ELEMENT_POSITION,
      "Normal",
      DefaultVertexFormat.ELEMENT_NORMAL
    )
  )
}
