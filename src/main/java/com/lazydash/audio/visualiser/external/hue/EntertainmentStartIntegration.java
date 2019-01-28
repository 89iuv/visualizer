package com.lazydash.audio.visualiser.external.hue;

import com.lazydash.audio.visualiser.system.config.AppConfig;
import com.lazydash.audio.visualiser.system.notification.NotificationService;
import com.philips.lighting.hue.sdk.wrapper.entertainment.Area;
import com.philips.lighting.hue.sdk.wrapper.entertainment.Color;
import com.philips.lighting.hue.sdk.wrapper.entertainment.StartCallback;
import com.philips.lighting.hue.sdk.wrapper.entertainment.effect.AreaEffect;

public class EntertainmentStartIntegration implements StartCallback {
    private HueIntegration hueIntegration;

    public EntertainmentStartIntegration(HueIntegration hueIntegration) {
        this.hueIntegration = hueIntegration;
    }

    @Override
    public void handleCallback(StartStatus startStatus) {
        if (StartStatus.Success.equals(startStatus)) {
            AreaEffect backAreaEffect = new AreaEffect("globalColorBack", 0);
            backAreaEffect.setArea(Area.Predefine.Back);
            backAreaEffect.setFixedColor(new Color(0,0,0));
            backAreaEffect.enable();

            AreaEffect frontAreaEffect = new AreaEffect("globalColorFront", 0);
            frontAreaEffect.setArea(Area.Predefine.Front);
            frontAreaEffect.setFixedColor(new Color(0,0,0));
            frontAreaEffect.enable();

            hueIntegration.setBackAreaEffect(backAreaEffect);
            hueIntegration.setFrontAreaEffect(frontAreaEffect);
            hueIntegration.getEntertainment().addEffect(backAreaEffect);
            hueIntegration.getEntertainment().addEffect(frontAreaEffect);
            hueIntegration.setReady(true);
            NotificationService.getInstance().emit("hue-integration-status", startStatus.getMessage());

            AppConfig.setHueIntegration(true);

        } else {
            hueIntegration.setReady(false);
            NotificationService.getInstance().emit("hue-integration-status", startStatus.getMessage());

        }
    }
}
