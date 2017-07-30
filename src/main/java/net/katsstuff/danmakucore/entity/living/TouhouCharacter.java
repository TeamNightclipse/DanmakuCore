/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import net.katsstuff.danmakucore.misc.ITranslatable;

/**
 * The different Touhou characters. Currently only used for localizing spellcards.
 */
@SuppressWarnings("unused")
public class TouhouCharacter implements ITranslatable {

	public static final TouhouCharacter REIMU_HAKUREI = new TouhouCharacter("REIMU_HAKUREI", "reimu", TouhouSpecies.HUMAN);
	public static final TouhouCharacter MARISA_KIRISAME = new TouhouCharacter("MARISA_KIRISAME", "marisa", TouhouSpecies.HUMAN);

	//Embodiment of Scarlet Devil
	public static final TouhouCharacter RUMIA = new TouhouCharacter("rumia", TouhouSpecies.YOUKAI);
	public static final TouhouCharacter DAIYOUSEI = new TouhouCharacter("daiyousei", TouhouSpecies.FAIRY);
	public static final TouhouCharacter CIRNO = new TouhouCharacter("cirno", TouhouSpecies.FAIRY_ICE);
	public static final TouhouCharacter HONG_MEILING = new TouhouCharacter("HONG_MEILING", "meiling", TouhouSpecies.YOUKAI);
	public static final TouhouCharacter KOAKUMA = new TouhouCharacter("koakuma", TouhouSpecies.YOUKAI_DEVIL);
	public static final TouhouCharacter PATCHULI_KNOWLEDGE = new TouhouCharacter("PATCHULI_KNOWLEDGE", "patchuli", TouhouSpecies.YOUKAI_MAGICIAN);
	public static final TouhouCharacter SAKUYA_IZAYOI = new TouhouCharacter("SAKUYA_IZAYOI", "sakuya", TouhouSpecies.HUMAN);
	public static final TouhouCharacter REMILIA_SCARLET = new TouhouCharacter("REMILIA_SCARLET", "remilia", TouhouSpecies.YOUKAI_DEVIL_VAMPIRE);
	public static final TouhouCharacter FLANDRE_SCARLET = new TouhouCharacter("FLANDRE_SCARLET", "flandre", TouhouSpecies.YOUKAI_DEVIL_VAMPIRE);
	public static final TouhouCharacter SCARLET_SISTERS = new TouhouCharacter("SCARLET_SISTERS", "scarletSisters", REMILIA_SCARLET, FLANDRE_SCARLET);

	//Perfect Cherry Blossom
	public static final TouhouCharacter LETTY_WHITEROCK = new TouhouCharacter("LETTY_WHITEROCK", "letty", TouhouSpecies.YOUKAI_YUKIONNA);
	public static final TouhouCharacter CHEN = new TouhouCharacter("chen", TouhouSpecies.YOUKAI_BEAST_CAT, TouhouSpecies.SHIKIGAMI_FAMILIAR);
	public static final TouhouCharacter ALICE_MARGATROID = new TouhouCharacter("ALICE_MARGATROID", "alice", TouhouSpecies.YOUKAI_MAGICIAN);
	public static final TouhouCharacter SHANGHAI_DOLL = new TouhouCharacter("SHANGHAI_DOLL", "shanghai", TouhouSpecies.SHIKIGAMI_DOLL);
	public static final TouhouCharacter HOURAI_DOLL = new TouhouCharacter("HOURAI_DOLL", "hourai", TouhouSpecies.SHIKIGAMI_DOLL);
	public static final TouhouCharacter LILY_WHITE = new TouhouCharacter("LILY_WHITE", "lily", TouhouSpecies.FAIRY);
	public static final TouhouCharacter LYRICA_PRIMSRIVER = new TouhouCharacter("LYRICA_PRIMSRIVER", "lyrica", TouhouSpecies.POLTERGEIST);
	public static final TouhouCharacter LUNASA_PRIMSRIVER = new TouhouCharacter("LUNASA_PRIMSRIVER", "lunasa", TouhouSpecies.POLTERGEIST);
	public static final TouhouCharacter MERLIN_PRIMSRIVER = new TouhouCharacter("MERLIN_PRIMSRIVER", "merlin", TouhouSpecies.POLTERGEIST);
	public static final TouhouCharacter PRIMSRIVER_SISTERS = new TouhouCharacter("PRIMSRIVER_SISTERS", "primsriverSisters", LYRICA_PRIMSRIVER, LUNASA_PRIMSRIVER, MERLIN_PRIMSRIVER);
	public static final TouhouCharacter YOUMU_KONPAKU = new TouhouCharacter("YOUMU_KONPAKU", "youmu", TouhouSpecies.HUMAN_HALF, TouhouSpecies.PHANTOM_HALF);
	public static final TouhouCharacter YUYUKO_SAIGYOJI = new TouhouCharacter("YUYUKO_SAIGYOJI", "yuyuko", TouhouSpecies.PHANTOM_GHOST);
	public static final TouhouCharacter RAN_YAKUMO = new TouhouCharacter("RAN_YAKUMO", "ran", TouhouSpecies.YOUKAI_BEAST_FOX, TouhouSpecies.SHIKIGAMI_FAMILIAR);
	public static final TouhouCharacter YUKARI_YAKUMO = new TouhouCharacter("YUKARI_YAKUMO", "yukari", TouhouSpecies.YOUKAI);

