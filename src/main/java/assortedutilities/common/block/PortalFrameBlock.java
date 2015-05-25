package assortedutilities.common.block;

import java.util.ArrayList;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import assortedutilities.common.tileentity.ObliteratorTile;
import assortedutilities.common.tileentity.PortalControllerTile;
import assortedutilities.common.util.AULog;
import cpw.mods.fml.common.registry.GameRegistry;

public class PortalFrameBlock extends Block {
	
	private IIcon[] icons = new IIcon[2];

	public PortalFrameBlock() {
		super(Material.iron);
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.tabTransport);
		GameRegistry.registerBlock(this, "portalFrame");
		setBlockName("assortedutilities.portalFrame");
		this.setBlockTextureName("assortedutilities:portalFrame");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		for (int i = 0; i < 2; i++) {
			this.icons[i] = register.registerIcon(this.textureName + "_" + i);
		}
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return this.icons[meta];
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
	
	/*private ArrayList<PortalControllerTile> findPortalControllers(World world, int x, int y, int z) {
		ChunkCoordinates origin = new ChunkCoordinates(x, y, z);
		AULog.debug("Origin: %s", origin.toString());
		ArrayList<PortalControllerTile> result = new ArrayList<PortalControllerTile>();
		ChunkCoordinates last = origin;
		ChunkCoordinates current = origin;
		int axisFlag = 0;	
		
		while (true) {
			AULog.debug("Adding current block to list: %s", current.toString());
			Block currBlock = world.getBlock(current.posX, current.posY, current.posZ);
			if (currBlock instanceof PortalFrameBlock) {
				AULog.debug("Current block is instance of PFB");
				PortalFrameBlock portalFrame = (PortalFrameBlock)currBlock;
				result.addAll(portalFrame.getConnectedControllers(world, current.posX, current.posY, current.posZ));
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
				if (neighborCount != 2 || axisFlag == 7) { break; }
				if (origin.equals(next)) { break; }
				last = current;
				current = next;
			} else {
				AULog.debug("Panic! Current Block is not instance of PFB!");
				break;
			}
		}
		
		return result;
	}*/
	
	public ChunkCoordinates[] getFrameNeighbors(World world, int x, int y, int z) {
		ChunkCoordinates[] result = new ChunkCoordinates[6];
		if (world.getBlock(x, y - 1, z) instanceof PortalFrameBlock) {result[0] = new ChunkCoordinates(x, y - 1, z);}
		if (world.getBlock(x, y + 1, z) instanceof PortalFrameBlock) {result[1] = new ChunkCoordinates(x, y + 1, z);}
		if (world.getBlock(x, y, z - 1) instanceof PortalFrameBlock) {result[2] = new ChunkCoordinates(x, y, z - 1);}
		if (world.getBlock(x, y, z + 1) instanceof PortalFrameBlock) {result[3] = new ChunkCoordinates(x, y, z + 1);}
		if (world.getBlock(x - 1, y, z) instanceof PortalFrameBlock) {result[4] = new ChunkCoordinates(x - 1, y, z);}
		if (world.getBlock(x + 1, y, z) instanceof PortalFrameBlock) {result[5] = new ChunkCoordinates(x + 1, y, z);}
		return result;
	}
	
	public int getFrameNeighborCount(World world, int x, int y, int z) {
		int result = 0;
		if (world.getBlock(x, y - 1, z) instanceof PortalFrameBlock) {result++;}
		if (world.getBlock(x, y + 1, z) instanceof PortalFrameBlock) {result++;}
		if (world.getBlock(x, y, z - 1) instanceof PortalFrameBlock) {result++;}
		if (world.getBlock(x, y, z + 1) instanceof PortalFrameBlock) {result++;}
		if (world.getBlock(x - 1, y, z) instanceof PortalFrameBlock) {result++;}
		if (world.getBlock(x + 1, y, z) instanceof PortalFrameBlock) {result++;}
		return result;
	}
	
	public ArrayList<PortalControllerTile> getConnectedControllers(World world, int x, int y, int z) {
		ArrayList<PortalControllerTile> result = new ArrayList<PortalControllerTile>();
		if (world.getBlock(x, y - 1, z) instanceof PortalControllerBlock && (world.getBlockMetadata(x, y - 1, z) & 7) == 1) {result.add((PortalControllerTile) world.getTileEntity(x, y - 1, z));}
		if (world.getBlock(x, y + 1, z) instanceof PortalControllerBlock && (world.getBlockMetadata(x, y + 1, z) & 7) == 0) {result.add((PortalControllerTile) world.getTileEntity(x, y + 1, z));}
		if (world.getBlock(x, y, z - 1) instanceof PortalControllerBlock && (world.getBlockMetadata(x, y, z - 1) & 7) == 3) {result.add((PortalControllerTile) world.getTileEntity(x, y, z - 1));}
		if (world.getBlock(x, y, z + 1) instanceof PortalControllerBlock && (world.getBlockMetadata(x, y, z + 1) & 7) == 2) {result.add((PortalControllerTile) world.getTileEntity(x, y, z + 1));}
		if (world.getBlock(x - 1, y, z) instanceof PortalControllerBlock && (world.getBlockMetadata(x - 1, y, z) & 7) == 5) {result.add((PortalControllerTile) world.getTileEntity(x - 1, y, z));}
		if (world.getBlock(x + 1, y, z) instanceof PortalControllerBlock && (world.getBlockMetadata(x + 1, y, z) & 7) == 4) {result.add((PortalControllerTile) world.getTileEntity(x + 1, y, z));}
		return result;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		ArrayList<PortalControllerTile> controllers = this.findPortalControllers(world, x, y, z);
		for (PortalControllerTile controller : controllers) {
			controller.blockAdded();
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		ArrayList<PortalControllerTile> controllers = this.findPortalControllers(world, x, y, z);
		for (PortalControllerTile controller : controllers) {
			controller.blockRemoved();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}
	
}
