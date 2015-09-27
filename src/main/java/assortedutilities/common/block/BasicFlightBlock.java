package assortedutilities.common.block;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.tileentity.BasicFlightTile;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class BasicFlightBlock extends FlightBlockBase {
	
	public BasicFlightBlock() {
		super(AssortedUtilities.Config.silkTouchRequiredBsc, new ItemStack(Item.getItemFromBlock((Block)Block.blockRegistry.getObject("iron_block")), 2));
		GameRegistry.registerBlock(this, "flightBlockBasic");
		GameRegistry.registerTileEntity(BasicFlightTile.class, "flightBlockBasic");
		setBlockName("assortedutilities.bscflightblock");
		this.setBlockTextureName("assortedutilities:basic_flightblock");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(this.textureName);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BasicFlightTile();
	}
	
}
