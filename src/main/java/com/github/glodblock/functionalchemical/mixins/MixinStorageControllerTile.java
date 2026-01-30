package com.github.glodblock.functionalchemical.mixins;

import com.buuz135.functionalstorage.block.tile.StorageControllerTile;
import com.buuz135.functionalstorage.util.ConnectedDrawers;
import com.github.glodblock.functionalchemical.common.inventory.ControllerChemicalHandler;
import com.github.glodblock.functionalchemical.util.ChemType;
import com.github.glodblock.functionalchemical.util.asm.ChemicalController;
import com.github.glodblock.functionalchemical.util.asm.ChemicalModule;
import com.hrznstudio.titanium.block.BasicTileBlock;
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
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(StorageControllerTile.class)
public abstract class MixinStorageControllerTile implements ChemicalController {

    @Shadow(remap = false)
    protected ConnectedDrawers connectedDrawers;
    @Unique
    private ControllerChemicalHandler<Gas, GasStack> gasHandler;
    @Unique
    private ControllerChemicalHandler<InfuseType, InfusionStack> infusionHandler;
    @Unique
    private ControllerChemicalHandler<Pigment, PigmentStack> pigmentHandler;
    @Unique
    private ControllerChemicalHandler<Slurry, SlurryStack> slurryHandler;
    @Unique
    private LazyOptional<IChemicalHandler<Gas, GasStack>> gasHandlerCap;
    @Unique
    private LazyOptional<IChemicalHandler<InfuseType, InfusionStack>> infusionHandlerCap;
    @Unique
    private LazyOptional<IChemicalHandler<Pigment, PigmentStack>> pigmentHandlerCap;
    @Unique
    private LazyOptional<IChemicalHandler<Slurry, SlurryStack>> slurryHandlerCap;

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            remap = false
    )
    private void init(BasicTileBlock<?> base, BlockEntityType<?> entityType, BlockPos pos, BlockState state, CallbackInfo ci) {
        this.gasHandler = new ControllerChemicalHandler<>(ChemType.GAS, this);
        this.infusionHandler = new ControllerChemicalHandler<>(ChemType.INFUSE, this);
        this.pigmentHandler = new ControllerChemicalHandler<>(ChemType.PIGMENT, this);
        this.slurryHandler = new ControllerChemicalHandler<>(ChemType.SLURRY, this);
        this.gasHandlerCap = LazyOptional.of(() -> this.gasHandler);
        this.infusionHandlerCap = LazyOptional.of(() -> this.infusionHandler);
        this.pigmentHandlerCap = LazyOptional.of(() -> this.pigmentHandler);
        this.slurryHandlerCap = LazyOptional.of(() -> this.slurryHandler);
    }

    @Redirect(
            method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lcom/buuz135/functionalstorage/block/tile/StorageControllerTile;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/buuz135/functionalstorage/util/ConnectedDrawers;getExtensions()I"
            ),
            remap = false
    )
    private int compareSize(ConnectedDrawers instance) {
        return instance.getExtensions() + this.getChemicalModule().size();
    }

    @Inject(
            method = "getCapability",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private <U> void getChemicalCap(Capability<U> cap, Direction side, CallbackInfoReturnable<LazyOptional<U>> cir) {
        if (cap == Capabilities.GAS_HANDLER) {
            cir.setReturnValue(this.gasHandlerCap.cast());
            cir.cancel();
        } else if (cap == Capabilities.INFUSION_HANDLER) {
            cir.setReturnValue(this.infusionHandlerCap.cast());
            cir.cancel();
        } else if (cap == Capabilities.PIGMENT_HANDLER) {
            cir.setReturnValue(this.pigmentHandlerCap.cast());
            cir.cancel();
        } else if (cap == Capabilities.SLURRY_HANDLER) {
            cir.setReturnValue(this.slurryHandlerCap.cast());
            cir.cancel();
        }
    }

    @Inject(
            method = "invalidateCaps",
            at = @At("TAIL"),
            remap = false
    )
    private void onInvalidate(CallbackInfo ci) {
        this.gasHandlerCap.invalidate();
        this.infusionHandlerCap.invalidate();
        this.pigmentHandlerCap.invalidate();
        this.slurryHandlerCap.invalidate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Chemical<C>, S extends ChemicalStack<C>> ControllerChemicalHandler<C, S> getHandler(ChemType type) {
        return (ControllerChemicalHandler<C, S>) switch (type) {
            case GAS -> this.gasHandler;
            case INFUSE -> this.infusionHandler;
            case PIGMENT -> this.pigmentHandler;
            case SLURRY -> this.slurryHandler;
        };
    }

    @Unique
    public ChemicalModule getChemicalModule() {
        return (ChemicalModule) this.connectedDrawers;
    }

}
