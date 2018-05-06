/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.javastuff

import java.util
import java.util.Optional

import scala.collection.JavaConverters._
import javax.annotation.Nullable

object DanCoreJavaHelper {

  def option[A](@Nullable obj: A): Option[A] = Option(obj)
  def some[A](obj: A):             Some[A]   = Some(obj)
  def none[A]:                     Option[A] = None

  def optional[A](option: Option[A]): Optional[A] = option.fold[Optional[A]](Optional.empty())(Optional.of)

  def seq[A](list: util.List[A]): Seq[A] = list.asScala
}
