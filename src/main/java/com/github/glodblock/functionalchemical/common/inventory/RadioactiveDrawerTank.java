package com.github.glodblock.functionalchemical.common.inventory;

import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.gas.attribute.GasAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class RadioactiveDrawerTank extends ChemicalDrawerTank<Gas, GasStack> implements IGasTank {

    private static final ChemicalAttributeValidator FILTER = ChemicalAttributeValidator.createStrict(GasAttributes.Radiation.class);

    public RadioactiveDrawerTank(long capacity, Predicate<Gas> filter, Supplier<MultiChemicalHandler<Gas, GasStack>> handler) {
        super(capacity, filter, handler, FILTER);
    }

    @Override
    public boolean isValid(@NotNull GasStack stack) {
        if (stack.getAttributeTypes().contains(GasAttributes.Radiation.class)) {
            return super.isValid(stack);
        } else {
            return false;
        }
    }

}
