///*
// * Copyright (c) 2014, DoubleDoorDevelopment
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// *  Redistributions of source code must retain the above copyright notice, this
// *   list of conditions and the following disclaimer.
// *
// *  Redistributions in binary form must reproduce the above copyright notice,
// *   this list of conditions and the following disclaimer in the documentation
// *   and/or other materials provided with the distribution.
// *
// *  Neither the name of the project nor the names of its
// *   contributors may be used to endorse or promote products derived from
// *   this software without specific prior written permission.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//
//package net.doubledoordev.autoCrafter.network;
//
//import ccm.autoCrafter2000.guis.AutoCrafterContainer;
//import ccm.autoCrafter2000.guis.AutoCrafterGui;
//import ccm.autoCrafter2000.tile.AutoCrafterTile;
//import ccm.nucleumOmnium.helpers.NetworkHelper;
//import cpw.mods.fml.common.FMLCommonHandler;
//import cpw.mods.fml.common.network.IPacketHandler;
//import cpw.mods.fml.common.network.Player;
//import net.minecraft.client.Minecraft;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.network.INetworkManager;
//import net.minecraft.network.packet.Packet250CustomPayload;
//import net.minecraft.tileentity.TileEntity;
//
//import static ccm.autoCrafter2000.util.Constants.CHANNEL_NEI;
//import static ccm.autoCrafter2000.util.Constants.CHANNEL_RMU;
//
///**
// * Packets!
// * Allows the client to update the redstone state
// * Allows the server to update the craft count
// *
// * @author Dries007
// */
//public class PacketHandler implements IPacketHandler
//{
//    @Override
//    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
//    {
//        if (packet.channel.equalsIgnoreCase(CHANNEL_RMU))
//        {
//            String[] split = new String(packet.data).split(";");
//            TileEntity tileEntity = ((EntityPlayer) player).getEntityWorld().getBlockTileEntity(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
//            if (tileEntity instanceof AutoCrafterTile)
//            {
//                ((AutoCrafterTile) tileEntity).redstoneMode = Integer.parseInt(split[3]);
//                if (FMLCommonHandler.instance().getEffectiveSide().isClient() && split.length > 4) ((AutoCrafterTile) tileEntity).crafts = Integer.parseInt(split[4]);
//                try
//                {
//                    if (FMLCommonHandler.instance().getSide().isClient())
//                    {
//                        if (Minecraft.getMinecraft().currentScreen instanceof AutoCrafterGui)
//                        {
//                            ((AutoCrafterGui) Minecraft.getMinecraft().currentScreen).setRedstonebutton();
//                        }
//                    }
//                }
//                catch (Exception e)
//                {
//                    // Meh... might be some client issues.
//                }
//            }
//        }
//
//        if (packet.channel.equalsIgnoreCase(CHANNEL_NEI) && ((EntityPlayer) player).openContainer instanceof AutoCrafterContainer)
//        {
//            NBTTagCompound root = NetworkHelper.byteArrayToNBT(packet.data);
//
//            for (int i = 0; i < AutoCrafterTile.MATRIX; i++)
//            {
//                ItemStack itemStack = ItemStack.loadItemStackFromNBT(root.getCompoundTag(String.valueOf(i)));
//                Slot slot = ((EntityPlayer) player).openContainer.getSlot(i + 1);
//                slot.putStack(itemStack);
//                slot.onSlotChanged();
//            }
//            ((EntityPlayer) player).openContainer.onCraftMatrixChanged(((AutoCrafterContainer) ((EntityPlayer) player).openContainer).tile);
//        }
//    }
//}
