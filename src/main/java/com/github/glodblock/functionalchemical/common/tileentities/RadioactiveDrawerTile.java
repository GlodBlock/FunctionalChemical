package com.github.glodblock.functionalchemical.common.tileentities;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.item.UpgradeItem;
import com.github.glodblock.functionalchemical.common.inventory.ChemicalDrawerTank;
import com.github.glodblock.functionalchemical.common.inventory.RadioactiveDrawerTank;
import com.github.glodblock.functionalchemical.common.items.DecayUpgradeItem;
import com.github.glodblock.functionalchemical.util.ChemType;
import com.github.glodblock.functionalchemical.util.ReflectUtil;
import com.hrznstudio.titanium.block.BasicTileBlock;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tags.MekanismTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RadioactiveDrawerTile extends ChemicalDrawerTile {

    public RadioactiveDrawerTile(BasicTileBlock<ChemicalDrawerTile> base, BlockEntityType<ChemicalDrawerTile> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state, FunctionalStorage.DrawerType.X_1);
        this.getUtilityUpgrades().setInputFilter((stack, slot) -> {
            var item = stack.getItem();
            if (item == FunctionalStorage.COLLECTOR_UPGRADE.get() || item == FunctionalStorage.VOID_UPGRADE.get()) {
                return false;
            } else {
                return item instanceof UpgradeItem && ((UpgradeItem) item).getType() == UpgradeItem.Type.UTILITY;
            }
        });
    }

    @Override
    public boolean isVoid() {
        return false;
    }

    @Override
    protected MergedChemicalTank createTank(long cap, int slot) {
        return MergedChemicalTank.create(
                new RadioactiveDrawerTank(cap, c -> this.checkFilter(slot, c), () -> this.gasHandler),
                (IInfusionTank) ChemType.INFUSE.tankBuilder().create(cap, ConstantPredicates.alwaysFalse(), () -> this.infuseHandler),
                (IPigmentTank) ChemType.PIGMENT.tankBuilder().create(cap, ConstantPredicates.alwaysFalse(), () -> this.pigmentHandler),
                (ISlurryTank) ChemType.SLURRY.tankBuilder().create(cap, ConstantPredicates.alwaysFalse(), () -> this.slurryHandler)
        );
    }

    @Override
    protected long getTankCapacity(long storageMultiplier) {
        return (this.type.getSlotAmount() / 256) * 1000L * storageMultiplier;
    }

    @Nonnull
    public <U> LazyOptional<U> getCapability(@Nonnull Capability<U> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER || cap == Capabilities.INFUSION_HANDLER || cap == Capabilities.PIGMENT_HANDLER || cap == Capabilities.SLURRY_HANDLER) {
            return LazyOptional.empty();
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    protected void setCapacity(long capacity) {
        for (var mt : this.chemTank.tanks()) {
            ChemicalDrawerTank<?, ?> tank = ReflectUtil.INTERNAL_TANK.get(mt.getTankForType(ChemicalType.GAS));
            tank.setCapacity(capacity);
        }
    }

    @Override
    public void serverTick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState stateOwn, @NotNull ChemicalDrawerTile drawer) {
        super.serverTick(level, pos, stateOwn, drawer);
        // Use Mek config settings
        if (level.getGameTime() % MekanismConfig.general.radioactiveWasteBarrelProcessTicks.get() == 0) {
            for (int i = 0; i < this.getUtilityUpgrades().getSlots(); i++) {
                var stack = this.getUtilityUpgrades().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    int rate = getDecayRate(stack.getItem());
                    if (rate > 0) {
                        var toDecay = MekanismConfig.general.radioactiveWasteBarrelDecayAmount.get() * rate;
                        for (var mt : this.chemTank.tanks()) {
                            var tank = mt.getTankForType(ChemicalType.GAS);
                            GasStack chem = (GasStack) tank.getStack();
                            if (!chem.isEmpty() && !MekanismTags.Gases.WASTE_BARREL_DECAY_LOOKUP.contains(chem.getType())) {
                                tank.shrinkStack(toDecay, Action.EXECUTE);
                            }
                        }
                    }
                }
            }
        }
    }

    private static int getDecayRate(ItemLike item) {
        if (item instanceof DecayUpgradeItem decay) {
            return decay.getSpeed();
        }
        return 0;
    }

}
