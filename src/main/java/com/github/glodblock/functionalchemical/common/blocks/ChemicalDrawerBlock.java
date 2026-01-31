package com.github.glodblock.functionalchemical.common.blocks;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.DrawerBlock;
import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.block.tile.StorageControllerTile;
import com.buuz135.functionalstorage.inventory.item.DrawerCapabilityProvider;
import com.buuz135.functionalstorage.item.ConfigurationToolItem;
import com.buuz135.functionalstorage.item.LinkingToolItem;
import com.github.glodblock.functionalchemical.common.FCItemAndBlock;
import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import com.github.glodblock.functionalchemical.util.ChemType;
import com.github.glodblock.functionalchemical.util.FCUtil;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.RayTraceUtils;
import com.hrznstudio.titanium.util.TileUtil;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public class ChemicalDrawerBlock extends RotatableBlock<ChemicalDrawerTile> {

    private final FunctionalStorage.DrawerType type;

    public ChemicalDrawerBlock(FunctionalStorage.DrawerType type, BlockBehaviour.Properties properties) {
        super("chem_" + type.getSlots(), properties, ChemicalDrawerTile.class);
        this.type = type;
        this.setItemGroup(FCItemAndBlock.TAB);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(RotatableBlock.FACING_HORIZONTAL, Direction.NORTH)
                .setValue(DrawerBlock.LOCKED, false)
        );
    }

    private static List<VoxelShape> getShapes(BlockState state, FunctionalStorage.DrawerType type) {
        List<VoxelShape> boxes = new ArrayList<>(DrawerBlock.CACHED_SHAPES.get(type).get(state.getValue(DrawerBlock.FACING_HORIZONTAL)));
        VoxelShape total = Shapes.block();
        boxes.add(total);
        return boxes;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DrawerBlock.LOCKED);
    }

    @Override
    public RotatableBlock.@NotNull RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, state) -> {
            RegistryObject<BlockEntityType<?>> entityType = FCItemAndBlock.CHEM_DRAWER_1.getRight();
            if (this.type == FunctionalStorage.DrawerType.X_2) {
                entityType = FCItemAndBlock.CHEM_DRAWER_2.getRight();
            }
            if (this.type == FunctionalStorage.DrawerType.X_4) {
                entityType = FCItemAndBlock.CHEM_DRAWER_4.getRight();
            }
            return new ChemicalDrawerTile(this, (BlockEntityType<ChemicalDrawerTile>) entityType.get(), blockPos, state, this.type);
        };
    }

    @Override
    public List<VoxelShape> getBoundingBoxes(@NotNull BlockState state, @NotNull BlockGetter source, @NotNull BlockPos pos) {
        return getShapes(state, this.type);
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext selectionContext) {
        return Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    @Override
    public boolean hasCustomBoxes(@NotNull BlockState state, @NotNull BlockGetter source, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public boolean hasIndividualRenderVoxelShape() {
        return true;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult ray) {
        return TileUtil.getTileEntity(worldIn, pos, ChemicalDrawerTile.class).map((drawerTile) -> drawerTile.onSlotActivated(player, hand, ray.getDirection(), ray.getLocation().x, ray.getLocation().y, ray.getLocation().z, this.getHit(state, worldIn, pos, player))).orElse(InteractionResult.PASS);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void attack(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player) {
        TileUtil.getTileEntity(worldIn, pos, ChemicalDrawerTile.class).ifPresent((drawerTile) -> drawerTile.onClicked(player, this.getHit(state, worldIn, pos, player)));
    }

    public int getHit(BlockState state, Level worldIn, BlockPos pos, Player player) {
        HitResult result = RayTraceUtils.rayTraceSimple(worldIn, player, 32.0, 0.0F);
        if (result instanceof BlockHitResult blockHit) {
            VoxelShape hit = RayTraceUtils.rayTraceVoxelShape(blockHit, worldIn, player, 32.0, 0.0F);
            if (hit != null) {
                if (hit.equals(Shapes.block())) {
                    return -1;
                }
                List<VoxelShape> shapes = new ArrayList<>(DrawerBlock.CACHED_SHAPES.get(this.type).get(state.getValue(RotatableBlock.FACING_HORIZONTAL)));
                for(int i = 0; i < shapes.size(); ++i) {
                    if (Shapes.joinIsNotEmpty(shapes.get(i), hit, BooleanOp.AND)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public LootTable.Builder getLootTable(@Nonnull BasicBlockLootTables blockLootTables) {
        return blockLootTables.droppingNothing();
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState p_60537_, LootContext.Builder builder) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        ItemStack stack = new ItemStack(this);
        BlockEntity drawerTile = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (drawerTile instanceof ChemicalDrawerTile tile) {
            if (!tile.isEverythingEmpty()) {
                stack.getOrCreateTag().put("Tile", drawerTile.saveWithoutMetadata());
            }
            if (tile.isLocked()) {
                stack.getOrCreateTag().putBoolean("Locked", tile.isLocked());
            }
        }
        stacks.add(stack);
        return stacks;
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        return NonNullList.create();
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity player, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, player, stack);
        if (stack.hasTag()) {
            if (stack.getTag().contains("Tile")) {
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof ControllableDrawerTile<?> tile) {
                    entity.load(stack.getTag().getCompound("Tile"));
                    tile.markForUpdate();
                }
            }
            if (stack.getTag().contains("Locked")) {
                level.setBlock(pos, state.setValue(DrawerBlock.LOCKED, true), 3);
            }
        }
        if (player == null) {
            return;
        }
        BlockEntity entity = level.getBlockEntity(pos);
        ItemStack offhand = player.getOffhandItem();
        if (offhand.is(FunctionalStorage.CONFIGURATION_TOOL.get())) {
            ConfigurationToolItem.ConfigurationAction action = ConfigurationToolItem.getAction(offhand);
            if (entity instanceof ControllableDrawerTile<?> tile) {
                if (action == ConfigurationToolItem.ConfigurationAction.LOCKING) {
                    tile.setLocked(true);
                } else if (action.getMax() == 1) {
                    tile.getDrawerOptions().setActive(action, false);
                } else {
                    tile.getDrawerOptions().setAdvancedValue(action, 1);
                }
            }
        }
    }

    @Override
    public void registerRecipe(@NotNull Consumer<FinishedRecipe> consumer) {
        if (this.type == FunctionalStorage.DrawerType.X_1) {
            TitaniumShapedRecipeBuilder.shapedRecipe(this).pattern("PPP").pattern("PCP").pattern("PPP").define('P', ItemTags.PLANKS).define('C', MekanismBlocks.BASIC_CHEMICAL_TANK).save(consumer);
        }
        if (this.type == FunctionalStorage.DrawerType.X_2) {
            TitaniumShapedRecipeBuilder.shapedRecipe(this, 2).pattern("PCP").pattern("PPP").pattern("PCP").define('P', ItemTags.PLANKS).define('C', MekanismBlocks.BASIC_CHEMICAL_TANK).save(consumer);
        }
        if (this.type == FunctionalStorage.DrawerType.X_4) {
            TitaniumShapedRecipeBuilder.shapedRecipe(this, 4).pattern("CPC").pattern("PPP").pattern("CPC").define('P', ItemTags.PLANKS).define('C', MekanismBlocks.BASIC_CHEMICAL_TANK).save(consumer);
        }
    }

    public FunctionalStorage.DrawerType getType() {
        return this.type;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileUtil.getTileEntity(worldIn, pos, ChemicalDrawerTile.class).ifPresent((tile) -> {
                if (tile.getControllerPos() != null) {
                    TileUtil.getTileEntity(worldIn, tile.getControllerPos(), StorageControllerTile.class).ifPresent((drawerControllerTile) -> drawerControllerTile.addConnectedDrawers(LinkingToolItem.ActionMode.REMOVE, pos));
                }
                tile.onRemove();
            });
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable BlockGetter world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(itemStack, world, tooltip, flag);
        if (itemStack.hasTag() && itemStack.getTag().contains("Tile")) {
            CompoundTag tileTag = itemStack.getTag().getCompound("Tile").getCompound("chemTank");
            tooltip.add(Component.translatable("drawer.block.contents").withStyle(ChatFormatting.GRAY));
            for(int i = 0; i < this.type.getSlots(); ++i) {
                for (var type : ChemType.values()) {
                    var key = "#" + i + "_" + type.getId();
                    if (tileTag.contains(key)) {

                        var stackTag = new CompoundTag();
                        stackTag.put("stack", tileTag.getCompound(key).getCompound(NBTConstants.STORED));
                        stackTag.putString("type", type.name().toLowerCase(Locale.US));
                        var stack = FCUtil.loadChemStackFromNBT(stackTag);
                        if (stack != null && !stack.isEmpty()) {
                            tooltip.add(Component.literal(" - " + ChatFormatting.YELLOW + FCUtil.getFormatedChemBigNumber(stack.getAmount()) + ChatFormatting.WHITE + " of ").append(stack.getTextComponent().copy().withStyle(ChatFormatting.GOLD)));
                        }
                    }
                }

            }
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(@NotNull BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getSignal(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull Direction p_60486_) {
        var tile = TileUtil.getTileEntity(blockGetter, blockPos, ChemicalDrawerTile.class).orElse(null);
        if (tile != null) {
            for(int i = 0; i < tile.getUtilityUpgrades().getSlots(); ++i) {
                ItemStack stack = tile.getUtilityUpgrades().getStackInSlot(i);
                if (stack.getItem().equals(FunctionalStorage.REDSTONE_UPGRADE.get())) {
                    int slot = stack.getOrCreateTag().getInt("Slot");
                    if (slot < tile.getChemTank().tanks().length) {
                        var tank = tile.getChemTank().tanks()[slot];
                        if (tank.getCurrent() == MergedChemicalTank.Current.EMPTY) {
                            return 0;
                        } else {
                            var chem = tank.getTankFromCurrent(tank.getCurrent());
                            return Math.toIntExact(chem.getStored() * 15 / chem.getCapacity());
                        }
                    }
                }
            }
        }
        return 0;
    }

    public static class DrawerItem extends BlockItem {

        private final ChemicalDrawerBlock drawerBlock;

        public DrawerItem(ChemicalDrawerBlock block, Item.Properties properties) {
            super(block, properties);
            this.drawerBlock = block;
        }

        @Override
        public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
            return super.getTooltipImage(stack);
        }

        @Override
        public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return new DrawerCapabilityProvider(stack, this.drawerBlock.getType());
        }

    }

}
