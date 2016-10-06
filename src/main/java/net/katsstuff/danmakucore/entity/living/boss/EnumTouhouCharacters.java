/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.boss;

import java.util.Arrays;

import net.katsstuff.danmakucore.entity.living.EnumSpecies;

/**
 * The different Touhou characters. Currently only used for localizing spellcards.
 */
@SuppressWarnings("unused")
public enum EnumTouhouCharacters {

	OLD("old", EnumSpecies.OTHERS),
	REIMU_HAKUREI("reimu", EnumSpecies.HUMAN),
	MARISA_KIRISAME("marisa", EnumSpecies.HUMAN),

	//Embodiment of Scarlet Devil
	RUMIA("rumia", EnumSpecies.YOUKAI),
	DAIYOUSEI("daiyousei", EnumSpecies.FAIRY),
	CIRNO("cirno", EnumSpecies.FAIRY_ICE),
	HONG_MEILING("meiling", EnumSpecies.YOUKAI),
	KOAKUMA("koakuma", EnumSpecies.YOUKAI_DEVIL),
	PATCHULI_KNOWLEDGE("patchuli", EnumSpecies.YOUKAI_MAGICIAN),
	SAKUYA_IZAYOI("sakuya", EnumSpecies.HUMAN),
	REMILIA_SCARLET("remilia", EnumSpecies.YOUKAI_DEVIL_VAMPIRE),
	FLANDRE_SCARLET("flandre", EnumSpecies.YOUKAI_DEVIL_VAMPIRE),
	SCARLET_SISTERS("scarletSisters", EnumSpecies.YOUKAI_DEVIL_VAMPIRE), //Should this be here?

	//Perfect Cherry Blossom
	LETTY_WHITEROCK("letty", EnumSpecies.YOUKAI_YUKIONNA),
	CHEN("chen", EnumSpecies.YOUKAI_BEAST_CAT, EnumSpecies.SHIKIGAMI_FAMILIAR),
	ALICE_MARGATROID("alice", EnumSpecies.YOUKAI_MAGICIAN),
	SHANGHAI_DOLL("shanghai", EnumSpecies.SHIKIGAMI_DOLL),
	HOURAI_DOLL("hourai", EnumSpecies.SHIKIGAMI_DOLL),
	LILY_WHITE("lily", EnumSpecies.FAIRY),
	LYRICA_PRIMSRIVER("lyrica", EnumSpecies.POLTERGEIST),
	LUNASA_PRIMSRIVER("lunasa", EnumSpecies.POLTERGEIST),
	MERLIN_PRIMSRIVER("merlin", EnumSpecies.POLTERGEIST),
	PRIMSRIVER_SISTERS("primsriver sisters", EnumSpecies.POLTERGEIST), //Should this be here?
	YOUMU_KONPAKU("youmu", EnumSpecies.HUMAN_HALF, EnumSpecies.PHANTOM_HALF),
	YUYUKO_SAIGYOJI("yuyuko", EnumSpecies.PHANTOM_GHOST),
	RAN_YAKUMO("ran", EnumSpecies.YOUKAI_BEAST_FOX, EnumSpecies.SHIKIGAMI_FAMILIAR),

	LAYLA_PRIMSRIVER("layla", EnumSpecies.HUMAN),
	YOUKI_KONPAKU("youki", EnumSpecies.HUMAN_HALF, EnumSpecies.PHANTOM_HALF),
	SAIGYOU_AYAKASHI("saigyouAyakashi", EnumSpecies.YOUKAI), //More specific here?

	//Immaterial and Missing Power
	SUIKA_IKBUKI("suika", EnumSpecies.YOUKAI_ONI),

	//Imperishable Night
	WRIGGLE_NIGHTBUG("wriggle", EnumSpecies.YOUKAI_BEAST_BUG),
	MYSTIA_LORELEI("mystia", EnumSpecies.YOUKAI_BIRD_YOSUZUME),
	KEINE_KAMISHIRASAWA("keine", EnumSpecies.YOUKAI_BEAST_WERE, EnumSpecies.YOUKAI_BEAST_HAKUTAKU),
	TEWI_INABA("tewi", EnumSpecies.YOUKAI_BEAST_RABBIT),
	REISEN_UDONGEIN_INABA("reisen", EnumSpecies.ANIMAL_RABBIT_MOON),
	EIRIN_YAGOKORO("eirin", EnumSpecies.HUMAN_LUNARIAN, EnumSpecies.GOD), //God?, Wiki lists her as a goddess
	KAGUYA_HOURAISAN("kaguya", EnumSpecies.HUMAN_LUNARIAN),
	FUJIWARA_NO_MOKOU("mokou", EnumSpecies.HUMAN_IMMORTAL),

