/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability;

import net.katsstuff.danmakucore.network.CoreDataPacket;
import net.katsstuff.danmakucore.network.DanmakuCorePacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public interface IDanmakuCoreData {

	float getPower();

	void setPower(float power);

	default void addPower(float power) {
		setPower(getPower() + power);
	}

	int getScore();

	void setScore(int score);

	default void addScore(int score) {
		setScore(getScore() + score);
	}

	int getLives();

	void setLives(int lives);

	default void addLives(int lives) {
		setLives(getLives() + lives);
	}

	default void addLife() {
		addLives(1);
	}
	default void removeLife() {
		addLives(-1);
	}

	int getBombs();

	void setBombs(int bombs);

	default void addBombs(int bombs) {
		setBombs(getBombs() + bombs);
	}

	default void addBomb() {
		addBombs(1);
	}

	default void removeBomb() {
		addBombs(-1);
	}

	default void syncTo(EntityPlayerMP playerMP, Entity target) {
		DanmakuCorePacketHandler.INSTANCE.sendTo(new CoreDataPacket.CoreDataMessage(this, target), playerMP);
	}

	default void syncToClose(NetworkRegistry.TargetPoint point, Entity target) {
		DanmakuCorePacketHandler.INSTANCE.sendToAllAround(new CoreDataPacket.CoreDataMessage(this, target), point);
	}
}
