package net.katsstuff.danmakucore.helper

import java.util.function.Supplier

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
  def key:         String
  def default:     () => A
  def tpe:         Int
  def holderToNbt: Holder => NBTTagCompound

  /**
    * Get value represented by this property, or the
    * default if it's not present.
    */
  def get(holder: Holder): A = {
    val nbt = holderToNbt(holder)
    if (nbt.hasKey(key, tpe)) getNbt(nbt)
    else default()
  }

  /**
    * Set the value represented by this property.
    */
  def set(a: A, holder: Holder): Unit = setNbt(a, holderToNbt(holder))

  protected def getNbt(holder: NBTTagCompound): A

  protected def setNbt(a: A, holder: NBTTagCompound): Unit
}
object NBTProperty {
  val ItemStackToNbt: ItemStack => NBTTagCompound = ItemNBTHelper.getNBT
}

case class BooleanNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Boolean = () => false)
    extends NBTProperty[Boolean, Holder] {
  override def tpe:                                                  Int     = Constants.NBT.TAG_BYTE
  protected override def getNbt(holder: NBTTagCompound):             Boolean = holder.getBoolean(key)
  protected override def setNbt(a: Boolean, holder: NBTTagCompound): Unit    = holder.setBoolean(key, a)

  override def get(holder: Holder):             Boolean = super.get(holder)
  override def set(a: Boolean, holder: Holder): Unit    = super.set(a, holder)
}
object BooleanNBTProperty {
  def ofStack(key: String, default: Boolean):       BooleanNBTProperty[ItemStack] = BooleanNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Boolean): BooleanNBTProperty[ItemStack] = BooleanNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: Supplier[Boolean]): BooleanNBTProperty[ItemStack] =
    BooleanNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String): BooleanNBTProperty[ItemStack] = BooleanNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Boolean):           BooleanNBTProperty[NBTTagCompound] = BooleanNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Boolean):     BooleanNBTProperty[NBTTagCompound] = BooleanNBTProperty(key, identity, default)
  def ofNbt(key: String, default: Supplier[Boolean]): BooleanNBTProperty[NBTTagCompound] = BooleanNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String):                             BooleanNBTProperty[NBTTagCompound] = BooleanNBTProperty(key, identity)
}

case class ByteNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Byte = () => 0)
    extends NBTProperty[Byte, Holder] {
  override def tpe:                                               Int  = Constants.NBT.TAG_BYTE
  protected override def getNbt(holder: NBTTagCompound):          Byte = holder.getByte(key)
  protected override def setNbt(a: Byte, holder: NBTTagCompound): Unit = holder.setByte(key, a)

  override def get(holder: Holder):          Byte = super.get(holder)
  override def set(a: Byte, holder: Holder): Unit = super.set(a, holder)
}
object ByteNBTProperty {
  def ofStack(key: String, default: Byte):           ByteNBTProperty[ItemStack] = ByteNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Byte):     ByteNBTProperty[ItemStack] = ByteNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: Supplier[Byte]): ByteNBTProperty[ItemStack] = ByteNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String):                          ByteNBTProperty[ItemStack] = ByteNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Byte):           ByteNBTProperty[NBTTagCompound] = ByteNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Byte):     ByteNBTProperty[NBTTagCompound] = ByteNBTProperty(key, identity, default)
  def ofNbt(key: String, default: Supplier[Byte]): ByteNBTProperty[NBTTagCompound] = ByteNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String):                          ByteNBTProperty[NBTTagCompound] = ByteNBTProperty(key, identity)
}

case class ShortNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Short = () => 0)
    extends NBTProperty[Short, Holder] {
  override def tpe:                                                Int   = Constants.NBT.TAG_SHORT
  protected override def getNbt(holder: NBTTagCompound):           Short = holder.getShort(key)
  protected override def setNbt(a: Short, holder: NBTTagCompound): Unit  = holder.setShort(key, a)

  override def get(holder: Holder):           Short = super.get(holder)
  override def set(a: Short, holder: Holder): Unit  = super.set(a, holder)
}
object ShortNBTProperty {
  def ofStack(key: String, default: Short):           ShortNBTProperty[ItemStack] = ShortNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Short):     ShortNBTProperty[ItemStack] = ShortNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: Supplier[Short]): ShortNBTProperty[ItemStack] = ShortNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String):                           ShortNBTProperty[ItemStack] = ShortNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Short):           ShortNBTProperty[NBTTagCompound] = ShortNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Short):     ShortNBTProperty[NBTTagCompound] = ShortNBTProperty(key, identity, default)
  def ofNbt(key: String, default: Supplier[Short]): ShortNBTProperty[NBTTagCompound] = ShortNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String):                           ShortNBTProperty[NBTTagCompound] = ShortNBTProperty(key, identity)
}

