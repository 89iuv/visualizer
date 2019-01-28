package com.lazydash.audio.visualiser.external.hue;

import com.lazydash.audio.visualiser.system.notification.NotificationService;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscovery;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;

import java.util.List;

public class BridgeDiscoveryIntegration implements BridgeDiscovery.Callback {
    private HueIntegration hueIntegration;

    public BridgeDiscoveryIntegration(HueIntegration hueIntegration) {
        this.hueIntegration = hueIntegration;
    }

    @Override
    public void onFinished(List<BridgeDiscoveryResult> list, BridgeDiscovery.ReturnCode returnCode) {
        if (returnCode.equals(BridgeDiscovery.ReturnCode.SUCCESS)) {
            if (list.get(0) != null) {
                hueIntegration.connectToBridge(list.get(0).getIp());

            } else {
                hueIntegration.setReady(false);
                NotificationService.getInstance().emit("hue-integration-status", returnCode.toString());
            }

        } else {
            hueIntegration.setReady(false);
            NotificationService.getInstance().emit("hue-integration-status", returnCode.toString());
        }

    }
}
