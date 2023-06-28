package io.github.EmiliaThorsen;

import net.minecraft.entity.Entity;

public interface WorldInterface {
	void addBoatsAfterBoat(Entity entity, int index); //lets you insert boats at any point in the entity ticking order
}
