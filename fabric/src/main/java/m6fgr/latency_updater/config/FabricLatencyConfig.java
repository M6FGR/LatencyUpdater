package m6fgr.latency_updater.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import m6fgr.latency_updater.LatencyUpdaterMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FabricLatencyConfig extends AbstractLatencyConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/latency_updater.json");

    private ConfigData data = new ConfigData();
    private long lastModified = 0L;

    public FabricLatencyConfig() {
        this.loadConfig();
    }

    public synchronized void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            this.saveConfig();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData loaded = GSON.fromJson(reader, ConfigData.class);
            if (loaded != null) {
                this.data = loaded;
                this.lastModified = CONFIG_FILE.lastModified();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this.data, writer);
                this.lastModified = CONFIG_FILE.lastModified();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFileUpdate() {
        if (CONFIG_FILE.exists() && CONFIG_FILE.lastModified() > this.lastModified) {
            this.loadConfig();
        }
    }

    @Override
    public int getPingUpdateTicks() {
        this.checkFileUpdate();
        return this.data.pingUpdateTicks;
    }

    @Override
    public boolean shouldDebugLog() {
        this.checkFileUpdate();
        return this.data.debugLog;
    }

    private static class ConfigData {
        int pingUpdateTicks = 20;
        boolean debugLog = false;
    }
}