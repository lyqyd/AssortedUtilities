package assortedutilities.common.block;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.tileentity.BasicFlightTile;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BasicFlightBlock extends FlightBlockBase {
	
	public BasicFlightBlock() {
		super(AssortedUtilities.Config.silkTouchRequiredBsc, new ItemStack(Block.getBlockFromName("minecraft:iron_block"), 2));
		setRegistryName("flight-block-basic");
		setUnlocalizedName("flight-block-basic");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BasicFlightTile();
	}
	
}
