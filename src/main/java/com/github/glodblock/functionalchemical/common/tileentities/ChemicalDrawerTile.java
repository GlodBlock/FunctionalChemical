package com.github.glodblock.functionalchemical.common.tileentities;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.config.FunctionalStorageConfig;
import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import com.buuz135.functionalstorage.item.UpgradeItem;
import com.github.glodblock.functionalchemical.FunctionalChemical;
import com.github.glodblock.functionalchemical.client.gui.ChemDrawerInfoGuiAddon;
import com.github.glodblock.functionalchemical.common.inventory.MultiChemicalHandler;
import com.github.glodblock.functionalchemical.common.inventory.ChemicalDrawerTank;
import com.github.glodblock.functionalchemical.config.FunctionalChemicalConfig;
import com.github.glodblock.functionalchemical.util.ChemType;
import com.github.glodblock.functionalchemical.util.FCUtil;
import com.github.glodblock.functionalchemical.util.ReflectUtil;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.util.TileUtil;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.Coord4D;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.ChemicalTankWrapper;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.radiation.IRadiationManager;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.LongSupplier;

@SuppressWarnings("rawtypes")
public class ChemicalDrawerTile extends ControllableDrawerTile<ChemicalDrawerTile> {

    private LazyOptional<IChemicalHandler<Gas, GasStack>> gasHandlerLazy;
    private LazyOptional<IChemicalHandler<InfuseType, InfusionStack>> infuseHandlerLazy;
    private LazyOptional<IChemicalHandler<Pigment, PigmentStack>> pigmentHandlerLazy;
    private LazyOptional<IChemicalHandler<Slurry, SlurryStack>> slurryHandlerLazy;
    protected MultiChemicalHandler<Gas, GasStack> gasHandler;
    protected MultiChemicalHandler<InfuseType, InfusionStack> infuseHandler;
    protected MultiChemicalHandler<Pigment, PigmentStack> pigmentHandler;
    protected MultiChemicalHandler<Slurry, SlurryStack> slurryHandler;
    @Save
    protected final SyncTank chemTank;
    @Save
    private final SyncFilter filter;
    protected final FunctionalStorage.DrawerType type;

    public ChemicalDrawerTile(BasicTileBlock<ChemicalDrawerTile> base, BlockEntityType<ChemicalDrawerTile> blockEntityType, BlockPos pos, BlockState state, FunctionalStorage.DrawerType type) {
        super(base, blockEntityType, pos, state);
        this.type = type;
        var mergedTank = new MergedChemicalTank[type.getSlots()];
        this.filter = new SyncFilter(new ChemicalStack[type.getSlots()]);
        var cap = this.getCapacity();
        for (int i = 0; i < mergedTank.length; i++) {
            mergedTank[i] = this.createTank(cap, i);
        }
        this.chemTank = new SyncTank(mergedTank);
        this.gasHandler = new ProtypeHandler<>(type.getSlots(), this.chemTank, this::getCapacity, ChemType.GAS);
        this.infuseHandler = new ProtypeHandler<>(type.getSlots(), this.chemTank, this::getCapacity, ChemType.INFUSE);
        this.pigmentHandler = new ProtypeHandler<>(type.getSlots(), this.chemTank, this::getCapacity, ChemType.PIGMENT);
        this.slurryHandler = new ProtypeHandler<>(type.getSlots(), this.chemTank, this::getCapacity, ChemType.SLURRY);
        this.gasHandlerLazy = LazyOptional.of(() -> this.gasHandler);
        this.infuseHandlerLazy = LazyOptional.of(() -> this.infuseHandler);
        this.pigmentHandlerLazy = LazyOptional.of(() -> this.pigmentHandler);
        this.slurryHandlerLazy = LazyOptional.of(() -> this.slurryHandler);
        this.getUtilityUpgrades().setInputFilter((stack, slot) -> stack.getItem() != FunctionalStorage.COLLECTOR_UPGRADE.get() && stack.getItem() instanceof UpgradeItem && ((UpgradeItem) stack.getItem()).getType() == UpgradeItem.Type.UTILITY);
    }

