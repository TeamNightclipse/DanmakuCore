/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.misc;

/**
 * A function that can throw an exception
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowFunction<T, R, E extends Exception> {

	R apply(T t) throws E;
}
