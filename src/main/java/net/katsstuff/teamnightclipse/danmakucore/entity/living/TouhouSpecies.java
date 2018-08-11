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
package net.katsstuff.teamnightclipse.danmakucore.entity.living;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import net.katsstuff.teamnightclipse.danmakucore.misc.Translatable;

/**
 * All the different species. Naming should be done like this TYPE_SUPERSPECIES_SPECIES TYPE refers
 * to one of the top species, which has no SUPERSPECIES or SPECIES defined. SUPERSPECIES refers to
 * what you but as the superspecies. SPECIES is what you are actually referring to. This is no avoid
 * different species colliding.
 */
@SuppressWarnings("unused")
public class TouhouSpecies implements Translatable {

	private static final Map<String, TouhouSpecies> byName = new HashMap<>();

	public static final TouhouSpecies HUMAN = new TouhouSpecies("HUMAN", null);
	public static final TouhouSpecies ANIMAL = new TouhouSpecies("ANIMAL", null);
	public static final TouhouSpecies GOD = new TouhouSpecies("GOD", null);
	public static final TouhouSpecies SHIKIGAMI = new TouhouSpecies("SHIKIGAMI", null);
	public static final TouhouSpecies YOUKAI = new TouhouSpecies("YOUKAI", null);
	public static final TouhouSpecies FAIRY = new TouhouSpecies("FAIRY", null);
	public static final TouhouSpecies PHANTOM = new TouhouSpecies("PHANTOM", null);
	public static final TouhouSpecies POLTERGEIST = new TouhouSpecies("POLTERGEIST", null);
	public static final TouhouSpecies SPIRIT = new TouhouSpecies("SPIRIT", null);
	public static final TouhouSpecies OTHERS = new TouhouSpecies("OTHERS", null);

	//Human
	public static final TouhouSpecies HUMAN_HALF = new TouhouSpecies("HUMAN_HALF", HUMAN);
	public static final TouhouSpecies HUMAN_LUNARIAN = new TouhouSpecies("HUMAN_LUNARIAN", HUMAN);
	public static final TouhouSpecies HUMAN_HERMIT = new TouhouSpecies("HUMAN_HERMIT", HUMAN);
	public static final TouhouSpecies HUMAN_SAINT = new TouhouSpecies("HUMAN_SAINT", HUMAN);
	public static final TouhouSpecies HUMAN_INCHLINGS = new TouhouSpecies("HUMAN_INCHLINGS", HUMAN);
	public static final TouhouSpecies HUMAN_IMMORTAL = new TouhouSpecies("HUMAN_IMMORTAL", HUMAN);

	//Animal
	public static final TouhouSpecies ANIMAL_RABBIT = new TouhouSpecies("ANIMAL_RABBIT", ANIMAL);
	public static final TouhouSpecies ANIMAL_RABBIT_MOON = new TouhouSpecies("ANIMAL_RABBIT_MOON", ANIMAL_RABBIT);
	public static final TouhouSpecies ANIMAL_RAVEN = new TouhouSpecies("ANIMAL_RAVEN", ANIMAL);
	public static final TouhouSpecies ANIMAL_RAVEN_HELL = new TouhouSpecies("ANIMAL_RAVEN_HELL", ANIMAL_RAVEN);

	//Hermit
	public static final TouhouSpecies HUMAN_HERMIT_CELESTIAL = new TouhouSpecies("HUMAN_HERMIT_CELESTIAL", HUMAN_HERMIT);
	public static final TouhouSpecies HUMAN_HERMIT_SHIKAISEN = new TouhouSpecies("HUMAN_HERMIT_SHIKAISEN", HUMAN_HERMIT);

	// God
	public static final TouhouSpecies GOD_ARAHITOGAMI = new TouhouSpecies("GOD_ARAHITOGAMI", GOD);
	public static final TouhouSpecies GOD_YATAGARASU = new TouhouSpecies("GOD_YATAGARASU", GOD);
	public static final TouhouSpecies GOD_YAMA = new TouhouSpecies("GOD_YAMA", GOD);
	public static final TouhouSpecies GOD_DRAGON = new TouhouSpecies("GOD_DRAGON", GOD);
	public static final TouhouSpecies GOD_SHINIGAMI = new TouhouSpecies("GOD_SHINIGAMI", GOD);

