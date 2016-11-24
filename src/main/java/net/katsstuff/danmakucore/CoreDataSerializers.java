/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore;

import java.io.IOException;

import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;

public class CoreDataSerializers {

	public static final DataSerializer<ShotData> SHOTDATA = new DataSerializer<ShotData>() {

		@Override
		public void write(PacketBuffer buf, ShotData shot) {
			shot.serializeByteBuf(buf);
		}

		@Override
		public ShotData read(PacketBuffer buf) throws IOException {
			return new ShotData(buf);
		}

		@Override
		public DataParameter<ShotData> createKey(int id) {
			return new DataParameter<>(id, this);
		}
	};

	@SuppressWarnings("WeakerAccess")
	public static final DataSerializer<Vector3> VECTOR_3 = new DataSerializer<Vector3>() {

		@Override
		public void write(PacketBuffer buf, Vector3 vector) {
			buf.writeDouble(vector.x());
			buf.writeDouble(vector.y());
			buf.writeDouble(vector.z());
		}

		@Override
		public Vector3 read(PacketBuffer buf) throws IOException {
			return new Vector3(buf.readDouble(), buf.readDouble(), buf.readDouble());
		}

		@Override
		public DataParameter<Vector3> createKey(int id) {
			return new DataParameter<>(id, this);
		}
	};
}
