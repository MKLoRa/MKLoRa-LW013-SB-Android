package com.moko.support.lw013sb.task;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw013sb.entity.OrderCHAR;

public class SetPasswordTask extends OrderTask {
    public byte[] data;

    public SetPasswordTask() {
        super(OrderCHAR.CHAR_PASSWORD, OrderTask.RESPONSE_TYPE_WRITE_NO_RESPONSE);
    }

    public void setData(String password) {
        this.data = new byte[13];
        byte[] passwordBytes = password.getBytes();
        int length = passwordBytes.length;
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 5] = passwordBytes[i];
        }
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