	public static final TouhouCharacter LAYLA_PRIMSRIVER = new TouhouCharacter("LAYLA_PRIMSRIVER", "layla", TouhouSpecies.HUMAN);
	public static final TouhouCharacter YOUKI_KONPAKU = new TouhouCharacter("YOUKI_KONPAKU", "youki", TouhouSpecies.HUMAN_HALF, TouhouSpecies.PHANTOM_HALF);
	public static final TouhouCharacter SAIGYOU_AYAKASHI = new TouhouCharacter("SAIGYOU_AYAKASHI", "saigyouAyakashi", TouhouSpecies.YOUKAI);

	//Immaterial and Missing Power
	public static final TouhouCharacter SUIKA_IKBUKI = new TouhouCharacter("SUIKA_IKBUKI", "suika", TouhouSpecies.YOUKAI_ONI);

	//Imperishable Night
	public static final TouhouCharacter WRIGGLE_NIGHTBUG = new TouhouCharacter("WRIGGLE_NIGHTBUG", "wriggle", TouhouSpecies.YOUKAI_BEAST_BUG);
	public static final TouhouCharacter MYSTIA_LORELEI = new TouhouCharacter("MYSTIA_LORELEI", "mystia", TouhouSpecies.YOUKAI_BIRD_YOSUZUME);
	public static final TouhouCharacter KEINE_KAMISHIRASAWA = new TouhouCharacter("KEINE_KAMISHIRASAWA", "keine", TouhouSpecies.YOUKAI_BEAST_WERE, TouhouSpecies.YOUKAI_BEAST_HAKUTAKU);
	public static final TouhouCharacter TEWI_INABA = new TouhouCharacter("TEWI_INABA", "tewi", TouhouSpecies.YOUKAI_BEAST_RABBIT);
	public static final TouhouCharacter REISEN_UDONGEIN_INABA = new TouhouCharacter("REISEN_UDONGEIN_INABA", "reisen", TouhouSpecies.ANIMAL_RABBIT_MOON);
	public static final TouhouCharacter EIRIN_YAGOKORO = new TouhouCharacter("EIRIN_YAGOKORO", "eirin", TouhouSpecies.HUMAN_LUNARIAN, TouhouSpecies.GOD); //God?, Wiki lists her as a goddess
	public static final TouhouCharacter KAGUYA_HOURAISAN = new TouhouCharacter("KAGUYA_HOURAISAN", "kaguya", TouhouSpecies.HUMAN_LUNARIAN);
	public static final TouhouCharacter FUJIWARA_NO_MOKOU = new TouhouCharacter("FUJIWARA_NO_MOKOU", "mokou", TouhouSpecies.HUMAN_IMMORTAL);

