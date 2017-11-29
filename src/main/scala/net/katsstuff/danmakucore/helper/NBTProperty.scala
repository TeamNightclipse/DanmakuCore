/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper

import java.util.{function => jfunc}
import java.{lang => jlang}

import net.katsstuff.danmakucore.helper.Implicits._
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraftforge.common.util.Constants

/**
  * Something that describes a specific variable in an nbt tag. Allowing you
  * to set and get the value, with a default value if it's missing.
  * @tparam A The type to get and set.
  * @tparam Holder What the nbt is attached to.
  */
trait NBTProperty[A, Holder] {
  def default:                        () => A
  def isDefined(nbt: NBTTagCompound): Boolean
  def holderToNbt:                    Holder => NBTTagCompound
  def modify[B](f: A => B, fInverse: B => A): NBTProperty[B, Holder] = ModifiedNBTProperty(this, f, fInverse)
  def modify[B](f: jfunc.Function[A, B], fInverse: jfunc.Function[B, A]): NBTProperty[B, Holder] =
    modify(a => f(a), b => fInverse(b))
  def compose[B](other: NBTProperty[B, Holder]): NBTProperty[(A, B), Holder] = ComposedNBTProperty(this, other)

  /**
    * Get value represented by this property, or the
    * default if it's not present.
    */
  def get(holder: Holder): A = {
    val nbt = holderToNbt(holder)
    if (isDefined(nbt)) getNbt(nbt)
    else default()
  }

  /**
    * Set the value represented by this property.
    */
  def set(a: A, holder: Holder): Unit = setNbt(a, holderToNbt(holder))

  def getNbt(nbt: NBTTagCompound): A

  def setNbt(a: A, nbt: NBTTagCompound): Unit
}
object NBTProperty {
  val ItemStackToNbt: ItemStack => NBTTagCompound = ItemNBTHelper.getNBT
}

trait PrimitiveNBTProperty[A, Holder] extends NBTProperty[A, Holder] {
  def tpe: Int
  def key: String
  override def isDefined(nbt: NBTTagCompound): Boolean = nbt.hasKey(key, tpe)
}

case class BooleanNBTProperty[Holder](
    key: String,
    holderToNbt: Holder => NBTTagCompound,
    default: () => Boolean = () => false
) extends PrimitiveNBTProperty[Boolean, Holder] {
  override def tpe:                                     Int     = Constants.NBT.TAG_BYTE
  override def getNbt(nbt: NBTTagCompound):             Boolean = nbt.getBoolean(key)
  override def setNbt(a: Boolean, nbt: NBTTagCompound): Unit    = nbt.setBoolean(key, a)

  override def get(holder: Holder):             Boolean = super.get(holder)
  override def set(a: Boolean, holder: Holder): Unit    = super.set(a, holder)
  def modify2[B](
      f: jfunc.Function[jlang.Boolean, B],
      fInverse: jfunc.Function[B, jlang.Boolean]
  ): NBTProperty[B, Holder] =
    super.modify(a => f(a), b => fInverse(b))
}
object BooleanNBTProperty {
  def ofStack(key: String, default: Boolean): BooleanNBTProperty[ItemStack] =
    BooleanNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Boolean): BooleanNBTProperty[ItemStack] =
    BooleanNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: jfunc.BooleanSupplier): BooleanNBTProperty[ItemStack] =
    BooleanNBTProperty(key, NBTProperty.ItemStackToNbt, () => default.getAsBoolean)
  def ofStack(key: String): BooleanNBTProperty[ItemStack] = BooleanNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Boolean): BooleanNBTProperty[NBTTagCompound] =
    BooleanNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Boolean): BooleanNBTProperty[NBTTagCompound] =
    BooleanNBTProperty(key, identity, default)
  def ofNbt(key: String, default: jfunc.BooleanSupplier): BooleanNBTProperty[NBTTagCompound] =
    BooleanNBTProperty(key, identity, () => default.getAsBoolean)
  def ofNbt(key: String): BooleanNBTProperty[NBTTagCompound] = BooleanNBTProperty(key, identity)
}

