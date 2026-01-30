package com.github.glodblock.functionalchemical.util;

import com.github.glodblock.functionalchemical.common.inventory.ChemicalDrawerTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;

@SuppressWarnings("unchecked")
public enum ChemType {

    GAS(0, GasStack.EMPTY, ChemicalDrawerTank.GasDrawerTank::new, ChemicalType.GAS),
    INFUSE(1, InfusionStack.EMPTY, ChemicalDrawerTank.InfuseDrawerTank::new, ChemicalType.INFUSION),
    PIGMENT(2, PigmentStack.EMPTY, ChemicalDrawerTank.PigmentDrawerTank::new, ChemicalType.PIGMENT),
    SLURRY(3, SlurryStack.EMPTY, ChemicalDrawerTank.SlurryDrawerTank::new, ChemicalType.SLURRY),;

    final int id;
    final ChemicalStack<?> empty;
    final ChemicalDrawerTank.DrawerTankFactory tankBuilder;
    final ChemicalType type;

    ChemType(int id, ChemicalStack<?> empty, ChemicalDrawerTank.DrawerTankFactory tankBuilder, ChemicalType mekType) {
        this.id = id;
        this.empty = empty;
        this.tankBuilder = tankBuilder;
        this.type = mekType;
    }

    public static ChemType fromId(int id) {
        return switch (id) {
            case 0 -> GAS;
            case 1 -> INFUSE;
            case 2 -> PIGMENT;
            case 3 -> SLURRY;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    public int getId() {
        return id;
    }

    public <C extends Chemical<C>, S extends ChemicalStack<C>> S empty() {
        return (S) this.empty;
    }

    public ChemicalDrawerTank.DrawerTankFactory tankBuilder() {
        return this.tankBuilder;
    }

    public boolean check(MergedChemicalTank.Current type) {
        return switch (type) {
            case GAS -> this == GAS;
            case INFUSION -> this == INFUSE;
            case PIGMENT -> this == PIGMENT;
            case SLURRY -> this == SLURRY;
            case EMPTY -> true;
        };
    }

    public ChemicalType getNativeType() {
        return this.type;
    }

}
