package com.moko.support.lw013sb;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.elvishew.xlog.XLog;
import com.moko.ble.lib.MokoBleManager;
import com.moko.ble.lib.callback.MokoResponseCallback;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.support.lw013sb.entity.OrderCHAR;
import com.moko.support.lw013sb.entity.OrderServices;

import androidx.annotation.NonNull;

final class MokoBleConfig extends MokoBleManager {

    private MokoResponseCallback mMokoResponseCallback;
    private BluetoothGattCharacteristic passwordCharacteristic;
    private BluetoothGattCharacteristic disconnectedCharacteristic;
    private BluetoothGattCharacteristic paramsCharacteristic;
    private BluetoothGattCharacteristic storageDataCharacteristic;
    private BluetoothGattCharacteristic logCharacteristic;
    private BluetoothGatt gatt;

    public MokoBleConfig(@NonNull Context context, MokoResponseCallback callback) {
        super(context);
        mMokoResponseCallback = callback;
    }

    @Override
    public boolean checkServiceCharacteristicSupported(BluetoothGatt gatt) {
        final BluetoothGattService service = gatt.getService(OrderServices.SERVICE_CUSTOM.getUuid());
        if (service != null) {
            this.gatt = gatt;
            passwordCharacteristic = service.getCharacteristic(OrderCHAR.CHAR_PASSWORD.getUuid());
            disconnectedCharacteristic = service.getCharacteristic(OrderCHAR.CHAR_DISCONNECTED_NOTIFY.getUuid());
            paramsCharacteristic = service.getCharacteristic(OrderCHAR.CHAR_PARAMS.getUuid());
            storageDataCharacteristic = service.getCharacteristic(OrderCHAR.CHAR_STORAGE_DATA_NOTIFY.getUuid());
            logCharacteristic = service.getCharacteristic(OrderCHAR.CHAR_LOG.getUuid());
            return passwordCharacteristic != null
                    && disconnectedCharacteristic != null
                    && paramsCharacteristic != null;
        }
        return false;
    }

    @Override
    public void init() {
        requestMtu(247).with(((device, mtu) -> {
        })).then((device -> {
            enablePasswordNotify();
            enableDisconnectedNotify();
            enableStorageDataNotify();
            enableParamsNotify();
        })).enqueue();
    }

    @Override
    public void write(BluetoothGattCharacteristic characteristic, byte[] value) {
    }

    @Override
    public void read(BluetoothGattCharacteristic characteristic, byte[] value) {
        mMokoResponseCallback.onCharacteristicRead(characteristic, value);
    }

    @Override
    public void onDeviceConnecting(@NonNull BluetoothDevice device) {

    }

    @Override
    public void onDeviceConnected(@NonNull BluetoothDevice device) {

    }

    @Override
    public void onDeviceFailedToConnect(@NonNull BluetoothDevice device, int reason) {
        mMokoResponseCallback.onDeviceDisconnected(device, reason);
    }

    @Override
    public void onDeviceReady(@NonNull BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(@NonNull BluetoothDevice device, int reason) {
        mMokoResponseCallback.onDeviceDisconnected(device, reason);
    }


    public void enableParamsNotify() {
        setNotificationCallback(paramsCharacteristic).with((device, data) -> {
            final byte[] value = data.getValue();
            XLog.e("onDataReceived");
            XLog.e("device to app : " + MokoUtils.bytesToHexString(value));
            mMokoResponseCallback.onCharacteristicChanged(paramsCharacteristic, value);
        });
        enableNotifications(paramsCharacteristic).done(device -> mMokoResponseCallback.onServicesDiscovered(gatt)).enqueue();
    }

    public void disableParamsNotify() {
        disableNotifications(paramsCharacteristic).enqueue();
    }

    public void enablePasswordNotify() {
        setNotificationCallback(passwordCharacteristic).with((device, data) -> {
            final byte[] value = data.getValue();
            XLog.e("onDataReceived");
            XLog.e("device to app : " + MokoUtils.bytesToHexString(value));
            mMokoResponseCallback.onCharacteristicChanged(passwordCharacteristic, value);
        });
        enableNotifications(passwordCharacteristic).enqueue();
    }

    public void disablePasswordNotify() {
        disableNotifications(passwordCharacteristic).enqueue();
    }

    public void enableDisconnectedNotify() {
        setNotificationCallback(disconnectedCharacteristic).with((device, data) -> {
            final byte[] value = data.getValue();
            XLog.e("onDataReceived");
            XLog.e("device to app : " + MokoUtils.bytesToHexString(value));
            mMokoResponseCallback.onCharacteristicChanged(disconnectedCharacteristic, value);
        });
        enableNotifications(disconnectedCharacteristic).enqueue();
    }

    public void disableDisconnectedNotify() {
        disableNotifications(disconnectedCharacteristic).enqueue();
    }

    public void enableStorageDataNotify() {
        setNotificationCallback(storageDataCharacteristic).with((device, data) -> {
            final byte[] value = data.getValue();
            XLog.e("onDataReceived");
            XLog.e("device to app : " + MokoUtils.bytesToHexString(value));
            mMokoResponseCallback.onCharacteristicChanged(storageDataCharacteristic, value);
        });
        enableNotifications(storageDataCharacteristic).enqueue();
    }

    public void disableStorageDataNotify() {
        disableNotifications(storageDataCharacteristic).enqueue();
    }

    public void enableLogNotify() {
        setNotificationCallback(logCharacteristic).with((device, data) -> {
            final byte[] value = data.getValue();
            XLog.e("onDataReceived");
            XLog.e("device to app : " + MokoUtils.bytesToHexString(value));
            mMokoResponseCallback.onCharacteristicChanged(logCharacteristic, value);
        });
        enableNotifications(logCharacteristic).enqueue();
    }

    public void disableLogNotify() {
        disableNotifications(logCharacteristic).enqueue();
    }
}