	//Phantasmagoria of Flower View
	public static final TouhouCharacter AYA_SHAMEIMARU = new TouhouCharacter("AYA_SHAMEIMARU", "aya", TouhouSpecies.YOUKAI_TENGU_CROW);
	public static final TouhouCharacter MEDICINE_MELANCHOLY = new TouhouCharacter("MEDICINE_MELANCHOLY", "medicine", TouhouSpecies.YOUKAI); //Tsukumogami?
	public static final TouhouCharacter YUUKA_KAZAMI = new TouhouCharacter("YUUKA_KAZAMI", "yuuka", TouhouSpecies.YOUKAI);
	public static final TouhouCharacter KOMACHI_ONOZUKA = new TouhouCharacter("KOMACHI_ONOZUKA", "komachi", TouhouSpecies.GOD_SHINIGAMI);
	public static final TouhouCharacter EIKU_SHIKI_YAMAXANADU = new TouhouCharacter("EIKU_SHIKI_YAMAXANADU", "shikieiki", TouhouSpecies.GOD_YAMA);

	//Mountain of Faith
	public static final TouhouCharacter SHIZUHA_AKI = new TouhouCharacter("SHIZUHA_AKI", "aki", TouhouSpecies.GOD);
	public static final TouhouCharacter MINORIKO_AKI = new TouhouCharacter("MINORIKO_AKI", "minoriko", TouhouSpecies.GOD);
	public static final TouhouCharacter AKI_SISTERS = new TouhouCharacter("AKI_SISTERS", "akiSisters", SHIZUHA_AKI, MINORIKO_AKI);
	public static final TouhouCharacter HINA_KAGIYAMA = new TouhouCharacter("HINA_KAGIYAMA", "hina", TouhouSpecies.GOD);
	public static final TouhouCharacter NITORI_KAWASHIRO = new TouhouCharacter("NITORI_KAWASHIRO", "nitori", TouhouSpecies.YOUKAI_KAPPA);
	public static final TouhouCharacter MOMIJI_INUBASHIRI = new TouhouCharacter("MOMIJI_INUBASHIRI", "momiji", TouhouSpecies.YOUKAI_TENGU_WHITEWOLF);
	public static final TouhouCharacter SANAE_KOCHIYA = new TouhouCharacter("SANAE_KOCHIYA", "sanae", TouhouSpecies.HUMAN);
	public static final TouhouCharacter KANAKO_YASAKA = new TouhouCharacter("KANAKO_YASAKA", "kanako", TouhouSpecies.GOD, TouhouSpecies.SPIRIT_DIVINE); //Divine spirit? Wiki mentions her being one
	public static final TouhouCharacter SUWAKO_MORIYA = new TouhouCharacter("SUWAKO_MORIYA", "suwako", TouhouSpecies.GOD);
	public static final TouhouCharacter TENMA = new TouhouCharacter("tenma", TouhouSpecies.YOUKAI_BEAST_TENGU);
	public static final TouhouCharacter MISHAGUJI = new TouhouCharacter("mishaguji", TouhouSpecies.GOD); //Species?

	//Scarlet Weather Rhapsody
	public static final TouhouCharacter IKU_NAGAE = new TouhouCharacter("IKU_NAGAE", "iku", TouhouSpecies.YOUKAI_BEAST);
	public static final TouhouCharacter TENSHI_HINANAWI = new TouhouCharacter("TENSHI_HINANAWI", "tenshi", TouhouSpecies.HUMAN_HERMIT_CELESTIAL);

