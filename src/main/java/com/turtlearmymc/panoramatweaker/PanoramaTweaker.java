package com.turtlearmymc.panoramatweaker;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;

public class PanoramaTweaker implements ClientModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "panorama_tweaker";
    public static final String MOD_NAME = "Panorama Tweaker";

    public static Config config;

    @Override
    public void onInitializeClient() {
        log(Level.INFO, "Initializing");
        config = Config.loadOrCreate();
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
}