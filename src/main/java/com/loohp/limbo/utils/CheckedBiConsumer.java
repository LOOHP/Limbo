package com.loohp.limbo.utils;

@FunctionalInterface
public interface CheckedBiConsumer<T, U, TException extends Throwable> {
	
	void consume(T t, U u) throws TException;
	
}