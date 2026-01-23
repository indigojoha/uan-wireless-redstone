package uan.wirelessredstone.mc;

import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import net.minecraft.client.renderer.RenderType;
import uan.wirelessredstone.mc.blocks.WirelessRedstoneModBlocks;

public class WirelessRedstoneModClient {
    public static void init() {
        RenderTypeRegistry.register(RenderType.cutout(),
                WirelessRedstoneModBlocks.REDSTONE_ANTENNA_WEAK.get(),
                WirelessRedstoneModBlocks.REDSTONE_ANTENNA_STRONG.get()
        );
    }
}