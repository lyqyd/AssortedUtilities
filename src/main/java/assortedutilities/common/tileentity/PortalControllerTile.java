package assortedutilities.common.tileentity;

import java.util.ArrayList;
import java.util.Stack;

import assortedutilities.common.block.PortalControllerBlock;
import assortedutilities.common.block.PortalFrameBlock;
import assortedutilities.common.util.AULog;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class PortalControllerTile extends TileEntity {
	
	private ArrayList<ChunkCoordinates> ring = new ArrayList<ChunkCoordinates>();
	private ArrayList<ChunkCoordinates> interior = new ArrayList<ChunkCoordinates>();
	
	@Override
	public void updateEntity() {
		
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (!worldObj.isRemote) {
			if (validateRing()) {
				lightRing(ring);
			}
		}
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
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
				return true;
			}
		}
		if (ring != null && !ring.isEmpty()) {
			extinguishRing(ring);
			ring = new ArrayList<ChunkCoordinates>();
			interior = new ArrayList<ChunkCoordinates>();
		}
		return false;
	}
	
	private void lightRing(ArrayList<ChunkCoordinates> locations) {
		for (ChunkCoordinates location : locations) {
			worldObj.setBlockMetadataWithNotify(location.posX, location.posY, location.posZ, 1, 3);
		}
	}
	
	private void extinguishRing(ArrayList<ChunkCoordinates> locations) {
		for (ChunkCoordinates location : locations) {
			worldObj.setBlockMetadataWithNotify(location.posX, location.posY, location.posZ, 0, 3);
		}
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
			if (validateRing()) {
				lightRing(ring);
			}
		}
	}
	
	public void blockRemoved() {
		if (!worldObj.isRemote) {
			if (validateRing()) {
				lightRing(ring);
			}
		}
	}

	public void onPlacement() {
		if (!worldObj.isRemote) {
			if (validateRing()) {
				lightRing(ring);
			}
		}
	}

	public void onBreak() {
		AULog.info("On break!");
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
					extinguishRing(ring);
				}
			}
		}
	}
	
}
