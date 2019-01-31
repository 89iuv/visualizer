package com.lazydash.audio.visualiser.external.hue.callbacks;

import com.lazydash.audio.visualiser.external.hue.HueIntegration;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedEvent;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;

public class BridgeStateUpdatedIntegration extends BridgeStateUpdatedCallback {
    private HueIntegration hueIntegration;

    public BridgeStateUpdatedIntegration(HueIntegration hueIntegration) {
        this.hueIntegration = hueIntegration;
    }

    @Override
    public void onBridgeStateUpdated(Bridge bridge, BridgeStateUpdatedEvent bridgeStateUpdatedEvent) {
        if (bridgeStateUpdatedEvent.equals(BridgeStateUpdatedEvent.FULL_CONFIG)) {
            hueIntegration.setBridge(bridge);
            hueIntegration.refreshUser();
        }
    }

}
