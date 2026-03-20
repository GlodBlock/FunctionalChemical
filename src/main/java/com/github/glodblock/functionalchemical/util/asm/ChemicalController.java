package com.github.glodblock.functionalchemical.util.asm;

import com.github.glodblock.functionalchemical.common.inventory.ControllerChemicalHandler;

public interface ChemicalController {

    default ChemicalModule getChemicalModule() {
        return null;
    }

    default ControllerChemicalHandler getHandler() {
        return null;
    }

}
