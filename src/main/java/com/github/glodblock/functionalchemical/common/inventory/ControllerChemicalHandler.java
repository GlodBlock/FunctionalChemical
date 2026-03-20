package com.github.glodblock.functionalchemical.common.inventory;

import com.github.glodblock.functionalchemical.util.asm.ChemicalController;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ControllerChemicalHandler implements IChemicalHandler {

    private final List<IndexedHandler> handlers = new ArrayList<>();
    private final ChemicalController controller;

    public ControllerChemicalHandler(ChemicalController controller) {
        this.controller = controller;
        this.refresh();
    }

    public void refresh() {
        this.handlers.clear();
        for (var handler : this.controller.getChemicalModule().getHandlers()) {
            for (int x = 0; x < handler.getChemicalTanks(); x++) {
                this.handlers.add(new IndexedHandler(handler, x));
            }
        }
    }

    private boolean checkBounds(int index) {
        return index >= 0 && index < handlers.size();
    }

    @Override
    public int getChemicalTanks() {
        return this.handlers.size();
    }

    @Override
    public @NotNull ChemicalStack getChemicalInTank(int tank) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            return handler.handler.getChemicalInTank(handler.index);
        } else {
            return ChemicalStack.EMPTY;
        }
    }

    @Override
    public void setChemicalInTank(int tank, @NotNull ChemicalStack stack) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            handler.handler.setChemicalInTank(handler.index, stack);
        }
    }

    @Override
    public long getChemicalTankCapacity(int tank) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            return handler.handler.getChemicalTankCapacity(handler.index);
        } else {
            return 0;
        }
    }

    @Override
    public boolean isValid(int tank, @NotNull ChemicalStack stack) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            return handler.handler.isValid(handler.index, stack);
        } else {
            return false;
        }
    }

    @Override
    public @NotNull ChemicalStack insertChemical(int tank, @NotNull ChemicalStack stack, @NotNull Action action) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            return handler.handler.insertChemical(handler.index, stack, action);
        } else {
            return stack;
        }
    }

    @Override
    public @NotNull ChemicalStack extractChemical(int tank, long amount, @NotNull Action action) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            return handler.handler.extractChemical(handler.index, amount, action);
        } else {
            return ChemicalStack.EMPTY;
        }
    }

    private record IndexedHandler(IChemicalHandler handler, int index) {}

}
