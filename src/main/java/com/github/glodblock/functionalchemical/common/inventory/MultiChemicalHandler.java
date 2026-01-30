package com.github.glodblock.functionalchemical.common.inventory;

import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import com.github.glodblock.functionalchemical.util.ChemType;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntSupplier;

public abstract class MultiChemicalHandler<C extends Chemical<C>, S extends ChemicalStack<C>> implements IChemicalHandler<C, S> {

    private final ChemicalDrawerTile.SyncTank tanks;
    private final IntSupplier capacity;
    private final int size;
    private final ChemType type;

    public MultiChemicalHandler(int size, ChemicalDrawerTile.SyncTank tanks, IntSupplier capacity, ChemType type) {
        this.size = size;
        this.tanks = tanks;
        this.type = type;
        this.capacity = capacity;
    }

    public abstract void onChange();

    public abstract boolean isDrawerLocked();

    public abstract boolean isDrawerVoid();

    public abstract boolean isDrawerCreative();

    @Override
    public int getTanks() {
        return this.size;
    }

    private boolean checkType(int tank) {
        return this.type.check(this.tanks.tanks()[tank].getCurrent());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull S getChemicalInTank(int tank) {
        var poj = this.tanks.tanks()[tank];
        if (this.checkType(tank)) {
            return (S) poj.getTankForType(this.type.getNativeType()).getStack();
        } else {
            return this.type.empty();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void setChemicalInTank(int tank, @NotNull S stack) {
        var poj = this.tanks.tanks()[tank];
        if (this.checkType(tank)) {
            ((IChemicalTank) poj.getTankForType(this.type.getNativeType())).setStack(stack);
        }
    }

    @Override
    public long getTankCapacity(int tank) {
        return this.capacity.getAsInt();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public boolean isValid(int tank, @NotNull S stack) {
        if (this.checkType(tank)) {
            var poj = this.tanks.tanks()[tank];
            return ((IChemicalTank) poj.getTankForType(this.type.getNativeType())).isValid(stack);
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public @NotNull S insertChemical(int tank, @NotNull S stack, @NotNull Action action) {
        if (this.checkType(tank)) {
            var poj = this.tanks.tanks()[tank];
            return (S) ((IChemicalTank) poj.getTankForType(this.type.getNativeType())).insert(stack, action, AutomationType.EXTERNAL);
        }
        return stack;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull S extractChemical(int tank, long amount, @NotNull Action action) {
        if (this.checkType(tank)) {
            var poj = this.tanks.tanks()[tank];
            return (S) poj.getTankForType(this.type.getNativeType()).extract(amount, action, AutomationType.EXTERNAL);
        }
        return this.type.empty();
    }

    @Override
    public @NotNull S getEmptyStack() {
        return this.type.empty();
    }

}
