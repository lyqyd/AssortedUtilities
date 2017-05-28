package assortedutilities.common.block;

import java.util.ArrayList;
import java.util.Stack;

import assortedutilities.common.tileentity.PortalControllerTile;
import assortedutilities.common.util.AULog;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class PortalFrameBlock extends Block {

	public static final PropertyBool LIT = PropertyBool.create("lit");

	public PortalFrameBlock() {
		super(Material.IRON);
		setHardness(0.5F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(PortalFrameBlock.LIT, false));
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		setRegistryName("portal-frame");
		setUnlocalizedName("portal-frame");
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
	
	private ArrayList<PortalControllerTile> findPortalControllers(World world, BlockPos origin) {
		ArrayList<PortalControllerTile> result = new ArrayList<PortalControllerTile>();
		ArrayList<BlockPos> connectedFrames = findAllConnectedFrames(world, origin);
		for (BlockPos location : connectedFrames) {
			result.addAll(getConnectedControllers(world, location));
		}
		
		return result;
	}
	
	/*private ArrayList<PortalControllerTile> findPortalControllers(World world, int x, int y, int z) {
		BlockPos origin = new BlockPos(x, y, z);
		AULog.debug("Origin: %s", origin.toString());
		ArrayList<PortalControllerTile> result = new ArrayList<PortalControllerTile>();
		BlockPos last = origin;
		BlockPos current = origin;
		int axisFlag = 0;	
		
		while (true) {
			AULog.debug("Adding current block to list: %s", current.toString());
			Block currBlock = world.getBlock(current.posX, current.posY, current.posZ);
			if (currBlock instanceof PortalFrameBlock) {
				AULog.debug("Current block is instance of PFB");
				PortalFrameBlock portalFrame = (PortalFrameBlock)currBlock;
				result.addAll(portalFrame.getConnectedControllers(world, current.posX, current.posY, current.posZ));
				BlockPos[] neighbors = portalFrame.getFrameNeighbors(world, current.posX, current.posY, current.posZ);
				BlockPos next = null;
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

	private BlockPos[] getNeighborCoordinates(BlockPos origin) {
		return new BlockPos[] {
				origin.down(), origin.up(),
				origin.north(), origin.south(),
				origin.west(), origin.east()
		};
	}
	
	public BlockPos[] getFrameNeighbors(World world, BlockPos origin) {
		BlockPos[] coords = getNeighborCoordinates(origin);
		BlockPos[] result = new BlockPos[6];
		int i = 0;
		for (BlockPos pos : coords) {
			if (world.getBlockState(pos).getBlock() instanceof PortalFrameBlock) {
				result[i] = pos;
			} else {
				result[i] = null;
			}
			i++;
		}
		return result;
	}
	
	public int getFrameNeighborCount(World world, BlockPos origin) {
		int result = 0;
		BlockPos[] coords = getNeighborCoordinates(origin);
		for (int i = 0; i < 6; i++) {
			if (world.getBlockState(coords[i]).getBlock() instanceof PortalFrameBlock) {result++;}
		}
		return result;
	}
	
	public ArrayList<PortalControllerTile> getConnectedControllers(World world, BlockPos origin) {
		ArrayList<PortalControllerTile> result = new ArrayList<PortalControllerTile>();
		BlockPos[] coords = getNeighborCoordinates(origin);
		for (int i = 0; i < 6; i++) {
			IBlockState state = world.getBlockState(coords[i]);
			if (state.getBlock() instanceof PortalControllerBlock) {
				AULog.debug("i: %d index: %d", i, state.getValue(PortalControllerBlock.FACING).getIndex());
			}
			if (state.getBlock() instanceof PortalControllerBlock && state.getValue(PortalControllerBlock.FACING).getIndex() == i) {result.add((PortalControllerTile) world.getTileEntity(coords[i]));}
		}
		return result;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		ArrayList<PortalControllerTile> controllers = this.findPortalControllers(world, pos);
		for (PortalControllerTile controller : controllers) {
			controller.blockAdded();
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		ArrayList<PortalControllerTile> controllers = this.findPortalControllers(world, pos);
		for (PortalControllerTile controller : controllers) {
			controller.blockRemoved();
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return this.getDefaultState().withProperty(PortalFrameBlock.LIT, meta == 1);
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(PortalFrameBlock.LIT) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, PortalFrameBlock.LIT);
	}
}
