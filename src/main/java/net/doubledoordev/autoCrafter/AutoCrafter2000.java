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

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
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
import net.doubledoordev.d3core.util.ID3Mod;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

import static net.doubledoordev.autoCrafter.util.Constants.*;

/**
 * The main mod class
 *
 * @author Dries007
 */
@Mod(modid = MODID, canBeDeactivated = false)
public class AutoCrafter2000 implements ID3Mod
{
    @Mod.Instance(MODID)
    public static AutoCrafter2000 instance;

    private SimpleNetworkWrapper snw;

    public int     craftDelay           = 10;
    public boolean updateCraftCountLive = true;
    private Configuration configuration;
    private Logger logger;

    @Mod.EventHandler()
    public void event(FMLPreInitializationEvent event) throws IOException
    {
        logger = event.getModLog();

        configuration = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();

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

    public static Logger getLogger()
    {
        return instance.logger;
    }

    public static SimpleNetworkWrapper getSnw()
    {
        return instance.snw;
    }

    @Override
    public void syncConfig()
    {
        configuration.setCategoryLanguageKey(MODID, "d3.autocrafter2000.config.autocrafter2000");

        updateCraftCountLive = configuration.get(MODID, "updateCraftCountLive", updateCraftCountLive, "Send a packet to all players in the GUI to update craft count.\nDisable if network speed is an issue.").getBoolean(updateCraftCountLive);
        craftDelay = configuration.get(MODID, "craftDelay", craftDelay, "Amount of ticks in between each craft operation. 20 ticks is 1 second.\nLower values (< +-5) increase item duping when shift-clicking. I can't fix that.").getInt();

        if (configuration.hasChanged()) configuration.save();
    }

    @Override
    public void addConfigElements(List<IConfigElement> configElements)
    {
        configElements.add(new ConfigElement(configuration.getCategory(MODID.toLowerCase())));
    }
}
