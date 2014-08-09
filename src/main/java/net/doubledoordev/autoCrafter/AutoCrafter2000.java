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

package net.doubledoordev.autoCrafter;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.doubledoordev.autoCrafter.blocks.AutoCrafterBlock;
import net.doubledoordev.autoCrafter.buildcraft.BuildcraftHelper;
import net.doubledoordev.autoCrafter.nei.NEIHelper;
import net.doubledoordev.autoCrafter.network.CounterMessage;
import net.doubledoordev.autoCrafter.network.GuiHandler;
import net.doubledoordev.autoCrafter.network.NEIMessage;
import net.doubledoordev.autoCrafter.network.RedstoneModeMessage;
import net.doubledoordev.autoCrafter.util.Config;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static net.doubledoordev.autoCrafter.util.Constants.*;

/**
 * The main mod class
 *
 * @author Dries007
 */
@Mod(modid = MODID, useMetadata = true)
public class AutoCrafter2000
{
    @Mod.Instance(MODID)
    public static AutoCrafter2000 instance;

    @Mod.Metadata(MODID)
    private ModMetadata metadata;

    private SimpleNetworkWrapper snw;
    private Config               config;
    private Logger               logger;

    @Mod.EventHandler()
    public void event(FMLPreInitializationEvent event) throws IOException
    {
        logger = event.getModLog();

        config = new Config(event.getSuggestedConfigurationFile());

        new AutoCrafterBlock();

        int id = 0;
        snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        snw.registerMessage(RedstoneModeMessage.Handler.class, RedstoneModeMessage.class, id++, Side.SERVER);
        snw.registerMessage(RedstoneModeMessage.Handler.class, RedstoneModeMessage.class, id++, Side.CLIENT);
        snw.registerMessage(NEIMessage.Handler.class, NEIMessage.class, id++, Side.SERVER);
        snw.registerMessage(CounterMessage.Handler.class, CounterMessage.class, id++, Side.CLIENT);
    }

    @Mod.EventHandler()
    public void event(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        if (Loader.isModLoaded(BC_MODID)) BuildcraftHelper.init();
        if (Loader.isModLoaded(NEI_MODID)) NEIHelper.init();
    }

    public static String getVersion()
    {
        return instance.metadata.version;
    }

    public static Config getConfig()
    {
        return instance.config;
    }

    public static Logger getLogger()
    {
        return instance.logger;
    }

    public static SimpleNetworkWrapper getSnw()
    {
        return instance.snw;
    }
}
