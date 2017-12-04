/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib;

import net.katsstuff.danmakucore.DanmakuCore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class LibSounds {

	public static final SoundEvent ENEMY_POWER = createSoundEvent("power.enemy");
	public static final SoundEvent DAMAGE = createSoundEvent("damage.normal");
	public static final SoundEvent DAMAGE_LOW = createSoundEvent("damage.low");
	public static final SoundEvent BOSS_EXPLODE = createSoundEvent("boss.explode");
	public static final SoundEvent TIMEOUT = createSoundEvent("timeout");
	public static final SoundEvent SHADOW = createSoundEvent("shadow");
	public static final SoundEvent HIDDEN = createSoundEvent("shadow.hidden");
	public static final SoundEvent SUDDEN = createSoundEvent("shadow.sudden");
	public static final SoundEvent LASER1 = createSoundEvent("laser.laser1");
	public static final SoundEvent LASER2 = createSoundEvent("laser.laser2");
	public static final SoundEvent SHOT1 = createSoundEvent("shot.shot1");
	public static final SoundEvent SHOT2 = createSoundEvent("shot.shot2");
	public static final SoundEvent SHOT3 = createSoundEvent("shot.shot3");
	public static final SoundEvent GRAZE = createSoundEvent("graze");
	public static final SoundEvent SCORE = createSoundEvent("score");

	/* TODO
	public static final SoundEvent EXTEND = createSoundEvent("extend");
	public static final SoundEvent BIG_LASER = createSoundEvent("laser.big");
	public static final SoundEvent DEATH = createSoundEvent("death");
	public static final SoundEvent PLAYER_POWER = createSoundEvent("power.player");
	*/
	
	public static SoundEvent createSoundEvent(String name) {
		ResourceLocation resource = DanmakuCore.resource(name);
		SoundEvent soundEvent = new SoundEvent(resource);
		soundEvent.setRegistryName(resource);
		return soundEvent;
	}
}
