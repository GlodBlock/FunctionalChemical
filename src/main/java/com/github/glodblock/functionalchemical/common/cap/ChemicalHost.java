package com.github.glodblock.functionalchemical.common.cap;

import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;

public interface ChemicalHost {

    IChemicalHandler getChemicalHandler();

    default IChemicalHandler getChemicalHandler(Direction side) {
        return getChemicalHandler();
    }

}
