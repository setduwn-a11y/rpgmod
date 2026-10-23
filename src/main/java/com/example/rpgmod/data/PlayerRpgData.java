package com.example.rpgmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;

/** Datos RPG guardados por jugador (se persisten con el mundo). */
public record PlayerRpgData(String rpgClass, String race, int level, int xp,
                            int skillPoints, List<String> skills) {

    public static final PlayerRpgData DEFAULT =
        new PlayerRpgData("ninguna", "humano", 1, 0, 0, List.of());

    public static final Codec<PlayerRpgData> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("class", "ninguna").forGetter(PlayerRpgData::rpgClass),
        Codec.STRING.optionalFieldOf("race", "humano").forGetter(PlayerRpgData::race),
        Codec.INT.optionalFieldOf("level", 1).forGetter(PlayerRpgData::level),
        Codec.INT.optionalFieldOf("xp", 0).forGetter(PlayerRpgData::xp),
        Codec.INT.optionalFieldOf("skill_points", 0).forGetter(PlayerRpgData::skillPoints),
        Codec.STRING.listOf().optionalFieldOf("skills", List.of()).forGetter(PlayerRpgData::skills)
    ).apply(i, PlayerRpgData::new));

    public PlayerRpgData withClass(RpgClass c) {
        return new PlayerRpgData(c.id(), race, level, xp, skillPoints, skills);
    }

    public PlayerRpgData withRace(RpgRace r) {
        return new PlayerRpgData(rpgClass, r.id(), level, xp, skillPoints, skills);
    }

    public PlayerRpgData withSkill(String skillId) {
        List<String> copy = new ArrayList<>(skills);
        if (!copy.contains(skillId)) copy.add(skillId);
        return new PlayerRpgData(rpgClass, race, level, xp, skillPoints, List.copyOf(copy));
    }

    public RpgClass rpgClassEnum() { return RpgClass.byId(rpgClass).orElse(RpgClass.NINGUNA); }
    public RpgRace raceEnum() { return RpgRace.byId(race).orElse(RpgRace.HUMANO); }
}
