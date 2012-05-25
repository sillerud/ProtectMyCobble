package me.kevin.protectmycobble.permissionhandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.kevin.protectmycobble.API.Permission;
import me.kevin.protectmycobble.API.Permission.Type;

public class PMC_OP implements Permission.PermissionHandler{

	@Override
	public boolean hasPermission(Type permission, Player player) {
		return player.isOp();
	}

	@Override
	public void init() {
		Bukkit.getLogger().info("ProtectMyCobble's OP binder started!");
	}

}
