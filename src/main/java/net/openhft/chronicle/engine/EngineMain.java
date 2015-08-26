/*
 *     Copyright (C) 2015  higherfrequencytrading.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.openhft.chronicle.engine;


import net.openhft.chronicle.core.pool.ClassAliasPool;
import net.openhft.chronicle.engine.api.tree.AssetTree;
import net.openhft.chronicle.engine.cfg.EngineCfg;
import net.openhft.chronicle.engine.cfg.Installable;
import net.openhft.chronicle.engine.cfg.JmxCfg;
import net.openhft.chronicle.engine.cfg.ServerCfg;
import net.openhft.chronicle.engine.tree.VanillaAssetTree;
import net.openhft.chronicle.wire.TextWire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This engine main uses a configuration file
 */
public class EngineMain {
    static final Logger LOGGER = LoggerFactory.getLogger(SimpleEngineMain.class);
    static final int HOST_ID = Integer.getInteger("engine.hostId", 0);

    static <I extends Installable> void addClass(Class<I>... iClasses) {
        ClassAliasPool.CLASS_ALIASES.addAlias(iClasses);
    }

    public static void main(String[] args) throws IOException {
        addClass(EngineCfg.class);
        addClass(JmxCfg.class);
        addClass(ServerCfg.class);

        String name = args.length > 0 ? args[0] : "engine.yaml";
        TextWire yaml = TextWire.fromFile(name);
        Installable installable = (Installable) yaml.readObject();
        AssetTree assetTree = new VanillaAssetTree(HOST_ID).forServer(false);
        try {
            installable.install("/", assetTree);
            LOGGER.info("Engine started");

        } catch (Exception e) {
            LOGGER.error("Error starting a component, stopping", e);
            assetTree.close();
        }
    }
}
