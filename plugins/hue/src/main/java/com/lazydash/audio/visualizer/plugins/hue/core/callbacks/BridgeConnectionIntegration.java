package com.lazydash.audio.visualizer.plugins.hue.core.callbacks;

import com.lazydash.audio.visualizer.plugins.hue.core.HueIntegration;
import com.lazydash.audio.visualizer.spectrum.system.notification.EventEnum;
import com.lazydash.audio.visualizer.spectrum.system.notification.NotificationService;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnection;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.ConnectionEvent;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;

import java.util.List;

public class BridgeConnectionIntegration extends BridgeConnectionCallback {
    private HueIntegration hueIntegration;

    public BridgeConnectionIntegration(HueIntegration hueIntegration) {
        this.hueIntegration = hueIntegration;
    }

    @Override
    public void onConnectionEvent(BridgeConnection bridgeConnection, ConnectionEvent connectionEvent) {
        switch (connectionEvent) {
            case AUTHENTICATED: {
                NotificationService.getInstance().emit(EventEnum.HUE_INTEGRATION_STATUS, ConnectionEvent.AUTHENTICATED.toString());
                break;
            }

            default:
                hueIntegration.setReady(false);
                NotificationService.getInstance().emit(EventEnum.HUE_INTEGRATION_STATUS, connectionEvent.toString());
        }
    }

    @Override
    public void onConnectionError(BridgeConnection bridgeConnection, List<HueError> list) {
        StringBuilder message = new StringBuilder();
        for (HueError hueError : list) {
            message.append(hueError.toString()).append("\n");
        }

        NotificationService.getInstance().emit(EventEnum.HUE_INTEGRATION_STATUS, ConnectionEvent.DISCONNECTED.toString());
        throw new RuntimeException(message.toString());
    }
}
