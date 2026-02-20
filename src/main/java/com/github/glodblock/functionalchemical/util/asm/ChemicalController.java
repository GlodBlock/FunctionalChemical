package com.github.glodblock.functionalchemical.util.asm;

import com.github.glodblock.functionalchemical.common.inventory.ControllerChemicalHandler;
import com.github.glodblock.functionalchemical.util.ChemType;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;

public interface ChemicalController {

    default ChemicalModule getChemicalModule() {
        return null;
    }

    default <C extends Chemical<C>, S extends ChemicalStack<C>> ControllerChemicalHandler<C, S> getHandler(ChemType type) {
        return null;
    }

}
