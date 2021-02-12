package com.turtlearmymc.panoramatweaker;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.apache.logging.log4j.Level;

import net.fabricmc.loader.api.FabricLoader;

public class Config {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("panorama_tweaker.json")
            .toFile();

    public static final float DEFAULT_ROTATION_SPEED = 1;
    public static final float DEFAULT_STARTING_HORIZONTAL_ANGLE = 0;
    public static final float DEFAULT_VERTICAL_ANGLE = -25;
    public static final float DEFAULT_SWAY_ANGLE = 5;
    public static final float DEFAULT_SWAY_SPEED = 1;
    public static final float DEFAULT_INITIAL_SWAY_PROGRESS = 0;

    public float rotationSpeed;
    public float startingHorizontalAngle;
    public float verticalAngle;
    public float swayAngle;
    public float swaySpeed;
    public float initialSwayProgress;

    public Config() {
        this.rotationSpeed = DEFAULT_ROTATION_SPEED;
        this.startingHorizontalAngle = DEFAULT_STARTING_HORIZONTAL_ANGLE;
        this.verticalAngle = DEFAULT_VERTICAL_ANGLE;
        this.swayAngle = DEFAULT_SWAY_ANGLE;
        this.swaySpeed = DEFAULT_SWAY_SPEED;
        this.initialSwayProgress = DEFAULT_INITIAL_SWAY_PROGRESS;
    }

    public Config(Config config) {
        this.rotationSpeed = config.rotationSpeed;
        this.startingHorizontalAngle = config.startingHorizontalAngle;
        this.verticalAngle = config.verticalAngle;
        this.swayAngle = config.swayAngle;
        this.swaySpeed = config.swaySpeed;
        this.initialSwayProgress = config.initialSwayProgress;
    }

    public static Config loadOrCreate() {
        if (!CONFIG_FILE.exists()) {
            Config config = new Config();
            if (config.save()) {
                PanoramaTweaker.log(Level.INFO, "Created new config file");
            }
            return config;
        }

        try {
            FileReader reader = new FileReader(CONFIG_FILE);
            Config config = GSON.fromJson(reader, Config.class);
            return config != null ? config : new Config();
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            PanoramaTweaker.LOGGER.error(e.getMessage(), e);
            return new Config();
        }
    }

    public boolean save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            writer.write(GSON.toJson(this));
            return true;
        } catch (IOException e) {
            PanoramaTweaker.LOGGER.error(e.getMessage(), e);
            return false;
        }
    }
}
