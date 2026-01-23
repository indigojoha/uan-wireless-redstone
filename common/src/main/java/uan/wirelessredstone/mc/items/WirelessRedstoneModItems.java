package uan.wirelessredstone.mc.items;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import uan.wirelessredstone.mc.WirelessRedstoneMod;
import uan.wirelessredstone.mc.blocks.WirelessRedstoneModBlocks;
import uan.wirelessredstone.mc.item.RedstoneAntennaLinkerItem;

import java.util.function.Supplier;
public class WirelessRedstoneModItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(WirelessRedstoneMod.MOD_ID, Registries.ITEM);

    private static RegistrySupplier<RedstoneAntennaLinkerItem> REDSTONE_ANTENNA_LINKER;
    private static RegistrySupplier<Item> REDSTONE_ANTENNA_WEAK;
    private static RegistrySupplier<Item> REDSTONE_ANTENNA_STRONG;

    public static void init() {
        REDSTONE_ANTENNA_LINKER = registerItem("redstone_antenna_linker", () -> new RedstoneAntennaLinkerItem(baseSettings()));
        CreativeTabRegistry.appendStack(CreativeModeTabs.REDSTONE_BLOCKS, () -> REDSTONE_ANTENNA_LINKER.get().getDefaultInstance());

        REDSTONE_ANTENNA_WEAK = registerItem("redstone_antenna_weak", () -> new BlockItem(WirelessRedstoneModBlocks.REDSTONE_ANTENNA_WEAK.get(), baseSettings()));
        CreativeTabRegistry.appendStack(CreativeModeTabs.REDSTONE_BLOCKS, () -> REDSTONE_ANTENNA_WEAK.get().getDefaultInstance());

        REDSTONE_ANTENNA_STRONG = registerItem("redstone_antenna_strong", () -> new BlockItem(WirelessRedstoneModBlocks.REDSTONE_ANTENNA_STRONG.get(), baseSettings()));
        CreativeTabRegistry.appendStack(CreativeModeTabs.REDSTONE_BLOCKS, () -> REDSTONE_ANTENNA_STRONG.get().getDefaultInstance());

        ITEMS.register();
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(String name, Supplier<T> item) {
        return (RegistrySupplier<T>) ITEMS.<T>register(ResourceLocation.fromNamespaceAndPath(WirelessRedstoneMod.MOD_ID, name), item);
    }

    public static RegistrySupplier<Item> registerStandardItem(String name, Supplier<Item> item) {
        return ITEMS.register(ResourceLocation.fromNamespaceAndPath(WirelessRedstoneMod.MOD_ID, name), item);
    }

    public static Item.Properties baseSettings() {
        return new Item.Properties();
    }
}
