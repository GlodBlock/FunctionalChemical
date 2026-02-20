package com.github.glodblock.functionalchemical.common.inventory;

import com.github.glodblock.functionalchemical.util.ChemType;
import com.github.glodblock.functionalchemical.util.asm.ChemicalController;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ControllerChemicalHandler<C extends Chemical<C>, S extends ChemicalStack<C>> implements IChemicalHandler<C, S> {

    private final List<IndexedHandler<C, S>> handlers = new ArrayList<>();
    private final ChemicalController controller;
    private final ChemType type;

    public ControllerChemicalHandler(ChemType type, ChemicalController controller) {
        this.type = type;
        this.controller = controller;
        this.refresh();
    }

    @SuppressWarnings("unchecked")
    public void refresh() {
        this.handlers.clear();
        for (var handler : this.controller.getChemicalModule().getHandlers(this.type)) {
            for (int x = 0; x < handler.getTanks(); x++) {
                this.handlers.add(new IndexedHandler<>((IChemicalHandler<C, S>) handler, x));
            }
        }
    }

    private boolean checkBounds(int index) {
        return index >= 0 && index < handlers.size();
    }

    @Override
    public int getTanks() {
        return this.handlers.size();
    }

    @Override
    public @NotNull S getChemicalInTank(int tank) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            return handler.handler.getChemicalInTank(handler.index);
        } else {
            return this.type.empty();
        }
    }

    @Override
    public void setChemicalInTank(int tank, @NotNull S stack) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            handler.handler.setChemicalInTank(handler.index, stack);
        }
    }

    @Override
    public long getTankCapacity(int tank) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            return handler.handler.getTankCapacity(handler.index);
        } else {
            return 0;
        }
    }

    @Override
    public boolean isValid(int tank, @NotNull S stack) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            return handler.handler.isValid(handler.index, stack);
        } else {
            return false;
        }
    }

    @Override
    public @NotNull S insertChemical(int tank, @NotNull S stack, @NotNull Action action) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            return handler.handler.insertChemical(handler.index, stack, action);
        } else {
            return stack;
        }
    }

    @Override
    public @NotNull S extractChemical(int tank, long amount, @NotNull Action action) {
        if (this.checkBounds(tank)) {
            var handler = this.handlers.get(tank);
            return handler.handler.extractChemical(handler.index, amount, action);
        } else {
            return this.type.empty();
        }
    }

    @Override
    public @NotNull S getEmptyStack() {
        return this.type.empty();
    }

    private record IndexedHandler<C extends Chemical<C>, S extends ChemicalStack<C>>(IChemicalHandler<C, S> handler, int index) {}

}
