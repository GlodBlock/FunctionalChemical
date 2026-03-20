package com.github.glodblock.functionalchemical.config;

import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile("functionalchemical-common")
public class FunctionalChemicalConfig {

    @ConfigVal(
            comment = "How much chemical (in mb) the pulling upgrade will try to pull"
    )
    public static int UPGRADE_PULL_CHEMICAL = 4000;
    @ConfigVal(
            comment = "How much chemical (in mb) the pushing upgrade will try to pull"
    )
    public static int UPGRADE_PUSH_CHEMICAL = 4000;
    @ConfigVal(
            comment = "How much should the chemical storage be divided by for any given Storage Upgrade"
    )
    @ConfigVal.InRangeDouble(
            min = 1
    )
    public static double CHEMICAL_DIVISOR = 1;

}
