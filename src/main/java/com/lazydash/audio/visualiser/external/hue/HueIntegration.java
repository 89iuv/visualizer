package com.lazydash.audio.visualiser.external.hue;

import com.lazydash.audio.visualiser.external.hue.callbacks.*;
import com.lazydash.audio.visualiser.system.config.AppConfig;
import com.lazydash.audio.visualiser.system.notification.EventEnum;
import com.lazydash.audio.visualiser.system.notification.NotificationService;
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
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridge;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridges;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;

public class HueIntegration {
    private Bridge bridge;
    private BridgeDiscovery bridgeDiscovery;
    private Entertainment entertainment;
    private AreaEffect backAreaEffect;
    private AreaEffect frontAreaEffect;

    private boolean running = false;
    private boolean ready = false;


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

    public AreaEffect getBackAreaEffect() {
        return backAreaEffect;
    }

    public AreaEffect getFrontAreaEffect() {
        return frontAreaEffect;
    }

    public void setFrontAreaEffect(AreaEffect frontAreaEffect) {
        this.frontAreaEffect = frontAreaEffect;
    }

    public void setBackAreaEffect(AreaEffect backAreaEffect) {
        this.backAreaEffect = backAreaEffect;
    }

    public HueIntegration() {
        // save hue status on change
        NotificationService.getInstance().register(EventEnum.HUE_INTEGRATION_STATUS, AppConfig::setHueStatus);
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
            AppConfig.setHueIntegrationEnabled(false);

            if (backAreaEffect != null && !backAreaEffect.isFinished()) {
                backAreaEffect.finish();
            }

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

        Optional<Group> group = groups.stream().filter(currentGroup -> currentGroup.getName().equals(AppConfig.getHueEntertainmentName())).findFirst();
        if (group.isPresent()) {
            entertainment = new Entertainment(bridge, group.get().getIdentifier());
            entertainment.start(new EntertainmentStartIntegration(this));

        } else {
            throw new RuntimeException("Group: " + AppConfig.getHueEntertainmentName() + " is not found.");
        }
    }

    public void setColor(Color frontColor, Color backColor) {
        frontAreaEffect.setColorAnimation(
                new ConstantAnimation(frontColor.getRed()),
                new ConstantAnimation(frontColor.getGreen()),
                new ConstantAnimation(frontColor.getBlue()));

        backAreaEffect.setColorAnimation(
                new ConstantAnimation(backColor.getRed()),
                new ConstantAnimation(backColor.getGreen()),
                new ConstantAnimation(backColor.getBlue()));
    }
}



