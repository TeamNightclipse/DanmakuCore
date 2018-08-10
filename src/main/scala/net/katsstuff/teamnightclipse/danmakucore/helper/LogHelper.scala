/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.helper

import org.apache.logging.log4j.{Level, LogManager, Logger}

import net.katsstuff.teamnightclipse.danmakucore.lib.LibMod

object LogHelper {
  //We use a separate logger until we receive one from Forge
  private var log = LogManager.getLogger(LibMod.Id)

  private var setLogger = false

  private[danmakucore] def setLog(log: Logger): Unit = {
    if (setLogger) throw new IllegalStateException("Log has already been set")
    setLogger = true
    this.log = log
  }

  private def log(logLevel: Level, obj: Any): Unit = log.log(logLevel, obj)

  private def log(logLevel: Level, e: Throwable, obj: Any): Unit = log.log(logLevel, obj, e)

  private[danmakucore] def debug(obj: Any): Unit = log(Level.DEBUG, obj)
  private[danmakucore] def error(obj: Any): Unit = log(Level.ERROR, obj)
  private[danmakucore] def fatal(obj: Any): Unit = log(Level.FATAL, obj)
  private[danmakucore] def info(obj: Any): Unit  = log(Level.INFO, obj)
  private[danmakucore] def trace(obj: Any): Unit = log(Level.TRACE, obj)
  private[danmakucore] def warn(obj: Any): Unit  = log(Level.WARN, obj)

  private[danmakucore] def error(obj: Any, e: Throwable): Unit = log(Level.ERROR, e, obj)
  private[danmakucore] def warn(obj: Any, e: Throwable): Unit  = log(Level.WARN, e, obj)
}
