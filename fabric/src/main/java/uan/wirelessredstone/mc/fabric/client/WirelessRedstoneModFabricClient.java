package uan.wirelessredstone.mc.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import uan.wirelessredstone.mc.WirelessRedstoneModClient;

public final class WirelessRedstoneModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        WirelessRedstoneModClient.init();
    }
}
