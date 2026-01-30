package com.github.glodblock.functionalchemical;

import com.github.glodblock.functionalchemical.client.ClientRegistryHandler;
import com.github.glodblock.functionalchemical.common.FCItemAndBlock;
import com.github.glodblock.functionalchemical.common.blocks.ChemicalDrawerBlock;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FunctionalChemical.MODID)
public class FunctionalChemical extends ModuleController {

    public static final String MODID = "functionalchemical";
    public static FunctionalChemical INSTANCE;

    public FunctionalChemical() {
        assert INSTANCE == null;
        INSTANCE = this;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.register(ClientRegistryHandler.INSTANCE));
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        EventManager.forge(BlockEvent.BreakEvent.class).process(event -> {
            if (event.getState().getBlock() instanceof ChemicalDrawerBlock block) {
                int hit = block.getHit(event.getState(), event.getPlayer().getLevel(), event.getPos(), event.getPlayer());
                if (hit != -1) {
                    event.setCanceled(true);
                    block.attack(event.getState(), event.getPlayer().getLevel(), event.getPos(), event.getPlayer());
                }
            }
        }).subscribe();
    }

    @Override
    protected void initModules() {
        FCItemAndBlock.init(this.getRegistries());
    }

    public void commonSetup(FMLCommonSetupEvent event) {

    }

    public void clientSetup(FMLClientSetupEvent event) {
        ClientRegistryHandler.INSTANCE.init();
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

}
