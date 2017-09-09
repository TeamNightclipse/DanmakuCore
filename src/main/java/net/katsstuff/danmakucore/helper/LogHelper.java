/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraftforge.fml.common.FMLLog;

@SuppressWarnings("unused")
public class LogHelper {

	private static Logger log;

	public static void setLog(Logger log) {
		if(LogHelper.log != null) {
			throw new IllegalStateException("Log has already been set");
		}
		LogHelper.log = log;
	}

	private static void log(Level logLevel, Object obj) {
		log.log(logLevel, obj);
	}

	private static void log(Level logLevel, Exception e, Object obj) {
		log.log(logLevel, obj, e);
	}

	public static void debug(Object obj) {
		log(Level.DEBUG, obj);
	}

	public static void error(Object obj) {
		log(Level.ERROR, obj);
	}

	public static void error(Object obj, Exception e) {
		log(Level.ERROR, e, obj);
	}

	public static void fatal(Object obj) {
		log(Level.FATAL, obj);
	}

	public static void info(Object obj) {
		log(Level.INFO, obj);
	}

	public static void trace(Object obj) {
		log(Level.TRACE, obj);
	}

	public static void warn(Object obj) {
		log(Level.WARN, obj);
	}

	public static void warn(Object obj, Exception e) {
		log(Level.WARN, e, obj);
	}
}