	//Subterranean Animism
	public static final TouhouCharacter KISUME = new TouhouCharacter("kisume", TouhouSpecies.YOUKAI_TSURUBEOTOSHI);
	public static final TouhouCharacter YAMAME_KURODANI = new TouhouCharacter("YAMAME_KURODANI", "yamame", TouhouSpecies.YOUKAI_TSUCIGUMO);
	public static final TouhouCharacter PARSEE_MIZUHASHI = new TouhouCharacter("PARSEE_MIZUHASHI", "parsee", TouhouSpecies.YOUKAI_HASHIHIME);
	public static final TouhouCharacter YUUGI_HOSHIGUMA = new TouhouCharacter("YUUGI_HOSHIGUMA", "yuugi", TouhouSpecies.YOUKAI_ONI);
	public static final TouhouCharacter SATORI_KOMEIJI = new TouhouCharacter("SATORI_KOMEIJI", "satori", TouhouSpecies.YOUKAI_SATORI);
	public static final TouhouCharacter RIN_KAENBYOU_ORIN = new TouhouCharacter("RIN_KAENBYOU_ORIN", "orin", TouhouSpecies.YOUKAI_KASHA);
	public static final TouhouCharacter UTSUHO_REIUJI_OKUU = new TouhouCharacter("UTSUHO_REIUJI_OKUU", "okuu", TouhouSpecies.ANIMAL_RAVEN_HELL);
	public static final TouhouCharacter KOISHI_KOMEIJI = new TouhouCharacter("KOISHI_KOMEIJI", "koishi", TouhouSpecies.YOUKAI_SATORI);
	public static final TouhouCharacter KOMEIJI_SISTERS = new TouhouCharacter("KOMEIJI_SISTERS", "komeijiSisters", SATORI_KOMEIJI, KOISHI_KOMEIJI);

	//Hisoutensoku
	public static final TouhouCharacter GOLIATH_DOLL = new TouhouCharacter("GOLIATH_DOLL", "goliathDoll", TouhouSpecies.SHIKIGAMI_DOLL);
	public static final TouhouCharacter GIANT_CATFISH = new TouhouCharacter("GIANT_CATFISH", "giantCatfish", TouhouSpecies.ANIMAL);
	public static final TouhouCharacter HISOUTENSOKU = new TouhouCharacter("hisoutensoku", TouhouSpecies.OTHERS); //I am NOT making a floating balloon species

	//Double Spoiler
	public static final TouhouCharacter HATATE_HIMAKAIDOU = new TouhouCharacter("HATATE_HIMAKAIDOU", "hatate", TouhouSpecies.YOUKAI_TENGU_CROW);

	//Great Fairy Wars
	public static final TouhouCharacter LUNA_CHILD = new TouhouCharacter("LUNA_CHILD", "lunaChild", TouhouSpecies.FAIRY);
	public static final TouhouCharacter STAR_SAPPHIRE = new TouhouCharacter("STAR_SAPPHIRE", "starSapphire", TouhouSpecies.FAIRY);
	public static final TouhouCharacter SUNNY_MILK = new TouhouCharacter("SUNNY_MILK", "sunnyMilk", TouhouSpecies.FAIRY);
	public static final TouhouCharacter THREE_FAIRIES = new TouhouCharacter("THREE_FAIRIES", "threeFairies", LUNA_CHILD, STAR_SAPPHIRE, SUNNY_MILK);

