package io.github.EmiliaThorsen.mixin;

import io.github.EmiliaThorsen.BoatDataGetter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(World world) {super(world);}

	/**
	 * @author Emilia
	 * @reason make stacked boats cram correctly
	 */
	@Overwrite
	public void pushAwayCollidingEntities() {
		List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), EntityFilter.m_7080423(this));
		if (!list.isEmpty()) {
			int i = this.world.getGameRules().getInt("maxEntityCramming");

			if (i > 0 && this.random.nextInt(4) == 0) {
				int j = 0;
				//check if a touching entity is a boat and add its count
				for (Entity entity : list) j += (!entity.hasVehicle()) ? (entity instanceof BoatEntity) ? ((BoatDataGetter)entity).getStackSize() : 1 : 0;

				if (j > i - 1) this.damage(DamageSource.CRAMMING, 6.0F);
			}

			for (Entity entity : list) {
				entity.push(this);
			}
		}
	}
}
