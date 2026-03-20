package com.github.glodblock.functionalchemical.common.items;

import com.buuz135.functionalstorage.item.UpgradeItem;
import com.github.glodblock.functionalchemical.common.FCSingletons;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DecayUpgradeItem extends UpgradeItem {

    final boolean advanced;

    public DecayUpgradeItem(Properties properties, boolean advanced) {
        super(properties, UpgradeItem.Type.UTILITY);
        this.setItemGroup(FCSingletons.TAB);
        this.advanced = advanced;
    }

    @Override
    public void setItemGroup(@NotNull TitaniumTab tap) {
        if (tap == FCSingletons.TAB) {
            super.setItemGroup(tap);
        }
    }

    @Override
    public void addTooltipDetails(BasicItem.@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        tooltip.add(Component.translatable("tooltip.functionalchemical.decay_upgrade").withStyle(ChatFormatting.GRAY));
        if (this.advanced) {
            tooltip.add(Component.translatable("tooltip.functionalchemical.adv_decay_upgrade").withStyle(ChatFormatting.GOLD));
        }
    }

    public final int getSpeed() {
        if (this.advanced) {
            return 10;
        }
        return 1;
    }

}