	//Undefined Fantastic Object
	public static final TouhouCharacter NAZRIN = new TouhouCharacter("nazrin", TouhouSpecies.YOUKAI_BEAST_MOUSE);
	public static final TouhouCharacter KOGASA_TATARA = new TouhouCharacter("KOGASA_TATARA", "kogasa", TouhouSpecies.YOUKAI_TSUKUMOGAMI); //Specific species?
	public static final TouhouCharacter ICHIRIN_KUMOI = new TouhouCharacter("ICHIRIN_KUMOI", "ichirin", TouhouSpecies.YOUKAI);
	public static final TouhouCharacter UNZAN = new TouhouCharacter("unzan", TouhouSpecies.YOUKAI_NYUUDOU);
	public static final TouhouCharacter MINAMITSU_MURASA = new TouhouCharacter("MINAMITSU_MURASA", "murasa", TouhouSpecies.PHANTOM_SHIP);
	public static final TouhouCharacter SHOU_TORAMARU = new TouhouCharacter("SHOU_TORAMARU", "shou", TouhouSpecies.YOUKAI_BEAST_TIGER);
	public static final TouhouCharacter BYAKUREN_HIJIRI = new TouhouCharacter("SHOU_TORAMARU", "byakuren", TouhouSpecies.YOUKAI_MAGICIAN);
	public static final TouhouCharacter NUE_HOUJUU = new TouhouCharacter("NUE_HOUJUU", "nue", TouhouSpecies.YOUKAI_NUE);
	public static final TouhouCharacter BISHAMONTEN = new TouhouCharacter("BISHAMONTEN", "bishamonten", TouhouSpecies.GOD);
	public static final TouhouCharacter MYOUREN_HIJIRI = new TouhouCharacter("MYOUREN_HIJIRI", "myouren", TouhouSpecies.HUMAN);

	//Ten Desires
	public static final TouhouCharacter KYOUKO_KASADANI = new TouhouCharacter("KYOUKO_KASADANI", "kyouko", TouhouSpecies.YOUKAI_YAMABIKO);
	public static final TouhouCharacter YOSHIKA_MIYAKO = new TouhouCharacter("YOSHIKA_MIYAKO", "yoshika", TouhouSpecies.YOUKAI_JIANGSHI);
	public static final TouhouCharacter SEIGA_KAKU = new TouhouCharacter("SEIGA_KAKU", "seiga", TouhouSpecies.HUMAN_HERMIT);
	public static final TouhouCharacter SOGA_NO_TOJIKO = new TouhouCharacter("SOGA_NO_TOJIKO", "tojiko", TouhouSpecies.PHANTOM_GHOST);
	public static final TouhouCharacter MONONOBE_NO_FUTO = new TouhouCharacter("MONONOBE_NO_FUTO", "futo", TouhouSpecies.HUMAN_HERMIT_SHIKAISEN);
	public static final TouhouCharacter TOYOSATOMIMI_NO_MIKO = new TouhouCharacter("TOYOSATOMIMI_NO_MIKO", "miko", TouhouSpecies.HUMAN_SAINT, TouhouSpecies.HUMAN_HERMIT);
	public static final TouhouCharacter MAMIZOU_FUTATSUIWA = new TouhouCharacter("MAMIZOU_FUTATSUIWA", "mamizou", TouhouSpecies.YOUKAI_BEAST_BAKEDANUKI);

	//Hopeless Masquerade
	public static final TouhouCharacter HATA_NO_KOKORO = new TouhouCharacter("HATA_NO_KOKORO", "kokoro", TouhouSpecies.YOUKAI_TSUKUMOGAMI); //More specific?

	//Double Dealing Character
	public static final TouhouCharacter WAKASAGIHIME = new TouhouCharacter("wakasagihime", TouhouSpecies.YOUKAI_MERMAID);
	public static final TouhouCharacter SEKIBANKI = new TouhouCharacter("sekibanki", TouhouSpecies.YOUKAI_RUKUROKUBI);
	public static final TouhouCharacter KAGEROU_IMAIZUMI = new TouhouCharacter("KAGEROU_IMAIZUMI", "kagerou", TouhouSpecies.YOUKAI_BEAST_WERE, TouhouSpecies.YOUKAI_BEAST_WOLF);
	public static final TouhouCharacter BENBEN_TSUKUMO = new TouhouCharacter("BENBEN_TSUKUMO", "benben", TouhouSpecies.YOUKAI_TSUKUMOGAMI);
	public static final TouhouCharacter YATSUHASHI_TSUKUMO = new TouhouCharacter("YATSUHASHI_TSUKUMO", "yatsuhashi", TouhouSpecies.YOUKAI_TSUKUMOGAMI);
	public static final TouhouCharacter TSUKUMO_SISTERS = new TouhouCharacter("TSUKUMO_SISTERS", "tsukumoSisters", BENBEN_TSUKUMO, YATSUHASHI_TSUKUMO);
	public static final TouhouCharacter SEIJA_KIJIN = new TouhouCharacter("SEIJA_KIJIN", "seija", TouhouSpecies.YOUKAI_AMANOJAKU);
	public static final TouhouCharacter SHINMYOUMARU_SUKUNA = new TouhouCharacter("SHINMYOUMARU_SUKUNA", "shinmyoumaru", TouhouSpecies.HUMAN_INCHLINGS);
	public static final TouhouCharacter RAIKO_HORIKAWA = new TouhouCharacter("RAIKO_HORIKAWA", "raiko", TouhouSpecies.YOUKAI_TSUKUMOGAMI);

