package com.lazydash.audio.visualiser.external.hue;

import com.lazydash.audio.visualiser.system.notification.NotificationService;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeResponseCallback;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.ClipResponse;

import java.util.List;

public class RefreshUsernameIntegration extends BridgeResponseCallback {
    private HueIntegration hueIntegration;

    public RefreshUsernameIntegration(HueIntegration hueIntegration) {
        this.hueIntegration = hueIntegration;
    }

    @Override
    public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> list1) {
        if (returnCode == ReturnCode.SUCCESS) {
            NotificationService.getInstance().emit("hue-integration-status", returnCode.toString());
            hueIntegration.openStream();

        } else {
            hueIntegration.setReady(false);
            NotificationService.getInstance().emit("hue-integration-status", returnCode.toString());

        }
    }
}