case class IntNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Int = () => 0) extends NBTProperty[Int, Holder] {
  override def tpe:                                              Int  = Constants.NBT.TAG_INT
  protected override def getNbt(holder: NBTTagCompound):         Int  = holder.getInteger(key)
  protected override def setNbt(a: Int, holder: NBTTagCompound): Unit = holder.setInteger(key, a)

  override def get(holder: Holder):         Int  = super.get(holder)
  override def set(a: Int, holder: Holder): Unit = super.set(a, holder)
}
object IntNBTProperty {
  def ofStack(key: String, default: Int):           IntNBTProperty[ItemStack] = IntNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Int):     IntNBTProperty[ItemStack] = IntNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: Supplier[Int]): IntNBTProperty[ItemStack] = IntNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String):                         IntNBTProperty[ItemStack] = IntNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Int):           IntNBTProperty[NBTTagCompound] = IntNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Int):     IntNBTProperty[NBTTagCompound] = IntNBTProperty(key, identity, default)
  def ofNbt(key: String, default: Supplier[Int]): IntNBTProperty[NBTTagCompound] = IntNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String):                         IntNBTProperty[NBTTagCompound] = IntNBTProperty(key, identity)
}

case class LongNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Long = () => 0)
    extends NBTProperty[Long, Holder] {
  override def tpe:                                               Int  = Constants.NBT.TAG_LONG
  protected override def getNbt(holder: NBTTagCompound):          Long = holder.getLong(key)
  protected override def setNbt(a: Long, holder: NBTTagCompound): Unit = holder.setLong(key, a)

  override def get(holder: Holder):          Long = super.get(holder)
  override def set(a: Long, holder: Holder): Unit = super.set(a, holder)
}
object LongNBTProperty {
  def ofStack(key: String, default: Long):           LongNBTProperty[ItemStack] = LongNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Long):     LongNBTProperty[ItemStack] = LongNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: Supplier[Long]): LongNBTProperty[ItemStack] = LongNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String):                          LongNBTProperty[ItemStack] = LongNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Long):           LongNBTProperty[NBTTagCompound] = LongNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Long):     LongNBTProperty[NBTTagCompound] = LongNBTProperty(key, identity, default)
  def ofNbt(key: String, default: Supplier[Long]): LongNBTProperty[NBTTagCompound] = LongNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String):                          LongNBTProperty[NBTTagCompound] = LongNBTProperty(key, identity)
}

case class FloatNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Float = () => 0)
    extends NBTProperty[Float, Holder] {
  override def tpe:                                                Int   = Constants.NBT.TAG_FLOAT
  protected override def getNbt(holder: NBTTagCompound):           Float = holder.getFloat(key)
  protected override def setNbt(a: Float, holder: NBTTagCompound): Unit  = holder.setFloat(key, a)

  override def get(holder: Holder):           Float = super.get(holder)
  override def set(a: Float, holder: Holder): Unit  = super.set(a, holder)
}
object FloatNBTProperty {
  def ofStack(key: String, default: Float):           FloatNBTProperty[ItemStack] = FloatNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Float):     FloatNBTProperty[ItemStack] = FloatNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: Supplier[Float]): FloatNBTProperty[ItemStack] = FloatNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String):                           FloatNBTProperty[ItemStack] = FloatNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Float):           FloatNBTProperty[NBTTagCompound] = FloatNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Float):     FloatNBTProperty[NBTTagCompound] = FloatNBTProperty(key, identity, default)
  def ofNbt(key: String, default: Supplier[Float]): FloatNBTProperty[NBTTagCompound] = FloatNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String):                           FloatNBTProperty[NBTTagCompound] = FloatNBTProperty(key, identity)
}

