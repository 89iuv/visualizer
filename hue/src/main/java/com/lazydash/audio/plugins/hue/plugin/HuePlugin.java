package com.lazydash.audio.plugins.hue.plugin;

import com.lazydash.audio.plugins.hue.system.config.AppConfig;
import com.lazydash.audio.spectrum.system.persistance.AppConfigPersistence;
import org.pf4j.Plugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;

public class HuePlugin extends Plugin  {
    private AppConfigPersistence persistence = null;

    public HuePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() throws PluginException {
        persistence = AppConfigPersistence.createAndReadConfiguration(AppConfig.class, "./data/hue/hue.properties");

    }

    @Override
    public void stop() throws PluginException {
        persistence.persistConfig(AppConfig.class, "./data/hue/hue.properties");
    }
}
