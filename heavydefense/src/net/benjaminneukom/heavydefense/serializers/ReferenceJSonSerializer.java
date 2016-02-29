package net.benjaminneukom.heavydefense.serializers;

import java.util.ArrayList;
import java.util.List;

import net.benjaminneukom.heavydefense.Explosion;
import net.benjaminneukom.heavydefense.enemies.AirEnemy;
import net.benjaminneukom.heavydefense.enemies.BossEnemy;
import net.benjaminneukom.heavydefense.enemies.GroundEnemy;
import net.benjaminneukom.heavydefense.enemies.WaveSpawner;
import net.benjaminneukom.heavydefense.enemies.debuffs.FireDebuff;
import net.benjaminneukom.heavydefense.enemies.debuffs.SlowDebuff;
import net.benjaminneukom.heavydefense.game.EruptionSpawner;
import net.benjaminneukom.heavydefense.game.LavaEruption;
import net.benjaminneukom.heavydefense.game.worlds.ClassicGameWorld;
import net.benjaminneukom.heavydefense.game.worlds.ClassicLavaGameWorld;
import net.benjaminneukom.heavydefense.game.worlds.NoRoadsGameWorld;
import net.benjaminneukom.heavydefense.game.worlds.NoRoadsLavaGameWorld;
import net.benjaminneukom.heavydefense.tower.ArtilleryTower;
import net.benjaminneukom.heavydefense.tower.FireTower;
import net.benjaminneukom.heavydefense.tower.RocketTower;
import net.benjaminneukom.heavydefense.tower.SlowTower;
import net.benjaminneukom.heavydefense.tower.TeslaTower;
import net.benjaminneukom.heavydefense.tower.TurretTower;
import net.benjaminneukom.heavydefense.tower.bullets.Rocket;
import net.benjaminneukom.heavydefense.tower.bullets.TurretBullet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class ReferenceJSonSerializer {
	private Json json;
	private ReferenceStore referenceStore;
	private List<ReferenceSerializer<?>> serializers = new ArrayList<ReferenceSerializer<?>>();

	public ReferenceJSonSerializer(final ReferenceStore store) {
		this.json = new Json();
		this.referenceStore = store;

		addSerializer(TurretTower.class, new ReferenceActorSerializer<TurretTower>(TurretTower.class, referenceStore));
		addSerializer(RocketTower.class, new ReferenceActorSerializer<RocketTower>(RocketTower.class, referenceStore));
		addSerializer(SlowTower.class, new ReferenceActorSerializer<SlowTower>(SlowTower.class, referenceStore));
		addSerializer(FireTower.class, new ReferenceActorSerializer<FireTower>(FireTower.class, referenceStore));
		addSerializer(TeslaTower.class, new ReferenceActorSerializer<TeslaTower>(TeslaTower.class, referenceStore));
		addSerializer(ArtilleryTower.class, new ReferenceActorSerializer<ArtilleryTower>(ArtilleryTower.class, referenceStore));
		addSerializer(NoRoadsGameWorld.class, new ReferenceActorSerializer<NoRoadsGameWorld>(NoRoadsGameWorld.class, referenceStore, true));
		addSerializer(ClassicGameWorld.class, new ReferenceActorSerializer<ClassicGameWorld>(ClassicGameWorld.class, referenceStore, true));
		addSerializer(ClassicLavaGameWorld.class, new ReferenceActorSerializer<ClassicLavaGameWorld>(ClassicLavaGameWorld.class, referenceStore, true));
		addSerializer(NoRoadsLavaGameWorld.class, new ReferenceActorSerializer<NoRoadsLavaGameWorld>(NoRoadsLavaGameWorld.class, referenceStore, true));
		addSerializer(GroundEnemy.class, new ReferenceActorSerializer<GroundEnemy>(GroundEnemy.class, referenceStore));
		addSerializer(AirEnemy.class, new ReferenceActorSerializer<AirEnemy>(AirEnemy.class, referenceStore));
		addSerializer(BossEnemy.class, new ReferenceActorSerializer<BossEnemy>(BossEnemy.class, referenceStore));
		addSerializer(WaveSpawner.class, new ReferenceActorSerializer<WaveSpawner>(WaveSpawner.class, referenceStore));
		addSerializer(Explosion.class, new ReferenceActorSerializer<Explosion>(Explosion.class, referenceStore));
		addSerializer(LavaEruption.class, new ReferenceActorSerializer<LavaEruption>(LavaEruption.class, referenceStore));
		addSerializer(EruptionSpawner.class, new ReferenceActorSerializer<EruptionSpawner>(EruptionSpawner.class, referenceStore));
		addSerializer(FireDebuff.class, new ReferenceActorSerializer<FireDebuff>(FireDebuff.class, referenceStore));
		addSerializer(SlowDebuff.class, new ReferenceActorSerializer<SlowDebuff>(SlowDebuff.class, referenceStore));
		addSerializer(Rocket.class, new ReferenceActorSerializer<Rocket>(Rocket.class, referenceStore));
		addSerializer(TurretBullet.class, new ReferenceActorSerializer<TurretBullet>(TurretBullet.class, referenceStore));
	}

	public String prettyPrint(Object o) {
		referenceStore.clear();

		return json.prettyPrint(o);
	}

	public Object fromJson(Class<?> type, FileHandle handle) {
		referenceStore.clear();

		return json.fromJson(type, handle);
	}

	public void toJson(Object o, FileHandle handle) {
		referenceStore.clear();

		json.toJson(o, handle);
	}

	public <T> void addSerializer(Class<T> type, ReferenceSerializer<T> serializer) {
		json.setSerializer(type, serializer);
		serializers.add(serializer);
	}

}