case class DoubleNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Double = () => 0)
    extends NBTProperty[Double, Holder] {
  override def tpe:                                                 Int    = Constants.NBT.TAG_DOUBLE
  protected override def getNbt(holder: NBTTagCompound):            Double = holder.getDouble(key)
  protected override def setNbt(a: Double, holder: NBTTagCompound): Unit   = holder.setDouble(key, a)

  override def get(holder: Holder):            Double = super.get(holder)
  override def set(a: Double, holder: Holder): Unit   = super.set(a, holder)
}
object DoubleNBTProperty {
  def ofStack(key: String, default: Double):       DoubleNBTProperty[ItemStack] = DoubleNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => Double): DoubleNBTProperty[ItemStack] = DoubleNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: Supplier[Double]): DoubleNBTProperty[ItemStack] =
    DoubleNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String): DoubleNBTProperty[ItemStack] = DoubleNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: Double):           DoubleNBTProperty[NBTTagCompound] = DoubleNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => Double):     DoubleNBTProperty[NBTTagCompound] = DoubleNBTProperty(key, identity, default)
  def ofNbt(key: String, default: Supplier[Double]): DoubleNBTProperty[NBTTagCompound] = DoubleNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String):                            DoubleNBTProperty[NBTTagCompound] = DoubleNBTProperty(key, identity)
}

case class StringNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => String = () => "")
    extends NBTProperty[String, Holder] {
  override def tpe:                                                 Int    = Constants.NBT.TAG_STRING
  protected override def getNbt(holder: NBTTagCompound):            String = holder.getString(key)
  protected override def setNbt(a: String, holder: NBTTagCompound): Unit   = holder.setString(key, a)

  override def get(holder: Holder):            String = super.get(holder)
  override def set(a: String, holder: Holder): Unit   = super.set(a, holder)
}
object StringNBTProperty {
  def ofStack(key: String, default: String):       StringNBTProperty[ItemStack] = StringNBTProperty(key, NBTProperty.ItemStackToNbt, () => default)
  def ofStack(key: String, default: () => String): StringNBTProperty[ItemStack] = StringNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: Supplier[String]): StringNBTProperty[ItemStack] =
    StringNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String): StringNBTProperty[ItemStack] = StringNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: String):           StringNBTProperty[NBTTagCompound] = StringNBTProperty(key, identity, () => default)
  def ofNbt(key: String, default: () => String):     StringNBTProperty[NBTTagCompound] = StringNBTProperty(key, identity, default)
  def ofNbt(key: String, default: Supplier[String]): StringNBTProperty[NBTTagCompound] = StringNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String):                            StringNBTProperty[NBTTagCompound] = StringNBTProperty(key, identity)
}

case class ByteArrayNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Array[Byte] = () => Array.empty)
    extends NBTProperty[Array[Byte], Holder] {
  override def tpe:                                                      Int         = Constants.NBT.TAG_DOUBLE
  protected override def getNbt(holder: NBTTagCompound):                 Array[Byte] = holder.getByteArray(key)
  protected override def setNbt(a: Array[Byte], holder: NBTTagCompound): Unit        = holder.setByteArray(key, a)

  override def get(holder: Holder):                 Array[Byte] = super.get(holder)
  override def set(a: Array[Byte], holder: Holder): Unit        = super.set(a, holder)
}
object ByteArrayNBTProperty {
  def ofStack(key: String, default: () => Array[Byte]): ByteArrayNBTProperty[ItemStack] =
    ByteArrayNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: Supplier[Array[Byte]]): ByteArrayNBTProperty[ItemStack] =
    ByteArrayNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String): ByteArrayNBTProperty[ItemStack] = ByteArrayNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: () => Array[Byte]):     ByteArrayNBTProperty[NBTTagCompound] = ByteArrayNBTProperty(key, identity, default)
  def ofNbt(key: String, default: Supplier[Array[Byte]]): ByteArrayNBTProperty[NBTTagCompound] = ByteArrayNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String):                                 ByteArrayNBTProperty[NBTTagCompound] = ByteArrayNBTProperty(key, identity)
}

case class IntArrayNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => Array[Int] = () => Array.empty)
    extends NBTProperty[Array[Int], Holder] {
  override def tpe:                                                     Int        = Constants.NBT.TAG_DOUBLE
  protected override def getNbt(holder: NBTTagCompound):                Array[Int] = holder.getIntArray(key)
  protected override def setNbt(a: Array[Int], holder: NBTTagCompound): Unit       = holder.setIntArray(key, a)

  override def get(holder: Holder):                Array[Int] = super.get(holder)
  override def set(a: Array[Int], holder: Holder): Unit       = super.set(a, holder)
}
object IntArrayNBTProperty {
  def ofStack(key: String, default: () => Array[Int]): IntArrayNBTProperty[ItemStack] = IntArrayNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, default: Supplier[Array[Int]]): IntArrayNBTProperty[ItemStack] =
    IntArrayNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String): IntArrayNBTProperty[ItemStack] = IntArrayNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, default: () => Array[Int]):     IntArrayNBTProperty[NBTTagCompound] = IntArrayNBTProperty(key, identity, default)
  def ofNbt(key: String, default: Supplier[Array[Int]]): IntArrayNBTProperty[NBTTagCompound] = IntArrayNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String):                                IntArrayNBTProperty[NBTTagCompound] = IntArrayNBTProperty(key, identity)
}

