package assortedutilities.common.tileentity;

import assortedutilities.common.util.AULog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.*;

public class PortalTile extends TileEntity {
	
	private Vec3d destination;
	private int destDimension = 0;
	private float yaw;

	public void onCollide(Entity entity) {
		EntityPlayerMP player = null;
		if (entity instanceof EntityPlayerMP) {player = (EntityPlayerMP) entity;}
		if (!world.isRemote && destination != null) {
			if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entity, destDimension)) return;
			double x = destination.x, y = destination.y, z = destination.z;
			int oldDimension = entity.dimension;
			AULog.debug("Initialize teleport");
			if (player != null) {
				//Terrible, awful, reflection to set necessary fields in the player. Don't try this at home.
				try {
					Field invuln = ReflectionHelper.findField(EntityPlayerMP.class, "invulnerableDimensionChange", "field_184851_cj");
					invuln.setAccessible(true);
					invuln.set(player, true);
				} catch (ReflectionHelper.UnableToFindFieldException e) {
					AULog.warn("Cannot find invulnerability field!");
				} catch (IllegalAccessException e) {
					AULog.warn("Cannot access invulnerability field!");
				}
			}

			MinecraftServer server = entity.getServer();
			WorldServer worldserver = server.getWorld(oldDimension);
			WorldServer worldserver1 = server.getWorld(destDimension);
			if (entity.dimension != destDimension) {
				AULog.debug("Entity isn't in target dimension");
				PlayerList list = server.getPlayerList();

				entity.dimension = destDimension;
				if (player != null) {
					AULog.debug("Entity is a Player");
					player.connection.sendPacket(new SPacketRespawn(player.dimension, worldserver1.getDifficulty(), worldserver1.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
					worldserver.removeEntityDangerously(player);
					entity.isDead = false;

				}

				//begin transfer-entity-to-world
				worldserver.profiler.startSection("placing");
				AULog.debug("Setting entity position and angles");
				entity.setLocationAndAngles(x, y, z, yaw, entity.rotationPitch);
				worldserver1.spawnEntity(entity);
				if (player != null) {player.connection.setPlayerLocation(x, y, z, yaw, entity.rotationPitch);}
				worldserver1.updateEntityWithOptionalForce(entity, false);

				if (entity.isEntityAlive() && player == null) {
					this.world.profiler.startSection("reloading");
					Entity newEntity = EntityList.createEntityByIDFromName(EntityList.getKey(entity), worldserver1);

					if (newEntity != null) {
						NBTTagCompound tag = entity.writeToNBT(new NBTTagCompound());
						tag.removeTag("Dimension");
						newEntity.readFromNBT(tag);

						AULog.debug("Setting new entity position and angles");
						newEntity.setLocationAndAngles(x, y, z, yaw, entity.rotationPitch);

						boolean flag = newEntity.forceSpawn;
						newEntity.forceSpawn = true;
						worldserver1.spawnEntity(newEntity);
						newEntity.forceSpawn = flag;
						worldserver1.updateEntityWithOptionalForce(newEntity, true);
					}

					entity.isDead = true;
					this.world.profiler.endSection();
				}

				worldserver.profiler.endSection();
				entity.setWorld(worldserver1);
				//end transfer-entity-to-world

				if (player != null) {
					list.preparePlayer(player, worldserver);
					AULog.debug("Setting player position and angles");
					player.connection.setPlayerLocation(x, y, z, yaw, entity.rotationPitch);
					player.interactionManager.setWorld(worldserver1);
					player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
					list.updateTimeAndWeatherForPlayer(player, worldserver1);
					list.syncPlayerInventory(player);
					for (PotionEffect potioneffect : player.getActivePotionEffects()) {
						player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
					}
					net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDimension, destDimension);
				}
			}

			if (player != null) {
				AULog.debug("Setting new player position: %f %f %f", x, y, z);
				player.connection.setPlayerLocation(x, y, z, yaw, entity.rotationPitch);
				player.addExperienceLevel(0);
			} else {
				AULog.debug("Setting new entity position: %f %f %f", x, y, z);
				entity.setPosition(x, y, z);
				worldserver1.updateEntityWithOptionalForce(entity, false);
			}
			AULog.debug("Teleport complete");
		}
	}

	public void setDestination(double x, double y, double z, int dim, float yaw) {
		this.destination = new Vec3d(x, y, z);
		this.destDimension = dim;
		this.yaw = yaw;
		this.markDirty();
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound dest = tag.getCompoundTag("destination");
		destination = new Vec3d(dest.getDouble("x"), dest.getDouble("y"), dest.getDouble("z"));
		yaw = dest.getFloat("yaw");
		destDimension = dest.getInteger("dim");
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		NBTTagCompound dest = new NBTTagCompound();
		dest.setDouble("x", destination.x);
		dest.setDouble("y", destination.y);
		dest.setDouble("z", destination.z);
		dest.setFloat("yaw", yaw);
		dest.setInteger("dim", destDimension);
		tag.setTag("destination", dest);
		return tag;
	}

}
