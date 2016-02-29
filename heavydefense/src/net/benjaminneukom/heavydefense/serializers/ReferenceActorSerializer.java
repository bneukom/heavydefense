package net.benjaminneukom.heavydefense.serializers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;

public class ReferenceActorSerializer<T extends Actor> extends ReferenceSerializer<T> {

	public ReferenceActorSerializer(final Class<T> clazz, ReferenceStore referenceStore) {
		super(clazz, referenceStore);
	}

	public ReferenceActorSerializer(final Class<T> clazz, ReferenceStore referenceStore, boolean writeType) {
		super(clazz, referenceStore, writeType);
	}

	@Override
	protected Array<Field> createFields() {
		final Array<Field> fields = new Array<Field>();
		Class<?> fieldsClass = clazz;
		do {
			if (fieldsClass == Group.class) continue;

			final Field[] declaredFields = ClassReflection.getDeclaredFields(fieldsClass);

			for (Field field : declaredFields) {
				if (fieldsClass == Actor.class && !validActorField(field)) continue;

				field.setAccessible(true);
				if (validFieldType(field)) {
					fields.add(field);
				}
			}

		} while ((fieldsClass = fieldsClass.getSuperclass()) != null);

		return fields;
	}

	private boolean validActorField(Field field) {
		final String name = field.getName();
		return name.equals("x") || name.equals("y") || name.equals("width") || name.equals("height") || name.equals("rotation");
	}

}
