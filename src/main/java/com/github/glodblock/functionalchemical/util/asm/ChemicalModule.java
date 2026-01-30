package com.github.glodblock.functionalchemical.util.asm;

import com.github.glodblock.functionalchemical.util.ChemType;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;

import java.util.List;

public interface ChemicalModule {

    default <C extends Chemical<C>, S extends ChemicalStack<C>> List<IChemicalHandler<C, S>> getHandlers(ChemType type) {
        return List.of();
    }

    default int size() {
        return 0;
    }

}
