package assortedutilities.common.block;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.tileentity.AdvancedFlightTile;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class AdvancedFlightBlock extends FlightBlockBase {

	public IIcon[] icons = new IIcon[6];
	
	public AdvancedFlightBlock() {
		super(AssortedUtilities.Config.silkTouchRequiredAdv, new ItemStack(Item.getItemFromBlock((Block)Block.blockRegistry.getObject("diamond_block")), 2));
		GameRegistry.registerBlock(this, "flightBlockAdvanced");
		GameRegistry.registerTileEntity(AdvancedFlightTile.class, "flightBlockAdvanced");
		setBlockName("assortedutilities.advflightblock");
		this.setBlockTextureName("assortedutilities:flightblock");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		for (int i = 0; i < 6; i++) {
			this.icons[i] = register.registerIcon(this.textureName + "_" + Math.min(i, 2));
		}
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return this.icons[side];
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new AdvancedFlightTile();
	}
	
}