    protected MergedChemicalTank createTank(long cap, int slot) {
        return MergedChemicalTank.create(
                (IGasTank) ChemType.GAS.tankBuilder().create(cap, c -> this.checkFilter(slot, (Chemical) c), () -> this.gasHandler),
                (IInfusionTank) ChemType.INFUSE.tankBuilder().create(cap, c -> this.checkFilter(slot, (Chemical) c), () -> this.infuseHandler),
                (IPigmentTank) ChemType.PIGMENT.tankBuilder().create(cap, c -> this.checkFilter(slot, (Chemical) c), () -> this.pigmentHandler),
                (ISlurryTank) ChemType.SLURRY.tankBuilder().create(cap, c -> this.checkFilter(slot, (Chemical) c), () -> this.slurryHandler)
        );
    }

    public MultiChemicalHandler<Gas, GasStack> getGasHandler() {
        return this.gasHandler;
    }

    public MultiChemicalHandler<InfuseType, InfusionStack> getInfuseHandler() {
        return this.infuseHandler;
    }

    public MultiChemicalHandler<Pigment, PigmentStack> getPigmentHandler() {
        return this.pigmentHandler;
    }

    public MultiChemicalHandler<Slurry, SlurryStack> getSlurryHandler() {
        return this.slurryHandler;
    }

    public long getCapacity() {
        if (this.isCreative()) {
            return Long.MAX_VALUE;
        }
        return this.getTankCapacity(this.getStorageMultiplier());
    }

    public void onRemove() {
        if (this.level != null && this.isServer() && IRadiationManager.INSTANCE.isRadiationEnabled() && !this.isCreative()) {
            IRadiationManager.INSTANCE.dumpRadiation(
                    new Coord4D(this.worldPosition, this.level),
                    Arrays.stream(this.chemTank.tanks()).map(MergedChemicalTank::getGasTank).toList(),
                    false
            );
        }
    }

    public RenderStatus getRenderStack(int slot) {
        var mode = this.chemTank.tanks()[slot].getCurrent();
        if (mode == MergedChemicalTank.Current.EMPTY) {
            if (this.isLocked()) {
                return new RenderStatus(this.filter.filter()[slot], true);
            } else {
                return new RenderStatus(null, false);
            }
        } else {
            return new RenderStatus(this.chemTank.tanks()[slot].getTankFromCurrent(mode).getStack(), false);
        }
    }

    public FunctionalStorage.DrawerType getDrawerType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    protected boolean checkFilter(int slot, Chemical stack) {
        if (!this.isLocked()) {
            return true;
        }
        var filter = this.filter.filter()[slot];
        if (filter == null || filter.isEmpty()) {
            return false;
        }
        var typeA = ChemicalType.getTypeFor(stack);
        var typeB = ChemicalType.getTypeFor(filter);
        if (typeA == typeB) {
            return filter.isTypeEqual(stack);
        } else {
            return false;
        }
    }

    public SyncTank getChemTank() {
        return this.chemTank;
    }

    protected long getTankCapacity(long storageMultiplier) {
        return (this.type.getSlotAmount() / 32L) * 1000L * storageMultiplier;
    }

    @Override
    public int getStorageSlotAmount() {
        return 4;
    }

    @Override
    public int getBaseSize(int i) {
        return this.type.getSlotAmount();
    }

    @Override
    public double getStorageDiv() {
        return 2F;
    }

    @Override
    public void setLocked(boolean locked) {
        super.setLocked(locked);
        for (int i = 0; i < this.type.getSlots(); i++) {
            var tank = this.chemTank.tanks()[i];
            var mode = tank.getCurrent();
            if (mode != MergedChemicalTank.Current.EMPTY) {
                var stored = tank.getTankFromCurrent(mode);
                this.filter.filter()[i] = stored.getStack().copy();
                if (!this.filter.filter()[i].isEmpty()) {
                    this.filter.filter()[i].setAmount(1);
                } else {
                    this.filter.filter()[i] = null;
                }
            }
        }
        this.syncObject(this.filter);
        this.markDirty();
    }

