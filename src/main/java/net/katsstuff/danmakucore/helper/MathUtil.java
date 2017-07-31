/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper;

//We use Util instead of helper to avoid collision with minecraft's MathHelper
public class MathUtil {

	public static final double EPSILON = 1E-5;

	public static boolean fuzzyEqual(float a, float b) {
		return Math.abs(a - b) <= EPSILON;
	}

	public static boolean fuzzyEqual(double a, double b) {
		return Math.abs(a - b) <= EPSILON;
	}

	public static int fuzzyCompare(float a, float b) {
		return Math.abs(a - b) <= EPSILON ? 0 : Float.compare(a, b);
	}

	public static int fuzzyCompare(double a, double b) {
		return Math.abs(a - b) <= EPSILON ? 0 : Double.compare(a, b);
	}
}
