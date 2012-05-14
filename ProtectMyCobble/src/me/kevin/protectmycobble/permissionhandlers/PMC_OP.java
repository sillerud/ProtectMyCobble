package me.kevin.protectmycobble.permissionhandlers;

import org.bukkit.entity.Player;

import me.kevin.protectmycobble.Permission;
import me.kevin.protectmycobble.Permission.Type;

public class PMC_OP implements Permission.PermissionHandler{

	@Override
	public boolean hasPermission(Type permission, Player player) {
		return player.isOp();
	}

}
