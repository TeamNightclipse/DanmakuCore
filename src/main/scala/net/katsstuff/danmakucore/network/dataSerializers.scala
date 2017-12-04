/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network

import net.katsstuff.danmakucore.data.{ShotData, Vector3}
import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.{DataParameter, DataSerializer}

class OptionSerializers[A](serializer: DataSerializer[A]) extends DataSerializer[Option[A]] {

  override def write(buf: PacketBuffer, value: Option[A]): Unit = value match {
    case Some(present) =>
      buf.writeBoolean(true)
      serializer.write(buf, present)
    case None => buf.writeBoolean(false)
  }
  override def read(buf: PacketBuffer): Option[A] =
    if (buf.readBoolean()) {
      Some(serializer.read(buf))
    } else None

  override def createKey(id: Int):          DataParameter[Option[A]] = new DataParameter(id, this)
  override def copyValue(value: Option[A]): Option[A]                = value.map(serializer.copyValue)
}

class SeqSerializer[A](serializer: DataSerializer[A]) extends DataSerializer[Seq[A]] {
  override def write(buf: PacketBuffer, value: Seq[A]): Unit = {
    buf.writeInt(value.size)
    value.foreach(serializer.write(buf, _))
  }

  override def read(buf: PacketBuffer): Seq[A] = {
    val size = buf.readInt()
    for (_ <- 0 until size) yield serializer.read(buf)
  }

  override def createKey(id: Int):       DataParameter[Seq[A]] = new DataParameter(id, this)
  override def copyValue(value: Seq[A]): Seq[A]                = value.map(serializer.copyValue)
}

object Vector3Serializer extends DataSerializer[Vector3] {
  override def write(buf: PacketBuffer, value: Vector3): Unit = {
    buf.writeDouble(value.x)
    buf.writeDouble(value.y)
    buf.writeDouble(value.z)
  }
  override def read(buf: PacketBuffer):   Vector3                = Vector3(buf.readDouble(), buf.readDouble(), buf.readDouble())
  override def createKey(id: Int):        DataParameter[Vector3] = new DataParameter(id, this)
  override def copyValue(value: Vector3): Vector3                = value
}

object ShotDataSerializer extends DataSerializer[ShotData]() {
  override def write(buf: PacketBuffer, shot: ShotData): Unit =
    shot
      .serializeByteBuf(buf)
  override def read(buf: PacketBuffer) = new ShotData(buf)
  override def createKey(id: Int)      = new DataParameter[ShotData](id, this)
  override def copyValue(value: ShotData): ShotData = value
}

class EnumSerializer[T <: Enum[T]](val enumClass: Class[T]) extends DataSerializer[T] {
  override def write(buf: PacketBuffer, value: T): Unit = buf.writeEnumValue(value)
  override def read(buf: PacketBuffer):            T    = buf.readEnumValue(enumClass)
  override def createKey(id: Int) = new DataParameter[T](id, this)
  override def copyValue(value: T): T = value
}
