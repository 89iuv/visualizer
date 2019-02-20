package com.lazydash.audio.plugins.hue.extensions;

import org.junit.Test;

import java.net.URL;

public class HueUiExtensionTest {


    @Test
    public void run() {
        URL resource = getClass().getResource("/ui.fxml.settings/components/hue_integration.fxml");
        System.out.println(resource);
    }

}