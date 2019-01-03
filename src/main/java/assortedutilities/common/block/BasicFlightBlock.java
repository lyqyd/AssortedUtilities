package assortedutilities.common.block;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.tileentity.AdvancedFlightTile;
import assortedutilities.common.tileentity.BasicFlightTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BasicFlightBlock extends FlightBlockBase {
	
	public BasicFlightBlock() {
		super(AssortedUtilities.Config.silkTouchRequiredBsc, new ItemStack(Block.getBlockFromName("minecraft:iron_block"), 2));
		setRegistryName("flight-block-basic");
		setUnlocalizedName("flight-block-basic");
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BasicFlightTile();
	}
	
}