case class ByteNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Byte = () => 0)
    extends PrimitiveNBTProperty[Byte, Holder] {
  override def tpe:                                  Int  = Constants.NBT.TAG_BYTE
  override def getNbt(nbt: NBTTagCompound):          Byte = nbt.getByte(key)
  override def setNbt(a: Byte, nbt: NBTTagCompound): Unit = nbt.setByte(key, a)

  override def get(holder: Holder):          Byte = super.get(holder)
  override def set(a: Byte, holder: Holder): Unit = super.set(a, holder)
  def modify2[B](f: jfunc.Function[jlang.Byte, B], fInverse: jfunc.Function[B, jlang.Byte]): NBTProperty[B, Holder] =
    super.modify(a => f(a), b => fInverse(b))
}
object ByteNBTProperty {
  def ofStack(key: String, default: Byte): ByteNBTProperty[ItemStack] =
    ByteNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Byte): ByteNBTProperty[ItemStack] =
    ByteNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: jfunc.Supplier[jlang.Byte]): ByteNBTProperty[ItemStack] =
    ByteNBTProperty(key, NBTProperty.ItemStackToNbt, () => default.get())
  def ofStack(key: String): ByteNBTProperty[ItemStack] = ByteNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Byte):       ByteNBTProperty[NBTTagCompound] = ByteNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Byte): ByteNBTProperty[NBTTagCompound] = ByteNBTProperty(key, identity, default)
  def ofNbt(key: String, default: jfunc.Supplier[jlang.Byte]): ByteNBTProperty[NBTTagCompound] =
    ByteNBTProperty(key, identity, () => default.get())
  def ofNbt(key: String): ByteNBTProperty[NBTTagCompound] = ByteNBTProperty(key, identity)
}

case class ShortNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Short = () => 0)
    extends PrimitiveNBTProperty[Short, Holder] {
  override def tpe:                                   Int   = Constants.NBT.TAG_SHORT
  override def getNbt(nbt: NBTTagCompound):           Short = nbt.getShort(key)
  override def setNbt(a: Short, nbt: NBTTagCompound): Unit  = nbt.setShort(key, a)

  override def get(holder: Holder):           Short = super.get(holder)
  override def set(a: Short, holder: Holder): Unit  = super.set(a, holder)
  def modify2[B](f: jfunc.Function[jlang.Short, B], fInverse: jfunc.Function[B, jlang.Short]): NBTProperty[B, Holder] =
    super.modify(a => f(a), b => fInverse(b))
}
object ShortNBTProperty {
  def ofStack(key: String, default: Short): ShortNBTProperty[ItemStack] =
    ShortNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Short): ShortNBTProperty[ItemStack] =
    ShortNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: jfunc.Supplier[jlang.Short]): ShortNBTProperty[ItemStack] =
    ShortNBTProperty(key, NBTProperty.ItemStackToNbt, () => default.get())
  def ofStack(key: String): ShortNBTProperty[ItemStack] = ShortNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Short): ShortNBTProperty[NBTTagCompound] =
    ShortNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Short): ShortNBTProperty[NBTTagCompound] =
    ShortNBTProperty(key, identity, default)
  def ofNbt(key: String, default: jfunc.Supplier[jlang.Short]): ShortNBTProperty[NBTTagCompound] =
    ShortNBTProperty(key, identity, () => default.get())
  def ofNbt(key: String): ShortNBTProperty[NBTTagCompound] = ShortNBTProperty(key, identity)
}

case class IntNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Int = () => 0)
    extends PrimitiveNBTProperty[Int, Holder] {
  override def tpe:                                 Int  = Constants.NBT.TAG_INT
  override def getNbt(nbt: NBTTagCompound):         Int  = nbt.getInteger(key)
  override def setNbt(a: Int, nbt: NBTTagCompound): Unit = nbt.setInteger(key, a)

  override def get(holder: Holder):         Int  = super.get(holder)
  override def set(a: Int, holder: Holder): Unit = super.set(a, holder)
  def modify[B](f: jfunc.IntFunction[B], fInverse: jfunc.ToIntFunction[B]): NBTProperty[B, Holder] =
    super.modify(a => f(a), b => fInverse.applyAsInt(b))
}
object IntNBTProperty {
  def ofStack(key: String, default: Int): IntNBTProperty[ItemStack] =
    IntNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Int): IntNBTProperty[ItemStack] =
    IntNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: jfunc.IntSupplier): IntNBTProperty[ItemStack] =
    IntNBTProperty(key, NBTProperty.ItemStackToNbt, () => default.getAsInt)
  def ofStack(key: String): IntNBTProperty[ItemStack] = IntNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Int):       IntNBTProperty[NBTTagCompound] = IntNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Int): IntNBTProperty[NBTTagCompound] = IntNBTProperty(key, identity, default)
  def ofNbt(key: String, default: jfunc.IntSupplier): IntNBTProperty[NBTTagCompound] =
    IntNBTProperty(key, identity, () => default.getAsInt)
  def ofNbt(key: String): IntNBTProperty[NBTTagCompound] = IntNBTProperty(key, identity)
}

