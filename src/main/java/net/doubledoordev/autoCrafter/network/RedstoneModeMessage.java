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

package net.doubledoordev.autoCrafter.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.doubledoordev.autoCrafter.guis.AutoCrafterGui;
import net.doubledoordev.autoCrafter.tile.AutoCrafterTile;
import net.minecraft.client.Minecraft;

/**
 * @author Dries007
 */
public class RedstoneModeMessage implements IMessage
{
    int x, y, z, mode;

    public RedstoneModeMessage(AutoCrafterTile tile)
    {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
        mode = tile.redstoneMode;
    }

    public RedstoneModeMessage()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(mode);
    }

    public static class Handler implements IMessageHandler<RedstoneModeMessage, IMessage>
    {
        @Override
        public IMessage onMessage(RedstoneModeMessage message, MessageContext ctx)
        {
            if (ctx.side.isServer())
            {
                ((AutoCrafterTile) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z)).redstoneMode = message.mode;
            }
            else
            {
                ((AutoCrafterTile) Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z)).redstoneMode = message.mode;
                if (Minecraft.getMinecraft().currentScreen instanceof AutoCrafterGui) ((AutoCrafterGui) Minecraft.getMinecraft().currentScreen).setRedstonebutton();
            }
            return null;
        }
    }
}
