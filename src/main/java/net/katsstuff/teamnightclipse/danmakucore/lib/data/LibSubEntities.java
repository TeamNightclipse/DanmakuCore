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
package net.katsstuff.teamnightclipse.danmakucore.lib.data;

import net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.SubEntityType;
import net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.SubEntityTypeDummy;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibModJ;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibSubEntityName;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibModJ.ID)
public final class LibSubEntities {

	@ObjectHolder(LibSubEntityName.DEFAULT)
	public static final SubEntityType DEFAULT_TYPE = SubEntityTypeDummy.instance();
	@ObjectHolder(LibSubEntityName.FIRE)
	public static final SubEntityType FIRE = SubEntityTypeDummy.instance();
	@ObjectHolder(LibSubEntityName.EXPLODE)
	public static final SubEntityType EXPLOSION = SubEntityTypeDummy.instance();
	@ObjectHolder(LibSubEntityName.TELEPORT)
	public static final SubEntityType TELEPORT = SubEntityTypeDummy.instance();
	@ObjectHolder(LibSubEntityName.DANMAKU_EXPLODE)
	public static final SubEntityType DANMAKU_EXPLODE = SubEntityTypeDummy.instance();
	@ObjectHolder(LibSubEntityName.RAINBOW)
	public static final SubEntityType RAINBOW = SubEntityTypeDummy.instance();
	@ObjectHolder(LibSubEntityName.SHIFTING_RAINBOW)
	public static final SubEntityType SHIFTING_RAINBOW = SubEntityTypeDummy.instance();
}
