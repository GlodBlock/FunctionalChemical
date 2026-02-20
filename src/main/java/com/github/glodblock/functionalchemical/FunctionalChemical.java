package com.github.glodblock.functionalchemical;

import com.github.glodblock.functionalchemical.client.ClientRegistryHandler;
import com.github.glodblock.functionalchemical.common.FCItemAndBlock;
import com.github.glodblock.functionalchemical.common.FCRegistryHandler;
import com.github.glodblock.functionalchemical.common.blocks.ChemicalDrawerBlock;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(FunctionalChemical.MODID)
public class FunctionalChemical extends ModuleController {

    public static final String MODID = "functionalchemical";
    public static FunctionalChemical INSTANCE;

    public FunctionalChemical() {
        assert INSTANCE == null;
        INSTANCE = this;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey().equals(Registries.BLOCK)) {
                FCItemAndBlock.init(FCRegistryHandler.INSTANCE);
                FCRegistryHandler.INSTANCE.runRegister(e);
            }
        });
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.register(ClientRegistryHandler.INSTANCE));
        EventManager.forge(BlockEvent.BreakEvent.class).process(event -> {
            if (event.getState().getBlock() instanceof ChemicalDrawerBlock block) {
                int hit = block.getHit(event.getState(), event.getPlayer().level(), event.getPlayer());
                if (hit != -1) {
                    event.setCanceled(true);
                    block.attack(event.getState(), event.getPlayer().level(), event.getPos(), event.getPlayer());
                }
            }
        }).subscribe();
    }

    @Override
    protected void initModules() {
        this.addCreativeTab("main", () -> new ItemStack(FCItemAndBlock.CHEM_DRAWER_1), MODID, FCItemAndBlock.TAB);
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

}
