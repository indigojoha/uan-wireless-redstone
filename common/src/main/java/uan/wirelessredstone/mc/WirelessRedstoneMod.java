package uan.wirelessredstone.mc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uan.wirelessredstone.mc.blockentitytypes.WirelessRedstoneModBlockEntityTypes;
import uan.wirelessredstone.mc.blocks.WirelessRedstoneModBlocks;
import uan.wirelessredstone.mc.items.WirelessRedstoneModItems;

public final class WirelessRedstoneMod {
    public static final String MOD_ID = "indigo_wireless_redstone";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Initializing Indigo Wireless Redstone Mod");

        WirelessRedstoneModBlocks.init();
        WirelessRedstoneModBlockEntityTypes.init();
        WirelessRedstoneModItems.init();
    }
}
