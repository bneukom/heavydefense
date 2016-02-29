package net.benjaminneukom.heavydefense.serializers;

import net.benjaminneukom.heavydefense.PostSerialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class ReferenceSerializer<T> implements Serializer<T> {
	protected Array<Field> fields = new Array<Field>();
	protected Class<T> clazz;
	protected ReferenceStore referenceStore;
	public static final String REFERENCE_FIELD_ID_NAME = "reference-id";
	public static final String REFERENCE_FIELD_TO_NAME = "reference-to";
	private boolean writeType;

	public ReferenceSerializer(Class<T> clazz, ReferenceStore referenceStore) {
		this(clazz, referenceStore, false);
	}

	public ReferenceSerializer(Class<T> clazz, ReferenceStore referenceStore, boolean writeType) {
		this.clazz = clazz;
		this.referenceStore = referenceStore;
		this.writeType = writeType;
		this.fields = createFields();
	}

	protected Array<Field> createFields() {
		final Array<Field> fields = new Array<Field>();
		Class<?> fieldsClass = clazz;
		do {
			final Field[] declaredFields = ClassReflection.getDeclaredFields(fieldsClass);

			for (Field field : declaredFields) {
				field.setAccessible(true);
				if (validFieldType(field)) {
					fields.add(field);
				}
			}

		} while ((fieldsClass = fieldsClass.getSuperclass()) != null);

		return fields;
	}

	protected boolean validFieldType(Field field) {
		return !field.isSynthetic() && !field.isTransient() && !(field.isStatic() && field.isFinal());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void write(Json json, T object, Class knownType) {
		json.writeObjectStart();

		if (referenceStore.isReferenced(object)) {
			json.writeType(object.getClass());
			json.writeValue(REFERENCE_FIELD_TO_NAME, referenceStore.reference(object));
		} else {
			if (knownType != object.getClass() || writeType)
				json.writeType(object.getClass());

			// write the reference
			if (referenceStore.shouldReference(object.getClass())) {
				json.writeValue(REFERENCE_FIELD_ID_NAME, referenceStore.reference(object));
			}

			writeFields(json, object);
		}

		json.writeObjectEnd();
	}

	private void writeFields(Json json, T object) {
		final ArrayIterator<Field> fieldIterator = new ArrayIterator<Field>(fields);
		while (fieldIterator.hasNext()) {
			Field field = fieldIterator.next();
			try {
				Object value = field.get(object);
				json.writeValue(field.getName(), value, field.getType());
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public T read(Json json, JsonValue jsonData, Class type) {

		// if this is reference to, load it from the reference store
		final int referenceToId = getReferenceToId(jsonData);
		if (referenceToId != ReferenceStore.INVALID_ID) {
			return (T) referenceStore.getObject(referenceToId);
		}

		try {
			final Object newInstance = type.newInstance();

			// store reference for further use
			int referenceId = getReferenceId(jsonData);
			if (referenceId != ReferenceStore.INVALID_ID) {
				referenceStore.putReference(referenceId, newInstance);
			}

			final ArrayIterator<Field> arrayIterator = new ArrayIterator<Field>(fields);
			while (arrayIterator.hasNext()) {
				Field field = (Field) arrayIterator.next();

				field.setAccessible(true);

				// TODO use read value and resolve class here
				json.readField(newInstance, field.getName(), jsonData);
			}

			// callback
			if (newInstance instanceof PostSerialization) {
				((PostSerialization) newInstance).postSerialized();
			}

			return (T) newInstance;

		} catch (Exception e) {
			e.printStackTrace();
			Gdx.app.error("HeavyDefense", e.getMessage());
		}

		return null;
	}

	private int getId(JsonValue jsonData, String name) {
		return jsonData.getInt(name, ReferenceStore.INVALID_ID);
	}

	private int getReferenceId(JsonValue jsonData) {
		return getId(jsonData, REFERENCE_FIELD_ID_NAME);
	}

	private int getReferenceToId(JsonValue jsonData) {
		return getId(jsonData, REFERENCE_FIELD_TO_NAME);
	}

}
