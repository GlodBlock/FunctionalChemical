package com.github.glodblock.functionalchemical.mixins;

import com.buuz135.functionalstorage.client.ClientSetup;
import com.github.glodblock.functionalchemical.common.FCSingletons;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientSetup.class)
public abstract class MixinClientSetup {

    @Inject(
            method = "init",
            at = @At("TAIL"),
            remap = false
    )
    private static void addChemicalTooltip(CallbackInfo ci) {
        EventManager.forge(ItemTooltipEvent.class).process((event) -> {
            var stack = event.getItemStack();
            var tooltip = event.getToolTip();
            var chem = stack.get(FCSingletons::getChemStorageModifier);
            if (chem != null) {
                final var size = tooltip.size();
                final var chemTip = chem.getTooltip(Component.translatable("storageupgrade.obj.chem_storage")).copy().withStyle(ChatFormatting.GRAY);
                if (size >= 4) {
                    tooltip.add(4, chemTip);
                } else {
                    tooltip.add(chemTip);
                }
            }
        }).subscribe();
    }

}
