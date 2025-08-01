package com.moko.support.lw013sb.task;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw013sb.entity.OrderCHAR;

public class GetHardwareRevisionTask extends OrderTask {

    public byte[] data;

    public GetHardwareRevisionTask() {
        super(OrderCHAR.CHAR_HARDWARE_REVISION, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
