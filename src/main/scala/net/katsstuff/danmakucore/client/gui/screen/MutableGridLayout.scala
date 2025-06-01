package net.katsstuff.danmakucore.client.gui.screen

import net.minecraft.client.gui.layouts.GridLayout
import net.minecraftforge.fml.util.ObfuscationReflectionHelper

class MutableGridLayout extends GridLayout {

  def clear(): Unit =
    MutableGridLayout.childrenField.get(this).asInstanceOf[java.util.List[_]].clear()
    MutableGridLayout.cellInhabitantsField.get(this).asInstanceOf[java.util.List[_]].clear()
}
object MutableGridLayout {
  private val childrenField        = ObfuscationReflectionHelper.findField(classOf[GridLayout], "f_263670_")
  private val cellInhabitantsField = ObfuscationReflectionHelper.findField(classOf[GridLayout], "f_263660_")
  childrenField.setAccessible(true)
  cellInhabitantsField.setAccessible(true)
}