	//Urban Legend in Limbo
	public static final TouhouCharacter SUMIREKO_USAMI = new TouhouCharacter("SUMIREKO_USAMI", "sumireko", TouhouSpecies.HUMAN); //Just human?

	//Legacy of Lunatic Kingdom
	public static final TouhouCharacter SEIRAN = new TouhouCharacter("seiran", TouhouSpecies.ANIMAL_RABBIT_MOON);
	public static final TouhouCharacter RINGO = new TouhouCharacter("ringo", TouhouSpecies.ANIMAL_RABBIT_MOON);
	public static final TouhouCharacter DOROMY_SWEET = new TouhouCharacter("DOROMY_SWEET", "doromy", TouhouSpecies.YOUKAI_BAKU);
	public static final TouhouCharacter SAGUME_KISHIN = new TouhouCharacter("SAGUME_KISHIN", "sagume", TouhouSpecies.HUMAN_LUNARIAN, TouhouSpecies.GOD);
	public static final TouhouCharacter CLOWNPIECE = new TouhouCharacter("clownpiece", TouhouSpecies.FAIRY_HELL);
	public static final TouhouCharacter JUNKO = new TouhouCharacter("junko", TouhouSpecies.SPIRIT_DIVINE);
	public static final TouhouCharacter HECATIA_LAPISLAZULI = new TouhouCharacter("HECATIA_LAPISLAZULI", "hecatia", TouhouSpecies.GOD);

	//Curiosities of Lotus Asia
	public static final TouhouCharacter RINNOSUKE_MORICHIKA = new TouhouCharacter("RINNOSUKE_MORICHIKA", "rinnosuke", TouhouSpecies.HUMAN_HALF, TouhouSpecies.YOUKAI_HALF);
	public static final TouhouCharacter TOKIKO = new TouhouCharacter("tokiko", TouhouSpecies.YOUKAI);

	//Bougetsushou, Lots of weird characters if anyone need them
	public static final TouhouCharacter REISEN2 = new TouhouCharacter("reisen2", TouhouSpecies.ANIMAL_RABBIT_MOON);
	public static final TouhouCharacter WATATSUKI_NO_TOYOHIME = new TouhouCharacter("WATATSUKI_NO_TOYOHIME", "toyohime", TouhouSpecies.HUMAN_LUNARIAN);
	public static final TouhouCharacter WATATSUKI_NO_YORIHIME = new TouhouCharacter("WATATSUKI_NO_YORIHIME", "yorihime", TouhouSpecies.HUMAN_LUNARIAN);
	public static final TouhouCharacter WATATSUKI_SISTERS = new TouhouCharacter("WATATSUKI_SISTERS", "watasukiSisters", WATATSUKI_NO_TOYOHIME, WATATSUKI_NO_YORIHIME);
	public static final TouhouCharacter CHANGE = new TouhouCharacter("change", TouhouSpecies.GOD); //Hard to do special characters here, lunarian?
	public static final TouhouCharacter LORD_TSUKUYOMI = new TouhouCharacter("LORD_TSUKUYOMI", "tsukuyomi", TouhouSpecies.HUMAN_LUNARIAN);
	public static final TouhouCharacter MIZUE_NO_URANOSHIMAKO = new TouhouCharacter("MIZUE_NO_URANOSHIMAKO", "uranoshimako", TouhouSpecies.HUMAN);
	public static final TouhouCharacter IWAKASA = new TouhouCharacter("iwakasa", TouhouSpecies.HUMAN);
	public static final TouhouCharacter KONOHANA_SAKUYAHIME = new TouhouCharacter("KONOHANA_SAKUYAHIME", "sakuyahime", TouhouSpecies.GOD);
	public static final TouhouCharacter IWANAGAHIME = new TouhouCharacter("iwanagahime", TouhouSpecies.GOD);

