package net.katsstuff.danmakucore.client.gui

import java.util.UUID

import net.minecraft.resources.ResourceLocation

enum GraphNodeIdentifier {
  case Core(identifier: ResourceLocation, uuid: UUID)
  case IO(core: Core, id: String)
  case Misc(from: GraphNodeIdentifier, to: GraphNodeIdentifier)
}
