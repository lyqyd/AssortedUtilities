package assortedutilities.common;

import java.util.Iterator;

import cpw.mods.fml.common.FMLCommonHandler;
import assortedutilities.common.util.AULog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

public class CustomTeleport extends Teleporter {
   

    // Setup Specific Variables
    private WorldServer worldserver;

    public CustomTeleport(WorldServer worldserver) {
        super(worldserver);
       
        // Setup Variables
        this.worldserver = worldserver;
       
    }

    // Move the Entity to the portal
    public void teleport(Entity entity, int dimension, double x, double y, double z) {
    	AULog.debug("Initialize teleport");
    	entity.motionX = entity.motionY = entity.motionZ = 0.0D;
        entity.setPosition(x, y, z);
    	
        if (entity.dimension != dimension) {
	    	MinecraftServer server = MinecraftServer.getServer();
	    	ServerConfigurationManager configurationManager = server.getConfigurationManager();
	    	EntityPlayerMP entityMP = null;
	    	if (entity instanceof EntityPlayerMP) {entityMP = (EntityPlayerMP) entity;}
	       
	    	int oldDimension = entity.dimension;
	        WorldServer worldserver = server.worldServerForDimension(entity.dimension);
	        entity.dimension = dimension;
	        WorldServer worldserver1 = server.worldServerForDimension(entity.dimension);
	    	if (entityMP != null) {
		        entityMP.playerNetServerHandler.sendPacket(new S07PacketRespawn(entity.dimension, entity.worldObj.difficultySetting, entity.worldObj.getWorldInfo().getTerrainType(), entityMP.theItemInWorldManager.getGameType()));
		        worldserver.removePlayerEntityDangerously(entity);
		        entity.isDead = false;
	    	}
	        
	        //begin transfer-entity-to-world
	        worldserver.theProfiler.startSection("moving");
	        worldserver.theProfiler.endSection();
	
	        worldserver.theProfiler.startSection("placing");
	
	        if (entity.isEntityAlive())
	        {
	            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
	            worldserver1.spawnEntityInWorld(entity);
	            worldserver1.updateEntityWithOptionalForce(entity, false);
	        }
	
	        worldserver.theProfiler.endSection();
	
	        entity.setWorld(worldserver1);
	        //end transfer-entity-to-world
	        
	        if (entityMP != null) {
		        configurationManager.func_72375_a(entityMP, worldserver);
		        entityMP.playerNetServerHandler.setPlayerLocation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
		        entityMP.theItemInWorldManager.setWorld(worldserver1);
		        configurationManager.updateTimeAndWeatherForPlayer(entityMP, worldserver1);
		        configurationManager.syncPlayerInventory(entityMP);
		        Iterator iterator = entityMP.getActivePotionEffects().iterator();
		
		        while (iterator.hasNext())
		        {
		            PotionEffect potioneffect = (PotionEffect)iterator.next();
		            entityMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(entity.getEntityId(), potioneffect));
		        }
		        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(entityMP, oldDimension, dimension);
	        }
        }
        
        entity.setPosition(x, y, z);
   
    }

    @Override
    public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {
        return false;
    }

    @Override
    public void removeStalePortalLocations(long par1) {}

    @Override
    public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {}
}
