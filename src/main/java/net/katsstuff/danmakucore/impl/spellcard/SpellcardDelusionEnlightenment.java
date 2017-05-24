/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.spellcard;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.data.AbstractVector3;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.living.boss.EnumTouhouCharacters;
import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.entity.spellcard.SpellcardEntity;
import net.katsstuff.danmakucore.helper.DanmakuCreationHelper;
import net.katsstuff.danmakucore.helper.DanmakuHelper;
import net.katsstuff.danmakucore.lib.LibColor;
import net.katsstuff.danmakucore.lib.LibSpellcardName;
import net.katsstuff.danmakucore.lib.data.LibShotData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;

public class SpellcardDelusionEnlightenment extends Spellcard {

	public SpellcardDelusionEnlightenment() {
		super(LibSpellcardName.DELUSION_OF_ENLIGHTENMENT);
	}

	@Override
	public SpellcardEntity instantiate(EntitySpellcard card, @Nullable EntityLivingBase target) {
		return new SpellcardEntityDelusionEnlightenment(this, card, target);
	}

	@Override
	public int getLevel() {
		return 1;
	}

	@Override
	public int getRemoveTime() {
		return 50;
	}

	@Override
	public int getEndTime() {
		return 100;
	}

	@Override
	public EnumTouhouCharacters getOriginalUser() {
		return EnumTouhouCharacters.YOUMU_KONPAKU;
	}

	private static class SpellcardEntityDelusionEnlightenment extends SpellcardEntity {

		private SpellcardEntityDelusionEnlightenment(SpellcardDelusionEnlightenment type, EntitySpellcard card, @Nullable EntityLivingBase target) {
			super(type, card, target);
		}

		@Override
		public void onSpellcardUpdate() {
			if(!getWorld().isRemote) {
				int danmakuLevelMultiplier = danmakuLevel.getMultiplier();

				DanmakuHelper.playShotSound(card);
				for(int i = 0; i < danmakuLevelMultiplier; i++) {
					spawnGroundDanmaku();
				}

				int time40 = time % 40;
				if(time40 < 10) {
					int place = Math.max(0, 10 - time40);
					DanmakuTemplate danmaku = DanmakuTemplate.builder()
							.setUser(getUser())
							.setSource(card)
							.setMovementData(1D / (place + 1))
							.setShot(LibShotData.SHOT_MEDIUM.setColor(LibColor.COLOR_SATURATED_RED).setDelay(place))
							.build();

					DanmakuHelper.playShotSound(card);
					DanmakuCreationHelper.createWideShot(danmaku, danmakuLevelMultiplier * 2, 120F, 180F, 1.25D);
				}

				if(time40 == 0) {

					DanmakuHelper.playShotSound(card);
					for(int i = 1; i < 11; i++) {
						DanmakuTemplate danmaku = DanmakuTemplate.builder()
								.setUser(getUser())
								.setSource(card)
								.setMovementData(1D / i)
								.setShot(LibShotData.SHOT_MEDIUM.setColor(LibColor.COLOR_SATURATED_RED))
								.build();

						DanmakuCreationHelper.createWideShot(danmaku, danmakuLevelMultiplier, 30F, 0F, 0.5D);
					}
				}
			}
		}

		private void spawnGroundDanmaku() {
			AbstractVector3 angle = Vector3.getVecWithoutY(Vector3.randomVector());

			Vector3 posSource = posUser().offset(angle, rand.nextDouble() * 16);
			Vector3 posReach = posSource.offset(Vector3.Down(), 16);

			RayTraceResult ray = getWorld().rayTraceBlocks(posSource.toVec3d(), posReach.toVec3d());

			Vector3 spawnPos = ray != null ? new Vector3(ray.hitVec) : posReach; //Can I multiply the vectors here?

			EntityDanmaku danmaku = DanmakuTemplate.builder()
					.setUser(getUser())
					.setSource(card)
					.setAngle(Vector3.Up())
					.setMovementData(0.2D)
					.setPos(spawnPos)
					.setShot(LibShotData.SHOT_RICE.setColor(LibColor.COLOR_SATURATED_BLUE))
					.build().asEntity();

			getWorld().spawnEntityInWorld(danmaku);
		}
	}
}
