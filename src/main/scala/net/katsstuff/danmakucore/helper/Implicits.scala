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