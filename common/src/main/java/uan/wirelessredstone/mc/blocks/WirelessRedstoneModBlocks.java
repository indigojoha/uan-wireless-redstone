package uan.wirelessredstone.mc.blocks;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import uan.wirelessredstone.mc.WirelessRedstoneMod;
import uan.wirelessredstone.mc.block.RedstoneAntennaBlock;
import java.util.function.Supplier;

public class WirelessRedstoneModBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
            WirelessRedstoneMod.MOD_ID,
            Registries.BLOCK
    );

    public static RegistrySupplier<Block> REDSTONE_ANTENNA_WEAK;
    public static RegistrySupplier<Block> REDSTONE_ANTENNA_STRONG;

    public static void init() {
        REDSTONE_ANTENNA_WEAK = registerBlock("redstone_antenna_weak",
                () -> new RedstoneAntennaBlock(baseSettings("redstone_antenna_weak")));

        REDSTONE_ANTENNA_STRONG = registerBlock("redstone_antenna_strong",
                () -> new RedstoneAntennaBlock(baseSettings("redstone_antenna_strong")));

        BLOCKS.register();
    }

    public static RegistrySupplier<Block> registerBlock(String name, Supplier<Block> block) {
        return BLOCKS.register(ResourceLocation.fromNamespaceAndPath(WirelessRedstoneMod.MOD_ID, name), block);
    }

    public static BlockBehaviour.Properties baseSettings(String name) {
        return BlockBehaviour.Properties.of();
    }
}