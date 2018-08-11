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
package net.katsstuff.teamnightclipse.danmakucore.javastuff

import java.util
import java.util.Optional

import scala.collection.JavaConverters._
import javax.annotation.Nullable

object DanCoreJavaHelper {

  def option[A](@Nullable obj: A): Option[A] = Option(obj)
  def some[A](obj: A): Some[A]               = Some(obj)
  def none[A]: Option[A]                     = None

  def optional[A](option: Option[A]): Optional[A] = option.fold[Optional[A]](Optional.empty())(Optional.of)

  def seq[A](list: util.List[A]): Seq[A] = list.asScala
}