	//Phantasmagoria of Flower View
	AYA_SHAMEIMARU("aya", EnumSpecies.YOUKAI_TENGU_CROW),
	MEDICINE_MELANCHOLY("medicine", EnumSpecies.YOUKAI), //Tsukumogami?
	YUUKA_KAZAMI("yuuka", EnumSpecies.YOUKAI),
	KOMACHI_ONOZUKA("komachi", EnumSpecies.GOD_SHINIGAMI),
	EIKU_SHIKI_YAMAXANADU("shikieiki", EnumSpecies.GOD_YAMA),

	//Mountain of Faith
	SHIZUHA_AKI("aki", EnumSpecies.GOD),
	MINORIKO_AKI("minoriko", EnumSpecies.GOD),
	AKI_SISTERS("akiSisters", EnumSpecies.GOD), //Should this be here?
	HINA_KAGIYAMA("hina", EnumSpecies.GOD),
	NITORI_KAWASHIRO("nitori", EnumSpecies.YOUKAI_KAPPA),
	MOMIJI_INUBASHIRI("momiji", EnumSpecies.YOUKAI_TENGU_WHITEWOLF),
	SANAE_KOCHIYA("sanae", EnumSpecies.HUMAN),
	KANAKO_YASAKA("kanako", EnumSpecies.GOD, EnumSpecies.SPIRIT_DIVINE), //Divine spirit? Wiki mentions her being one
	SUWAKO_MORIYA("suwako", EnumSpecies.GOD),
	TENMA("tenma", EnumSpecies.YOUKAI_BEAST_TENGU),
	MISHAGUJI("mishaguji", EnumSpecies.GOD), //Species?

	//Scarlet Weather Rhapsody
	IKU_NAGAE("iku", EnumSpecies.YOUKAI_BEAST), //More specific?
	TENSHI_HINANAWI("tenshi", EnumSpecies.HUMAN_HERMIT_CELESTIAL),

	//Subterranean Animism
	KISUME("kisume", EnumSpecies.YOUKAI_TSURUBEOTOSHI),
	YAMAME_KURODANI("yamame", EnumSpecies.YOUKAI_TSUCIGUMO),
	PARSEE_MIZUHASHI("parsee", EnumSpecies.YOUKAI_HASHIHIME),
	YUUGI_HOSHIGUMA("yuugi", EnumSpecies.YOUKAI_ONI),
	SATORI_KOMEIJI("satori", EnumSpecies.YOUKAI_SATORI),
	RIN_KAENBYOU_ORIN("orin", EnumSpecies.YOUKAI_KASHA),
	UTSUHO_REIUJI_OKUU("okuu", EnumSpecies.ANIMAL_RAVEN_HELL),
	KOISHI_KOMEIJI("koishi", EnumSpecies.YOUKAI_SATORI),
	KOMEIJI_SISTERS("komeijiSisters", EnumSpecies.YOUKAI_SATORI), //Should this be here?

	//Hisoutensoku
	GOLIATH_DOLL("goliathDoll", EnumSpecies.SHIKIGAMI_DOLL),
	GIANT_CATFISH("giantCatfish", EnumSpecies.ANIMAL),
	HISOUTENSOKU("hisoutensoku", EnumSpecies.OTHERS), //I am NOT making a floating balloon species

	//Double Spoiler
	HATATE_HIMAKAIDOU("hatate", EnumSpecies.YOUKAI_TENGU_CROW),

	//Great Fairy Wars
	LUNA_CHILD("lunaChild", EnumSpecies.FAIRY),
	STAR_SAPPHIRE("starSapphire", EnumSpecies.FAIRY),
	SUNNY_MILK("sunnyMilk", EnumSpecies.FAIRY),
	THREE_FAIRIES("threeFairies", EnumSpecies.FAIRY), //Should this be here?

	//Undefined Fantastic Object
	NAZRIN("nazrin", EnumSpecies.YOUKAI_BEAST_MOUSE),
	KOGASA_TATARA("kogasa", EnumSpecies.YOUKAI_TSUKUMOGAMI), //Specific species?
	ICHIRIN_KUMOI("ichirin", EnumSpecies.YOUKAI),
	UNZAN("unzan", EnumSpecies.YOUKAI_NYUUDOU),
	MINAMITSU_MURASA("murasa", EnumSpecies.PHANTOM_SHIP),
	SHOU_TORAMARU("shou", EnumSpecies.YOUKAI_BEAST_TIGER),
	BYAKUREN_HIJIRI("byakuren", EnumSpecies.YOUKAI_MAGICIAN),
	NUE_HOUJUU("nue", EnumSpecies.YOUKAI_NUE),
	BISHAMONTEN("bishamonten", EnumSpecies.GOD),
	MYOUREN_HIJIRI("myouren", EnumSpecies.HUMAN),

