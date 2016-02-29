package net.benjaminneukom.heavydefense;

/**
 * Callback interface used to reload resources when the implementing class gets deserialized.
 * 
 */
public interface PostSerialization {

	/**
	 * Called when this class has been deserialized. Note that not all classes in the object graph might be deserialized during this point.
	 */
	public void postSerialized();
}
