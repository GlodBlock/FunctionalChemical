package com.github.glodblock.functionalchemical.mixins;

import com.hrznstudio.titanium.block.tile.ActiveTile;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.text.ILangEntry;
import mekanism.common.capabilities.merged.MergedTank;
import mekanism.common.integration.lookingat.LookingAtHelper;
import mekanism.common.integration.lookingat.LookingAtUtils;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.util.CapabilityUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Mixin(LookingAtUtils.class)
public abstract class MixinLookingAtUtils {

    @Shadow(remap = false)
    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> void addChemicalInfo(LookingAtHelper info, ILangEntry langEntry, STACK chemicalInTank, long capacity) {
    }

    @Inject(
            method = "addInfo(Lnet/minecraft/world/level/block/entity/BlockEntity;Lmekanism/common/lib/multiblock/MultiblockData;Lnet/minecraftforge/common/capabilities/Capability;Ljava/util/function/Function;Lmekanism/common/integration/lookingat/LookingAtHelper;Lmekanism/api/text/ILangEntry;Lmekanism/api/chemical/merged/MergedChemicalTank$Current;Lmekanism/common/capabilities/merged/MergedTank$CurrentType;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>, HANDLER extends IChemicalHandler<CHEMICAL, STACK>> void filterEmptyDrawer(BlockEntity tile, @Nullable MultiblockData structure, Capability<HANDLER> capability, Function<MultiblockData, List<TANK>> multiBlockToTanks, LookingAtHelper info, ILangEntry langEntry, MergedChemicalTank.Current matchingCurrent, MergedTank.CurrentType matchingCurrentType, CallbackInfo ci) {
        if (tile instanceof ActiveTile<?>) {
            Optional<HANDLER> cap = CapabilityUtils.getCapability(tile, capability, null).resolve();
            if (cap.isPresent()) {
                var handler = cap.get();
                for (int i = 0; i < handler.getTanks(); i++) {
                    if (!handler.getChemicalInTank(i).isEmpty()) {
                        addChemicalInfo(info, langEntry, handler.getChemicalInTank(i), handler.getTankCapacity(i));
                    }
                }
            }
            ci.cancel();
        }

    }

}
