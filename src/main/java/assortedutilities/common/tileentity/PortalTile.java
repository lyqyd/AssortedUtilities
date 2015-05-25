package assortedutilities.common.tileentity;

import java.util.Iterator;

import cpw.mods.fml.common.FMLCommonHandler;
import assortedutilities.common.util.AULog;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;

public class PortalTile extends TileEntity {
	
	private Vec3 destination;
	private int destDimension = 0;
	private float yaw;
	
	public void onCollide(Entity entity) {
		if (!worldObj.isRemote && destination != null) {
			double x = destination.xCoord, y = destination.yCoord, z = destination.zCoord;
			AULog.debug("Initialize teleport");
	    	entity.motionX = entity.motionY = entity.motionZ = 0.0D;
	        entity.setPosition(x, y, z);
	        EntityPlayerMP entityMP = null;
	    	if (entity instanceof EntityPlayerMP) {entityMP = (EntityPlayerMP) entity;}
	    	
	        MinecraftServer server = MinecraftServer.getServer();
	        WorldServer worldserver1 = server.worldServerForDimension(destDimension);
	        if (entity.dimension != destDimension) {
	        	AULog.debug("Entity isn't in target dimension");
		    	ServerConfigurationManager configurationManager = server.getConfigurationManager();
		       
		    	int oldDimension = entity.dimension;
		        WorldServer worldserver = server.worldServerForDimension(entity.dimension);
		        entity.dimension = destDimension;
		    	if (entityMP != null) {
		    		AULog.debug("Entity is a Player");
			        entityMP.playerNetServerHandler.sendPacket(new S07PacketRespawn(entity.dimension, entity.worldObj.difficultySetting, entity.worldObj.getWorldInfo().getTerrainType(), entityMP.theItemInWorldManager.getGameType()));
			        worldserver.removePlayerEntityDangerously(entity);
			        entity.isDead = false;
		    	}
		        
		        //begin transfer-entity-to-world
		        worldserver.theProfiler.startSection("placing");
		
		        if (entity.isEntityAlive())
		        {
		            entity.setLocationAndAngles(x, y, z, yaw, entity.rotationPitch);
		            worldserver1.spawnEntityInWorld(entity);
		            worldserver1.updateEntityWithOptionalForce(entity, false);
		        }
		
		        worldserver.theProfiler.endSection();
		        entity.setWorld(worldserver1);
		        //end transfer-entity-to-world
		        
		        if (entityMP != null) {
			        configurationManager.func_72375_a(entityMP, worldserver);
			        entityMP.playerNetServerHandler.setPlayerLocation(entity.posX, entity.posY, entity.posZ, yaw, entity.rotationPitch);
			        entityMP.theItemInWorldManager.setWorld(worldserver1);
			        configurationManager.updateTimeAndWeatherForPlayer(entityMP, worldserver1);
			        configurationManager.syncPlayerInventory(entityMP);
			        Iterator iterator = entityMP.getActivePotionEffects().iterator();
			
			        while (iterator.hasNext())
			        {
			            PotionEffect potioneffect = (PotionEffect)iterator.next();
			            entityMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(entity.getEntityId(), potioneffect));
			        }
			        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(entityMP, oldDimension, destDimension);
		        }
	        }
	        
	        AULog.debug("Setting new position: %f %f %f", x, y, z);
	        entity.setPosition(x, y, z);
	        if (entityMP != null) {entityMP.playerNetServerHandler.setPlayerLocation(entity.posX, entity.posY, entity.posZ, yaw, entity.rotationPitch);}
	        worldserver1.updateEntityWithOptionalForce(entity, false);
		}
	}

	public void setDestination(double x, double y, double z, int dim, float yaw) {
		this.destination = Vec3.createVectorHelper(x, y, z);
		this.destDimension = dim;
		this.yaw = yaw;
		this.markDirty();
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound dest = tag.getCompoundTag("destination");
		destination = Vec3.createVectorHelper(dest.getDouble("x"), dest.getDouble("y"), dest.getDouble("z"));
		yaw = dest.getFloat("yaw");
		destDimension = dest.getInteger("dim");
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound dest = new NBTTagCompound();
		dest.setDouble("x", destination.xCoord);
		dest.setDouble("y", destination.yCoord);
		dest.setDouble("z", destination.zCoord);
		dest.setFloat("yaw", yaw);
		dest.setInteger("dim", destDimension);
		tag.setTag("destination", dest);
	}

}
