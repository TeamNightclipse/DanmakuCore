/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living;

import java.util.Optional;

import javax.annotation.Nullable;

/**
 * All the different species. Naming should be done like this TYPE_SUPERSPECIES_SPECIES TYPE refers
 * to one of the top species, which has no SUPERSPECIES or SPECIES defined. SUPERSPECIES refers to
 * what you but as the superspecies. SPECIES is what you are actually referring to. This is no avoid
 * different species colliding.
 */
@SuppressWarnings("unused")
public enum EnumSpecies {

	HUMAN(null),
	ANIMAL(null),
	GOD(null),
	SHIKIGAMI(null),
	YOUKAI(null),
	FAIRY(null),
	PHANTOM(null),
	POLTERGEIST(null),
	SPIRIT(null),
	OTHERS(null),

	//Human
	HUMAN_HALF(HUMAN),
	HUMAN_LUNARIAN(HUMAN),
	HUMAN_HERMIT(HUMAN),
	HUMAN_SAINT(HUMAN),
	HUMAN_INCHLINGS(HUMAN),
	HUMAN_IMMORTAL(HUMAN),

	//Animal
	ANIMAL_RABBIT(ANIMAL),
	ANIMAL_RABBIT_MOON(ANIMAL_RABBIT),
	ANIMAL_RAVEN(ANIMAL),
	ANIMAL_RAVEN_HELL(ANIMAL_RAVEN),

	//Hermit
	HUMAN_HERMIT_CELESTIAL(HUMAN_HERMIT),
	HUMAN_HERMIT_SHIKAISEN(HUMAN_HERMIT),

	// God
	GOD_ARAHITOGAMI(GOD),
	GOD_YATAGARASU(GOD),
	GOD_YAMA(GOD),
	GOD_DRAGON(GOD),
	GOD_SHINIGAMI(GOD),

	// Shikigami
	SHIKIGAMI_DOLL(SHIKIGAMI),
	SHIKIGAMI_FAMILIAR(SHIKIGAMI),

	// Fairy
	FAIRY_ICE(FAIRY),
	FAIRY_HELL(FAIRY),

	//Phantom
	PHANTOM_HALF(PHANTOM),
	PHANTOM_GHOST(PHANTOM),
	PHANTOM_SHIP(PHANTOM),

	//Spirit
	SPIRIT_DIVINE(SPIRIT),
	SPIRIT_EVIL(SPIRIT),

	// Youkai
	YOUKAI_HALF(YOUKAI),
	YOUKAI_MAGICIAN(YOUKAI),
	YOUKAI_BEAST(YOUKAI),
	YOUKAI_DEVIL(YOUKAI),
	YOUKAI_JIANGSHI(YOUKAI),
	YOUKAI_ONI(YOUKAI),
	YOUKAI_KAPPA(YOUKAI),
	YOUKAI_YUKIONNA(YOUKAI),
	YOUKAI_TSUKUMOGAMI(YOUKAI),
	YOUKAI_TSURUBEOTOSHI(YOUKAI),
	YOUKAI_HASHIHIME(YOUKAI),
	YOUKAI_SATORI(YOUKAI),
	YOUKAI_NYUUDOU(YOUKAI), //Youkai?
	YOUKAI_NUE(YOUKAI),
	YOUKAI_MERMAID(YOUKAI),
	YOUKAI_AMANOJAKU(YOUKAI),
	YOUKAI_TSUCIGUMO(YOUKAI), //Beast?
	YOUKAI_KASHA(YOUKAI),
	YOUKAI_YAMABIKO(YOUKAI),
	YOUKAI_RUKUROKUBI(YOUKAI),
	YOUKAI_BAKU(YOUKAI),

	//Youkai oni
	YOUKAI_ONI_KISHIN(YOUKAI_ONI),

	//Youkai devil
	YOUKAI_DEVIL_VAMPIRE(YOUKAI_DEVIL),

	//Youkai beast
	/**
	 * Please don't use this one alone. Instead use it together with one of the others here.
	 */
	YOUKAI_BEAST_WERE(YOUKAI_BEAST),
	YOUKAI_BEAST_BUG(YOUKAI_BEAST),
	YOUKAI_BEAST_RABBIT(YOUKAI_BEAST),
	YOUKAI_BEAST_BIRD(YOUKAI_BEAST),
	YOUKAI_BEAST_CAT(YOUKAI_BEAST),
	YOUKAI_BEAST_FOX(YOUKAI_BEAST),
	YOUKAI_BEAST_MOUSE(YOUKAI_BEAST),
	YOUKAI_BEAST_TIGER(YOUKAI_BEAST),
	YOUKAI_BEAST_BAKEDANUKI(YOUKAI_BEAST),
	YOUKAI_BEAST_WOLF(YOUKAI_BEAST),
	YOUKAI_BEAST_TENGU(YOUKAI_BEAST),
	YOUKAI_BEAST_HAKUTAKU(YOUKAI_BEAST),
	YOUKAI_BIRD_YOSUZUME(YOUKAI_BEAST),

	//Tengu
	YOUKAI_TENGU_CROW(YOUKAI_BEAST_TENGU),
	YOUKAI_TENGU_WHITEWOLF(YOUKAI_BEAST_TENGU);

	private final EnumSpecies superSpecies;

	EnumSpecies(@Nullable EnumSpecies superSpecies) {
		this.superSpecies = superSpecies;
	}

	public Optional<EnumSpecies> getSuperSpecies() {
		return Optional.ofNullable(superSpecies);
	}

	public boolean isSpecies(EnumSpecies species) {
		EnumSpecies testingSpecies = this;

		while(testingSpecies != null) {
			if(testingSpecies == species) return true;
			testingSpecies = testingSpecies.superSpecies;
		}

		return false;
	}
}