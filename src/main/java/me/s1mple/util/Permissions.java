package me.s1mple.util;

import org.bukkit.entity.Player;

public enum Permissions {
    MN_ADD("mn.add"),
    MN_REMOVE("mn.remove"),
    MN_SETSPAWN("mn.setspawn"),
    MN_ADDARENA("mn.addarena"),
    MN_JOIN("mn.join"),
    MN_LEAVE("mn.leave"),
    MN_ARENAS("mn.arenas"),
    MN_REMOVEARENA("mn.removearenas"),
    MN_BACKUP("mn.backup");

    private final String perm;

    Permissions(final String perm) {
        this.perm = perm;
    }

    public boolean hasPerm(Player player) {
        return player.hasPermission(perm);
    }


}
