package net.katsstuff.danmakucore

import net.minecraftforge.common.ForgeConfigSpec

object DanCoreCommonConfig {
  val (danCoreConfig, forgeConfig) = {
    val res = new ForgeConfigSpec.Builder().configure(new DanCoreCommonConfig(_))
    (res.getLeft, res.getRight)
  }
}
class DanCoreCommonConfig(builder: ForgeConfigSpec.Builder) {
  //builder.define("foo", false).save()
}
