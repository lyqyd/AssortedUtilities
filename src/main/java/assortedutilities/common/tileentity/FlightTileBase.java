package assortedutilities.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import assortedutilities.common.FlightTicket;
import assortedutilities.common.PlayerTicketManager;
import assortedutilities.common.ServerTicketManager;
import assortedutilities.common.handler.FallDamageHandler;
import assortedutilities.common.handler.IFallDamageHandler;
import assortedutilities.common.handler.IPlayerPresenceHandler;
import assortedutilities.common.handler.PlayerPresenceHandler;
import assortedutilities.common.util.AULog;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class FlightTileBase extends TileEntity implements IPlayerPresenceHandler, IFallDamageHandler {

	private ArrayList<String> flyingWhenLeft = new ArrayList<String>();
	private ArrayList<String> fallingWhenLeft = new ArrayList<String>();
	private HashMap<String, FlightTicket> tickets = new HashMap<String, FlightTicket>();
	private boolean enabled = false;
	private int chargeTime = 0;
	private int chargeDelay = 0;
	private int radius = 0;
	
	public FlightTileBase(int radius, int chargeDelay) {
		this.radius = radius;
		this.chargeDelay = chargeDelay;
		PlayerPresenceHandler.instance.addListener(this);
		FallDamageHandler.instance.addListener(this);
		if (this.chargeDelay == 0) {
			this.enabled = true;
			worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 6);
		}
	}
	
	private EntityPlayerMP getPlayerFromUUID(String playerID) {
		for (Object ent : worldObj.playerEntities) {
			if (ent instanceof EntityPlayerMP) {
				EntityPlayerMP entMP = (EntityPlayerMP) ent;
				if (playerID.equals(entMP.getUniqueID().toString())) {
					return entMP;
				}
			}
		}
		return null;
	}
	
	private boolean withinRange(EntityPlayer player) {
		return (player.posX >= this.xCoord - this.radius && player.posX <= this.xCoord + this.radius + 1 && player.posY >= 0 && player.posY <= 256 && player.posZ >= this.zCoord - this.radius && player.posZ <= this.zCoord + this.radius + 1);
	}
	
	private Vec3 randomSpot(int x0, int y0, int z0) {
		double radius = 5.0d;
		double u = Math.random();
		double v = Math.random();
		double theta = 2 * Math.PI * u;
		double phi = Math.acos(2 * v - 1);
		double x = x0 + (radius * Math.sin(phi) * Math.cos(theta));
		double y = y0 + (radius * Math.sin(phi) * Math.sin(theta));
		double z = z0 + (radius * Math.cos(phi));
		return Vec3.createVectorHelper(x, y, z);
	}

	private void dropPlayer(EntityPlayerMP player) {
		String id = player.getUniqueID().toString();
		int count = 0;
		PlayerTicketManager manager = ServerTicketManager.instance.getManagerForPlayer(player);
		if (manager != null) {
			count = manager.getFlightTicketCount();
			AULog.debug("%d, %d, %d dropping player %s, %d -> %d flight tickets", this.xCoord, this.yCoord, this.zCoord, player.getDisplayName(), count, --count);
		}
		synchronized(this.tickets) {
			this.tickets.get(id).setFalling();
			if (manager != null) {
				manager.update();
				if (manager.getFlightTicketCount() > 0) {
					this.tickets.remove(id);
				}
			}
		}
	}
	
	private void flyPlayer(EntityPlayerMP player) {
		int count = 0;
		PlayerTicketManager manager = ServerTicketManager.instance.getManagerForPlayer(player);
		String id = player.getUniqueID().toString();
		FlightTicket ticket;
		synchronized(this.tickets) {
			if (this.tickets.containsKey(id)) {
				ticket = this.tickets.get(id);
				AULog.debug("Flying player with existing ticket, re-using ticket %x", ticket.hashCode());
				ticket.setFlying();
			} else {
				ticket = new FlightTicket(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId, id);
				AULog.debug("Flying player on new ticket %x", ticket.hashCode());
				this.tickets.put(id, ticket);
			}
		}
		if (manager != null) {
			count = manager.getFlightTicketCount();
			AULog.debug("%d, %d, %d flying player %s on ticket %x, %d -> %d flight tickets", this.xCoord, this.yCoord, this.zCoord, player.getDisplayName(), ticket.hashCode(), count, ++count);
			manager.addTicket(ticket);
		}
	}
	
	@Override
	public void invalidate() {
		AULog.debug("Invalidating Flight Tile @ %d, %d, %d", this.xCoord, this.yCoord, this.zCoord);
		this.dropAllFlyers();
		PlayerPresenceHandler.instance.removeListener(this);
		FallDamageHandler.instance.removeListener(this);
		super.invalidate();
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote){ 
			if (this.enabled) {
				float radius = (float) this.radius;
				List<EntityPlayerMP> players = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox((float)this.xCoord - radius, 0.0f, (float)this.zCoord - radius, (float)this.xCoord + radius + 1.0f, 256.0f, (float)this.zCoord + radius + 1.0f));
				for (EntityPlayerMP player : players) {
					String id = player.getUniqueID().toString();
					if (!this.tickets.containsKey(id) || this.tickets.get(id).isFalling()) {
						if (player.isDead) {continue;}
						
						flyPlayer(player);
					}
				}
				
				ArrayList<EntityPlayerMP> droplist = new ArrayList<EntityPlayerMP>();
				synchronized(this.tickets) {
					for (String trackedID : this.tickets.keySet()) {
						EntityPlayerMP tracked = getPlayerFromUUID(trackedID);
						if (tracked != null) {
							if (this.tickets.get(trackedID).isFlying() && !players.contains(tracked) && tracked.dimension == this.worldObj.provider.dimensionId) {
								AULog.debug("OoR drop decision, %s: %d, %d", tracked.getDisplayName(), tracked.dimension, this.worldObj.provider.dimensionId);
								droplist.add(tracked);
							}
						}
					}
				}
				for (EntityPlayerMP player : droplist) {
					dropPlayer(player);
				}
			} else {
				this.chargeTime++;
				if (this.chargeTime > this.chargeDelay) {
					this.enabled = true;
					worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 6);
				}
			}
		} else {
			if (this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) != 1) {
				//spawn particles to show charging.
				double velocity = 0.6d;
				for (int i = 0; i < 4; i++) {
					Vec3 vec = this.randomSpot(this.xCoord, this.yCoord, this.zCoord);
					this.worldObj.spawnParticle("crit", vec.xCoord + 0.5d, vec.yCoord + 0.5d, vec.zCoord + 0.5d, ((double)this.xCoord - vec.xCoord) * velocity, ((double)this.yCoord - vec.yCoord) * velocity, ((double)this.zCoord - vec.zCoord) * velocity);
				}
			}
		}
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	
		NBTTagList ids = tag.getTagList("UUIDs", 8);
		for (int i = 0; i < ids.tagCount(); i++) {
			this.fallingWhenLeft.add(ids.getStringTagAt(i));
		}
		NBTTagList flyingIDs = tag.getTagList("FlyingUUIDs", 8);
		for (int i = 0; i < flyingIDs.tagCount(); i++) {
			this.flyingWhenLeft.add(flyingIDs.getStringTagAt(i));
		}
		
		this.chargeTime = tag.getInteger("chargeTime");
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		NBTTagList names = new NBTTagList();
		NBTTagList flying = new NBTTagList();
		synchronized(this.tickets) {
			for (Entry<String, FlightTicket> entry : this.tickets.entrySet()) {
				String id = entry.getKey();
				EntityPlayerMP player = getPlayerFromUUID(id);
				if (player != null) {
					if (entry.getValue().isFalling()) {
						names.appendTag(new NBTTagString(id));
					} else if (player.capabilities.isFlying) {
						flying.appendTag(new NBTTagString(id));
					}
				}
			}
		}
		for (String name : this.flyingWhenLeft) {
			flying.appendTag(new NBTTagString(name));
		}
		for (String name : this.fallingWhenLeft) {
			names.appendTag(new NBTTagString(name));
		}
		
		tag.setTag("UUIDs", names);
		tag.setTag("FlyingUUIDs", flying);
		tag.setInteger("chargeTime", this.chargeTime);
	}
	
	public void dropAllFlyers() {
		synchronized(this.tickets) {
			for (String player : this.tickets.keySet()) {
				dropPlayer(getPlayerFromUUID(player));
			}
		}
	}

	@Override
	public void onLogin(PlayerLoggedInEvent event) {
		if (this.flyingWhenLeft.contains(event.player.getUniqueID().toString())) {
			AULog.debug("Player %s logged in and was previously flying", event.player.getDisplayName());
			this.flyingWhenLeft.remove(event.player.getUniqueID().toString());
			if (withinRange(event.player)) {
				if (event.player instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP)event.player;
					flyPlayer(player);
					player.capabilities.isFlying = true;
					player.sendPlayerAbilities();
				} else {
					AULog.debug("Player logged in, but event player is not instance of EntityPlayerMP");
				}
			}
		}
	}

	@Override
	public void onLogout(PlayerLoggedOutEvent event) {
		if (event.player.capabilities.isFlying && withinRange(event.player)) {
			this.flyingWhenLeft.add(event.player.getUniqueID().toString());
		}
	}

	@Override
	public void onWorldChange(EntityJoinWorldEvent event) {
		if (!worldObj.isRemote){ 
			EntityPlayerMP player = (EntityPlayerMP) event.entity;
			if (event.world.provider.dimensionId == this.worldObj.provider.dimensionId) {
				if (withinRange(player)) {
					if (!player.isDead) {
						flyPlayer(player);
					}
				}
			} else {
				synchronized(this.tickets) {
					if (this.tickets.containsKey(player.getUniqueID().toString())) {
						dropPlayer(player);
					}
				}
			}
		}
	}

	@Override
	public int getHandlerDimension() {
		if (this.worldObj == null) {
			return 0;
		} else {
			return this.worldObj.provider.dimensionId;
		}
		
	}

	@Override
	public void onFall(LivingFallEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.entity;
			synchronized(this.tickets) {
				this.tickets.remove(player.getUniqueID().toString());
			}
		}
		
	}

}
