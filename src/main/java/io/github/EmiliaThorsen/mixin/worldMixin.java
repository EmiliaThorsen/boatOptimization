package io.github.EmiliaThorsen.mixin;

import io.github.EmiliaThorsen.worldInterface;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(World.class)
public class worldMixin implements worldInterface {
	@Mutable
	@Final
	@Shadow
	public final List<Entity> entities;

	public worldMixin(List<Entity> entities) {
		this.entities = entities;
	}

	public void addBoatsAfterBoat(Entity entity, int index) {
		this.getChunkAt(MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0)).addEntity(entity);
		this.entities.add(index, entity);
		this.onEntityAdded(entity);
	}

	@Shadow
	public WorldChunk getChunkAt(int chunkX, int chunkZ) {
		return null;
	}

	@Shadow
	protected void onEntityAdded(Entity entity) {}

}
