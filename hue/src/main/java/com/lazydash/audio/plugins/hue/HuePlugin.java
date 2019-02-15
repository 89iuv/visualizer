package com.lazydash.audio.plugins.hue;

import com.lazydash.audio.plugins.hue.system.config.AppConfig;
import com.lazydash.audio.plugins.hue.system.config.UserConfig;
import com.lazydash.audio.plugins.hue.system.setup.Setup;
import com.lazydash.audio.spectrum.system.persistance.ConfigFilePersistence;
import org.pf4j.Plugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;

import java.io.InputStream;

public class HuePlugin extends Plugin  {
    private ConfigFilePersistence persistence = new ConfigFilePersistence();

    public HuePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() throws PluginException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("application.properties");
        if (resourceAsStream != null) {
            persistence.load(AppConfig.class, resourceAsStream);
        }

        persistence.load(UserConfig.class, "./data/" + AppConfig.getPluginId() + "/user.properties");

        Setup setup = new Setup();
        setup.checkHueEnvironment();
        setup.runHueConfig();
    }

    @Override
    public void stop() throws PluginException {
        persistence.persist(UserConfig.class, "./data/"+ AppConfig.getPluginId() +"/user.properties");
    }
}
