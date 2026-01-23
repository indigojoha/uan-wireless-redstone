package uan.wirelessredstone.mc.fabric;

import net.fabricmc.api.ModInitializer;

import uan.wirelessredstone.mc.WirelessRedstoneMod;

public final class WirelessRedstoneModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        WirelessRedstoneMod.init();
    }
}
