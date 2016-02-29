package net.benjaminneukom.heavydefense.serializers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ReferenceStore {
	private HashMap<Object, Integer> objectToId = new HashMap<Object, Integer>();
	private HashMap<Integer, Object> idToObject = new HashMap<Integer, Object>();

	private Set<Class<?>> shouldReference = new HashSet<Class<?>>();

	// TODO implement
	private Set<Class<?>> shouldReferenceWithSubType = new HashSet<Class<?>>();

	private int id;
	public static final int INVALID_ID = Integer.MIN_VALUE;

	public void addReferences(Class<?>... classes) {
		shouldReference.addAll(Arrays.asList(classes));
	}

	public boolean shouldReference(Class<?> clazz) {
		return shouldReference.contains(clazz);
	}

	public boolean isReferenced(Object o) {
		return objectToId.containsKey(o);
	}

	public int getId(Object o) {
		if (objectToId.containsKey(o))
			return objectToId.get(o);
		return INVALID_ID;
	}

	public Object getObject(int id) {
		return idToObject.get(id);
	}

	public void putReference(int id, Object object) {
		idToObject.put(id, object);
		objectToId.put(object, id);
	}

	public int reference(Object object) {
		if (objectToId.containsKey(object)) {
			return getId(object);
		}

		int newId = newId();
		objectToId.put(object, newId);
		idToObject.put(newId, object);
		return newId;
	}

	private int newId() {
		return id++;
	}

	public void clear() {
		id = 0;
		objectToId.clear();
		idToObject.clear();
	}

}
