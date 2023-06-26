package com.example.example_mod.mixin;

import com.example.example_mod.boatDataGetter;
import com.example.example_mod.worldInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataAttribute;
import net.minecraft.entity.data.DataSerializers;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.water.WaterMobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.unmapped.C_9151277;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;

import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BoatEntity.class)
public class boatEntityMixin extends Entity implements boatDataGetter {
	@Shadow
	private BoatEntity.Status f_9935108;

	@Shadow
	private BoatEntity.Status f_1162355;

	@Shadow
	private float f_5538943;

	@Mutable
	@Final
	@Shadow
	private final float[] f_8753258;

	public boatEntityMixin(World world, float[] f87532581) {
		super(world);
		f_8753258 = f87532581;
	}

	@Shadow
	@Override
	protected void initDataTracker() {

	}

	@Shadow
	public void setBreakingWindow(int value) {}

	@Shadow
	public int getBreakingWindow() {
		return 0;
	}

	@Shadow
	private BoatEntity.Status m_0243142() {
		return null;
	}

	@Shadow
	public void setDamage(float damage) {}

	@Shadow
	public float getDamage() {
		return 0;
	}

	@Shadow
	private void m_6603229() {}

	@Shadow
	public void m_3106934(boolean bl, boolean bl2) {}

	@Shadow
	private void m_8878323() {}

	@Shadow
	private void m_2983793() {}

	@Shadow
	public boolean m_6343325(int i) {
		return false;
	}

	@Shadow
	protected SoundEvent m_3941801() {
		return null;
	}

	private static final DataAttribute<Integer> stackSize  = DataTracker.defineId(BoatEntity.class, DataSerializers.INTEGER_SERIALIZER);

	@Inject(method = "initDataTracker", at = @At("HEAD"))
	public void addTracker(CallbackInfo ci) {
		this.dataTracker.createData(stackSize, 1);
	}


	public void setStackSize(int value) {
		this.dataTracker.setValue(stackSize, value);
	}

	public int getStackSize() {
		return this.dataTracker.getValue(stackSize);
	}


	/**
	 * @author Emilia
	 * @reason this is the point of the mod
	 */
	@Overwrite
	public void tick() {
		this.f_9935108 = this.f_1162355;
		this.f_1162355 = this.m_0243142();
		if (this.f_1162355 != BoatEntity.Status.UNDER_WATER && this.f_1162355 != BoatEntity.Status.UNDER_FLOWING_WATER) {this.f_5538943 = 0.0F;} else {++this.f_5538943;}

		if (this.getBreakingWindow() > 0) {
			this.setBreakingWindow(this.getBreakingWindow() - 1);
		}

		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;

		if (this.getDamage() > 0.0F) {
			this.setDamage(this.getDamage() - 1.0F);
			if(this.getDamage() < 0) setDamage(0);
		}

		if (!this.world.isClient && this.f_5538943 >= 60.0F) {
			this.yeetPassengers();
		}
		super.tick();
		this.m_6603229();

		boolean hasMoved = false;
		if (this.m_7331429()) {
			if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof PlayerEntity)) {
				this.m_3106934(false, false);
			}

			this.m_8878323();
			if (this.world.isClient) {
				this.m_2983793();
				this.world.sendPacketToServer(new C_9151277(this.m_6343325(0), this.m_6343325(1)));
			}
			if(getStackSize() > 1) {
				double curX = this.x;
				double curY = this.y;
				double curZ = this.z;
				double curVelocityX = this.velocityX;
				double curVelocityY = this.velocityY;
				double curVelocityZ = this.velocityZ;
				this.move(MoverType.SELF, this.velocityX, this.velocityY, this.velocityZ);
				if(curX != this.x || curY != this.y || curZ != this.z) {
					hasMoved = true;
					int index = world.entities.indexOf(this) + 1;
					for(int i = 0; i < getStackSize() - 1; i++) {
						BoatEntity subBoat = new BoatEntity(world, curX, curY, curZ);
						subBoat.velocityX = curVelocityX;
						subBoat.velocityY = curVelocityY;
						subBoat.velocityZ = curVelocityZ;
						((worldInterface)world).addBoatsAfterBoat(subBoat, index);
					}
					setStackSize(1);

				}

			} else {
				this.move(MoverType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			}
		} else {
			this.velocityX = 0.0;
			this.velocityY = 0.0;
			this.velocityZ = 0.0;
		}

