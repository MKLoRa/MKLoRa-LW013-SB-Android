package com.moko.support.lw013sb.service;

import com.moko.support.lw013sb.entity.DeviceInfo;

public interface DeviceInfoParseable<T> {
    T parseDeviceInfo(DeviceInfo deviceInfo);
}
