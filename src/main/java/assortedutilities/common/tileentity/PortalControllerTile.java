package assortedutilities.common.tileentity;

import java.util.ArrayList;
import java.util.Stack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import assortedutilities.AssortedUtilities;
import assortedutilities.common.block.PortalControllerBlock;
import assortedutilities.common.block.PortalFrameBlock;
import assortedutilities.common.item.PortalLocationItem;
import assortedutilities.common.util.AULog;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PortalControllerTile extends TileEntity implements IInventory {
	
	private ArrayList<ChunkCoordinates> ring = new ArrayList<ChunkCoordinates>();
	private ArrayList<ChunkCoordinates> interior = new ArrayList<ChunkCoordinates>();
	private IInventory inventory;
	private boolean updateFrame = true;
	private boolean updatePortal = true;
	private boolean portalLit = false;
	private int portalPlane = 0;
	
	public PortalControllerTile() {
		super();
		this.inventory = new InventoryBasic("Controller", true, 1);
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (updateFrame) {
				AULog.debug("Updating frame state");
				validateRing();
				updateFrame = false;
				updatePortal = true;
			}
			if (updatePortal) {
				AULog.debug("Updating portal state");
				ItemStack stack = inventory.getStackInSlot(0);
				if (stack != null && stack.hasTagCompound()) {
					int meta = 0;
					switch (portalPlane) {
					case 3:
						meta = 1; break;
					case 5:
						meta = 3; break;
					case 6:
						meta = 2; break;
					}
					if (meta > 0) {
						int dim = stack.stackTagCompound.getInteger("dim");
						double x = stack.stackTagCompound.getDouble("x"), y = stack.stackTagCompound.getDouble("y"), z = stack.stackTagCompound.getDouble("z");
						if (portalSpaceClear()) {
							lightPortal(meta, x, y, z, dim);
						}
					}
				} else if (portalLit) {
					extinguishPortal();
				}
				updatePortal = false;
			}
		}
		
	}

	public boolean onActivate(EntityPlayer player) {
		ItemStack heldStack = player.getCurrentEquippedItem();
		if (inventory.getStackInSlot(0) != null && heldStack == null) {
			//drop card to player
			player.setCurrentItemOrArmor(0, this.decrStackSize(0, 1));
		} else if (heldStack != null && heldStack.getItem() instanceof PortalLocationItem && heldStack.stackSize == 1) {
			//our inventory is empty and they have a card for us!
			this.setInventorySlotContents(0, heldStack.copy());
			heldStack.stackSize = 0;
		} else {
			return false;
		}
		return true;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound item = tag.getCompoundTag("item");
		inventory.setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(item));
		this.markDirty();
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound item = new NBTTagCompound();
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack != null) {
			stack.writeToNBT(item);
		}
		tag.setTag("item", item);
	}
	
	public Vec3 getTranslation() {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		switch (meta) {
			case 0:
			case 1:
				return Vec3.createVectorHelper(0, .5d, .5d);
			case 2:
			case 3:
			case 4:
			case 5:
				return Vec3.createVectorHelper(.5d, 0, .5d);
			default:
				return null;
		}
	}
	
	public float getYRotation() {
		float[] rotation = {0,0,180,0,270,90};
		return rotation[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)];
	}
	
	public float getXRotation() {
		float[] rotation = {90,270,0,0,0,0};
		return rotation[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)];
	}
	
	private boolean validateRing() {
		ChunkCoordinates start = null;
		ChunkCoordinates[] frameNeighbors = getFrameNeighbors(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		for (int i = 0; i < frameNeighbors.length; i++) {
			if (frameNeighbors[i] != null) {
				start = frameNeighbors[i];
				break;
			}
		}
		
		if (start != null) {
			Block currBlock = worldObj.getBlock(start.posX, start.posY, start.posZ);
			ArrayList<ChunkCoordinates> result = findPortalFrameRing(worldObj, start.posX, start.posY, start.posZ);
			if (result != null) {
				ring = result;
				interior = findInteriorBlocks(ring);
				AULog.debug("Ring formed with %d frame blocks and %d interior blocks", ring.size(), interior.size());
				lightRing();
				return true;
			}
		}
		if (ring != null && !ring.isEmpty()) {
			extinguishRing();
			ring = new ArrayList<ChunkCoordinates>();
			interior = new ArrayList<ChunkCoordinates>();
		}
		return false;
	}
	
	private boolean portalSpaceClear() {
		for (ChunkCoordinates location : this.interior){
			if (!worldObj.isAirBlock(location.posX, location.posY, location.posZ)){
				return false;
			}
		}
		return true;
	}
	
	private void lightRing() {
		for (ChunkCoordinates location : this.ring) {
			worldObj.setBlockMetadataWithNotify(location.posX, location.posY, location.posZ, 1, 3);
		}
	}
	
	private void extinguishRing() {
		for (ChunkCoordinates location : this.ring) {
			worldObj.setBlockMetadataWithNotify(location.posX, location.posY, location.posZ, 0, 3);
		}
	}
	
	private void lightPortal(int meta, double x, double y, double z, int dim) {
		AULog.debug("Setting up portal blocks");
		for (ChunkCoordinates portal : this.interior) {
			worldObj.setBlock(portal.posX, portal.posY, portal.posZ, AssortedUtilities.Blocks.portalBlock, meta, 3);
			TileEntity tile = worldObj.getTileEntity(portal.posX, portal.posY, portal.posZ);
			if (tile instanceof PortalTile) {
				((PortalTile)tile).setDestination(x, y, z, dim);
			}
		}
		portalLit = true;
	}
	
	private void extinguishPortal() {
		AULog.debug("Destroying portal blocks");
		for (ChunkCoordinates portal : this.interior) {
			worldObj.setBlockToAir(portal.posX, portal.posY, portal.posZ);
		}
		portalLit = false;
	}
	
	private ChunkCoordinates[] getFrameNeighbors(World world, int x, int y, int z) {
		ChunkCoordinates[] result = new ChunkCoordinates[6];
		if (world.getBlock(x, y - 1, z) instanceof PortalFrameBlock) {result[0] = new ChunkCoordinates(x, y - 1, z);}
		if (world.getBlock(x, y + 1, z) instanceof PortalFrameBlock) {result[1] = new ChunkCoordinates(x, y + 1, z);}
		if (world.getBlock(x, y, z - 1) instanceof PortalFrameBlock) {result[2] = new ChunkCoordinates(x, y, z - 1);}
		if (world.getBlock(x, y, z + 1) instanceof PortalFrameBlock) {result[3] = new ChunkCoordinates(x, y, z + 1);}
		if (world.getBlock(x - 1, y, z) instanceof PortalFrameBlock) {result[4] = new ChunkCoordinates(x - 1, y, z);}
		if (world.getBlock(x + 1, y, z) instanceof PortalFrameBlock) {result[5] = new ChunkCoordinates(x + 1, y, z);}
		return result;
	}
	
	public ArrayList<PortalControllerTile> getConnectedControllers(World world, int x, int y, int z) {
		ArrayList<PortalControllerTile> result = new ArrayList<PortalControllerTile>();
		if (world.getBlock(x, y - 1, z) instanceof PortalControllerBlock) {result.add((PortalControllerTile) world.getTileEntity(x, y - 1, z));}
		if (world.getBlock(x, y + 1, z) instanceof PortalControllerBlock) {result.add((PortalControllerTile) world.getTileEntity(x, y + 1, z));}
		if (world.getBlock(x, y, z - 1) instanceof PortalControllerBlock) {result.add((PortalControllerTile) world.getTileEntity(x, y, z - 1));}
		if (world.getBlock(x, y, z + 1) instanceof PortalControllerBlock) {result.add((PortalControllerTile) world.getTileEntity(x, y, z + 1));}
		if (world.getBlock(x - 1, y, z) instanceof PortalControllerBlock) {result.add((PortalControllerTile) world.getTileEntity(x - 1, y, z));}
		if (world.getBlock(x + 1, y, z) instanceof PortalControllerBlock) {result.add((PortalControllerTile) world.getTileEntity(x + 1, y, z));}
		return result;
	}
	
	private ArrayList<ChunkCoordinates> findInteriorBlocks(ArrayList<ChunkCoordinates> frameBlocks) {
		ArrayList<ChunkCoordinates> result = new ArrayList<ChunkCoordinates>();
		ChunkCoordinates initial = frameBlocks.get(0);
		int minX = initial.posX;
		int maxX = initial.posX;
		int minY = initial.posY;
		int maxY = initial.posY;
		int minZ = initial.posZ;
		int maxZ = initial.posZ;
		for (ChunkCoordinates location : frameBlocks) {
			minX = Math.min(minX, location.posX);
			maxX = Math.max(maxX, location.posX);
			minY = Math.min(minY, location.posY);
			maxY = Math.max(maxY, location.posY);
			minZ = Math.min(minZ, location.posZ);
			maxZ = Math.max(maxZ, location.posZ);
		}
		if (minX == maxX) {	
			for (int y = minY + 1; y < maxY; y++) {
				boolean inside = false;
				int sidesFlag = 0;
				for (int z = minZ; z <= maxZ; z++) {
					if (worldObj.getBlock(minX, y, z) instanceof PortalFrameBlock && frameBlocks.contains(new ChunkCoordinates(minX, y, z))) {
						if (worldObj.getBlock(minX, y - 1, z) instanceof PortalFrameBlock && frameBlocks.contains(new ChunkCoordinates(minX, y - 1, z))) {sidesFlag = sidesFlag | 1;}
						if (worldObj.getBlock(minX, y + 1, z) instanceof PortalFrameBlock && frameBlocks.contains(new ChunkCoordinates(minX, y + 1, z))) {sidesFlag = sidesFlag | 2;}
						if (sidesFlag == 3) {
							inside = !inside;
						}
					} else {
						if (inside) {
							result.add(new ChunkCoordinates(minX, y, z));
						}
						sidesFlag = 0;
					}
				}
			}
		} else if (minY == maxY) {
			for (int x = minX + 1; x < maxX; x++) {
				boolean inside = false;
				int sidesFlag = 0;
				for (int z = minZ; z <= maxZ; z++) {
					if (worldObj.getBlock(x, minY, z) instanceof PortalFrameBlock && frameBlocks.contains(new ChunkCoordinates(x, minY, z))) {
						if (worldObj.getBlock(x - 1, minY, z) instanceof PortalFrameBlock && frameBlocks.contains(new ChunkCoordinates(x - 1, minY, z))) {sidesFlag = sidesFlag | 1;}
						if (worldObj.getBlock(x + 1, minY, z) instanceof PortalFrameBlock && frameBlocks.contains(new ChunkCoordinates(x + 1, minY, z))) {sidesFlag = sidesFlag | 2;}
						if (sidesFlag == 3) {
							inside = !inside;
						}
					} else {
						if (inside) {
							result.add(new ChunkCoordinates(x, minY, z));
						}
						sidesFlag = 0;
					}
				}
			}
		} else if (minZ == maxZ) {
			for (int x = minX + 1; x < maxX; x++) {
				boolean inside = false;
				int sidesFlag = 0;
				for (int y = minY; y <= maxY; y++) {
					if (worldObj.getBlock(x, y, minZ) instanceof PortalFrameBlock && frameBlocks.contains(new ChunkCoordinates(x, y, minZ))) {
						if (worldObj.getBlock(x - 1, y, minZ) instanceof PortalFrameBlock && frameBlocks.contains(new ChunkCoordinates(x - 1, y, minZ))) {sidesFlag = sidesFlag | 1;}
						if (worldObj.getBlock(x + 1, y, minZ) instanceof PortalFrameBlock && frameBlocks.contains(new ChunkCoordinates(x + 1, y, minZ))) {sidesFlag = sidesFlag | 2;}
						if (sidesFlag == 3) {
							inside = !inside;
						}
					} else {
						if (inside) {
							result.add(new ChunkCoordinates(x, y, minZ));
						}
						sidesFlag = 0;
					}
				}
			}
		}
		
		return result;
	}

	private ArrayList<ChunkCoordinates> findPortalFrameRing(World world, int x, int y, int z) {
		ChunkCoordinates origin = new ChunkCoordinates(x, y, z);
		AULog.debug("Origin: %s", origin.toString());
		ArrayList<ChunkCoordinates> result = new ArrayList<ChunkCoordinates>();
		ChunkCoordinates last = origin;
		ChunkCoordinates current = origin;
		int axisFlag = 0;	
		
		while (true) {
			result.add(current);
			AULog.debug("Adding current block to list: %s", current.toString());
			Block currBlock = world.getBlock(current.posX, current.posY, current.posZ);
			if (currBlock instanceof PortalFrameBlock) {
				AULog.debug("Current block is instance of PFB");
				PortalFrameBlock portalFrame = (PortalFrameBlock)currBlock;
				ChunkCoordinates[] neighbors = portalFrame.getFrameNeighbors(world, current.posX, current.posY, current.posZ);
				ChunkCoordinates next = null;
				int neighborCount = 0;
				for (int i = 0; i < neighbors.length; i++) {
					if (neighbors[i] != null) {
						neighborCount++;
						if (!last.equals(neighbors[i])) {
							next = neighbors[i];
							AULog.debug("Next found, is %s", next.toString());
							switch(i) {
								case 0:
								case 1:
									axisFlag = axisFlag | 1;
									break;
								case 2:
								case 3:
									axisFlag = axisFlag | 2;
									break;
								case 4:
								case 5:
									axisFlag = axisFlag | 4;
									break;
							}
						}
					}
				}
				if (neighborCount != 2 || axisFlag == 7) { return null; }
				if (origin.equals(next)) { break; }
				last = current;
				current = next;
			}
		}
		if (result.size() > 4) {
			portalPlane = axisFlag;
			return result;
		} else {
			return null;
		}
		
	}
	
	private ArrayList<ChunkCoordinates> findAllConnectedFrames(World world, int x, int y, int z) {
		ArrayList<ChunkCoordinates> result = new ArrayList<ChunkCoordinates>();
		Stack<ChunkCoordinates> stack =  new Stack<ChunkCoordinates>();
		ChunkCoordinates origin = new ChunkCoordinates(x, y, z);
		stack.push(origin);
		
		while (stack.size() > 0) {
			ChunkCoordinates current = stack.pop();
			if (!result.contains(current)) {
				ChunkCoordinates[] neighbors = getFrameNeighbors(world, current.posX, current.posY, current.posZ);
				for (int i = 0; i < neighbors.length; i++) {
					if (neighbors[i] != null && !result.contains(neighbors[i])) {
						stack.push(neighbors[i]);
					}
				}
				result.add(current);
			}
		}
		return result;
	}
	
	private ArrayList<PortalControllerTile> findPortalControllers(World world, int x, int y, int z) {
		ArrayList<PortalControllerTile> result = new ArrayList<PortalControllerTile>();
		ArrayList<ChunkCoordinates> connectedFrames = findAllConnectedFrames(world, x, y, z);
		for (ChunkCoordinates location : connectedFrames) {
			result.addAll(getConnectedControllers(world, location.posX, location.posY, location.posZ));
		}
		
		return result;
	}

	public void blockAdded() {
		if (!worldObj.isRemote) {
			updateFrame = true;
		}
	}
	
	public void blockRemoved() {
		if (!worldObj.isRemote) {
			updateFrame = true;
		}
	}

	public void onPlacement() {
		if (!worldObj.isRemote) {
			updateFrame = true;
		}
	}

	public void onBreak() {
		if (!worldObj.isRemote) {
			ChunkCoordinates start = null;
			if (!ring.isEmpty()) {
				ChunkCoordinates[] frameNeighbors = getFrameNeighbors(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
				for (int i = 0; i < frameNeighbors.length; i++) {
					if (frameNeighbors[i] != null) {
						start = frameNeighbors[i];
						break;
					}
				}
			}
			if (start != null) {
				AULog.debug("Got a starting point");
				ArrayList<PortalControllerTile> controllers = findPortalControllers(worldObj, start.posX, start.posY, start.posZ);
				AULog.debug("Controllers size was %d", controllers.size());
				if (controllers.size() == 0) {
					extinguishPortal();
					extinguishRing();
				}
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int quantity) {
		AULog.info("Setting flag for portal update.");
		this.updatePortal = true;
		this.markDirty();
		return inventory.decrStackSize(slot, quantity);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (isItemValidForSlot(slot, stack)) {
			inventory.setInventorySlotContents(slot, stack);
		}
		this.markDirty();
		AULog.info("Setting flag for portal update.");
		this.updatePortal = true;
	}

	@Override
	public String getInventoryName() {
		return inventory.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return inventory.hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (stack == null || (slot == 0 && stack.getItem() instanceof PortalLocationItem && stack.stackSize <= 1)) {
			return true;
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.SERVER)
	public Packet getDescriptionPacket()
	{
	 NBTTagCompound tag = new NBTTagCompound();
	 this.writeToNBT(tag);
	 return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag);
	}
		
	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
	 readFromNBT(packet.func_148857_g());
	}
	
}
