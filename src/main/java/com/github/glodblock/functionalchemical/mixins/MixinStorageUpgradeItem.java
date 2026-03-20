package com.github.glodblock.functionalchemical.mixins;

import com.buuz135.functionalstorage.block.config.FunctionalStorageConfig;
import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import com.buuz135.functionalstorage.item.component.SizeProvider;
import com.github.glodblock.functionalchemical.common.FCSingletons;
import com.github.glodblock.functionalchemical.config.FunctionalChemicalConfig;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StorageUpgradeItem.class)
public abstract class MixinStorageUpgradeItem {

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/buuz135/functionalstorage/item/StorageUpgradeItem;getProps(Lcom/buuz135/functionalstorage/item/StorageUpgradeItem$StorageTier;)Lnet/minecraft/world/item/Item$Properties;"
            ),
            remap = false
    )
    private static Item.Properties addChemicalStorageModifier(StorageUpgradeItem.StorageTier level) {
        var prop = getProps(level);
        if (level != StorageUpgradeItem.StorageTier.IRON) {
            prop.component(FCSingletons::getChemStorageModifier, new SizeProvider.ModifyFactor((float) (FunctionalStorageConfig.getLevelMult(level.getLevel()) / FunctionalChemicalConfig.CHEMICAL_DIVISOR)));
        }
        return prop;
    }

    @Shadow
    private static Item.Properties getProps(StorageUpgradeItem.StorageTier tier) {
        return new Item.Properties();
    }

}
