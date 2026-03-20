package com.github.glodblock.functionalchemical.common.cap;

import com.buuz135.functionalstorage.block.config.FunctionalStorageConfig;
import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.item.UpgradeItem;
import com.buuz135.functionalstorage.item.component.FunctionalUpgradeBehavior;
import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import com.github.glodblock.functionalchemical.config.FunctionalChemicalConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.Action;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record MoveChemicalBehavior(boolean drawerIsSource, int chemPerOperation) implements FunctionalUpgradeBehavior {

    public static final MapCodec<MoveChemicalBehavior> CODEC = RecordCodecBuilder.mapCodec((in) -> in.group(Codec.BOOL.fieldOf("drawer_is_source").forGetter(MoveChemicalBehavior::drawerIsSource), Codec.INT.fieldOf("chem_per_operation").forGetter(MoveChemicalBehavior::chemPerOperation)).apply(in, MoveChemicalBehavior::new));

    @Override
    public void work(Level level, BlockPos pos, ControllableDrawerTile<?> dr, ItemStack upgradeStack, int upgradeSlot) {
        if (dr instanceof ChemicalDrawerTile drawer) {
            Direction direction = UpgradeItem.getDirection(upgradeStack);
            var otherChemHandler = level.getCapability(Capabilities.CHEMICAL.block(), pos.relative(direction), direction.getOpposite());
            if (otherChemHandler != null) {
                if (this.drawerIsSource) {
                    for (var chemTank : drawer.getChemTank().handler().getInternalTanks()) {
                        if (!chemTank.getStack().isEmpty()) {
                            var extracted = chemTank.extractChemical(FunctionalChemicalConfig.UPGRADE_PUSH_CHEMICAL, Action.SIMULATE);
                            if (!extracted.isEmpty()) {
                                var overflow = otherChemHandler.insertChemical(extracted, Action.EXECUTE);
                                var actuallyDrain = extracted.copyWithAmount(extracted.getAmount() - overflow.getAmount());
                                if (!actuallyDrain.isEmpty()) {
                                    chemTank.extractChemical(actuallyDrain.getAmount(), Action.EXECUTE);
                                    drawer.onChange();
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    for (var chemTank : drawer.getChemTank().handler().getInternalTanks()) {
                        var extracted = otherChemHandler.extractChemical(FunctionalStorageConfig.UPGRADE_PULL_FLUID, Action.SIMULATE);
                        if (!extracted.isEmpty()) {
                            var overflow = chemTank.insertChemical(extracted, Action.EXECUTE);
                            var actuallyDrain = extracted.copyWithAmount(extracted.getAmount() - overflow.getAmount());
                            if (!actuallyDrain.isEmpty()) {
                                otherChemHandler.extractChemical(actuallyDrain.getAmount(), Action.EXECUTE);
                                drawer.onChange();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public MapCodec<? extends FunctionalUpgradeBehavior> codec() {
        return CODEC;
    }

}