case class CompoundNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, default: () => NBTTagCompound = () => new NBTTagCompound)
    extends NBTProperty[NBTTagCompound, Holder] {
  override def tpe:                                                         Int            = Constants.NBT.TAG_COMPOUND
  protected override def getNbt(holder: NBTTagCompound):                    NBTTagCompound = holder.getCompoundTag(key)
  protected override def setNbt(a: NBTTagCompound, holder: NBTTagCompound): Unit           = holder.setTag(key, a)

  override def get(holder: Holder):                    NBTTagCompound = super.get(holder)
  override def set(a: NBTTagCompound, holder: Holder): Unit           = super.set(a, holder)
}
object CompoundNBTProperty {
  def ofStack(key: String, listTpe: Int, default: () => NBTTagCompound): CompoundNBTProperty[ItemStack] =
    CompoundNBTProperty(key, NBTProperty.ItemStackToNbt, default)
  def ofStack(key: String, listTpe: Int, default: Supplier[NBTTagCompound]): CompoundNBTProperty[ItemStack] =
    CompoundNBTProperty(key, NBTProperty.ItemStackToNbt, default.asScala)
  def ofStack(key: String, listTpe: Int): CompoundNBTProperty[ItemStack] = CompoundNBTProperty(key, NBTProperty.ItemStackToNbt)

  def ofNbt(key: String, listTpe: Int, default: () => NBTTagCompound): CompoundNBTProperty[NBTTagCompound] =
    CompoundNBTProperty(key, identity, default)
  def ofNbt(key: String, listTpe: Int, default: Supplier[NBTTagCompound]): CompoundNBTProperty[NBTTagCompound] =
    CompoundNBTProperty(key, identity, default.asScala)
  def ofNbt(key: String, listTpe: Int): CompoundNBTProperty[NBTTagCompound] = CompoundNBTProperty(key, identity)
}

case class ListNBTProperty[Holder](key: String, holderToNbt: Holder => NBTTagCompound, listTpe: Int, default: () => NBTTagList = () => new NBTTagList)
    extends NBTProperty[NBTTagList, Holder] {
  override def tpe:                                                     Int        = Constants.NBT.TAG_LIST
  protected override def getNbt(holder: NBTTagCompound):                NBTTagList = holder.getTagList(key, listTpe)
  protected override def setNbt(a: NBTTagList, holder: NBTTagCompound): Unit       = holder.setTag(key, a)

  override def get(holder: Holder):                NBTTagList = super.get(holder)
  override def set(a: NBTTagList, holder: Holder): Unit       = super.set(a, holder)
}
object ListNBTProperty {
  def ofStack(key: String, listTpe: Int, default: () => NBTTagList): ListNBTProperty[ItemStack] =
    ListNBTProperty(key, NBTProperty.ItemStackToNbt, listTpe, default)
  def ofStack(key: String, listTpe: Int, default: Supplier[NBTTagList]): ListNBTProperty[ItemStack] =
    ListNBTProperty(key, NBTProperty.ItemStackToNbt, listTpe, default.asScala)
  def ofStack(key: String, listTpe: Int): ListNBTProperty[ItemStack] = ListNBTProperty(key, NBTProperty.ItemStackToNbt, listTpe)

  def ofNbt(key: String, listTpe: Int, default: () => NBTTagList): ListNBTProperty[NBTTagCompound] = ListNBTProperty(key, identity, listTpe, default)
  def ofNbt(key: String, listTpe: Int, default: Supplier[NBTTagList]): ListNBTProperty[NBTTagCompound] =
    ListNBTProperty(key, identity, listTpe, default.asScala)
  def ofNbt(key: String, listTpe: Int): ListNBTProperty[NBTTagCompound] = ListNBTProperty(key, identity, listTpe)
}
