/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.misc;

import net.katsstuff.danmakucore.helper.LogHelper;

/**
 * Something that needs to be initialized before it can be used.
 */
public interface IInitNeeded {

	default void init() {
		LogHelper.debug("Initializing " + this.getClass().getName());
	}
}
