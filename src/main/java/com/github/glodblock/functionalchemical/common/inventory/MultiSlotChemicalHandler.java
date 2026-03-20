package com.github.glodblock.functionalchemical.common.inventory;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public class MultiSlotChemicalHandler implements IChemicalHandler {

    private final ChemicalDrawerTank[] tanks;

    public MultiSlotChemicalHandler(int size, IntFunction<? extends ChemicalDrawerTank> creator) {
        this.tanks = new ChemicalDrawerTank[size];
        for (int i = 0; i < size; i++) {
            this.tanks[i] = creator.apply(i);
        }
    }

    public ChemicalDrawerTank[] getInternalTanks() {
        return this.tanks;
    }

    @Override
    public int getChemicalTanks() {
        return this.tanks.length;
    }

    @Override
    public @NotNull ChemicalStack getChemicalInTank(int tank) {
        return this.tanks[tank].getStack();
    }

    @Override
    public void setChemicalInTank(int tank, @NotNull ChemicalStack stack) {
        this.tanks[tank].setStack(stack);
    }

    @Override
    public long getChemicalTankCapacity(int tank) {
        return this.tanks[tank].getCapacity();
    }

    @Override
    public boolean isValid(int tank, @NotNull ChemicalStack stack) {
        return this.tanks[tank].isValid(stack);
    }

    @Override
    public @NotNull ChemicalStack insertChemical(int tank, @NotNull ChemicalStack stack, @NotNull Action action) {
        return this.tanks[tank].insertChemical(stack, action);
    }

    @Override
    public @NotNull ChemicalStack extractChemical(int tank, long amount, @NotNull Action action) {
        return this.tanks[tank].extractChemical(amount, action);
    }

}
