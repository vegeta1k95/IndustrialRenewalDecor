package com.cassiokf.irdecor;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static float razorWireDamage = 1.0f;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        razorWireDamage = (float) configuration
            .get(Configuration.CATEGORY_GENERAL, "razorWireDamage", 1.0, "Damage dealt by razor wire")
            .getDouble();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
