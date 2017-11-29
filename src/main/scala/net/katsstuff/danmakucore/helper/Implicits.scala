/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper

import java.util.Optional
import java.util.function.Supplier

object Implicits {

  implicit class RickOptional[A](val optional: Optional[A]) extends AnyVal {

    def toOption: Option[A] = if (optional.isPresent) Some(optional.get()) else None
  }

  implicit class RickOption[A](val option: Option[A]) extends AnyVal {

    def toOptional: Optional[A] = option.fold(Optional.empty[A])(Optional.of)
  }

  implicit class RichSupplier[A](val supplier: Supplier[A]) extends AnyVal {
    def asScala: () => A = () => supplier.get()
  }
}