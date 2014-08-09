/*
 * Copyright (c) 2014, DoubleDoorDevelopment
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.doubledoordev.autoCrafter.guis;

import net.doubledoordev.autoCrafter.AutoCrafter2000;
import net.doubledoordev.autoCrafter.guis.parts.GuiButtonItemStack;
import net.doubledoordev.autoCrafter.network.RedstoneModeMessage;
import net.doubledoordev.autoCrafter.tile.AutoCrafterTile;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * The gui for the autocrafter.
 *
 * @author Dries007
 */
public class AutoCrafterGui extends GuiContainer
{
    private static final int              ID_REDSTONE              = 0;
    private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("autocrafter2000:textures/gui/autocraftingtable.png");

    public GuiButtonItemStack redstonebutton;

    public AutoCrafterGui(EntityPlayer player, World world, int x, int y, int z)
    {
        super(new AutoCrafterContainer(player, (AutoCrafterTile) world.getTileEntity(x, y, z)));
        this.ySize = 230;
    }

    public void initGui()
    {
        super.initGui();
        //noinspection unchecked
        this.buttonList.add(redstonebutton = new GuiButtonItemStack(ID_REDSTONE, width / 2 + 64, height / 2 - 111, new ItemStack(Items.redstone), new ItemStack(Blocks.redstone_torch), new ItemStack(Items.gunpowder)));
        setRedstonebutton();
    }

    public void setRedstonebutton()
    {
        if (redstonebutton == null) return;
        redstonebutton.item = ((AutoCrafterContainer) this.inventorySlots).tile.redstoneMode;
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.id == ID_REDSTONE)
        {
            AutoCrafterTile tile = ((AutoCrafterContainer) this.inventorySlots).tile;
            redstonebutton.item = tile.redstoneMode = (tile.redstoneMode + 1) % 3;

            AutoCrafter2000.getSnw().sendToServer(new RedstoneModeMessage(tile));
        }
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString("Recipe", 28, 6, 4210752);
        this.fontRendererObj.drawString("Input", 25, 73, 4210752);
        this.fontRendererObj.drawString("Output", 98, 73, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 92, 4210752);
        this.fontRendererObj.drawString("Crafts: " + ((AutoCrafterContainer) this.inventorySlots).tile.crafts, 75, 6, 4210752);

        if (redstonebutton.xPosition < par1 && par1 < redstonebutton.xPosition + 20)
        {
            if (redstonebutton.yPosition < par2 && par2 < redstonebutton.yPosition + 20)
            {
                String s = "";
                switch (redstonebutton.item)
                {
                    case 0:
                        s = "Redstone disables";
                        break;
                    case 1:
                        s = "Redstone enables";
                        break;
                    case 2:
                        s = "Redstone ignored";
                        break;
                }
                this.drawCreativeTabHoveringText(s, par1 - this.guiLeft, par2 - this.guiTop);
                RenderHelper.enableGUIStandardItemLighting();
                //this.fontRenderer.drawString("StringTest", 50, 50, 4210752);
            }
        }
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}
