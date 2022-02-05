package de.thedodo24.adventuria.adventuriaproxy.common.ban;

import lombok.Getter;

@Getter
public enum BanType {


    PLAYER_BAN("Permanent", "Spieler"),
    IP_BAN("Permanent", "IP"),
    PLAYER_BAN_TEMP("Temporär", "Spieler"),
    IP_BAN_TEMP("Temporär", "IP");

    private final String duration;
    private final String banned;

    BanType(String duration, String banned) {
        this.duration = duration;
        this.banned = banned;
    }

    public static BanType fromString(String t) {
        return switch (t) {
            case "PLAYER_BAN" -> PLAYER_BAN;
            case "IP_BAN" -> IP_BAN;
            case "PLAYER_BAN_TEMP" -> PLAYER_BAN_TEMP;
            case "IP_BAN_TEMP" -> IP_BAN_TEMP;
            default -> null;
        };
    }

}