	// Shikigami
	public static final TouhouSpecies SHIKIGAMI_DOLL = new TouhouSpecies("SHIKIGAMI_DOLL", SHIKIGAMI);
	public static final TouhouSpecies SHIKIGAMI_FAMILIAR = new TouhouSpecies("SHIKIGAMI_FAMILIAR", SHIKIGAMI);

	// Fairy
	public static final TouhouSpecies FAIRY_ICE = new TouhouSpecies("FAIRY_ICE", FAIRY);
	public static final TouhouSpecies FAIRY_HELL = new TouhouSpecies("FAIRY_HELL", FAIRY);

	//Phantom
	public static final TouhouSpecies PHANTOM_HALF = new TouhouSpecies("PHANTOM_HALF", PHANTOM);
	public static final TouhouSpecies PHANTOM_GHOST = new TouhouSpecies("PHANTOM_GHOST", PHANTOM);
	public static final TouhouSpecies PHANTOM_SHIP = new TouhouSpecies("PHANTOM_SHIP", PHANTOM);

	//Spirit
	public static final TouhouSpecies SPIRIT_DIVINE = new TouhouSpecies("SPIRIT_DIVINE", SPIRIT);
	public static final TouhouSpecies SPIRIT_EVIL = new TouhouSpecies("SPIRIT_EVIL", SPIRIT);

	// Youkai
	public static final TouhouSpecies YOUKAI_HALF = new TouhouSpecies("YOUKAI_HALF", YOUKAI);
	public static final TouhouSpecies YOUKAI_MAGICIAN = new TouhouSpecies("YOUKAI_MAGICIAN", YOUKAI);
	public static final TouhouSpecies YOUKAI_BEAST = new TouhouSpecies("YOUKAI_BEAST", YOUKAI);
	public static final TouhouSpecies YOUKAI_DEVIL = new TouhouSpecies("YOUKAI_DEVIL", YOUKAI);
	public static final TouhouSpecies YOUKAI_JIANGSHI = new TouhouSpecies("YOUKAI_JIANGSHI", YOUKAI);
	public static final TouhouSpecies YOUKAI_ONI = new TouhouSpecies("YOUKAI_ONI", YOUKAI);
	public static final TouhouSpecies YOUKAI_KAPPA = new TouhouSpecies("YOUKAI_KAPPA", YOUKAI);
	public static final TouhouSpecies YOUKAI_YUKIONNA = new TouhouSpecies("YOUKAI_YUKIONNA", YOUKAI);
	public static final TouhouSpecies YOUKAI_TSUKUMOGAMI = new TouhouSpecies("YOUKAI_TSUKUMOGAMI", YOUKAI);
	public static final TouhouSpecies YOUKAI_TSURUBEOTOSHI = new TouhouSpecies("YOUKAI_TSURUBEOTOSHI", YOUKAI);
	public static final TouhouSpecies YOUKAI_HASHIHIME = new TouhouSpecies("YOUKAI_HASHIHIME", YOUKAI);
	public static final TouhouSpecies YOUKAI_SATORI = new TouhouSpecies("YOUKAI_SATORI", YOUKAI);
	public static final TouhouSpecies YOUKAI_NYUUDOU = new TouhouSpecies("YOUKAI_NYUUDOU", YOUKAI); //Youkai?
	public static final TouhouSpecies YOUKAI_NUE = new TouhouSpecies("YOUKAI_NUE", YOUKAI);
	public static final TouhouSpecies YOUKAI_MERMAID = new TouhouSpecies("YOUKAI_MERMAID", YOUKAI);
	public static final TouhouSpecies YOUKAI_AMANOJAKU = new TouhouSpecies("YOUKAI_AMANOJAKU", YOUKAI);
	public static final TouhouSpecies YOUKAI_TSUCIGUMO = new TouhouSpecies("YOUKAI_TSUCIGUMO", YOUKAI); //Beast?
	public static final TouhouSpecies YOUKAI_KASHA = new TouhouSpecies("YOUKAI_KASHA", YOUKAI);
	public static final TouhouSpecies YOUKAI_YAMABIKO = new TouhouSpecies("YOUKAI_YAMABIKO", YOUKAI);
	public static final TouhouSpecies YOUKAI_RUKUROKUBI = new TouhouSpecies("YOUKAI_RUKUROKUBI", YOUKAI);
	public static final TouhouSpecies YOUKAI_BAKU = new TouhouSpecies("YOUKAI_BAKU", YOUKAI);

