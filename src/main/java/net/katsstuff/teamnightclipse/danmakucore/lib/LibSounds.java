/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.lib;

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore;
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
