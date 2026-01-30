package com.github.glodblock.functionalchemical.common.inventory;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.functions.ConstantPredicates;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class ChemicalDrawerTank<C extends Chemical<C>, S extends ChemicalStack<C>> extends BasicChemicalTank<C, S> {

    private final Supplier<MultiChemicalHandler<C, S>> handler;
    private long capacity;

    protected ChemicalDrawerTank(long capacity, Predicate<C> filter, Supplier<MultiChemicalHandler<C, S>> handler) {
        this(capacity, filter, handler, null);
    }

    protected ChemicalDrawerTank(long capacity, Predicate<C> filter, Supplier<MultiChemicalHandler<C, S>> handler, ChemicalAttributeValidator validator) {
        super(0, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrueBi(), filter, validator, null);
        this.handler = handler;
        this.capacity = capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public void onContentsChanged() {
        this.handler.get().onChange();
    }

    @Override
    public long getCapacity() {
        if (this.handler.get().isDrawerCreative()) {
            return Integer.MAX_VALUE;
        }
        return this.capacity;
    }

    @Override
    public @NotNull S insert(@NotNull S stack, @NotNull Action action, @NotNull AutomationType type) {
        var overflow = super.insert(stack, action, type);
        if (this.handler.get().isDrawerVoid() &&
                ((this.handler.get().isDrawerLocked() && this.isValid(stack)) ||
                 (!this.getStack().isEmpty() && this.isTypeEqual(stack))))
            return this.getEmptyStack();
        return overflow;
    }

    @Override
    public @NotNull S extract(long amount, @NotNull Action action, @NotNull AutomationType type) {
        if (this.handler.get().isDrawerCreative()) {
            var extracted = super.extract(1, Action.SIMULATE, type);
            extracted.setAmount(amount);
            return extracted;
        }
        return super.extract(amount, action, type);
    }

    @Override
    public long getStored() {
        var stored = super.getStored();
        if (stored > 0 && this.handler.get().isDrawerCreative()) {
            return Integer.MAX_VALUE;
        }
        return stored;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull S getStack() {
        var stack = super.getStack();
        if (!stack.isEmpty() && this.handler.get().isDrawerCreative()) {
            var copy = stack.copy();
            copy.setAmount(Integer.MAX_VALUE);
            return (S) copy;
        }
        return stack;
    }

    public static class GasDrawerTank extends ChemicalDrawerTank<Gas, GasStack> implements IGasTank {

        public GasDrawerTank(long capacity, Predicate<Gas> filter, Supplier<MultiChemicalHandler<Gas, GasStack>> handler) {
            super(capacity, filter, handler);
        }

    }

    public static class InfuseDrawerTank extends ChemicalDrawerTank<InfuseType, InfusionStack> implements IInfusionTank {

        public InfuseDrawerTank(long capacity, Predicate<InfuseType> filter, Supplier<MultiChemicalHandler<InfuseType, InfusionStack>> handler) {
            super(capacity, filter, handler);
        }

    }

    public static class PigmentDrawerTank extends ChemicalDrawerTank<Pigment, PigmentStack> implements IPigmentTank {

        public PigmentDrawerTank(long capacity, Predicate<Pigment> filter, Supplier<MultiChemicalHandler<Pigment, PigmentStack>> handler) {
            super(capacity, filter, handler);
        }

    }

    public static class SlurryDrawerTank extends ChemicalDrawerTank<Slurry, SlurryStack> implements ISlurryTank {

        public SlurryDrawerTank(long capacity, Predicate<Slurry> filter, Supplier<MultiChemicalHandler<Slurry, SlurryStack>> handler) {
            super(capacity, filter, handler);
        }

    }

    @SuppressWarnings("rawtypes")
    public interface DrawerTankFactory {

        ChemicalDrawerTank create(long capacity, Predicate filter, Supplier handler);

    }

}
