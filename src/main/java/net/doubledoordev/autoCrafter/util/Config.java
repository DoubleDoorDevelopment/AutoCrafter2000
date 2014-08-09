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

package net.doubledoordev.autoCrafter.util;

import net.doubledoordev.lib.DevPerks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Config file
 * Contains ModID.
 *
 * @author Dries007
 */
public class Config
{
    public boolean debug = false;
    public int     craftDelay           = 10;
    public boolean updateCraftCountLive = true;
    public boolean allowDebug           = false;

    public Config(File file)
    {
        Configuration configuration = new Configuration(file);

        updateCraftCountLive = configuration.get(Configuration.CATEGORY_GENERAL, "updateCraftCountLive", updateCraftCountLive, "Send a packet to all players in the GUI to update craft count.\nDisable if network speed is an issue.").getBoolean(updateCraftCountLive);
        craftDelay = configuration.get(Configuration.CATEGORY_GENERAL, "craftDelay", craftDelay, "Amount of ticks in between each craft operation. 20 ticks is 1 second.\nLower values (< +-5) increase item duping when shift-clicking. I can't fix that.").getInt();
        allowDebug = configuration.get(Configuration.CATEGORY_GENERAL, "allowDebug", allowDebug, "Allow right click on the block with a stick to debug.").getBoolean(allowDebug);

        debug = configuration.getBoolean("debug", Configuration.CATEGORY_GENERAL, debug, "Enable extra debug output.");
        if (configuration.getBoolean("sillyness", Configuration.CATEGORY_GENERAL, true, "Disable sillyness only if you want to piss off the developers XD")) MinecraftForge.EVENT_BUS.register(new DevPerks(debug));
        if (configuration.hasChanged()) configuration.save();
    }
}
