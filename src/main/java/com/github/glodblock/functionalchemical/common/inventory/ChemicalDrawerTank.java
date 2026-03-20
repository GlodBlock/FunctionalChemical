package com.github.glodblock.functionalchemical.common.inventory;

import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.functions.ConstantPredicates;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ChemicalDrawerTank extends BasicChemicalTank {

    private final Supplier<ChemicalDrawerTile> host;
    private long capacity;

    public ChemicalDrawerTank(long capacity, Supplier<ChemicalDrawerTile> host, Predicate<ChemicalStack> filter) {
        this(capacity, host, filter, null);
    }

    public ChemicalDrawerTank(long capacity, Supplier<ChemicalDrawerTile> host, Predicate<ChemicalStack> filter, ChemicalAttributeValidator validator) {
        super(0, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrueBi(), filter, validator, () -> host.get().onChange(), null);
        this.host = host;
        this.capacity = capacity;
    }

    public void setCapacity(long capacity) {
        if (capacity < this.capacity) {
            var stack = super.getStack();
            if (stack.getAmount() > capacity) {
                stack.setAmount(capacity);
            }
        }
        this.capacity = capacity;
    }

    @Override
    public long getCapacity() {
        if (this.host.get().isCreative()) {
            return Long.MAX_VALUE;
        }
        return this.capacity;
    }

    @Override
    public @NotNull ChemicalStack insert(@NotNull ChemicalStack stack, @NotNull Action action, @NotNull AutomationType type) {
        var overflow = super.insert(stack, action, type);
        if (this.host.get().isVoid() &&
                ((this.host.get().isLocked() && this.isValid(stack)) ||
                 (!this.getStack().isEmpty() && this.isTypeEqual(stack))))
            return ChemicalStack.EMPTY;
        return overflow;
    }

    @Override
    public @NotNull ChemicalStack extract(long amount, @NotNull Action action, @NotNull AutomationType type) {
        if (this.host.get().isCreative()) {
            var extracted = super.extract(1, Action.SIMULATE, type);
            extracted.setAmount(amount);
            return extracted;
        }
        return super.extract(amount, action, type);
    }

    @Override
    public long getStored() {
        var stored = super.getStored();
        if (stored > 0 && this.host.get().isCreative()) {
            return Long.MAX_VALUE;
        }
        return stored;
    }

    @Override
    public @NotNull ChemicalStack getStack() {
        var stack = super.getStack();
        if (!stack.isEmpty() && this.host.get().isCreative()) {
            var copy = stack.copy();
            copy.setAmount(Long.MAX_VALUE);
            return copy;
        }
        return stack;
    }

}