	//Ten Desires
	KYOUKO_KASADANI("kyouko", EnumSpecies.YOUKAI_YAMABIKO),
	YOSHIKA_MIYAKO("yoshika", EnumSpecies.YOUKAI_JIANGSHI),
	SEIGA_KAKU("seiga", EnumSpecies.HUMAN_HERMIT),
	SOGA_NO_TOJIKO("tojiko", EnumSpecies.PHANTOM_GHOST),
	MONONOBE_NO_FUTO("futo", EnumSpecies.HUMAN_HERMIT_SHIKAISEN),
	TOYOSATOMIMI_NO_MIKO("miko", EnumSpecies.HUMAN_SAINT, EnumSpecies.HUMAN_HERMIT),
	MAMIZOU_FUTATSUIWA("mamizou", EnumSpecies.YOUKAI_BEAST_BAKEDANUKI),

	//Hopeless Masquerade
	HATA_NO_KOKORO("kokoro", EnumSpecies.YOUKAI_TSUKUMOGAMI), //More specific?

	//Double Dealing Character
	WAKASAGIHIME("wakasagihime", EnumSpecies.YOUKAI_MERMAID),
	SEKIBANKI("sekibanki", EnumSpecies.YOUKAI_RUKUROKUBI),
	KAGEROU_IMAIZUMI("kagerou", EnumSpecies.YOUKAI_BEAST_WERE, EnumSpecies.YOUKAI_BEAST_WOLF),
	BENBEN_TSUKUMO("benben", EnumSpecies.YOUKAI_TSUKUMOGAMI),
	YATSUHASHI_TSUKUMO("yatsuhashi", EnumSpecies.YOUKAI_TSUKUMOGAMI),
	TSUKUMO_SISTERS("tsukumoSisters", EnumSpecies.YOUKAI_TSUKUMOGAMI), //Should this be here?
	SEIJA_KIJIN("seija", EnumSpecies.YOUKAI_AMANOJAKU),
	SHINMYOUMARU_SUKUNA("shinmyoumaru", EnumSpecies.HUMAN_INCHLINGS),
	RAIKO_HORIKAWA("raiko", EnumSpecies.YOUKAI_TSUKUMOGAMI),

	//Urban Legend in Limbo
	SUMIREKO_USAMI("sumireko", EnumSpecies.HUMAN), //Just human?

	//Legacy of Lunatic Kingdom
	SEIRAN("seiran", EnumSpecies.ANIMAL_RABBIT_MOON),
	RINGO("ringo", EnumSpecies.ANIMAL_RABBIT_MOON),
	DOROMY_SWEET("doromy", EnumSpecies.YOUKAI_BAKU),
	SAGUME_KISHIN("sagume", EnumSpecies.HUMAN_LUNARIAN, EnumSpecies.GOD),
	CLOWNPIECE("clownpiece", EnumSpecies.FAIRY_HELL),
	JUNKO("junko", EnumSpecies.SPIRIT_DIVINE),
	HECATIA_LAPISLAZULI("hecatia", EnumSpecies.GOD),

	//Curiosities of Lotus Asia
	RINNOSUKE_MORICHIKA("rinnosuke", EnumSpecies.HUMAN_HALF, EnumSpecies.YOUKAI_HALF),
	TOKIKO("tokiko", EnumSpecies.YOUKAI),

	//Bougetsushou, Lots of weird characters if anyone need them
	REISEN2("reisen2", EnumSpecies.ANIMAL_RABBIT_MOON),
	WATATSUKI_NO_TOYOHIME("toyohime", EnumSpecies.HUMAN_LUNARIAN),
	WATATSUKI_NO_YORIHIME("yorihime", EnumSpecies.HUMAN_LUNARIAN),
	CHANGE("change", EnumSpecies.GOD), //Hard to do special characters here, lunarian?
	LORD_TSUKUYOMI("tsukuyomi", EnumSpecies.HUMAN_LUNARIAN),
	MIZUE_NO_URANOSHIMAKO("uranoshimako", EnumSpecies.HUMAN),
	IWAKASA("iwakasa", EnumSpecies.HUMAN),
	KONOHANA_SAKUYAHIME("sakuyahime", EnumSpecies.GOD),
	IWANAGAHIME("iwanagahime", EnumSpecies.GOD),

	//Wild and Horned Hermit
	KASEN_IBARAKI("kasen", EnumSpecies.OTHERS),

	//Forbidden Scrollery
	KOSUZU_MOTOORI("kosuzu", EnumSpecies.HUMAN),

	//Music CDs
	MERIBEL_HEARN("maribel", EnumSpecies.HUMAN),
	RENKO_USAMI("renko", EnumSpecies.HUMAN),
	HIEDA_NO_AKYUU("akyuu", EnumSpecies.HUMAN),

	/**
	 * Defines other Touhou characters that are not defined here.
	 */
	OTHER("other", EnumSpecies.OTHERS),

	/**
	 * Everything else which doesn't fit into OTHER.
	 */
	CUSTOM("custom", EnumSpecies.OTHERS);

	private final String name;
	private final EnumSpecies[] species;

	EnumTouhouCharacters(String name, EnumSpecies... species) {
		this.name = name;
		this.species = species;
	}

	public String getName() {
		return name;
	}

	public EnumSpecies[] getSpecies() {
		return Arrays.copyOf(species, species.length);
	}
}
