package com.moko.support.lw013sb.handler;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.moko.support.lw013sb.entity.OrderCHAR;
import com.moko.support.lw013sb.entity.OrderServices;

import java.util.HashMap;

public class MokoCharacteristicHandler {
    private HashMap<OrderCHAR, BluetoothGattCharacteristic> mCharacteristicMap;

    public MokoCharacteristicHandler() {
        //no instance
        mCharacteristicMap = new HashMap<>();
    }

    public HashMap<OrderCHAR, BluetoothGattCharacteristic> getCharacteristics(final BluetoothGatt gatt) {
        if (mCharacteristicMap != null && !mCharacteristicMap.isEmpty()) {
            mCharacteristicMap.clear();
        }
        if (gatt.getService(OrderServices.SERVICE_DEVICE_INFO.getUuid()) != null) {
            final BluetoothGattService service = gatt.getService(OrderServices.SERVICE_DEVICE_INFO.getUuid());
            if (service.getCharacteristic(OrderCHAR.CHAR_MODEL_NUMBER.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_MODEL_NUMBER.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_MODEL_NUMBER, characteristic);
            }
            if (service.getCharacteristic(OrderCHAR.CHAR_SERIAL_NUMBER.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_SERIAL_NUMBER.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_SERIAL_NUMBER, characteristic);
            }
            if (service.getCharacteristic(OrderCHAR.CHAR_FIRMWARE_REVISION.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_FIRMWARE_REVISION.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_FIRMWARE_REVISION, characteristic);
            }
            if (service.getCharacteristic(OrderCHAR.CHAR_HARDWARE_REVISION.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_HARDWARE_REVISION.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_HARDWARE_REVISION, characteristic);
            }
            if (service.getCharacteristic(OrderCHAR.CHAR_SOFTWARE_REVISION.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_SOFTWARE_REVISION.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_SOFTWARE_REVISION, characteristic);
            }
            if (service.getCharacteristic(OrderCHAR.CHAR_MANUFACTURER_NAME.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_MANUFACTURER_NAME.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_MANUFACTURER_NAME, characteristic);
            }
        }
        if (gatt.getService(OrderServices.SERVICE_CUSTOM.getUuid()) != null) {
            final BluetoothGattService service = gatt.getService(OrderServices.SERVICE_CUSTOM.getUuid());
            if (service.getCharacteristic(OrderCHAR.CHAR_PASSWORD.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_PASSWORD.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_PASSWORD, characteristic);
            }
            if (service.getCharacteristic(OrderCHAR.CHAR_DISCONNECTED_NOTIFY.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_DISCONNECTED_NOTIFY.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_DISCONNECTED_NOTIFY, characteristic);
            }
            if (service.getCharacteristic(OrderCHAR.CHAR_PARAMS.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_PARAMS.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_PARAMS, characteristic);
            }
            if (service.getCharacteristic(OrderCHAR.CHAR_STORAGE_DATA_NOTIFY.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_STORAGE_DATA_NOTIFY.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_STORAGE_DATA_NOTIFY, characteristic);
            }
            if (service.getCharacteristic(OrderCHAR.CHAR_LOG.getUuid()) != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(OrderCHAR.CHAR_LOG.getUuid());
                mCharacteristicMap.put(OrderCHAR.CHAR_LOG, characteristic);
            }
        }
        return mCharacteristicMap;
    }
}
