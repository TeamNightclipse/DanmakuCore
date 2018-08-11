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

import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.SpellcardDummy;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibModJ;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibSpellcardName;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibModJ.ID)
public final class LibSpellcards {

	@ObjectHolder(LibSpellcardName.DELUSION_OF_ENLIGHTENMENT)
	public static final Spellcard DELUSION_OF_ENLIGHTENMENT = SpellcardDummy.instance();
}