case class LongNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Long = () => 0)
    extends PrimitiveNBTProperty[Long, Holder] {
  override def tpe:                                  Int  = Constants.NBT.TAG_LONG
  override def getNbt(nbt: NBTTagCompound):          Long = nbt.getLong(key)
  override def setNbt(a: Long, nbt: NBTTagCompound): Unit = nbt.setLong(key, a)

  override def get(holder: Holder):          Long = super.get(holder)
  override def set(a: Long, holder: Holder): Unit = super.set(a, holder)
  def modify[B](f: jfunc.LongFunction[B], fInverse: jfunc.ToLongFunction[B]): NBTProperty[B, Holder] =
    super.modify(a => f(a), b => fInverse.applyAsLong(b))
}
object LongNBTProperty {
  def ofStack(key: String, default: Long): LongNBTProperty[ItemStack] =
    LongNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Long): LongNBTProperty[ItemStack] =
    LongNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: jfunc.LongSupplier): LongNBTProperty[ItemStack] =
    LongNBTProperty(key, NBTProperty.ItemStackToNbt, () => default.getAsLong)
  def ofStack(key: String): LongNBTProperty[ItemStack] = LongNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Long):       LongNBTProperty[NBTTagCompound] = LongNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Long): LongNBTProperty[NBTTagCompound] = LongNBTProperty(key, identity, default)
  def ofNbt(key: String, default: jfunc.LongSupplier): LongNBTProperty[NBTTagCompound] =
    LongNBTProperty(key, identity, () => default.getAsLong)
  def ofNbt(key: String): LongNBTProperty[NBTTagCompound] = LongNBTProperty(key, identity)
}

case class FloatNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Float = () => 0)
    extends PrimitiveNBTProperty[Float, Holder] {
  override def tpe:                                   Int   = Constants.NBT.TAG_FLOAT
  override def getNbt(nbt: NBTTagCompound):           Float = nbt.getFloat(key)
  override def setNbt(a: Float, nbt: NBTTagCompound): Unit  = nbt.setFloat(key, a)

  override def get(holder: Holder):           Float = super.get(holder)
  override def set(a: Float, holder: Holder): Unit  = super.set(a, holder)
  def modify2[B](f: jfunc.Function[jlang.Float, B], fInverse: jfunc.Function[B, jlang.Float]): NBTProperty[B, Holder] =
    super.modify(a => f(a), b => fInverse(b))
}
object FloatNBTProperty {
  def ofStack(key: String, default: Float): FloatNBTProperty[ItemStack] =
    FloatNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Float): FloatNBTProperty[ItemStack] =
    FloatNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: jfunc.Supplier[jlang.Float]): FloatNBTProperty[ItemStack] =
    FloatNBTProperty(key, NBTProperty.ItemStackToNbt, () => default.get())
  def ofStack(key: String): FloatNBTProperty[ItemStack] = FloatNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Float): FloatNBTProperty[NBTTagCompound] =
    FloatNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Float): FloatNBTProperty[NBTTagCompound] =
    FloatNBTProperty(key, identity, default)
  def ofNbt(key: String, default: jfunc.Supplier[jlang.Float]): FloatNBTProperty[NBTTagCompound] =
    FloatNBTProperty(key, identity, () => default.get())
  def ofNbt(key: String): FloatNBTProperty[NBTTagCompound] = FloatNBTProperty(key, identity)
}

