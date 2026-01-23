package uan.wirelessredstone.mc.blockentitytypes;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import uan.wirelessredstone.mc.WirelessRedstoneMod;
import uan.wirelessredstone.mc.block.entity.RedstoneAntennaBlockEntity;

import static uan.wirelessredstone.mc.blocks.WirelessRedstoneModBlocks.REDSTONE_ANTENNA_WEAK;
import static uan.wirelessredstone.mc.blocks.WirelessRedstoneModBlocks.REDSTONE_ANTENNA_STRONG;

public class WirelessRedstoneModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(WirelessRedstoneMod.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static RegistrySupplier<BlockEntityType<RedstoneAntennaBlockEntity>> REDSTONE_ANTENNA;

    public static void init() {
        REDSTONE_ANTENNA = BLOCK_ENTITY_TYPES.register("redstone_antenna", () ->
                BlockEntityType.Builder
                        .of(RedstoneAntennaBlockEntity::new,
                                REDSTONE_ANTENNA_WEAK.get(),
                                REDSTONE_ANTENNA_STRONG.get()
                        )
                        .build(null)
        );

        BLOCK_ENTITY_TYPES.register();
    }
}