		for(int i = 0; i <= 1; ++i) {
			if (this.m_6343325(i)) {
				if (!this.isSilent() && (double)(this.f_8753258[i] % 6.2831855F) <= 0.7853981852531433 && ((double)this.f_8753258[i] + 0.39269909262657166) % 6.2831854820251465 >= 0.7853981852531433) {
					SoundEvent soundEvent = this.m_3941801();
					if (soundEvent != null) {
						Vec3d vec3d = this.m_0430803(1.0F);
						double d = i == 1 ? -vec3d.z : vec3d.z;
						double e = i == 1 ? vec3d.x : -vec3d.x;
						this.world.playSound(null, this.x + d, this.y, this.z + e, soundEvent, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
					}
				}

				float[] var10000 = this.f_8753258;
				var10000[i] = (float)((double)var10000[i] + 0.39269909262657166);
			} else {
				this.f_8753258[i] = 0.0F;
			}
		}

		this.checkBlockCollisions();

		if(!hasMoved && !world.isClient && this.getDamage() == 0 && this.velocityX == 0 && this.velocityY == 0 && this.velocityZ == 0  && !this.hasPassengers()) {
			int index = world.entities.indexOf(this);
			if(world.entities.size() > index + 1) {
				Entity nextEntity = world.entities.get(index + 1);
				if (nextEntity instanceof BoatEntity) {
					BoatEntity boat = (BoatEntity) nextEntity;
					if (!boat.removed && boat.x == this.x && boat.y == this.y && boat.z == this.z && boat.velocityX == 0 && boat.velocityY == 0 && boat.velocityZ == 0 && boat.getDamage() == 0 && !boat.hasPassengers()) {
						((boatDataGetter) boat).setStackSize(this.getStackSize() + ((boatDataGetter) boat).getStackSize());
						world.removeEntity(this);
						return;
					}
				}
			}
		}

		List<Entity> list = this.world.getEntities(this, this.getBoundingBox().expand(0.20000000298023224, -0.009999999776482582, 0.20000000298023224), EntityFilter.m_7080423(this));
		if (!list.isEmpty()) {
			java.util.List<Entity> passengers = this.getPassengers();
			boolean bl = !this.world.isClient && !(!passengers.isEmpty() && (passengers.get(0) instanceof PlayerEntity));

			for (Entity entity : list) {
				if (entity instanceof BoatEntity) {
					if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
						pushWithMultiplier(entity);
					}
				} else if (!entity.getPassengers().contains(this)) {
					if (bl && this.getPassengers().size() < 2 && !entity.hasVehicle() && entity.width < this.width && entity instanceof LivingEntity && !(entity instanceof WaterMobEntity) && !(entity instanceof PlayerEntity)) {
						if(getStackSize() > 1) {
							BoatEntity subBoat = new BoatEntity(world, this.x, this.y, this.z);
							((boatDataGetter)subBoat).setStackSize(this.getStackSize() - 1);
							((worldInterface)world).addBoatsAfterBoat(subBoat, world.entities.indexOf(this) + 1);
							setStackSize(1);
						}
						entity.m_9441016(this);
					} else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
						if (!this.m_6989343(entity)) {
							if (!entity.noClip && !this.noClip) {
								pushWithMultiplier(entity);
							}
						}
					}
				}
			}
		}
	}

	private void pushWithMultiplier(Entity entity) {
		double dx = entity.x - this.x;
		double dy = entity.z - this.z;
		double a = MathHelper.absMax(dx, dy);
		if (a >= 0.009999999776482582) {
			a = (!(a > 1) ? MathHelper.sqrt(a) : a) * 20;
			dx /= a;
			dy /= a;
			if (!this.hasPassengers()) {
				this.addVelocity(-dx, 0.0, -dy);
			}
			int multiplier = getStackSize();
			if (!entity.hasPassengers()) {
				entity.addVelocity(dx * multiplier, 0.0, dy * multiplier);
			}
		}
	}

	@Shadow
	public void setAnitmationSide(int value) {

	}

	@Shadow
	public int getAnimationSide() {
		return 0;
	}

	@Shadow
	public Item m_8928015() {
		return null;
	}

	/**
	 * @author Emilia
	 * @reason this is the point of the mod
	 */
	@Overwrite
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerable(source)) {
			return false;
		} else if (!this.world.isClient && !this.removed) {
			if (source instanceof ProjectileDamageSource && source.getAttacker() != null && this.m_5613735(source.getAttacker())) {
				return false;
			} else if (source.isExplosive() && amount > 4) {
				for(int i = 0; i < getStackSize(); i++) {this.dropItem(this.m_8928015(), 1, 0.0F);}
				this.remove();
				return true;
			} else {
				if(getStackSize() > 1) {
					BoatEntity subBoat = new BoatEntity(world, this.x, this.y, this.z);
					((boatDataGetter)subBoat).setStackSize(this.getStackSize() - 1);
					((worldInterface)world).addBoatsAfterBoat(subBoat, world.entities.indexOf(this) + 1);
					setStackSize(1);
				}
				this.setAnitmationSide(-this.getAnimationSide());
				this.setBreakingWindow(10);
				this.setDamage(this.getDamage() + amount * 10.0F);
				this.onDamaged();
				boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).abilities.creativeMode;
				if (bl || this.getDamage() > 40.0F) {
					if (!bl && this.world.getGameRules().getBoolean("doEntityDrops")) {
						this.dropItem(this.m_8928015(), 1, 0.0F);
					}
					this.remove();
				}
				return true;
			}
		} else {
			return true;
		}
	}


	/**
	 * @author Emilia
	 * @reason this is the point of the mod
	 */
	@Overwrite
	public boolean interact(PlayerEntity playerEntity, InteractionHand interactionHand) {
		if (playerEntity.isSneaking()) {
			return false;
		} else {
			if (!this.world.isClient && this.f_5538943 < 60.0F) {
				if(getStackSize() > 1) {
					BoatEntity subBoat = new BoatEntity(world, this.x, this.y, this.z);
					((boatDataGetter)subBoat).setStackSize(this.getStackSize() - 1);
					((worldInterface)world).addBoatsAfterBoat(subBoat, world.entities.indexOf(this) + 1);
					setStackSize(1);
				}
				playerEntity.m_9441016(this);
			}
			return true;
		}
	}

	/**
	 * @author Emilia
	 * @reason make it save correctly
	 */
	@Overwrite
	public void writeCustomNbt(NbtCompound nbt) {
		nbt.putString("Type", this.getType().getName());
		nbt.putInt("stackCount", getStackSize());
	}

	/**
	 * @author Emilia
	 * @reason make it save correctly
	 */
	@Overwrite
	public void readCustomNbt(NbtCompound nbt) {
		if (nbt.isType("Type", 8)) {
			this.setType(BoatEntity.Type.byName(nbt.getString("Type")));
		}
		if(nbt.getInt("stackCount") > 0) setStackSize(nbt.getInt("stackCount"));
	}

	@Shadow
	public void setType(BoatEntity.Type type) {

	}

	@Shadow
	public BoatEntity.Type getType() {
		return null;
	}
}