case class DoubleNBTProperty[Holder](
    key: String,
    holderToNbt: Holder => NBTTagCompound,
    default: () => Double = () => 0
) extends PrimitiveNBTProperty[Double, Holder] {
  override def tpe:                                    Int    = Constants.NBT.TAG_DOUBLE
  override def getNbt(nbt: NBTTagCompound):            Double = nbt.getDouble(key)
  override def setNbt(a: Double, nbt: NBTTagCompound): Unit   = nbt.setDouble(key, a)

  override def get(holder: Holder):            Double = super.get(holder)
  override def set(a: Double, holder: Holder): Unit   = super.set(a, holder)
  def modify[B](f: jfunc.DoubleFunction[B], fInverse: jfunc.ToDoubleFunction[B]): NBTProperty[B, Holder] =
    super.modify(a => f(a), b => fInverse.applyAsDouble(b))
}
object DoubleNBTProperty {
  def ofStack(key: String, default: Double): DoubleNBTProperty[ItemStack] =
    DoubleNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Double): DoubleNBTProperty[ItemStack] =
    DoubleNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: jfunc.DoubleSupplier): DoubleNBTProperty[ItemStack] =
    DoubleNBTProperty(key, NBTProperty.ItemStackToNbt, () => default.getAsDouble)
  def ofStack(key: String): DoubleNBTProperty[ItemStack] = DoubleNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Double): DoubleNBTProperty[NBTTagCompound] =
    DoubleNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Double): DoubleNBTProperty[NBTTagCompound] =
    DoubleNBTProperty(key, identity, default)
  def ofNbt(key: String, default: jfunc.DoubleSupplier): DoubleNBTProperty[NBTTagCompound] =
    DoubleNBTProperty(key, identity, () => default.getAsDouble)
  def ofNbt(key: String): DoubleNBTProperty[NBTTagCompound] = DoubleNBTProperty(key, identity)
}

case class StringNBTProperty[Holder](
    key: String,
    holderToNbt: Holder => NBTTagCompound,
    default: () => String = () => ""
) extends PrimitiveNBTProperty[String, Holder] {
  override def tpe:                                    Int    = Constants.NBT.TAG_STRING
  override def getNbt(nbt: NBTTagCompound):            String = nbt.getString(key)
  override def setNbt(a: String, nbt: NBTTagCompound): Unit   = nbt.setString(key, a)

  override def get(holder: Holder):            String = super.get(holder)
  override def set(a: String, holder: Holder): Unit   = super.set(a, holder)
  override def modify[B](f: jfunc.Function[String, B], fInverse: jfunc.Function[B, String]): NBTProperty[B, Holder] =
    super.modify(f, fInverse)
}
object StringNBTProperty {
  def ofStack(key: String, default: String): StringNBTProperty[ItemStack] =
    StringNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => String): StringNBTProperty[ItemStack] =
    StringNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: jfunc.Supplier[String]): StringNBTProperty[ItemStack] =
    StringNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String): StringNBTProperty[ItemStack] = StringNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: String): StringNBTProperty[NBTTagCompound] =
    StringNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => String): StringNBTProperty[NBTTagCompound] =
    StringNBTProperty(key, identity, default)
  def ofNbt(key: String, default: jfunc.Supplier[String]): StringNBTProperty[NBTTagCompound] =
    StringNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String): StringNBTProperty[NBTTagCompound] = StringNBTProperty(key, identity)
}

case class ByteArrayNBTProperty[Holder](
    key: String,
    holderToNbt: Holder => NBTTagCompound,
    default: () => Array[Byte] = () => Array.empty
) extends PrimitiveNBTProperty[Array[Byte], Holder] {
  override def tpe:                                         Int         = Constants.NBT.TAG_DOUBLE
  override def getNbt(nbt: NBTTagCompound):                 Array[Byte] = nbt.getByteArray(key)
  override def setNbt(a: Array[Byte], nbt: NBTTagCompound): Unit        = nbt.setByteArray(key, a)

  override def get(holder: Holder):                 Array[Byte] = super.get(holder)
  override def set(a: Array[Byte], holder: Holder): Unit        = super.set(a, holder)
  override def modify[B](
      f: jfunc.Function[Array[Byte], B],
      fInverse: jfunc.Function[B, Array[Byte]]
  ): NBTProperty[B, Holder] =
    super.modify(f, fInverse)
}
object ByteArrayNBTProperty {
  def ofStack(key: String, default: () => Array[Byte]): ByteArrayNBTProperty[ItemStack] =
    ByteArrayNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: jfunc.Supplier[Array[Byte]]): ByteArrayNBTProperty[ItemStack] =
    ByteArrayNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String): ByteArrayNBTProperty[ItemStack] = ByteArrayNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: () => Array[Byte]): ByteArrayNBTProperty[NBTTagCompound] =
    ByteArrayNBTProperty(key, identity, default)
  def ofNbt(key: String, default: jfunc.Supplier[Array[Byte]]): ByteArrayNBTProperty[NBTTagCompound] =
    ByteArrayNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String): ByteArrayNBTProperty[NBTTagCompound] = ByteArrayNBTProperty(key, identity)
}

