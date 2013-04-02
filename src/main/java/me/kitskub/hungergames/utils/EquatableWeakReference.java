package me.kitskub.hungergames.utils;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * An extension of WeakReference that implements a sane equals and hashcode
 * method.
 * 
 * @param <T> The type of object that this reference contains
 */
public class EquatableWeakReference<T> extends WeakReference<T> {
    
	/**
	* Creates a new instance of EquatableWeakReference.
	* 
	* @param referent The object that this weak reference should reference.
	*/
	public EquatableWeakReference(T referent) {
		super(referent);
	}	
    
	/**
	* Creates a new instance of EquatableWeakReference.
	* 
	* @param referent The object that this weak reference should reference.
	*/
	public EquatableWeakReference(WeakReference<T> referent) {
		this(referent.get());
	}	
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Reference) {
			if (get() == null) {
				if (((Reference) obj).get() != null) return false;
				return true;
			}
			return get().equals(((Reference) obj).get());
		} else {
			return get().equals(obj);
		}
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		if (get() == null) return super.hashCode();
		return get().hashCode();
	}
    
}