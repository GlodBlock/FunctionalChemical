package com.github.glodblock.functionalchemical.mixins;

import com.buuz135.functionalstorage.block.tile.StorageControllerTile;
import com.buuz135.functionalstorage.util.ConnectedDrawers;
import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import com.github.glodblock.functionalchemical.util.asm.ChemicalController;
import com.github.glodblock.functionalchemical.util.asm.ChemicalModule;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ConnectedDrawers.class)
public abstract class MixinConnectedDrawers implements ChemicalModule {

    @Final
    @Shadow(remap = false)
    private StorageControllerTile<?> controllerTile;
    @Unique
    private List<IChemicalHandler> chemHandlers;

    @Unique
    private void init() {
        this.chemHandlers = new ArrayList<>();
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            remap = false
    )
    private void onConstruct(Level level, StorageControllerTile<?> controllerTile, CallbackInfo ci) {
        this.init();
    }

    @Inject(
            method = "rebuild",
            at = @At("HEAD"),
            remap = false
    )
    private void onRebuild(CallbackInfo ci) {
        this.init();
    }

    @Redirect(
            method = "rebuild",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
                    remap = true
            ),
            remap = false
    )
    private BlockEntity addChemicalHandler(Level world, BlockPos pos) {
        var te = world.getBlockEntity(pos);
        if (te instanceof ChemicalDrawerTile drawer) {
            this.chemHandlers.add(drawer.getChemicalHandler());
            return null;
        } else {
            return te;
        }
    }

    @Inject(
            method = "rebuild",
            at = @At("TAIL"),
            remap = false
    )
    private void afterRebuild(CallbackInfo ci) {
        ChemicalController controller = (ChemicalController) this.controllerTile;
        var handler = controller.getHandler();
        if (handler != null) {
            handler.refresh();
        }
    }

    @Override
    public List<IChemicalHandler> getHandlers() {
        return this.chemHandlers;
    }

    @Override
    public int size() {
        return this.chemHandlers.size();
    }

}