case class IntArrayNBTProperty[Holder](
    key: String,
    holderToNbt: Holder => NBTTagCompound,
    default: () => Array[Int] = () => Array.empty
) extends PrimitiveNBTProperty[Array[Int], Holder] {
  override def tpe:                                        Int        = Constants.NBT.TAG_DOUBLE
  override def getNbt(nbt: NBTTagCompound):                Array[Int] = nbt.getIntArray(key)
  override def setNbt(a: Array[Int], nbt: NBTTagCompound): Unit       = nbt.setIntArray(key, a)

  override def get(holder: Holder):                Array[Int] = super.get(holder)
  override def set(a: Array[Int], holder: Holder): Unit       = super.set(a, holder)
}
object IntArrayNBTProperty {
  def ofStack(key: String, default: () => Array[Int]): IntArrayNBTProperty[ItemStack] =
    IntArrayNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: jfunc.Supplier[Array[Int]]): IntArrayNBTProperty[ItemStack] =
    IntArrayNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String): IntArrayNBTProperty[ItemStack] = IntArrayNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: () => Array[Int]): IntArrayNBTProperty[NBTTagCompound] =
    IntArrayNBTProperty(key, identity, default)
  def ofNbt(key: String, default: jfunc.Supplier[Array[Int]]): IntArrayNBTProperty[NBTTagCompound] =
    IntArrayNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String): IntArrayNBTProperty[NBTTagCompound] = IntArrayNBTProperty(key, identity)
}

case class CompoundNBTProperty[Holder](
    key: String,
    holderToNbt: Holder => NBTTagCompound,
    default: () => NBTTagCompound = () => new NBTTagCompound
) extends PrimitiveNBTProperty[NBTTagCompound, Holder] {
  override def tpe:                                            Int            = Constants.NBT.TAG_COMPOUND
  override def getNbt(nbt: NBTTagCompound):                    NBTTagCompound = nbt.getCompoundTag(key)
  override def setNbt(a: NBTTagCompound, nbt: NBTTagCompound): Unit           = nbt.setTag(key, a)

  override def get(holder: Holder):                    NBTTagCompound = super.get(holder)
  override def set(a: NBTTagCompound, holder: Holder): Unit           = super.set(a, holder)
  override def modify[B](
      f: jfunc.Function[NBTTagCompound, B],
      fInverse: jfunc.Function[B, NBTTagCompound]
  ): NBTProperty[B, Holder] = super.modify(f, fInverse)
}
object CompoundNBTProperty {
  def ofStack(key: String, listTpe: Int, default: () => NBTTagCompound): CompoundNBTProperty[ItemStack] =
    CompoundNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, listTpe: Int, default: jfunc.Supplier[NBTTagCompound]): CompoundNBTProperty[ItemStack] =
    CompoundNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String, listTpe: Int): CompoundNBTProperty[ItemStack] =
    CompoundNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, listTpe: Int, default: () => NBTTagCompound): CompoundNBTProperty[NBTTagCompound] =
    CompoundNBTProperty(key, identity, default)
  def ofNbt(key: String, listTpe: Int, default: jfunc.Supplier[NBTTagCompound]): CompoundNBTProperty[NBTTagCompound] =
    CompoundNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String, listTpe: Int): CompoundNBTProperty[NBTTagCompound] = CompoundNBTProperty(key, identity)
}

