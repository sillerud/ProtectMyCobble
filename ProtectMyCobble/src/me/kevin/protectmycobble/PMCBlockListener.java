package me.kevin.protectmycobble;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PMCBlockListener implements Listener{
	ProtectMyCobble main;
	BlockFace[] sides = {BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH};
	Material[] nonrightclick = {Material.CHEST, Material.BREWING_STAND, Material.FURNACE, Material.BURNING_FURNACE, Material.DISPENSER};
	public PMCBlockListener(ProtectMyCobble protectMyCobble) {
		main = protectMyCobble;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(!event.isCancelled()){
			if(!main.getPermissionHandler(main.permissionType).hasPermission(Permission.Type.Break, event.getPlayer())){
				if(main.showOwner){
					String owner = main.getSQL().getOwner(event.getBlock().getLocation());
					if(owner != null){
						if(!owner.equals(event.getPlayer().getName())){
							event.setCancelled(true);
							event.getPlayer().sendMessage(format(main.protectedMessage, owner));
						}
					}
				}else{
					if(!main.getSQL().canBreakBlock(event.getBlock().getLocation(), event.getPlayer())){
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.getBlock().getType() == Material.CHEST){
			for(BlockFace face : sides){
				if(event.getBlock().getRelative(face).getType() == Material.CHEST){
					event.setCancelled(true);
					return;
				}
			}
		}
		if(!event.isCancelled()) {
			main.getSQL().protectBlock(event.getBlock().getLocation(), event.getPlayer());
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(main.showOwner){
				if(!main.permissionHandler.hasPermission(Permission.Type.OpenContainer, event.getPlayer())){
					String owner = main.getSQL().getOwner(event.getClickedBlock().getLocation());
					if(owner != null){
						if(!owner.equals(event.getPlayer().getName())){
							event.setCancelled(true);
							event.getPlayer().sendMessage(format(main.protectedMessage, owner));
						}
					}
				}
			}else{
				if(main.permissionHandler.hasPermission(Permission.Type.OpenContainer, event.getPlayer())){
					if(!main.getSQL().canBreakBlock(event.getClickedBlock().getLocation(), event.getPlayer())){
						event.setCancelled(true);
					}
				}
			}
		}
	}

	public static String format(String original){
		String modded = original;
		modded = modded.replaceAll("&0", ChatColor.BLACK.toString());
		modded = modded.replaceAll("&1", ChatColor.DARK_BLUE.toString());
		modded = modded.replaceAll("&2", ChatColor.DARK_GREEN.toString());
		modded = modded.replaceAll("&3", ChatColor.DARK_AQUA.toString());
		modded = modded.replaceAll("&4", ChatColor.DARK_RED.toString());
		modded = modded.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
		modded = modded.replaceAll("&6", ChatColor.GOLD.toString());
		modded = modded.replaceAll("&7", ChatColor.GRAY.toString());
		modded = modded.replaceAll("&8", ChatColor.DARK_GRAY.toString());
		modded = modded.replaceAll("&9", ChatColor.BLUE.toString());
		modded = modded.replaceAll("&a", ChatColor.GREEN.toString());
		modded = modded.replaceAll("&b", ChatColor.AQUA.toString());
		modded = modded.replaceAll("&c", ChatColor.RED.toString());
		modded = modded.replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
		modded = modded.replaceAll("&e", ChatColor.YELLOW.toString());
		modded = modded.replaceAll("&f", ChatColor.WHITE.toString());

		modded = modded.replaceAll("&A", ChatColor.GREEN.toString());
		modded = modded.replaceAll("&B", ChatColor.AQUA.toString());
		modded = modded.replaceAll("&C", ChatColor.RED.toString());
		modded = modded.replaceAll("&D", ChatColor.LIGHT_PURPLE.toString());
		modded = modded.replaceAll("&E", ChatColor.YELLOW.toString());
		modded = modded.replaceAll("&F", ChatColor.WHITE.toString());
		return modded;
	}
	public static String format(String original, String owner){
		String modded = format(original);
		modded = modded.replaceAll("!PlayerName!", owner);
		return modded;
	}
}
