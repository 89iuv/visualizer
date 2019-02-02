package com.lazydash.audio.visualizer.external.hue.callbacks;

import com.lazydash.audio.visualizer.external.hue.HueIntegration;
import com.lazydash.audio.visualizer.system.config.AppConfig;
import com.lazydash.audio.visualizer.system.config.ColorConfig;
import com.lazydash.audio.visualizer.system.notification.EventEnum;
import com.lazydash.audio.visualizer.system.notification.NotificationService;
import com.philips.lighting.hue.sdk.wrapper.entertainment.Area;
import com.philips.lighting.hue.sdk.wrapper.entertainment.Entertainment;
import com.philips.lighting.hue.sdk.wrapper.entertainment.StartCallback;
import com.philips.lighting.hue.sdk.wrapper.entertainment.animation.ConstantAnimation;
import com.philips.lighting.hue.sdk.wrapper.entertainment.effect.AreaEffect;

public class EntertainmentStartIntegration implements StartCallback {
    private HueIntegration hueIntegration;

    public EntertainmentStartIntegration(HueIntegration hueIntegration) {
        this.hueIntegration = hueIntegration;
    }

    @Override
    public void handleCallback(StartStatus startStatus) {
        if (StartStatus.Success.equals(startStatus)) {

            AreaEffect allAreaEffect = new AreaEffect("allAreaEffect", 0);
            allAreaEffect.setArea(Area.Predefine.All);
            allAreaEffect.setColorAnimation(
                    new ConstantAnimation(ColorConfig.baseColor.getRed()),
                    new ConstantAnimation(ColorConfig.baseColor.getGreen()),
                    new ConstantAnimation(ColorConfig.baseColor.getBlue())
            );

            Entertainment entertainment = hueIntegration.getEntertainment();

            entertainment.lockMixer();
            entertainment.addEffect(allAreaEffect);
            allAreaEffect.enable();
            entertainment.unlockMixer();

            hueIntegration.setAllAreaEffect(allAreaEffect);
            hueIntegration.setReady(true);
            NotificationService.getInstance().emit(EventEnum.HUE_INTEGRATION_STATUS, startStatus.getMessage());

            AppConfig.setHueIntegrationEnabled(true);

        } else {
            hueIntegration.setReady(false);
            NotificationService.getInstance().emit(EventEnum.HUE_INTEGRATION_STATUS, startStatus.getMessage());

        }
    }
}