case class ListNBTProperty[Holder](
    key: String,
    holderToNbt: Holder => NBTTagCompound,
    listTpe: Int,
    default: () => NBTTagList = () => new NBTTagList
) extends PrimitiveNBTProperty[NBTTagList, Holder] {
  override def tpe:                                        Int        = Constants.NBT.TAG_LIST
  override def getNbt(nbt: NBTTagCompound):                NBTTagList = nbt.getTagList(key, listTpe)
  override def setNbt(a: NBTTagList, nbt: NBTTagCompound): Unit       = nbt.setTag(key, a)

  override def get(holder: Holder):                NBTTagList = super.get(holder)
  override def set(a: NBTTagList, holder: Holder): Unit       = super.set(a, holder)
  override def modify[B](
      f: jfunc.Function[NBTTagList, B],
      fInverse: jfunc.Function[B, NBTTagList]
  ): NBTProperty[B, Holder] =
    super.modify(f, fInverse)
}
object ListNBTProperty {
  def ofStack(key: String, listTpe: Int, default: () => NBTTagList): ListNBTProperty[ItemStack] =
    ListNBTProperty(key, NBTProperty.ItemStackToNbt, listTpe, default)
  def ofStack(key: String, listTpe: Int, default: jfunc.Supplier[NBTTagList]): ListNBTProperty[ItemStack] =
    ListNBTProperty(key, NBTProperty.ItemStackToNbt, listTpe, default.asScala)
  def ofStack(key: String, listTpe: Int): ListNBTProperty[ItemStack] =
    ListNBTProperty(key, NBTProperty.ItemStackToNbt, listTpe)

  def ofNbt(key: String, listTpe: Int, default: () => NBTTagList): ListNBTProperty[NBTTagCompound] =
    ListNBTProperty(key, identity, listTpe, default)
  def ofNbt(key: String, listTpe: Int, default: jfunc.Supplier[NBTTagList]): ListNBTProperty[NBTTagCompound] =
    ListNBTProperty(key, identity, listTpe, default.asScala)
  def ofNbt(key: String, listTpe: Int): ListNBTProperty[NBTTagCompound] = ListNBTProperty(key, identity, listTpe)
}

case class ModifiedNBTProperty[A, B, Holder](underlying: NBTProperty[A, Holder], f: A => B, fInverse: B => A)
    extends NBTProperty[B, Holder] {
  override def isDefined(nbt: NBTTagCompound):    Boolean                  = underlying.isDefined(nbt)
  override def default:                           () => B                  = () => f(underlying.default())
  override def holderToNbt:                       Holder => NBTTagCompound = underlying.holderToNbt
  override def getNbt(nbt: NBTTagCompound):       B                        = f(underlying.getNbt(nbt))
  override def setNbt(a: B, nbt: NBTTagCompound): Unit                     = underlying.setNbt(fInverse(a), nbt)

  override def get(holder: Holder):       B    = super.get(holder)
  override def set(a: B, holder: Holder): Unit = super.set(a, holder)
  override def modify[C](f: jfunc.Function[B, C], fInverse: jfunc.Function[C, B]): NBTProperty[C, Holder] =
    super.modify(f, fInverse)
}

case class ComposedNBTProperty[A, B, Holder](first: NBTProperty[A, Holder], second: NBTProperty[B, Holder])
    extends NBTProperty[(A, B), Holder] {
  override def isDefined(nbt: NBTTagCompound): Boolean                  = first.isDefined(nbt) && second.isDefined(nbt)
  override def default:                        () => (A, B)             = () => (first.default(), second.default())
  override def holderToNbt:                    Holder => NBTTagCompound = first.holderToNbt
  override def getNbt(nbt: NBTTagCompound):    (A, B)                   = (first.getNbt(nbt), second.getNbt(nbt))
  override def setNbt(a: (A, B), nbt: NBTTagCompound): Unit = {
    first.setNbt(a._1, nbt)
    second.setNbt(a._2, nbt)
  }

  override def get(holder: Holder):            (A, B) = super.get(holder)
  override def set(a: (A, B), holder: Holder): Unit   = super.set(a, holder)
  override def modify[C](f: jfunc.Function[(A, B), C], fInverse: jfunc.Function[C, (A, B)]): NBTProperty[C, Holder] =
    super.modify(f, fInverse)
}
