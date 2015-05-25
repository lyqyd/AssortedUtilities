package assortedutilities.client.renderer;

import org.lwjgl.opengl.GL11;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.tileentity.PortalControllerTile;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

public class PortalControllerRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

	static float texPixel=1.0f/16f;
	static float depthInset = 0.5f;
	static float widthOffset = 0.5f/16f;
	static float heightOffset = 5.0f/16f;
	
	public void drawController(boolean displayCard) {
		Tessellator tessellator=Tessellator.instance;
		
		this.bindTexture(new ResourceLocation("assortedutilities", "textures/blocks/portalController_face.png"));
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0f, 0f, 1f);
		//inset face
		tessellator.addVertexWithUV( texPixel,      texPixel,      texPixel + depthInset, texPixel,    1f-texPixel);
		tessellator.addVertexWithUV( texPixel,      1f - texPixel, texPixel + depthInset, texPixel,    texPixel);
		tessellator.addVertexWithUV( 1f - texPixel, 1f - texPixel, texPixel + depthInset, 1f-texPixel, texPixel);
		tessellator.addVertexWithUV( 1f - texPixel, texPixel,      texPixel + depthInset, 1f-texPixel, 1f-texPixel);

		
		//bottom lip front
		tessellator.addVertexWithUV( 0f,          0f,          0.001f + depthInset, 0f,           0f);
		tessellator.addVertexWithUV( texPixel,    texPixel,    0.001f + depthInset, texPixel,     texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    0.001f + depthInset, 1f-texPixel,  texPixel);
		tessellator.addVertexWithUV( 1f,          0f,          0.001f + depthInset, 1f,           0f);
		//top lip front
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, 0.001f + depthInset, texPixel,     1f-texPixel);
		tessellator.addVertexWithUV( 0f,          1f,          0.001f + depthInset, 0f,           1f);
		tessellator.addVertexWithUV( 1f,          1f,          0.001f + depthInset, 1f,           1f);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, 0.001f + depthInset, 1f-texPixel,  1f-texPixel);
		//right lip front
		tessellator.addVertexWithUV( 0f,          0f,          0.001f + depthInset, 0f,           0f);
		tessellator.addVertexWithUV( 0f,          1f,          0.001f + depthInset, 0f,           1f);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, 0.001f + depthInset, texPixel,     1f-texPixel);
		tessellator.addVertexWithUV( texPixel,    texPixel,    0.001f + depthInset, texPixel,     texPixel);
		//left lip front
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    0.001f + 0.5f,       1f-texPixel,  texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, 0.001f + 0.5f,       1f-texPixel,  1f-texPixel);
		tessellator.addVertexWithUV( 1f,          1f,          0.001f + 0.5f,       1f,           1f);
		tessellator.addVertexWithUV( 1f,          0f,          0.001f + 0.5f,       1f,           0f);

		//bottom lip inside
		tessellator.setNormal(0f,1f,0f);
		tessellator.addVertexWithUV( texPixel,    texPixel,    0f + depthInset,       texPixel,    1f);
		tessellator.addVertexWithUV( texPixel,    texPixel,    texPixel + depthInset, texPixel,    1f-texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    texPixel + depthInset, 1f-texPixel, 1f-texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    0f + depthInset,       1f-texPixel, 1f);
		//top lip inside
		tessellator.setNormal(0f,-1f,0f);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, texPixel + depthInset, texPixel,    texPixel);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, 0f + depthInset,       texPixel,    0f);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, 0f + depthInset,       1f-texPixel, 0f);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, texPixel + depthInset, 1f-texPixel, texPixel);
		//right lip inside
		tessellator.setNormal(1f,0f,0f);
		tessellator.addVertexWithUV( texPixel,    texPixel,    0f + depthInset,       1f-texPixel, texPixel);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, 0f + depthInset,       1f-texPixel, 1f-texPixel);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, texPixel + depthInset, 1f,          1f-texPixel);
		tessellator.addVertexWithUV( texPixel,    texPixel,    texPixel + depthInset, 1f,          texPixel);
		//left lip inside
		tessellator.setNormal(-1f,1f,0f);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    texPixel + depthInset, 1f,          texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, texPixel + depthInset, 1f,          1f-texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, 0f + depthInset,       1f-texPixel, 1f-texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    0f + depthInset,       1f-texPixel, texPixel);
		
		tessellator.draw();
		
		this.bindTexture(new ResourceLocation("assortedutilities", "textures/blocks/portalController_side.png"));
		
		tessellator.startDrawingQuads();
		//l/r  vertical  b/f
		//top side
		tessellator.setNormal(0f,1f,0f);
		tessellator.addVertexWithUV(0f, 1f, 1f - depthInset, 0f,         1f); //lf
		tessellator.addVertexWithUV(0f, 1f, 1f,              depthInset, 1f); //lr
		tessellator.addVertexWithUV(1f, 1f, 1f,              depthInset, 0f); //rr
		tessellator.addVertexWithUV(1f, 1f, 1f - depthInset, 0f,         0f); //rf
		//bottom side
		tessellator.setNormal(0f,-1f,0f);
		tessellator.addVertexWithUV(0f, 0f, 1f,              depthInset, 1f); //lr
		tessellator.addVertexWithUV(0f, 0f, 1f - depthInset, 0f,         1f); //lf
		tessellator.addVertexWithUV(1f, 0f, 1f - depthInset, 0f,         0f); //rf
		tessellator.addVertexWithUV(1f, 0f, 1f,              depthInset, 0f); //rr
		//right side
		tessellator.setNormal(-1f,0f,0f);
		tessellator.addVertexWithUV(1f, 0f, 1f,              0f,         1f);
		tessellator.addVertexWithUV(1f, 0f, 1f - depthInset, depthInset, 1f);
		tessellator.addVertexWithUV(1f, 1f, 1f - depthInset, depthInset, 0f);
		tessellator.addVertexWithUV(1f, 1f, 1f,              0f,         0f);
		//left side
		tessellator.setNormal(1f,0f,0f);
		tessellator.addVertexWithUV(0f, 0f, 1f - depthInset, depthInset, 1f);
		tessellator.addVertexWithUV(0f, 0f, 1f,              0f,         1f);
		tessellator.addVertexWithUV(0f, 1f, 1f,              0f,         0f);
		tessellator.addVertexWithUV(0f, 1f, 1f - depthInset, depthInset, 0f);
		
		tessellator.draw();

		if (displayCard) {
			//draw location card sticking out of controller
			float backEnd = 5f/16f;
			float frontEnd = 9f/16f;

			this.bindTexture(new ResourceLocation("assortedutilities", "textures/items/locationCard.png"));
			
			tessellator.startDrawingQuads();	
			//l/r  vertical  b/f
			//top side
			tessellator.setNormal(0f,1f,0f);
			tessellator.addVertexWithUV(0.5f - widthOffset, 1f  -heightOffset, backEnd,  2f/16f,  4f/16f); //lf
			tessellator.addVertexWithUV(0.5f - widthOffset, 1f - heightOffset, frontEnd, 14f/16f, 4f/16f); //lr
			tessellator.addVertexWithUV(0.5f + widthOffset, 1f - heightOffset, frontEnd, 14f/16f, 3f/16f); //rr
			tessellator.addVertexWithUV(0.5f + widthOffset, 1f - heightOffset, backEnd,  2f/16f,  3f/16f); //rf
			//bottom side
			tessellator.setNormal(0f,-1f,0f);
			tessellator.addVertexWithUV(0.5f - widthOffset, 0f + heightOffset, frontEnd, 14f/16f, 4f/16f); //lr
			tessellator.addVertexWithUV(0.5f - widthOffset, 0f + heightOffset, backEnd,  2f/16f,  4f/16f); //lf
			tessellator.addVertexWithUV(0.5f + widthOffset, 0f + heightOffset, backEnd,  2f/16f,  3f/16f); //rf
			tessellator.addVertexWithUV(0.5f + widthOffset, 0f + heightOffset, frontEnd, 14f/16f, 3f/16f); //rr
			//left side
			tessellator.setNormal(-1f,0f,0f);
			tessellator.addVertexWithUV(0.5f + widthOffset, 0f + heightOffset, frontEnd, 2f/16f,  11f/16f); //front bottom
			tessellator.addVertexWithUV(0.5f + widthOffset, 0f + heightOffset, backEnd,  2f/16f,  3f/16f);  //rear bottom
			tessellator.addVertexWithUV(0.5f + widthOffset, 1f - heightOffset, backEnd,  14f/16f, 3f/16f);  //rear top
			tessellator.addVertexWithUV(0.5f + widthOffset, 1f - heightOffset, frontEnd, 14f/16f, 11f/16f); //front top
			//right side
			tessellator.setNormal(1f,0f,0f);
			tessellator.addVertexWithUV(0.5f - widthOffset, 0f + heightOffset, backEnd,  2f/16f, 3f/16f);   //rear bottom
			tessellator.addVertexWithUV(0.5f - widthOffset, 0f + heightOffset, frontEnd, 2f/16f,  11f/16f); //front bottom
			tessellator.addVertexWithUV(0.5f - widthOffset, 1f - heightOffset, frontEnd, 14f/16f, 11f/16f); //front top
			tessellator.addVertexWithUV(0.5f - widthOffset, 1f - heightOffset, backEnd,  14f/16f, 3f/16f);  //rear top
			//back side
			tessellator.setNormal(0f, 0f, 1f);
			tessellator.addVertexWithUV(0.5f - widthOffset, 0f + heightOffset, backEnd,  2f/16f,  4f/16f);
			tessellator.addVertexWithUV(0.5f - widthOffset, 1f - heightOffset, backEnd,  2f/16f,  3f/16f);
			tessellator.addVertexWithUV(0.5f + widthOffset, 1f - heightOffset, backEnd,  14f/16f, 3f/16f);
			tessellator.addVertexWithUV(0.5f + widthOffset, 0f + heightOffset, backEnd,  14f/16f, 4f/16f);
			
			tessellator.draw();
			
		}
	}
	
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
		
		PortalControllerTile te=(PortalControllerTile)tile;
		Tessellator tessellator=Tessellator.instance;
		
		int bx=te.xCoord, by=te.yCoord, bz=te.zCoord;
		
		World world=te.getWorldObj();
		
		float brightness=AssortedUtilities.Blocks.portalControllerBlock.getLightValue(world, bx, by, bz);
		int light=world.getLightBrightnessForSkyBlocks(bx,by,bz,0);				
		
		tessellator.setColorOpaque_F(brightness,brightness,brightness);		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(light&0xffff),(float)(light>>16));
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y,(float)z);
		Vec3 translation = te.getTranslation();
		GL11.glTranslated(translation.xCoord, translation.yCoord, translation.zCoord);
		GL11.glRotatef(te.getYRotation(), 0f, 1f, 0f);
		GL11.glRotatef(te.getXRotation(), 1f, 0f, 0f);
		GL11.glTranslated(translation.xCoord * -1d, translation.yCoord * -1d, translation.zCoord * -1d);
		
		drawController(te.getStackInSlot(0) != null);
		
		GL11.glPopMatrix();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();		

		if (type==ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glTranslatef(.5f,0,.5f);
			GL11.glRotatef(90,0f,1f,0f);
			GL11.glTranslatef(-.5f,0,-.5f);			
		}
		else if (type!=ItemRenderType.ENTITY)
		{
			GL11.glTranslatef(.5f,0,.5f);
			GL11.glRotatef(180,0f,1f,0f);
			GL11.glTranslatef(-.5f,-.1f,-.5f);
		}
		else
		{
			//only entity left
			GL11.glTranslatef(-.5f,-.5f,-.5f);
		}
		
		//have to draw the sides ourselves here!

		if (type!=ItemRenderType.INVENTORY)
		{	
			this.bindTexture(new ResourceLocation("assortedutilities", "textures/blocks/portalController_back.png"));
			
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glNormal3f(0f,-1f,0f);
				GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(0f, 0f, 1f);
				GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(1f, 0f, 1f);
				GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(1f, 1f, 1f);
				GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(0f, 1f, 1f);
			GL11.glEnd();
			
		}
		
		drawController(false);
			
		GL11.glPopMatrix();
	}

}
