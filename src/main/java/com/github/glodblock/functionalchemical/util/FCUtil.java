package com.github.glodblock.functionalchemical.util;

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

public class FCUtil {

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

}
