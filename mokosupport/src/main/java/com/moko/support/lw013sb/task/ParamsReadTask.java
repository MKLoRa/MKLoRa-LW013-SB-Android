package com.moko.support.lw013sb.task;

import android.text.TextUtils;

import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.support.lw013sb.LoRaLW013SBMokoSupport;
import com.moko.support.lw013sb.entity.OrderCHAR;
import com.moko.support.lw013sb.entity.ParamsKeyEnum;

import java.util.Arrays;
import java.util.Objects;

public class ParamsReadTask extends OrderTask {
    public byte[] data;

    public ParamsReadTask() {
        super(OrderCHAR.CHAR_PARAMS, OrderTask.RESPONSE_TYPE_WRITE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    public void setData(ParamsKeyEnum key) {
        createGetConfigData(key.getParamsKey());
    }

    private void createGetConfigData(int configKey) {
        byte[] cmdBytes = MokoUtils.toByteArray(configKey, 2);
        data = new byte[]{
                (byte) 0xED,
                (byte) 0x00,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x00
        };
        response.responseValue = data;
    }

    public void getFilterName() {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_NAME_RULES.getParamsKey(), 2);
        data = new byte[]{
                (byte) 0xEE,
                (byte) 0x00,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x00
        };
        response.responseValue = data;
    }

    private int packetCount;
    private int packetIndex;
    private int dataLength;
    private byte[] dataBytes;
    private StringBuilder dataSb;

    @Override
    public boolean parseValue(byte[] value) {
        final int header = value[0] & 0xFF;
        final int flag = value[1] & 0xFF;
        if (header == 0xEE && flag == 0x00) {
            // 分包读取时特殊处理
            final int cmd = MokoUtils.toInt(Arrays.copyOfRange(value, 2, 4));
            packetCount = value[4] & 0xFF;
            packetIndex = value[5] & 0xFF;
            final int length = value[6] & 0xFF;
            if (packetIndex == 0) {
                // 第一包
                dataLength = 0;
                dataSb = new StringBuilder();
            }
            ParamsKeyEnum keyEnum = ParamsKeyEnum.fromParamKey(cmd);
            if (Objects.requireNonNull(keyEnum) == ParamsKeyEnum.KEY_FILTER_NAME_RULES) {
                if (length > 0) {
                    dataLength += length;
                    byte[] responseData = Arrays.copyOfRange(value, 7, 7 + length);
                    dataSb.append(MokoUtils.bytesToHexString(responseData));
                }
                if (packetIndex == (packetCount - 1)) {
                    if (!TextUtils.isEmpty(dataSb.toString()))
                        dataBytes = MokoUtils.hex2bytes(dataSb.toString());
                    byte[] responseValue = new byte[5 + dataLength];
                    responseValue[0] = (byte) 0xED;
                    responseValue[1] = (byte) 0x00;
                    responseValue[2] = (byte) value[2];
                    responseValue[3] = (byte) value[3];
                    responseValue[4] = (byte) dataLength;
                    for (int i = 0; i < dataLength; i++) {
                        responseValue[5 + i] = dataBytes[i];
                    }
                    dataSb = null;
                    dataBytes = null;
                    // 最后一包
                    orderStatus = 1;
                    response.responseValue = responseValue;
                    LoRaLW013SBMokoSupport.getInstance().pollTask();
                    LoRaLW013SBMokoSupport.getInstance().executeTask();
                    LoRaLW013SBMokoSupport.getInstance().orderResult(response);
                }
            }
            return false;
        }
        return true;
    }
}
