package com.cassiokf.irdecor;

import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelRegistry.registerModid(IRDecor.MODID);
    }
}
