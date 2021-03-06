package com.lazydash.audio.visualizer.plugins.hue.core;

import com.lazydash.audio.visualizer.plugins.hue.core.callbacks.*;
import com.lazydash.audio.visualizer.plugins.hue.model.HueAreaMap;
import com.lazydash.audio.visualizer.plugins.hue.model.Location;
import com.lazydash.audio.visualizer.plugins.hue.system.config.UserConfig;
import com.lazydash.audio.visualizer.spectrum.system.notification.EventEnum;
import com.lazydash.audio.visualizer.spectrum.system.notification.NotificationService;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.ConnectionEvent;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscovery;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryImpl;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeBuilder;
import com.philips.lighting.hue.sdk.wrapper.domain.resource.Group;
import com.philips.lighting.hue.sdk.wrapper.entertainment.Entertainment;
import com.philips.lighting.hue.sdk.wrapper.entertainment.animation.ConstantAnimation;
import com.philips.lighting.hue.sdk.wrapper.entertainment.effect.AreaEffect;
import com.philips.lighting.hue.sdk.wrapper.entertainment.effect.Effect;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridge;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridges;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HueIntegration {
    private Bridge bridge;
    private BridgeDiscovery bridgeDiscovery;
    private Entertainment entertainment;

    private boolean running = false;
    private boolean ready = false;

    public HueIntegration() {
        // save hue status on change
        NotificationService.getInstance().register(EventEnum.HUE_INTEGRATION_STATUS, UserConfig::setHueStatus);
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }

    public Entertainment getEntertainment() {
        return entertainment;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void start() {
        if (!running) {
            running = true;
            if (!connectToLastKnownBridge()) {
                discoverAndConnectToBridge();
            }
        }
    }

    public void stop() {
        if (running) {
            ready = false;
            running = false;
            UserConfig.setHueIntegrationEnabled(false);

           /* for (AreaEffect effect : areaEffectList) {
                if (effect != null && !effect.isFinished()) {
                    effect.finish();
                }
            }

            for (Map.Entry<String, Area> entry : nameToArea.entrySet()) {
                Area effect = entry.getValue();
                if (effect != null && !effect.isFinished()) {
                    effect.finish();
                }
            }*/

            if (entertainment != null) {
                entertainment.stop(() -> {

                });
            }

            if (bridge != null && bridge.isConnected()) {
                bridge.disconnect(BridgeConnectionType.LOCAL);
            }

            if (bridgeDiscovery != null && bridgeDiscovery.isSearching()) {
                bridgeDiscovery.stop();
            }

            NotificationService.getInstance().emit(EventEnum.HUE_INTEGRATION_STATUS, ConnectionEvent.DISCONNECTED.toString());
        }
    }

    private boolean connectToLastKnownBridge() {
        List<KnownBridge> all = KnownBridges.getAll();
        Optional<KnownBridge> first = all.stream().findFirst();

        if (first.isPresent()) {
            KnownBridge knownBridge = first.get();
            connectToBridge(knownBridge.getIpAddress());

            return true;

        } else {

            return false;
        }
    }

    private void discoverAndConnectToBridge() {
        if (bridgeDiscovery == null) {
            bridgeDiscovery = new BridgeDiscoveryImpl();
        }

        bridgeDiscovery.search(new BridgeDiscoveryIntegration(this));
        NotificationService.getInstance().emit(EventEnum.HUE_INTEGRATION_STATUS, BridgeDiscovery.ReturnCode.BUSY.toString());
    }

    public void connectToBridge(String bridgeIp) {
        bridge = new BridgeBuilder("Bass Visualisation", "device")
                .setConnectionType(BridgeConnectionType.LOCAL)
                .setIpAddress(bridgeIp)
                .addBridgeStateUpdatedCallback(new BridgeStateUpdatedIntegration(this))
                .setBridgeConnectionCallback(new BridgeConnectionIntegration(this))
                .build();

        bridge.connect();
    }

    public void refreshUser() {
        bridge.refreshUsername(new RefreshUsernameIntegration(this));
    }

    public void openStream() {
        List<Group> groups = bridge.getBridgeState().getGroups();

        Optional<Group> group = groups.stream().filter(currentGroup -> currentGroup.getName().equals(UserConfig.getHueEntertainmentName())).findFirst();
        if (group.isPresent()) {
            entertainment = new Entertainment(bridge, group.get().getIdentifier());
            entertainment.start(new EntertainmentStartIntegration(this));

        } else {
            throw new RuntimeException("Group: " + UserConfig.getHueEntertainmentName() + " is not found.");
        }
    }

    public void updateHueEntertainment(Map<Location, Color> locationColorMap) {
        if (locationColorMap.isEmpty()) {
            return;
        }

        entertainment.lockMixer();

        for (String locationName : HueAreaMap.getNameToArea().keySet()) {
            Effect effectByName = entertainment.getEffectByName(locationName);
            if (effectByName != null && effectByName.isEnabled()) {
                effectByName.finish();
                effectByName.disable();
            }
        }

        for (Map.Entry<Location, Color> entry: locationColorMap.entrySet()) {
            Location location = entry.getKey();
            Color color = entry.getValue();

            AreaEffect effect = (AreaEffect) entertainment.getEffectByName(location.getName());
            if (effect == null) {
                effect = new AreaEffect(location.getName(), 0);
                effect.setArea(HueAreaMap.getNameToArea().get(location.getName()));
                entertainment.addEffect(effect);
            }

            effect.setColorAnimation(
                    new ConstantAnimation(color.getRed()),
                    new ConstantAnimation(color.getGreen()),
                    new ConstantAnimation(color.getBlue())
            );

            if (effect.isDisabled()) {
                effect.enable();
            }
        }

        entertainment.unlockMixer();

    }
}



