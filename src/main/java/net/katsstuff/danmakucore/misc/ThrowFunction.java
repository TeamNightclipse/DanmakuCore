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
