package me.s1mple.util;

import org.bukkit.entity.Player;

public enum Permissions {
    MN_ADD("mn.add"),
    MN_REMOVE("mn.add"),
    MN_SETSPAWN("mn.add"),
    MN_ADDARENA("mn.add"),
    MN_JOIN("mn.add"),
    MN_LEAVE("mn.add"),
    MN_ARENAS("mn.add"),
    MN_REMOVEARENA("mn.add");

    private final String perm;

    Permissions(final String perm) {
        this.perm=perm;
    }

    public boolean hasPerm(Player player) {
        if(player.hasPermission(perm)) {
            return true;
        }
        return false;
    }


}
