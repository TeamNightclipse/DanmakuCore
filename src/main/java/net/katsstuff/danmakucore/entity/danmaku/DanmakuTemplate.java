/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public final class DanmakuTemplate {

	public World world;
	@Nullable
	public EntityLivingBase user;
	@Nullable
	public Entity source;
	public ShotData shot;
	public Vector3 pos;
	public Vector3 direction;
	public float roll = 0F;
	public MovementData movement = MovementData.constant(0.4D);
	public RotationData rotation = RotationData.none();

	private DanmakuTemplate(World world, @Nullable EntityLivingBase user, @Nullable Entity source, ShotData shot, Vector3 pos, Vector3 direction,
			float roll, MovementData movement, RotationData rotation) {
		this.world = world;
		this.user = user;
		this.source = source;
		this.shot = shot;
		this.pos = pos;
		this.direction = direction;
		this.roll = roll;
		this.movement = movement;
		this.rotation = rotation;
	}

	public DanmakuTemplate copy() {
		return new DanmakuTemplate(world, user, source, shot, pos, direction, roll, movement, rotation);
	}

	public EntityDanmaku asEntity() {
		return new EntityDanmaku(world, user, source, shot, pos, direction, roll, movement, rotation);
	}

	public static Builder builder() {
		return new Builder();
	}

	@SuppressWarnings("unused")
	public static class Builder {

		public World world;
		@Nullable
		public EntityLivingBase user;
		@Nullable
		public Entity source;
		public ShotData shot;
		public Vector3 pos;
		public Vector3 direction;
		public float roll = 0F;
		public MovementData movement = MovementData.constant(0.4D);
		public RotationData rotation = RotationData.none();

		public DanmakuTemplate build() {

			if(source == null && user != null) {
				source = user;
			}

			if(world == null) {
				if(source != null) {
					world = source.world;
				}
				else throw new IllegalArgumentException("Could not find a world for builder, and neither source or user is set");
			}

			if(pos == null) {
				if(user != null) {
					pos = new Vector3(user);
				}
				else if(source != null) {
					pos = new Vector3(source);
				}
				else throw new IllegalArgumentException("Could not find a pos for builder, and neither source or user is set");
			}

			if(direction == null) {
				if(source != null) {
					direction = Vector3.directionEntity(source);
				}
				else throw new IllegalArgumentException("could not find an direction for builder, and neither source or user is set");
			}

			if(shot == null) throw new IllegalArgumentException("Make sure that shot is set");

			return new DanmakuTemplate(world, user, source, shot, pos, direction, roll, movement, rotation);
		}

		public Builder setWorld(World world) {
			this.world = world;
			return this;
		}

		public Builder setUser(@Nullable EntityLivingBase user) {
			this.user = user;
			return this;
		}

		public Builder setSource(@Nullable Entity source) {
			this.source = source;
			return this;
		}

		public Builder setShot(ShotData shot) {
			this.shot = shot;
			return this;
		}

		public Builder setPos(Vector3 pos) {
			this.pos = pos;
			return this;
		}

		public Builder setDirection(Vector3 direction) {
			this.direction = direction;
			return this;
		}

		public Builder setRoll(float roll) {
			this.roll = roll;
			return this;
		}

		public Builder setVariant(DanmakuVariant variant) {
			setShot(variant.getShotData());
			setMovementData(variant.getMovementData());
			setRotationData(variant.getRotationData());
			return this;
		}

		public Builder setMovementData(MovementData movementData) {
			this.movement = movementData;
			return this;
		}

		public Builder setMovementData(Vector3 gravity) {
			return setMovementData(
					movement.copy(movement.speedOriginal(), movement.lowerSpeedLimit(), movement.upperSpeedLimit(), movement.speedAcceleration(),
							gravity));
		}

		public Builder setMovementData(double speed) {
			return setMovementData(new MovementData(speed, speed, speed, 0D, movement.getGravity()));
		}

		public Builder setMovementData(double speed, Vector3 gravity) {
			return setMovementData(new MovementData(speed, speed, speed, 0D, gravity));
		}

		public Builder setMovementData(double speed, double speedLimit, double speedAcceleration) {
			return setMovementData(new MovementData(speed, 0F, speedLimit, speedAcceleration, movement.getGravity()));
		}

		public Builder setMovementData(double speed, double lowerSpeedLimit, double upperSpeedLimit, double speedAcceleration) {
			return setMovementData(new MovementData(speed, lowerSpeedLimit, upperSpeedLimit, speedAcceleration, movement.getGravity()));
		}

		public Builder setMovementData(double speed, double lowerSpeedLimit, double upperSpeedLimit, double speedAcceleration, Vector3 gravity) {
			return setMovementData(new MovementData(speed, lowerSpeedLimit, upperSpeedLimit, speedAcceleration, gravity));
		}

		public Builder setRotationData(RotationData rotationData) {
			this.rotation = rotationData;
			return this;
		}

		public Builder setRotationData(Quat rotation) {
			return setRotationData(new RotationData(true, rotation, 9999));
		}

		public Builder setRotationData(Vector3 axis, float angle) {
			return setRotationData(new RotationData(true, Quat.fromAxisAngle(axis, angle), 9999));
		}

		public Builder setRotationData(Vector3 axis, float angle, int endTime) {
			return setRotationData(new RotationData(true, Quat.fromAxisAngle(axis, angle), endTime));
		}
	}
}
