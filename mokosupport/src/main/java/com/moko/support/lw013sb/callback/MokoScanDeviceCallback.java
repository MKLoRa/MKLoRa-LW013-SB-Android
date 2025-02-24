package com.moko.support.lw013sb.callback;

import com.moko.support.lw013sb.entity.DeviceInfo;

public interface MokoScanDeviceCallback {
    void onStartScan();

    void onScanDevice(DeviceInfo device);

    void onStopScan();
}
