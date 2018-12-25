package assortedutilities.common.block;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.tileentity.AdvancedFlightTile;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class AdvancedFlightBlock extends FlightBlockBase {
	
	public AdvancedFlightBlock() {
		super(AssortedUtilities.Config.silkTouchRequiredAdv, new ItemStack(Item.getItemFromBlock(Block.getBlockFromName("minecraft:diamond_block")), 2));
		setRegistryName("flight-block-advanced");
		setUnlocalizedName("flight-block-advanced");
		GameRegistry.registerTileEntity(AdvancedFlightTile.class, new ResourceLocation("assortedutilities","flightBlockAdvanced"));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new AdvancedFlightTile();
	}
	
}
