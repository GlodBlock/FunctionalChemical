package com.github.glodblock.functionalchemical.util;

import com.buuz135.functionalstorage.util.NumberUtils;

import java.text.DecimalFormat;

public class FCUtil {

    private static final DecimalFormat F = new DecimalFormat("####0.#");

    public static String getFormatedChemBigNumber(long number) {
        if (number <= Integer.MAX_VALUE) {
            return NumberUtils.getFormatedFluidBigNumber((int) number);
        }
        if (number <= 1_000_000_000_000L) {
            double show = (double) number / 1_000_000_000L;
            return F.format(show) + "M B";
        }
        if (number <= 1_000_000_000_000_000L) {
            double show = (double) number / 1_000_000_000_000L;
            return F.format(show) + "G B";
        }
        if (number <= 1_000_000_000_000_000_000L) {
            double show = (double) number / 1_000_000_000_000_000L;
            return F.format(show) + "T B";
        }
        double show = (double) number / 1_000_000_000_000_000_000L;
        return F.format(show) + "P B";
    }

}
