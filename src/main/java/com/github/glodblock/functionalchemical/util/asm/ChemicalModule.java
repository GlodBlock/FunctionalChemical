package com.github.glodblock.functionalchemical.util.asm;

import mekanism.api.chemical.IChemicalHandler;

import java.util.List;

public interface ChemicalModule {

    default List<IChemicalHandler> getHandlers() {
        return List.of();
    }

    default int size() {
        return 0;
    }

}