	//Wild and Horned Hermit
	public static final TouhouCharacter KASEN_IBARAKI = new TouhouCharacter("KASEN_IBARAKI", "kasen", TouhouSpecies.OTHERS);

	//Forbidden Scrollery
	public static final TouhouCharacter KOSUZU_MOTOORI = new TouhouCharacter("KOSUZU_MOTOORI", "kosuzu", TouhouSpecies.HUMAN);

	//Music CDs
	public static final TouhouCharacter MERIBEL_HEARN = new TouhouCharacter("MERIBEL_HEARN", "maribel", TouhouSpecies.HUMAN);
	public static final TouhouCharacter RENKO_USAMI = new TouhouCharacter("RENKO_USAMI", "renko", TouhouSpecies.HUMAN);
	public static final TouhouCharacter HIEDA_NO_AKYUU = new TouhouCharacter("HIEDA_NO_AKYUU", "akyuu", TouhouSpecies.HUMAN);

	private static final Map<String, TouhouCharacter> byFullName = new HashMap<>();
	private static final Map<String, TouhouCharacter> byShortName = new HashMap<>();

	private final String fullName;
	private final String shortName;
	private final List<TouhouSpecies> species;
	private final List<TouhouCharacter> subCharacters;

	private TouhouCharacter(String fullName, String shortName, TouhouSpecies... species) {
		this.fullName = fullName.toUpperCase();
		this.shortName = shortName;
		this.species = ImmutableList.copyOf(species);
		this.subCharacters = ImmutableList.of();

		byFullName.put(this.fullName, this);
		byShortName.put(this.shortName, this);
	}

	private TouhouCharacter(String fullName, String shortName, TouhouCharacter... subCharacters) {
		this.fullName = fullName.toUpperCase();
		this.shortName = shortName;
		this.subCharacters = ImmutableList.copyOf(subCharacters);
		this.species = ImmutableList.copyOf(this.subCharacters.stream().flatMap(c -> c.species.stream()).distinct().collect(Collectors.toList()));

		byFullName.put(this.fullName, this);
		byShortName.put(this.shortName, this);
	}

	private TouhouCharacter(String name, TouhouSpecies... species) {
		this(name, name, species);
	}

	public static TouhouCharacter getOrCreate(String fullName, String shortName, TouhouSpecies... species) {
		fullName = fullName.toUpperCase();
		TouhouCharacter c = byFullName.get(fullName);
		if(c == null) {
			c = new TouhouCharacter(fullName, shortName, species);
		}

		return c;
	}

	public static TouhouCharacter getOrCreate(String fullName, String shortName, TouhouCharacter... subCharacters) {
		fullName = fullName.toUpperCase();
		TouhouCharacter c = byFullName.get(fullName);
		if(c == null) {
			c = new TouhouCharacter(fullName, shortName, subCharacters);
		}

		return c;
	}

	public static Optional<TouhouCharacter> getByFullName(String fullName) {
		return Optional.ofNullable(byFullName.get(fullName.toUpperCase()));
	}

	public static Optional<TouhouCharacter> getByShortName(String shortName) {
		return Optional.ofNullable(byShortName.get(shortName));
	}

	public String getShortName() {
		return shortName;
	}

	public String getFullName() {
		return fullName;
	}

	public List<TouhouSpecies> getSpecies() {
		return species;
	}

	@Override
	public String getUnlocalizedName() {
		return "touhouCharacter.name." + fullName;
	}
}
