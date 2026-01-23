package uan.wirelessredstone.mc.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import uan.wirelessredstone.mc.WirelessRedstoneMod;
import uan.wirelessredstone.mc.WirelessRedstoneModClient;

@Mod(WirelessRedstoneMod.MOD_ID)
public final class WirelessRedstoneModNeoForge {
    public WirelessRedstoneModNeoForge(IEventBus modbus) {
        WirelessRedstoneMod.init();
        modbus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        WirelessRedstoneModClient.init();
    }
}