	//Youkai oni
	public static final TouhouSpecies YOUKAI_ONI_KISHIN = new TouhouSpecies("YOUKAI_ONI", YOUKAI_ONI);

	//Youkai devil
	public static final TouhouSpecies YOUKAI_DEVIL_VAMPIRE = new TouhouSpecies("YOUKAI_DEVIL", YOUKAI_DEVIL);

	//Youkai beast
	/**
	 * Please don't use this one alone. Instead use it together with one of the others here.
	 */
	public static final TouhouSpecies YOUKAI_BEAST_WERE = new TouhouSpecies("YOUKAI_BEAST_WERE", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_BUG = new TouhouSpecies("YOUKAI_BEAST_BUG", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_RABBIT = new TouhouSpecies("YOUKAI_BEAST_RABBIT", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_BIRD = new TouhouSpecies("YOUKAI_BEAST_BIRD", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_CAT = new TouhouSpecies("YOUKAI_BEAST_CAT", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_FOX = new TouhouSpecies("YOUKAI_BEAST_FOX", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_MOUSE = new TouhouSpecies("YOUKAI_BEAST_MOUSE", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_TIGER = new TouhouSpecies("YOUKAI_BEAST_TIGER", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_BAKEDANUKI = new TouhouSpecies("YOUKAI_BEAST_BAKEDANUKI", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_WOLF = new TouhouSpecies("YOUKAI_BEAST_WOLF", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_TENGU = new TouhouSpecies("YOUKAI_BEAST_TENGU", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BEAST_HAKUTAKU = new TouhouSpecies("YOUKAI_BEAST_HAKUTAKU", YOUKAI_BEAST);
	public static final TouhouSpecies YOUKAI_BIRD_YOSUZUME = new TouhouSpecies("YOUKAI_BEAST_YOSUZUME", YOUKAI_BEAST);

	//Tengu
	public static final TouhouSpecies YOUKAI_TENGU_CROW = new TouhouSpecies("YOUKAI_TENGU_CROW", YOUKAI_BEAST_TENGU);
	public static final TouhouSpecies YOUKAI_TENGU_WHITEWOLF = new TouhouSpecies("YOUKAI_TENGU_WHITEWOLF", YOUKAI_BEAST_TENGU);

	private final TouhouSpecies superSpecies;
	private final String name;

	private TouhouSpecies(String name, @Nullable TouhouSpecies superSpecies) {
		this.name = name.toUpperCase(Locale.ROOT);
		this.superSpecies = superSpecies;

		byName.put(this.name, this);
	}

	public static TouhouSpecies getOrCreate(String name, @Nullable TouhouSpecies superSpecies) {
		name = name.toUpperCase(Locale.ROOT);
		TouhouSpecies s = byName.get(name);
		if(s == null) {
			s = new TouhouSpecies(name, superSpecies);
		}

		return s;
	}

	public static Optional<TouhouSpecies> getByName(String name) {
		return Optional.ofNullable(byName.get(name.toUpperCase(Locale.ROOT)));
	}

	public Optional<TouhouSpecies> getSuperSpecies() {
		return Optional.ofNullable(superSpecies);
	}

	public boolean isSpecies(TouhouSpecies species) {
		TouhouSpecies testingSpecies = this;

		while(testingSpecies != null) {
			if(testingSpecies == species) return true;
			testingSpecies = testingSpecies.superSpecies;
		}

		return false;
	}

	@Override
	public String unlocalizedName() {
		return "touhouCharacter.name." + name;
	}
}