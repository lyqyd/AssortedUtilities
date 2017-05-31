package assortedutilities.common.tileentity;

import java.util.ArrayList;
import java.util.Stack;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.block.PortalBlock;
import assortedutilities.common.block.PortalControllerBlock;
import assortedutilities.common.block.PortalFrameBlock;
import assortedutilities.common.item.PortalLocationItem;
import assortedutilities.common.util.AULog;
import assortedutilities.common.util.IPortalLocation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PortalControllerTile extends TileEntity implements IInventory, ITickable {
	
	private ArrayList<BlockPos> ring = new ArrayList<BlockPos>();
	private ArrayList<BlockPos> interior = new ArrayList<BlockPos>();
	private IInventory inventory;
	private boolean updateFrame = true;
	private boolean updatePortal = true;
	private boolean portalLit = false;
	private int portalPlane = 0;
	private boolean updateCard = false;
	
	public PortalControllerTile() {
		super();
		this.inventory = new InventoryBasic("Controller", true, 1);
	}

	public void update() {
		if (!worldObj.isRemote) {
			if (updateCard) {
				AULog.debug("Updating card state");
				if (this.inventory.getStackInSlot(0) != null) {
					worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(PortalControllerBlock.CARD_PRESENT, true), 3);
					AULog.debug("Set card presence true");
				} else {
					worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(PortalControllerBlock.CARD_PRESENT, false), 3);
					AULog.debug("Set card presence false");
				}
				this.markDirty();
				updateCard = false;
			}
			if (updateFrame) {
				AULog.debug("Updating frame state");
				validateRing();
				updateFrame = false;
				updatePortal = true;
			}
			if (updatePortal) {
				AULog.debug("Updating portal state");
				ItemStack stack = inventory.getStackInSlot(0);
				if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof IPortalLocation) {
					int meta = 0;
					switch (portalPlane) {
						//axis flag; 1 & 2, 1 & 4, 2 & 4.
					case 3:
						meta = 1; break;
					case 5:
						meta = 3; break;
					case 6:
						meta = 2; break;
					}
					if (meta > 0) {
						IPortalLocation location = (IPortalLocation) stack.getItem();
						int dim = location.getDimension(stack);
						Vec3d dest = location.getLocation(stack);
						double x = dest.xCoord, y = dest.yCoord, z = dest.zCoord;
						float yaw = location.getYaw(stack);
						if (portalSpaceClear()) {
							lightPortal(meta, x, y, z, dim, yaw);
						}
					}
				} else if (portalLit) {
					extinguishPortal();
				}
				updatePortal = false;
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public boolean onActivate(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
		if (!worldObj.isRemote && hand == EnumHand.MAIN_HAND) {
			if (inventory.getStackInSlot(0) != null && heldItem == null) {
				//drop card to player
				player.setHeldItem(hand, this.decrStackSize(0, 1));
			} else if (inventory.getStackInSlot(0) == null && heldItem != null && heldItem.getItem() instanceof PortalLocationItem && heldItem.stackSize == 1) {
				//our inventory is empty and they have a card for us!
				this.setInventorySlotContents(0, heldItem.copy());
				player.setHeldItem(hand, null);
			} else {
				return false;
			}
		}
		return true;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound item = tag.getCompoundTag("item");
		inventory.setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(item));
		portalLit = tag.getBoolean("portalLit");
		this.markDirty();
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound item = new NBTTagCompound();
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack != null) {
			stack.writeToNBT(item);
		}
		tag.setTag("item", item);
		tag.setBoolean("portalLit", portalLit);
		return tag;
	}
	
	private boolean validateRing() {
		int[][] translation = {
				{0, -1, 0},
				{0, 1, 0},
				{0, 0, -1},
				{0, 0, 1},
				{-1, 0, 0},
				{1, 0, 0},
		};
		EnumFacing facing = worldObj.getBlockState(pos).getValue(PortalControllerBlock.FACING);
		BlockPos start = pos.offset(facing.getOpposite());

		IBlockState currBlock = worldObj.getBlockState(start);
		if (currBlock.getBlock() instanceof PortalFrameBlock) {
			ArrayList<BlockPos> result = findPortalFrameRing(worldObj, start);
			if (result != null) {
				ring = result;
				interior = findInteriorBlocks(ring);
				AULog.debug("Ring formed with %d frame blocks and %d interior blocks", ring.size(), interior.size());
				lightRing();
				return true;
			}
		}

		if (ring != null && !ring.isEmpty()) {
			extinguishPortal();
			extinguishRing();
			ring = new ArrayList<BlockPos>();
			interior = new ArrayList<BlockPos>();
		}
		return false;
	}

	private boolean portalSpaceClear() {
		for (BlockPos location : this.interior){
			if (!worldObj.isAirBlock(location)){
				return false;
			}
		}
		return true;
	}
	
	private void lightRing() {
		for (BlockPos location : this.ring) {
			if (worldObj.getBlockState(location).getBlock() instanceof PortalFrameBlock) {
				worldObj.setBlockState(location, worldObj.getBlockState(location).withProperty(PortalFrameBlock.LIT, true), 3);
			}
		}
	}
	
	private void extinguishRing() {
		for (BlockPos location : this.ring) {
			if (worldObj.getBlockState(location).getBlock() instanceof PortalFrameBlock) {
				worldObj.setBlockState(location, worldObj.getBlockState(location).withProperty(PortalFrameBlock.LIT, false), 3);
			}
		}
	}
	
	private void lightPortal(int meta, double x, double y, double z, int dim, float yaw) {
		AULog.debug("Setting up portal blocks");
		for (BlockPos portal : this.interior) {
			worldObj.setBlockState(portal, AssortedUtilities.Blocks.portalBlock.getDefaultState().withProperty(PortalBlock.AXIS, meta), 3);
			TileEntity tile = worldObj.getTileEntity(portal);
			if (tile instanceof PortalTile) {
				((PortalTile)tile).setDestination(x, y, z, dim, yaw);
			}
		}
		portalLit = true;
	}
	
	private void extinguishPortal() {
		AULog.debug("Destroying portal blocks");
		for (BlockPos portal : this.interior) {
			worldObj.setBlockToAir(portal);
		}
		portalLit = false;
	}
	
	private BlockPos[] getFrameNeighbors(World world, BlockPos position) {
		BlockPos[] result = new BlockPos[6];
		if (world.getBlockState(position.down()).getBlock() instanceof PortalFrameBlock) {result[0] = position.down();}
		if (world.getBlockState(position.up()).getBlock() instanceof PortalFrameBlock) {result[1] = position.up();}
		if (world.getBlockState(position.north()).getBlock() instanceof PortalFrameBlock) {result[2] = position.north();}
		if (world.getBlockState(position.south()).getBlock() instanceof PortalFrameBlock) {result[3] = position.south();}
		if (world.getBlockState(position.west()).getBlock() instanceof PortalFrameBlock) {result[4] = position.west();}
		if (world.getBlockState(position.east()).getBlock() instanceof PortalFrameBlock) {result[5] = position.east();}
		return result;
	}
	
	public ArrayList<PortalControllerTile> getConnectedControllers(World world, BlockPos position) {
		ArrayList<PortalControllerTile> result = new ArrayList<PortalControllerTile>();
		if (world.getBlockState(position.down()).getBlock() instanceof PortalControllerBlock && (world.getBlockState(position.down()).getValue(PortalControllerBlock.FACING) == EnumFacing.UP)) {result.add((PortalControllerTile) world.getTileEntity(position.down()));}
		if (world.getBlockState(position.up()).getBlock() instanceof PortalControllerBlock && (world.getBlockState(position.up()).getValue(PortalControllerBlock.FACING) == EnumFacing.DOWN)) {result.add((PortalControllerTile) world.getTileEntity(position.up()));}
		if (world.getBlockState(position.north()).getBlock() instanceof PortalControllerBlock && (world.getBlockState(position.north()).getValue(PortalControllerBlock.FACING) == EnumFacing.SOUTH)) {result.add((PortalControllerTile) world.getTileEntity(position.north()));}
		if (world.getBlockState(position.south()).getBlock() instanceof PortalControllerBlock && (world.getBlockState(position.south()).getValue(PortalControllerBlock.FACING) == EnumFacing.NORTH)) {result.add((PortalControllerTile) world.getTileEntity(position.south()));}
		if (world.getBlockState(position.west()).getBlock() instanceof PortalControllerBlock && (world.getBlockState(position.west()).getValue(PortalControllerBlock.FACING) == EnumFacing.EAST)) {result.add((PortalControllerTile) world.getTileEntity(position.west()));}
		if (world.getBlockState(position.east()).getBlock() instanceof PortalControllerBlock && (world.getBlockState(position.east()).getValue(PortalControllerBlock.FACING) == EnumFacing.WEST)) {result.add((PortalControllerTile) world.getTileEntity(position.east()));}
		return result;
	}
	
	private ArrayList<BlockPos> findInteriorBlocks(ArrayList<BlockPos> frameBlocks) {
		ArrayList<BlockPos> result = new ArrayList<BlockPos>();
		BlockPos initial = frameBlocks.get(0);
		int minX = initial.getX();
		int maxX = minX;
		int minY = initial.getY();
		int maxY = minY;
		int minZ = initial.getZ();
		int maxZ = minZ;
		for (BlockPos location : frameBlocks) {
			minX = Math.min(minX, location.getX());
			maxX = Math.max(maxX, location.getX());
			minY = Math.min(minY, location.getY());
			maxY = Math.max(maxY, location.getY());
			minZ = Math.min(minZ, location.getZ());
			maxZ = Math.max(maxZ, location.getZ());
		}
		if (minX == maxX) {	
			for (int y = minY + 1; y < maxY; y++) {
				boolean inside = false;
				int sidesFlag = 0;
				for (int z = minZ; z <= maxZ; z++) {
					if (worldObj.getBlockState(new BlockPos(minX, y, z)).getBlock() instanceof PortalFrameBlock && frameBlocks.contains(new BlockPos(minX, y, z))) {
						if (worldObj.getBlockState(new BlockPos(minX, y - 1, z)).getBlock() instanceof PortalFrameBlock && frameBlocks.contains(new BlockPos(minX, y - 1, z))) {sidesFlag = sidesFlag | 1;}
						if (worldObj.getBlockState(new BlockPos(minX, y + 1, z)).getBlock() instanceof PortalFrameBlock && frameBlocks.contains(new BlockPos(minX, y + 1, z))) {sidesFlag = sidesFlag | 2;}
						if (sidesFlag == 3) {
							inside = !inside;
						}
					} else {
						if (inside) {
							result.add(new BlockPos(minX, y, z));
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
					if (worldObj.getBlockState(new BlockPos(x, minY, z)).getBlock() instanceof PortalFrameBlock && frameBlocks.contains(new BlockPos(x, minY, z))) {
						if (worldObj.getBlockState(new BlockPos(x - 1, minY, z)).getBlock() instanceof PortalFrameBlock && frameBlocks.contains(new BlockPos(x - 1, minY, z))) {sidesFlag = sidesFlag | 1;}
						if (worldObj.getBlockState(new BlockPos(x + 1, minY, z)).getBlock() instanceof PortalFrameBlock && frameBlocks.contains(new BlockPos(x + 1, minY, z))) {sidesFlag = sidesFlag | 2;}
						if (sidesFlag == 3) {
							inside = !inside;
						}
					} else {
						if (inside) {
							result.add(new BlockPos(x, minY, z));
						}
					}
				}
			}
		} else if (minZ == maxZ) {
			for (int x = minX + 1; x < maxX; x++) {
				boolean inside = false;
				int sidesFlag = 0;
				for (int y = minY; y <= maxY; y++) {
					if (worldObj.getBlockState(new BlockPos(x, y, minZ)).getBlock() instanceof PortalFrameBlock && frameBlocks.contains(new BlockPos(x, y, minZ))) {
						if (worldObj.getBlockState(new BlockPos(x - 1, y, minZ)).getBlock() instanceof PortalFrameBlock && frameBlocks.contains(new BlockPos(x - 1, y, minZ))) {sidesFlag = sidesFlag | 1;}
						if (worldObj.getBlockState(new BlockPos(x + 1, y, minZ)).getBlock() instanceof PortalFrameBlock && frameBlocks.contains(new BlockPos(x + 1, y, minZ))) {sidesFlag = sidesFlag | 2;}
						if (sidesFlag == 3) {
							inside = !inside;
						}
					} else {
						if (inside) {
							result.add(new BlockPos(x, y, minZ));
						}
					}
				}
			}
		}
		
		return result;
	}

	private ArrayList<BlockPos> findPortalFrameRing(World world, BlockPos origin) {
		ArrayList<BlockPos> result = new ArrayList<BlockPos>();
		BlockPos last = origin;
		BlockPos current = origin;
		int axisFlag = 0;	
		
		while (true) {
			result.add(current);
			Block currBlock = world.getBlockState(current).getBlock();
			if (currBlock instanceof PortalFrameBlock) {
				PortalFrameBlock portalFrame = (PortalFrameBlock)currBlock;
				BlockPos[] neighbors = portalFrame.getFrameNeighbors(world, current);
				BlockPos next = null;
				int neighborCount = 0;
				for (int i = 0; i < neighbors.length; i++) {
					if (neighbors[i] != null) {
						neighborCount++;
						if (!last.equals(neighbors[i])) {
							next = neighbors[i];
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
	
	private ArrayList<BlockPos> findAllConnectedFrames(World world, BlockPos origin) {
		ArrayList<BlockPos> result = new ArrayList<BlockPos>();
		Stack<BlockPos> stack =  new Stack<BlockPos>();
		stack.push(origin);
		
		while (stack.size() > 0) {
			BlockPos current = stack.pop();
			if (!result.contains(current)) {
				BlockPos[] neighbors = getFrameNeighbors(world, current);
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
	
	private ArrayList<PortalControllerTile> findPortalControllers(World world, BlockPos pos) {
		ArrayList<PortalControllerTile> result = new ArrayList<PortalControllerTile>();
		ArrayList<BlockPos> connectedFrames = findAllConnectedFrames(world, pos);
		for (BlockPos location : connectedFrames) {
			result.addAll(getConnectedControllers(world, location));
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

	public void onBreak(EnumFacing facing) {
		if (!worldObj.isRemote) {
			BlockPos start = null;
			if (!ring.isEmpty()) {
				if (portalLit) {
					extinguishPortal();
				}
				start = pos.add(facing.getDirectionVec());
			}
			if (start != null) {
				AULog.debug("Got a starting point");
				ArrayList<PortalControllerTile> controllers = findPortalControllers(worldObj, start);
				AULog.debug("Controllers size was %d", controllers.size());
				if (controllers.size() == 0) {
					extinguishRing();
				}
			}
		}
	}
	
	public void dropAll() {
	    if (!worldObj.isRemote && this.getStackInSlot(0) != null) {
		    ItemStack drop = this.decrStackSize(0, this.getStackInSlot(0).stackSize);
		    EntityItem dropItem = new EntityItem(worldObj, this.pos.getX(), this.pos.getY() + 0.2f, this.pos.getZ(), drop);
		    dropItem.setPickupDelay(10);
		    worldObj.spawnEntityInWorld(dropItem);
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
		AULog.debug("Setting flag for portal update.");
		this.updatePortal = true;
		this.updateCard  = true;
		this.markDirty();
		return inventory.decrStackSize(slot, quantity);
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (inventory.getStackInSlot(index) == null) {return null;}
		return inventory.decrStackSize(index, inventory.getStackInSlot(index).stackSize);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (isItemValidForSlot(slot, stack)) {
			inventory.setInventorySlotContents(slot, stack);
		}
		this.markDirty();
		AULog.debug("Setting flag for portal update.");
		this.updatePortal = true;
		this.updateCard = true;
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
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (stack == null || (slot == 0 && stack.getItem() instanceof PortalLocationItem && stack.stackSize <= 1)) {
			return true;
		}
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
}
