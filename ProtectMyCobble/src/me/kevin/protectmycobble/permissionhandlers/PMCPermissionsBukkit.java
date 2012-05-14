package me.kevin.protectmycobble.permissionhandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.kevin.protectmycobble.Permission;
import me.kevin.protectmycobble.Permission.Type;

public class PMCPermissionsBukkit implements Permission.PermissionHandler{

	@Override
	public boolean hasPermission(Type permission, Player player) {
		return player.hasPermission(Permission.getNode(permission));
	}

	@Override
	public void init() {
		Bukkit.getLogger().info("ProtectMyCobble's bukkit permission binder started!");
	}

}
