package com.github.glodblock.functionalchemical.common.inventory;

import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.datamaps.chemical.attribute.ChemicalRadioactivity;
import mekanism.api.datamaps.chemical.attribute.IChemicalAttribute;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class RadioactiveDrawerTank extends ChemicalDrawerTank {

    @SuppressWarnings("removal")
    public static final ChemicalAttributeValidator VALIDATOR = new ChemicalAttributeValidator.ChemicalAttributeValidatorLegacyAdapter() {

        @Override
        public boolean validate(IChemicalAttribute attr) {
            return attr instanceof ChemicalRadioactivity;
        }

        @Override
        public boolean process(Chemical chemical) {
            return chemical.isRadioactive();
        }

    };

    public RadioactiveDrawerTank(long capacity, Supplier<ChemicalDrawerTile> host, Predicate<ChemicalStack> filter) {
        super(capacity, host, filter, VALIDATOR);
    }

    @Override
    public boolean isValid(@NotNull ChemicalStack stack) {
        if (stack.isRadioactive()) {
            return super.isValid(stack);
        } else {
            return false;
        }
    }

}
