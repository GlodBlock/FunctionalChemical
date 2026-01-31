package com.github.glodblock.functionalchemical.util;

import com.buuz135.functionalstorage.util.NumberUtils;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class FCUtil {

    private static final DecimalFormat F = new DecimalFormat("####0.#");

    @Nullable
    public static ChemicalStack<?> loadChemStackFromNBT(CompoundTag tag) {
        if (tag.contains("type") && tag.contains("stack")) {
            var type = tag.getString("type");
            var stack = tag.getCompound("stack");
            return switch (type) {
                case "gas" -> GasStack.readFromNBT(stack);
                case "infuse" -> InfusionStack.readFromNBT(stack);
                case "pigment" -> PigmentStack.readFromNBT(stack);
                case "slurry" -> SlurryStack.readFromNBT(stack);
                default -> null;
            };
        }
        return null;
    }

    public static CompoundTag saveChemStackToNBT(ChemicalStack<?> stack) {
        if (stack != null) {
            var tag = new CompoundTag();
            if (stack.isEmpty()) {
                return tag;
            }
            var type = stack.getType();
            var stackTag = new CompoundTag();
            stack.write(stackTag);
            tag.put("stack", stackTag);
            if (type instanceof Gas) {
                tag.putString("type", "gas");
            } else if (type instanceof InfuseType) {
                tag.putString("type", "infuse");
            } else if (type instanceof Pigment) {
                tag.putString("type", "pigment");
            } else if (type instanceof Slurry) {
                tag.putString("type", "slurry");
            }
            return tag;
        }
        return new CompoundTag();
    }

    public static String getFormatedChemBigNumber(long number) {
        if (number <= Integer.MAX_VALUE) {
            return NumberUtils.getFormatedFluidBigNumber((int) number);
        }
        if (number <= 1_000_000_000_000L) {
            double show = (double) number / 1_000_000_000L;
            return F.format(show) + "M B";
        }
        if (number <= 1_000_000_000_000_000L) {
            double show = (double) number / 1_000_000_000_000L;
            return F.format(show) + "G B";
        }
        if (number <= 1_000_000_000_000_000_000L) {
            double show = (double) number / 1_000_000_000_000_000L;
            return F.format(show) + "T B";
        }
        double show = (double) number / 1_000_000_000_000_000_000L;
        return F.format(show) + "P B";
    }

}
