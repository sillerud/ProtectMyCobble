package me.kevin.protectmycobble.permissionhandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.kevin.protectmycobble.API.PermissionAPI;
import me.kevin.protectmycobble.API.PermissionAPI.Type;

public class PMCPermissionsBukkit implements PermissionAPI.PermissionHandler{

	@Override
	public boolean hasPermission(Type permission, Player player) {
		return player.hasPermission(PermissionAPI.getNode(permission));
	}

	@Override
	public void init() {
		Bukkit.getLogger().info("ProtectMyCobble's bukkit permission binder started!");
	}

}
