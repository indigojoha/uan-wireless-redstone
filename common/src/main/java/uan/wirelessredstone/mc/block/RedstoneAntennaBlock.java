package uan.wirelessredstone.mc.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uan.wirelessredstone.mc.WirelessRedstoneMod;
import uan.wirelessredstone.mc.block.entity.RedstoneAntennaBlockEntity;
import uan.wirelessredstone.mc.blockentitytypes.WirelessRedstoneModBlockEntityTypes;

public class RedstoneAntennaBlock extends BaseEntityBlock {
    public static final ResourceLocation WEAK_SIGNATURE = ResourceLocation.fromNamespaceAndPath(WirelessRedstoneMod.MOD_ID, "redstone_antenna_weak");
    public static final ResourceLocation STRONG_SIGNATURE = ResourceLocation.fromNamespaceAndPath(WirelessRedstoneMod.MOD_ID, "redstone_antenna_strong");

    private static final VoxelShape BODY_SHAPE = Block.box(2, 0, 2, 14, 4, 14);

    private static final VoxelShape WEAK_ANTENNA_SHAPE = Block.box(6, 4, 6, 10, 16, 10);
    private static final VoxelShape STRONG_ANTENNA_NS_SHAPE = Block.box(3, 4, 7, 13, 16, 9);
    private static final VoxelShape STRONG_ANTENNA_EW_SHAPE = Block.box(7, 4, 3, 9, 16, 13);

    private static final VoxelShape COMBINED_SHAPE_WEAK = Shapes.or(BODY_SHAPE, WEAK_ANTENNA_SHAPE);
    private static final VoxelShape COMBINED_SHAPE_STRONG_NS = Shapes.or(BODY_SHAPE, STRONG_ANTENNA_NS_SHAPE);
    private static final VoxelShape COMBINED_SHAPE_STRONG_EW = Shapes.or(BODY_SHAPE, STRONG_ANTENNA_EW_SHAPE);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public RedstoneAntennaBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.getOwner().defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() { return simpleCodec(RedstoneAntennaBlock::new); }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneAntennaBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    private VoxelShape getGlobalShape(BlockState state) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());

        if (blockId.equals(WEAK_SIGNATURE)) {
            return COMBINED_SHAPE_WEAK;
        }

        if (blockId.equals(STRONG_SIGNATURE)) {
            Direction facing = state.getValue(FACING);
            return (facing.getAxis() == Direction.Axis.Z) ? COMBINED_SHAPE_STRONG_NS : COMBINED_SHAPE_STRONG_EW;
        }

        return Shapes.block();
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext ctx) {
        return getGlobalShape(state);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext ctx) {
        return getGlobalShape(state);
    }

    // ---------------------------------------
    // Power Output
    // ---------------------------------------

    @Override
    public boolean isSignalSource(BlockState state) { return true; }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction dir) {
        BlockEntity be = world.getBlockEntity(pos);
        return be instanceof RedstoneAntennaBlockEntity a ? a.getOutputPower() : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction dir) {
        BlockEntity be = world.getBlockEntity(pos);
        return be instanceof RedstoneAntennaBlockEntity a ? a.getOutputPower() : 0;
    }

    // ---------------------------------------
    // Removal Cleanup
    // ---------------------------------------

    @Override
    public void onRemove(BlockState oldState, Level world, BlockPos pos,
                         BlockState newState, boolean moved) {
        if (!oldState.is(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof RedstoneAntennaBlockEntity antenna) {
                antenna.onRemoved();
            }
        }
        super.onRemove(oldState, world, pos, newState, moved);
    }

    // ---------------------------------------
    // Ticker
    // ---------------------------------------

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level world, BlockState state, BlockEntityType<T> type) {

        return type == WirelessRedstoneModBlockEntityTypes.REDSTONE_ANTENNA.get()
                ? (w, p, s, be) -> RedstoneAntennaBlockEntity.tick(w, p, s, (RedstoneAntennaBlockEntity) be)
                : null;
    }
}