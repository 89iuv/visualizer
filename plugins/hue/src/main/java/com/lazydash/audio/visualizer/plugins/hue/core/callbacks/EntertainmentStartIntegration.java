package com.lazydash.audio.visualizer.plugins.hue.core.callbacks;

import com.lazydash.audio.visualizer.plugins.hue.core.HueIntegration;
import com.lazydash.audio.visualizer.plugins.hue.system.config.UserConfig;
import com.lazydash.audio.visualizer.spectrum.system.notification.EventEnum;
import com.lazydash.audio.visualizer.spectrum.system.notification.NotificationService;
import com.philips.lighting.hue.sdk.wrapper.entertainment.StartCallback;

public class EntertainmentStartIntegration implements StartCallback {
    private HueIntegration hueIntegration;

    public EntertainmentStartIntegration(HueIntegration hueIntegration) {
        this.hueIntegration = hueIntegration;
    }

    @Override
    public void handleCallback(StartStatus startStatus) {
        if (StartStatus.Success.equals(startStatus)) {
            hueIntegration.setReady(true);
            NotificationService.getInstance().emit(EventEnum.HUE_INTEGRATION_STATUS, startStatus.getMessage());

            UserConfig.setHueIntegrationEnabled(true);

        } else {
            hueIntegration.setReady(false);
            NotificationService.getInstance().emit(EventEnum.HUE_INTEGRATION_STATUS, startStatus.getMessage());

        }
    }
}
