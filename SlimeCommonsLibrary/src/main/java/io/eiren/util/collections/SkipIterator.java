package io.eiren.util.collections;



import java.util.Iterator;

import javax.annotation.Nullable;


/**
 * {@link Iterator} that can return null on {@link #next()} or can lie on {@link #hasNext()}. It is <b>not thread-secure!</b>
 * 
 * @param <E> the type of elements returned by this iterator
 */
public interface SkipIterator<E> extends Iterator<E> {
	
	@Override
	@Nullable
	E next();
}
