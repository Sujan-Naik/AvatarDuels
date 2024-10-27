package com.serene.avatarduels.ability.earth;

import java.util.HashMap;
import java.util.Map;

import com.serene.avatarduels.AvatarDuels;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.TempBlock;

public class Crumble extends SandAbility implements AddonAbility {

	@Attribute(Attribute.RADIUS)
	private int maxRadius;
	@Attribute("RevertTime")
	private int revertTime;
	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	
	private int radius, counter;
	private Block center;
	private Map<Block, BlockData> revert;
	
	public Crumble(Player player, ClickType click) {
		super(player);
		
		if (bPlayer.isOnCooldown(this)) {
			return;
		}
		
		if (click == ClickType.LEFT_CLICK) {
			int selectRange = AvatarDuels.getConfig(getName()).getInt("Abilities.Earth.Crumble.SelectRange");
			this.center = player.getTargetBlock(null, selectRange);
		} else {
			this.center = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		}

		this.revert = new HashMap<>();
		this.revertTime = AvatarDuels.getConfig(getName()).getInt("Abilities.Earth.Crumble.RevertTime");
		this.counter = 0;
		this.radius = 0;
		this.cooldown = AvatarDuels.getConfig(getName()).getLong("Abilities.Earth.Crumble.Cooldown");
		this.maxRadius = AvatarDuels.getConfig(getName()).getInt("Abilities.Earth.Crumble.Radius");
		
		start();
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "Crumble";
	}

	@Override
	public boolean isHarmlessAbility() {
		return true;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public void progress() {
		if (!player.isOnline() || player.isDead()) {
			remove();
			return;
		} else if (radius > maxRadius) {
			remove();
			return;
		}
		
		counter++;
		if (counter % 2 != 0) {
			return;
		}
		
		for (int theta = 0; theta < 360; theta += 5) {
			double x = Math.cos(Math.toRadians(theta)) * radius;
			double z = Math.sin(Math.toRadians(theta)) * radius;
			
			Block block = center.getRelative((int)x, 0, (int)z);
			block = GeneralMethods.getTopBlock(block.getLocation(), 2);
			
			int i = 0;
			while (block.isPassable() && i < 2) {
				if (isPlant(block)) {
					new TempBlock(block, Material.AIR).setRevertTime(revertTime);
				}
				
				block = block.getRelative(BlockFace.DOWN);
				i++;
			}
			
			if (TempBlock.isTempBlock(block)) {
				continue;
			} else if (!isEarthbendable(block)) {
				continue;
			} else if (isSand(block)) {
				continue;
			}
			
			Material m = Material.SAND;
			if (isAir(block.getRelative(BlockFace.DOWN).getType())) {
				m = Material.SANDSTONE;
			}
			
			revert.put(block, block.getBlockData());
			final Block b = block;
			
			new BukkitRunnable() {

				@Override
				public void run() {
					b.setBlockData(revert.get(b));
				}
				
			}.runTaskLater(ProjectKorra.plugin, 20 * revertTime);
			
			block.setType(m);
		}
		
		radius++;
	}
	
	@Override
	public void remove() {
		super.remove();
		bPlayer.addCooldown(this);
	}

	@Override
	public String getAuthor() {
		return "AvatarDuels";
	}

	@Override
	public String getVersion() {
		return AvatarDuels.instance.version();
	}

	@Override
	public void load() {
	}

	@Override
	public void stop() {
	}
	
	public void revert() {
		for (Block b : revert.keySet()) {
			b.setBlockData(revert.get(b));
		}
	}
	
	@Override
	public boolean isEnabled() {
		return AvatarDuels.getConfig(getName()).getBoolean("Abilities.Earth.Crumble.Enabled");
	}
	
	@Override
	public String getDescription() {
		return "Crumble the earth into sand!";
	}
	
	@Override
	public String getInstructions() {
		return "Left click or sneak";
	}
}