    @Override
    public boolean isEverythingEmpty() {
        for (var tank : this.chemTank.tanks()) {
            if (tank.getCurrent() != MergedChemicalTank.Current.EMPTY) {
                return false;
            }
        }
        if (this.isLocked()) return false;
        for (int i = 0; i < this.getStorageUpgrades().getSlots(); i++) {
            if (!this.getStorageUpgrades().getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        for (int i = 0; i < this.getUtilityUpgrades().getSlots(); i++) {
            if (!this.getUtilityUpgrades().getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void initClient() {
        super.initClient();
        var slotName = "";
        if (type.getSlots() == 2) {
            slotName = "_2";
        }
        if (type.getSlots() == 4) {
            slotName = "_4";
        }
        String finalSlotName = slotName;
        addGuiAddonFactory(() -> new ChemDrawerInfoGuiAddon(64, 16,
                FunctionalChemical.id("textures/block/chem_front" + finalSlotName + ".png"),
                type.getSlots(),
                this.isLocked(),
                type.getSlotPosition(),
                this::getChemTank,
                () -> this.filter,
                i -> this.getCapacity()
        ));
    }

    @SuppressWarnings("unchecked")
    private long output(BlockEntity target, Direction side, ChemicalStack<?> stack) {
        var chemType = ChemicalType.getTypeFor(stack);
        var cap = switch (chemType) {
            case GAS -> target.getCapability(Capabilities.GAS_HANDLER, side.getOpposite());
            case INFUSION -> target.getCapability(Capabilities.INFUSION_HANDLER, side.getOpposite());
            case PIGMENT -> target.getCapability(Capabilities.PIGMENT_HANDLER, side.getOpposite());
            case SLURRY -> target.getCapability(Capabilities.SLURRY_HANDLER, side.getOpposite());
        };
        return cap.map( obj -> {
            var handler = (IChemicalHandler) obj;
            var overflow = handler.insertChemical(stack, Action.EXECUTE);
            if (overflow.isEmpty()) {
                return stack.getAmount();
            } else {
                return stack.getAmount() - overflow.getAmount();
            }
        }).orElse(0L);
    }

    @SuppressWarnings("unchecked")
    private ChemicalStack<?> input(BlockEntity target, Direction side, ChemicalStack<?> mark, long asked) {
        List<Capability<? extends IChemicalHandler>> caps = new ArrayList<>();
        if (mark == null || mark.isEmpty()) {
            caps.add(Capabilities.GAS_HANDLER);
            caps.add(Capabilities.INFUSION_HANDLER);
            caps.add(Capabilities.PIGMENT_HANDLER);
            caps.add(Capabilities.SLURRY_HANDLER);
        } else {
            caps.add(switch (ChemicalType.getTypeFor(mark)) {
                case GAS -> Capabilities.GAS_HANDLER;
                case INFUSION -> Capabilities.INFUSION_HANDLER;
                case PIGMENT -> Capabilities.PIGMENT_HANDLER;
                case SLURRY -> Capabilities.SLURRY_HANDLER;
            });
        }
        for (var cap : caps) {
            var opt = target.getCapability(cap, side.getOpposite());
            var result = opt.map(obj -> {
                var handler = (IChemicalHandler) obj;
                if (mark == null || mark.isEmpty()) {
                    return handler.extractChemical(asked, Action.EXECUTE);
                } else {
                    var stack = mark.copy();
                    stack.setAmount(asked);
                    return handler.extractChemical(stack, Action.EXECUTE);
                }
            }).orElse(null);
            if (result != null && !result.isEmpty()) {
                return result;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serverTick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState stateOwn, @NotNull ChemicalDrawerTile drawer) {
        super.serverTick(level, pos, stateOwn, drawer);
        if (level.getGameTime() % (long) FunctionalStorageConfig.UPGRADE_TICK == 0) {
            for (int i = 0; i < this.getUtilityUpgrades().getSlots(); i++) {
                var stack = this.getUtilityUpgrades().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    var item = stack.getItem();
                    if (item == FunctionalStorage.PUSHING_UPGRADE.get()) {
                        var direction = UpgradeItem.getDirection(stack);
                        TileUtil.getTileEntity(level, pos.relative(direction)).ifPresent(blockEntity1 -> {
                            for (var mt : this.chemTank.tanks()) {
                                var mode = mt.getCurrent();
                                if (mode != MergedChemicalTank.Current.EMPTY) {
                                    var tank = mt.getTankFromCurrent(mode);
                                    var canOutput = tank.extract(FunctionalChemicalConfig.UPGRADE_PUSH_CHEMICAL, Action.SIMULATE, AutomationType.EXTERNAL);
                                    if (!canOutput.isEmpty()) {
                                        long inserted = this.output(blockEntity1, direction, canOutput);
                                        if (inserted > 0) {
                                            tank.extract(inserted, Action.EXECUTE, AutomationType.EXTERNAL);
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                    if (item == FunctionalStorage.PULLING_UPGRADE.get()) {
                        var direction = UpgradeItem.getDirection(stack);
                        TileUtil.getTileEntity(level, pos.relative(direction)).ifPresent(blockEntity1 -> {
                            for (var mt : this.chemTank.tanks()) {
                                var mode = mt.getCurrent();
                                if (mode != MergedChemicalTank.Current.EMPTY) {
                                    IChemicalTank tank = mt.getTankFromCurrent(mode);
                                    var mark = tank.getStack();
                                    var tmp = mark.copy();
                                    tmp.setAmount(FunctionalChemicalConfig.UPGRADE_PULL_CHEMICAL);
                                    var overflow = tank.insert(tmp, Action.SIMULATE, AutomationType.EXTERNAL);
                                    var canHold = FunctionalChemicalConfig.UPGRADE_PULL_CHEMICAL - overflow.getAmount();
                                    if (canHold > 0) {
                                        var extracted = this.input(blockEntity1, direction, mark, canHold);
                                        if (extracted != null && !extracted.isEmpty()) {
                                            tank.insert(extracted, Action.EXECUTE, AutomationType.EXTERNAL);
                                            break;
                                        }
                                    }
                                } else {
                                    var canHold = Math.min(FunctionalChemicalConfig.UPGRADE_PULL_CHEMICAL, this.getCapacity());
                                    if (canHold > 0) {
                                        var extracted = this.input(blockEntity1, direction, null, canHold);
                                        if (extracted != null && !extracted.isEmpty()) {
                                            IChemicalTank tank = mt.getTankForType(ChemicalType.getTypeFor(extracted));
                                            tank.insert(extracted, Action.EXECUTE, AutomationType.EXTERNAL);
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @Nonnull
    public <U> LazyOptional<U> getCapability(@Nonnull Capability<U> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.empty();
        } else if (cap == Capabilities.GAS_HANDLER) {
            return this.gasHandlerLazy.cast();
        } else if (cap == Capabilities.INFUSION_HANDLER) {
            return this.infuseHandlerLazy.cast();
        } else if (cap == Capabilities.PIGMENT_HANDLER) {
            return this.pigmentHandlerLazy.cast();
        } else if (cap == Capabilities.SLURRY_HANDLER) {
            return this.slurryHandlerLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.gasHandlerLazy.invalidate();
        this.infuseHandlerLazy.invalidate();
        this.pigmentHandlerLazy.invalidate();
        this.slurryHandlerLazy.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.gasHandlerLazy = LazyOptional.of(() -> this.gasHandler);
        this.infuseHandlerLazy = LazyOptional.of(() -> this.infuseHandler);
        this.pigmentHandlerLazy = LazyOptional.of(() -> this.pigmentHandler);
        this.slurryHandlerLazy = LazyOptional.of(() -> this.slurryHandler);
    }

    @Override
    public InventoryComponent<ControllableDrawerTile<ChemicalDrawerTile>> getStorageUpgradesConstructor() {
        return new InventoryComponent<ControllableDrawerTile<ChemicalDrawerTile>>("storage_upgrades", 10, 70, this.getStorageSlotAmount()) {
            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                ItemStack stack = getStackInSlot(slot);
                if (stack.getItem() instanceof StorageUpgradeItem) {
                    long mult = 1;
                    for (int i = 0; i < getStorageUpgrades().getSlots(); i++) {
                        if (getStorageUpgrades().getStackInSlot(i).getItem() instanceof StorageUpgradeItem) {
                            if (i == slot) continue;
                            var calculated = ((StorageUpgradeItem) getStorageUpgrades().getStackInSlot(i).getItem()).getStorageMultiplier() / getStorageDiv();
                            if (mult == 1) {
                                mult = (long) calculated;
                            } else {
                                mult *= (long) calculated;
                            }
                        }
                    }
                    for (var tank : getChemTank().tanks()) {
                        if (tank.getCurrent() != MergedChemicalTank.Current.EMPTY) {
                            if (tank.getTankFromCurrent(tank.getCurrent()).getStored() > getTankCapacity(mult)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }
                return super.extractItem(slot, amount, simulate);
            }
        }
        .setInputFilter((stack, integer) -> {
            if (stack.getItem() == FunctionalStorage.STORAGE_UPGRADES.get(StorageUpgradeItem.StorageTier.IRON).get()) {
                return false;
            }
            return stack.getItem() instanceof UpgradeItem && ((UpgradeItem) stack.getItem()).getType() == UpgradeItem.Type.STORAGE;
        })
        .setOnSlotChanged((stack, integer) -> {
            setNeedsUpgradeCache(true);
            this.setCapacity(this.getCapacity());
            this.syncObject(this.chemTank);
            this.markDirty();
        })
        .setSlotLimit(1);
    }

    @Override
    public void syncObject(@NotNull Object object) {
        if (this.level != null) {
            super.syncObject(object);
        }
    }

    @Override
    public void onDataPacket(@NotNull Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(Objects.requireNonNullElse(pkt.getTag(), new CompoundTag()));
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        this.setCapacity(this.getCapacity());
    }

    @Override
    public @NotNull ChemicalDrawerTile getSelf() {
        return this;
    }

    protected void setCapacity(long capacity) {
        for (var mt : this.chemTank.tanks()) {
            mt.getAllTanks().stream()
                    .map(t -> (ChemicalTankWrapper<?, ?>) t)
                    .map(t -> (ChemicalDrawerTank<?, ?>) ReflectUtil.INTERNAL_TANK.get(t))
                    .forEach(t -> t.setCapacity(capacity));
        }
    }

    public void markDirty() {
        if (this.level != null) {
            this.level.blockEntityChanged(this.worldPosition);
        }
    }

    private class ProtypeHandler<C extends Chemical<C>, S extends ChemicalStack<C>> extends MultiChemicalHandler<C, S> {

        public ProtypeHandler(int size, SyncTank tanks, LongSupplier capacity, ChemType type) {
            super(size, tanks, capacity, type);
        }

        @Override
        public void onChange() {
            ChemicalDrawerTile.this.syncObject(ChemicalDrawerTile.this.chemTank);
            ChemicalDrawerTile.this.markDirty();
        }

        @Override
        public boolean isDrawerLocked() {
            return ChemicalDrawerTile.this.isLocked();
        }

        @Override
        public boolean isDrawerVoid() {
            return ChemicalDrawerTile.this.isVoid();
        }

        @Override
        public boolean isDrawerCreative() {
            return ChemicalDrawerTile.this.isCreative();
        }

    }

    public record SyncFilter(ChemicalStack[] filter) implements INBTSerializable<CompoundTag> {

        @Override
        public CompoundTag serializeNBT() {
            var filterTag = new CompoundTag();
            for (int i = 0; i < this.filter.length; i++) {
                filterTag.put("#" + i, FCUtil.saveChemStackToNBT(this.filter[i]));
            }
            return filterTag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            for (int i = 0; i < this.filter.length; i++) {
                this.filter[i] = FCUtil.loadChemStackFromNBT(nbt.getCompound("#" + i));
            }
        }

    }

    public record SyncTank(MergedChemicalTank[] tanks) implements INBTSerializable<CompoundTag> {

        @Override
        public CompoundTag serializeNBT() {
            var chemTankTag = new CompoundTag();
            for (int i = 0; i < this.tanks.length; i++) {
                for (var chemType : ChemType.values()) {
                    var tank = this.tanks[i].getTankForType(chemType.getNativeType());
                    if (!tank.isEmpty()) {
                        var key = "#" + i + "_" + chemType.getId();
                        chemTankTag.put(key, tank.serializeNBT());
                    }
                }
            }
            return chemTankTag;
        }

        @Override
        public void deserializeNBT(CompoundTag compound) {
            for (int i = 0; i < this.tanks.length; i++) {
                for (var chemType : ChemType.values()) {
                    var key = "#" + i + "_" + chemType.getId();
                    if (compound.contains(key)) {
                        this.tanks[i].getTankForType(chemType.getNativeType()).deserializeNBT(compound.getCompound(key));
                    } else {
                        this.tanks[i].getTankForType(chemType.getNativeType()).setEmpty();
                    }
                }
            }
        }

    }

    public record RenderStatus(ChemicalStack<?> stack, boolean isFilter) {

        public boolean valid() {
            return this.stack != null && !this.stack.isEmpty();
        }

    }

}
