package net.benjaminneukom.heavydefense.serializers;

public class SerializationTest {
	public static void main(String[] args) {
		//		testWorld();
	}

	//	public static void testWorld() {
	//		final AirEnemy airEnemy = new AirEnemy();
	//		final GameWorld classicGameWorld = new ClassicGameWorld();
	//		classicGameWorld.addEnemy(airEnemy);
	//
	//		final Json json = new Json();
	//		json.setSerializer(AirEnemy.class, new ReferenceActorSerializer<AirEnemy>(AirEnemy.class, referenceStore));
	//		json.setSerializer(ClassicGameWorld.class, new ReferenceActorSerializer<ClassicGameWorld>(ClassicGameWorld.class, referenceStore));
	//		final String prettyPrint = json.prettyPrint(classicGameWorld);
	//		System.out.println(prettyPrint);
	//	}
	//
	//	public static void testEnemy() {
	//		final AirEnemy airEnemy = new AirEnemy();
	//		final Json json = new Json();
	//		json.setSerializer(AirEnemy.class, new ReferenceActorSerializer<AirEnemy>(AirEnemy.class, referenceStore));
	//		final String prettyPrint = json.prettyPrint(airEnemy);
	//		System.out.println(prettyPrint);
	//	}
	//
	//	public static void testArray() {
	//		Array<Vector2> a = new Array<Vector2>();
	//		a.add(new Vector2(10, 15));
	//		a.add(new Vector2(12, 15));
	//		a.add(new Vector2(205, 25));
	//		a.add(new Vector2(205.5f, -0.3f));
	//
	//		final Json json = new Json();
	//		final String prettyPrint = json.prettyPrint(a);
	//		System.out.println(prettyPrint);
	//	}
}
