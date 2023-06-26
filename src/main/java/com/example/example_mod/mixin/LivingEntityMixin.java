package com.example.example_mod.mixin;

import com.example.example_mod.boatDataGetter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin extends Entity {


	public LivingEntityMixin(World world) {
		super(world);
	}

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

				for (Entity entity : list) j += (!entity.hasVehicle()) ? (entity instanceof BoatEntity) ? ((boatDataGetter)entity).getStackSize() : 1 : 0;

				if (j > i - 1) this.damage(DamageSource.CRAMMING, 6.0F);
			}

			for (Entity entity : list) {
				entity.push(this);
			}
		}
	}

	@Shadow
	@Override
	protected void initDataTracker() {

	}

	@Shadow
	@Override
	public void readCustomNbt(NbtCompound nbt) {

	}

	@Shadow
	@Override
	public void writeCustomNbt(NbtCompound nbt) {

	}
}
