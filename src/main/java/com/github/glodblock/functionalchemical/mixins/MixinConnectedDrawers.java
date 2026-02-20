package com.github.glodblock.functionalchemical.mixins;

import com.buuz135.functionalstorage.block.tile.StorageControllerTile;
import com.buuz135.functionalstorage.util.ConnectedDrawers;
import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import com.github.glodblock.functionalchemical.util.ChemType;
import com.github.glodblock.functionalchemical.util.asm.ChemicalController;
import com.github.glodblock.functionalchemical.util.asm.ChemicalModule;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    @Shadow(remap = false)
    private StorageControllerTile<?> controllerTile;
    @Unique
    private List<IChemicalHandler<Gas, GasStack>> gasHandlers;
    @Unique
    private List<IChemicalHandler<InfuseType, InfusionStack>> infuseHandlers;
    @Unique
    private List<IChemicalHandler<Pigment, PigmentStack>> pigmentHandlers;
    @Unique
    private List<IChemicalHandler<Slurry, SlurryStack>> slurryHandlers;

    @Unique
    private void init() {
        this.gasHandlers = new ArrayList<>();
        this.infuseHandlers = new ArrayList<>();
        this.pigmentHandlers = new ArrayList<>();
        this.slurryHandlers = new ArrayList<>();
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
            this.gasHandlers.add(drawer.getGasHandler());
            this.infuseHandlers.add(drawer.getInfuseHandler());
            this.pigmentHandlers.add(drawer.getPigmentHandler());
            this.slurryHandlers.add(drawer.getSlurryHandler());
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
        for (var type : ChemType.values()) {
            var handler = controller.getHandler(type);
            if (handler != null) {
                handler.refresh();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Chemical<C>, S extends ChemicalStack<C>> List<IChemicalHandler<C, S>> getHandlers(ChemType type) {
        return (List<IChemicalHandler<C, S>>) switch (type) {
            case GAS -> this.gasHandlers;
            case INFUSE -> this.infuseHandlers;
            case PIGMENT -> this.pigmentHandlers;
            case SLURRY -> this.slurryHandlers;
        };
    }

    @Override
    public int size() {
        return this.gasHandlers.size();
    }

}
