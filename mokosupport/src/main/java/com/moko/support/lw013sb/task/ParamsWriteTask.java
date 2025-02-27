package com.moko.support.lw013sb.task;

import android.text.TextUtils;

import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.support.lw013sb.LoRaLW013SBMokoSupport;
import com.moko.support.lw013sb.entity.OrderCHAR;
import com.moko.support.lw013sb.entity.ParamsKeyEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.annotation.IntRange;

public class ParamsWriteTask extends OrderTask {
    public byte[] data;

    public ParamsWriteTask() {
        super(OrderCHAR.CHAR_PARAMS, OrderTask.RESPONSE_TYPE_WRITE_NO_RESPONSE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    /**
     * 上行配置参数
     *
     * @param flag  0 1
     * @param times 1-4
     */
    public void setPayloadInfo(@IntRange(from = 0, to = 1) int flag, @IntRange(from = 1, to = 4) int times, int key) {
        byte[] cmdBytes = MokoUtils.toByteArray(key, 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                (byte) flag,
                (byte) times
        };
    }

    public void close() {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_CLOSE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x00
        };

    }

    public void reboot() {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_REBOOT.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x00
        };

    }

    public void reset() {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_RESET.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x00
        };

    }

    public void setTime() {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(timeZone);
        long time = calendar.getTimeInMillis() / 1000;
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; ++i) {
            bytes[i] = (byte) (time >> 8 * (3 - i) & 255);
        }
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_TIME_UTC.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x04,
                bytes[0],
                bytes[1],
                bytes[2],
                bytes[3],
        };

    }

    public void setTimeZone(@IntRange(from = -24, to = 28) int timeZone) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_TIME_ZONE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) timeZone
        };

    }

    public void setDeviceMode(@IntRange(from = 1, to = 5) int mode) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_DEVICE_MODE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) mode
        };

    }

    public void setIndicatorStatus(@IntRange(from = 0, to = 1) int lowPowerStatus,
                                   @IntRange(from = 0, to = 1) int bleAdvStatus,
                                   @IntRange(from = 0, to = 1) int networkCheckStatus,
                                   @IntRange(from = 0, to = 1) int alarmTriggered1Status,
                                   @IntRange(from = 0, to = 1) int alarmType1Status,
                                   @IntRange(from = 0, to = 1) int alarmTriggered2Status,
                                   @IntRange(from = 0, to = 1) int alarmType2Status,
                                   @IntRange(from = 0, to = 1) int alarmTriggered3Status,
                                   @IntRange(from = 0, to = 1) int alarmType3Status) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_INDICATOR_STATUS.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x09,
                (byte) lowPowerStatus,
                (byte) bleAdvStatus,
                (byte) networkCheckStatus,
                (byte) alarmTriggered1Status,
                (byte) alarmType1Status,
                (byte) alarmTriggered2Status,
                (byte) alarmType2Status,
                (byte) alarmTriggered3Status,
                (byte) alarmType3Status
        };

    }

    public void setHeartBeatInterval(@IntRange(from = 1, to = 14400) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_HEARTBEAT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                intervalBytes[0],
                intervalBytes[1],
        };

    }

    public void setManufacturer(String manufacturer) {
        byte[] manufacturerBytes = manufacturer.getBytes();
        int length = manufacturerBytes.length;
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MANUFACTURER.getParamsKey(), 2);
        data = new byte[length + 5];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) cmdBytes[0];
        data[3] = (byte) cmdBytes[1];
        data[4] = (byte) length;
        for (int i = 0; i < manufacturerBytes.length; i++) {
            data[i + 5] = manufacturerBytes[i];
        }
        response.responseValue = data;
    }


    public void setShutdownInfoReport(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_SHUTDOWN_PAYLOAD_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setHallPowerOnMethod(@IntRange(from = 0, to = 1) int method) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_HALL_POWER_ON_METHOD.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) method
        };
    }

    public void setOffByHall(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_OFF_BY_HALL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setAutoPowerOn(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_AUTO_POWER_ON_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setLowPowerPayloadEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LOW_POWER_PAYLOAD_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setOffByMagneticEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_OFF_BY_HALL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setLowPowerReportInterval(@IntRange(from = 1, to = 255) int interval) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LOW_POWER_REPORT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) interval
        };

    }

    public void setLowPowerPercent(@IntRange(from = 30, to = 99) int percent) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LOW_POWER_PERCENT.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) percent
        };
    }

    public void setBuzzerSound(@IntRange(from = 0, to = 2) int buzzer) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_BUZZER_SOUND_CHOOSE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) buzzer
        };
    }

    public void setPasswordVerifyEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_PASSWORD_VERIFY_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setBeaconEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_BEACON_MODE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void changePassword(String password) {
        byte[] passwordBytes = password.getBytes();
        int length = passwordBytes.length;
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_PASSWORD.getParamsKey(), 2);
        data = new byte[length + 5];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) cmdBytes[0];
        data[3] = (byte) cmdBytes[1];
        data[4] = (byte) length;
        for (int i = 0; i < passwordBytes.length; i++) {
            data[i + 5] = passwordBytes[i];
        }
        response.responseValue = data;
    }

    public void setAdvTimeout(@IntRange(from = 1, to = 60) int timeout) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ADV_TIMEOUT.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) timeout
        };

    }

    public void setAdvTxPower(@IntRange(from = -40, to = 8) int txPower) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ADV_TX_POWER.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) txPower
        };

    }

    public void setAdvName(String advName) {
        byte[] advNameBytes = advName.getBytes();
        int length = advNameBytes.length;
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ADV_NAME.getParamsKey(), 2);
        data = new byte[length + 5];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) cmdBytes[0];
        data[3] = (byte) cmdBytes[1];
        data[4] = (byte) length;
        for (int i = 0; i < advNameBytes.length; i++) {
            data[i + 5] = advNameBytes[i];
        }
        response.responseValue = data;
    }

    public void setAdvInterval(@IntRange(from = 1, to = 100) int interval) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ADV_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) interval
        };

    }

    public void setStandbyPosStrategy(@IntRange(from = 0, to = 3) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_STANDBY_MODE_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };

    }

    public void setPeriodicPosStrategy(@IntRange(from = 0, to = 4) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_PERIODIC_MODE_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };

    }


    public void setPeriodicReportInterval(@IntRange(from = 1, to = 14400) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_PERIODIC_MODE_REPORT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                intervalBytes[0],
                intervalBytes[1]
        };

    }


    public void setTimePosStrategy(@IntRange(from = 0, to = 3) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_TIME_MODE_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };

    }

    public void setTimePosReportPoints(ArrayList<Integer> timePoints) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_TIME_MODE_REPORT_TIME_POINT.getParamsKey(), 2);
        if (timePoints == null || timePoints.size() == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) cmdBytes[0],
                    (byte) cmdBytes[1],
                    (byte) 0x00
            };
        } else {
            int size = timePoints.size();
            int length = size * 2;
            data = new byte[5 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = cmdBytes[0];
            data[3] = cmdBytes[1];
            data[4] = (byte) length;
            for (int i = 0, index = 0; i < length && index < size; i += 2, index++) {
                byte[] pointBytes = MokoUtils.toByteArray(timePoints.get(index), 2);
                data[5 + i] = pointBytes[0];
                data[6 + i] = pointBytes[1];
            }
        }
        response.responseValue = data;
    }

    public void setMotionStartEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_START_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setMotionStartFixEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_START_FIX_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setMotionStartNumber(@IntRange(from = 1, to = 255) int number) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_START_NUMBER.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) number
        };

    }

    public void setMotionStartPosStrategy(@IntRange(from = 0, to = 3) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_START_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };

    }

    public void setMotionTripEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_TRIP_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setMotionTripFixEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_TRIP_FIX_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }


    public void setMotionTripInterval(@IntRange(from = 10, to = 86400) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 4);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_TRIP_REPORT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x04,
                intervalBytes[0],
                intervalBytes[1],
                intervalBytes[2],
                intervalBytes[3],
        };

    }

    public void setMotionTripPosStrategy(@IntRange(from = 0, to = 4) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_TRIP_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };

    }

    public void setMotionEndEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_END_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setMotionEndFixEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_END_FIX_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setMotionEndTimeout(@IntRange(from = 1, to = 180) int timeout) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_END_TIMEOUT.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) timeout
        };

    }


    public void setMotionEndNumber(@IntRange(from = 1, to = 255) int number) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_END_NUMBER.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) number
        };

    }


    public void setMotionEndInterval(@IntRange(from = 10, to = 300) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_END_REPORT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                intervalBytes[0],
                intervalBytes[1],
        };

    }

    public void setMotionEndPosStrategy(@IntRange(from = 0, to = 4) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_END_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };

    }

    public void setMotionStationaryFixEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_STATIONARY_FIX_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setMotionStationaryInterval(@IntRange(from = 1, to = 14400) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_STATIONARY_REPORT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                intervalBytes[0],
                intervalBytes[1]
        };

    }

    public void setMotionStationaryPosStrategy(@IntRange(from = 0, to = 3) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MOTION_MODE_STATIONARY_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };

    }


    public void setTimePeriodicPosStrategy(@IntRange(from = 0, to = 4) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_TIME_PERIODIC_MODE_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };

    }

    public void setTimePeriodicPosReportPoints(ArrayList<Integer> timePoints) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_TIME_PERIODIC_MODE_REPORT_TIME_POINT.getParamsKey(), 2);
        if (timePoints.size() == 0) {
            data = new byte[5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = cmdBytes[0];
            data[3] = cmdBytes[1];
            data[4] = (byte) 0;
        } else {
            int size = timePoints.size();
            int length = size / 3 * 8;
            data = new byte[5 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = cmdBytes[0];
            data[3] = cmdBytes[1];
            data[4] = (byte) length;
            for (int i = 0, j = 0; i < size; i++) {
                if ((i + 1) % 3 != 0) {
                    byte[] intervalBytes = MokoUtils.toByteArray(timePoints.get(i), 2);
                    data[5 + j * 2] = intervalBytes[0];
                    data[6 + j * 2] = intervalBytes[1];
                    j++;
                } else {
                    byte[] intervalBytes = MokoUtils.toByteArray(timePoints.get(i), 4);
                    data[5 + j * 2] = intervalBytes[0];
                    data[6 + j * 2] = intervalBytes[1];
                    data[7 + j * 2] = intervalBytes[2];
                    data[8 + j * 2] = intervalBytes[3];
                    j += 2;
                }
            }
        }
        response.responseValue = data;
    }

//    public void setWifiPosDataType(@IntRange(from = 0, to = 1) int type) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_WIFI_POS_DATA_TYPE.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//                (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) type
//        };
//        
//    }
//
//    public void setWifiPosTimeout(@IntRange(from = 1, to = 4) int timeout) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_WIFI_POS_TIMEOUT.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//            (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) timeout
//        };
//        
//    }
//
//    public void setWifiPosBSSIDNumber(@IntRange(from = 1, to = 5) int number) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_WIFI_POS_BSSID_NUMBER.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//            (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) number
//        };
//        
//    }

    public void setBlePosTimeout(@IntRange(from = 1, to = 10) int timeout) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_BLE_POS_TIMEOUT.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) timeout
        };

    }

    public void setBlePosNumber(@IntRange(from = 1, to = 15) int number) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_BLE_POS_MAC_NUMBER.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) number
        };

    }

    public void setFilterRSSI(@IntRange(from = -127, to = 0) int rssi) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_RSSI.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) rssi
        };

    }

//    public void setFilterBleScanPhy(@IntRange(from = 0, to = 4) int type) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BLE_SCAN_PHY.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//                (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) type
//        };
//        
//    }

    public void setFilterRelationship(@IntRange(from = 0, to = 6) int relationship) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_RELATIONSHIP.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) relationship
        };

    }

    public void setFilterMacPrecise(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MAC_PRECISE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterMacReverse(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MAC_REVERSE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterMacRules(ArrayList<String> filterMacRules) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MAC_RULES.getParamsKey(), 2);
        if (filterMacRules == null || filterMacRules.size() == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) cmdBytes[0],
                    (byte) cmdBytes[1],
                    (byte) 0x00
            };
        } else {
            int length = 0;
            for (String mac : filterMacRules) {
                length += 1;
                length += mac.length() / 2;
            }
            data = new byte[5 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) length;
            int index = 0;
            for (int i = 0, size = filterMacRules.size(); i < size; i++) {
                String mac = filterMacRules.get(i);
                byte[] macBytes = MokoUtils.hex2bytes(mac);
                int l = macBytes.length;
                data[5 + index] = (byte) l;
                index++;
                for (int j = 0; j < l; j++, index++) {
                    data[5 + index] = macBytes[j];
                }
            }
        }
        response.responseValue = data;
    }

    public void setFilterNamePrecise(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_NAME_PRECISE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterNameReverse(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_NAME_REVERSE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterRawData(int unknown, int ibeacon,
                                 int eddystone_uid, int eddystone_url, int eddystone_tlm,
                                 int bxp_acc, int bxp_th,
                                 int mkibeacon, int mkibeacon_acc) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_RAW_DATA.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x09,
                (byte) unknown,
                (byte) ibeacon,
                (byte) eddystone_uid,
                (byte) eddystone_url,
                (byte) eddystone_tlm,
                (byte) bxp_acc,
                (byte) bxp_th,
                (byte) mkibeacon,
                (byte) mkibeacon_acc
        };

    }

    public void setFilterIBeaconEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_IBEACON_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterIBeaconMajorRange(@IntRange(from = 0, to = 65535) int min,
                                           @IntRange(from = 0, to = 65535) int max) {
        byte[] minBytes = MokoUtils.toByteArray(min, 2);
        byte[] maxBytes = MokoUtils.toByteArray(max, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_IBEACON_MAJOR_RANGE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x04,
                minBytes[0],
                minBytes[1],
                maxBytes[0],
                maxBytes[1]
        };

    }

    public void setFilterIBeaconMinorRange(@IntRange(from = 0, to = 65535) int min,
                                           @IntRange(from = 0, to = 65535) int max) {
        byte[] minBytes = MokoUtils.toByteArray(min, 2);
        byte[] maxBytes = MokoUtils.toByteArray(max, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_IBEACON_MINOR_RANGE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x04,
                minBytes[0],
                minBytes[1],
                maxBytes[0],
                maxBytes[1]
        };

    }

    public void setFilterIBeaconUUID(String uuid) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_IBEACON_UUID.getParamsKey(), 2);
        if (TextUtils.isEmpty(uuid)) {
            data = new byte[5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) 0x00;
        } else {
            byte[] uuidBytes = MokoUtils.hex2bytes(uuid);
            int length = uuidBytes.length;
            data = new byte[length + 5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) length;
            for (int i = 0; i < uuidBytes.length; i++) {
                data[i + 5] = uuidBytes[i];
            }
        }
        response.responseValue = data;
    }

    public void setFilterBXPIBeaconEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_IBEACON_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterBXPIBeaconMajorRange(@IntRange(from = 0, to = 65535) int min,
                                              @IntRange(from = 0, to = 65535) int max) {
        byte[] minBytes = MokoUtils.toByteArray(min, 2);
        byte[] maxBytes = MokoUtils.toByteArray(max, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_IBEACON_MAJOR_RANGE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x04,
                minBytes[0],
                minBytes[1],
                maxBytes[0],
                maxBytes[1]
        };

    }

    public void setFilterBXPIBeaconMinorRange(@IntRange(from = 0, to = 65535) int min,
                                              @IntRange(from = 0, to = 65535) int max) {
        byte[] minBytes = MokoUtils.toByteArray(min, 2);
        byte[] maxBytes = MokoUtils.toByteArray(max, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_IBEACON_MINOR_RANGE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x04,
                minBytes[0],
                minBytes[1],
                maxBytes[0],
                maxBytes[1]
        };

    }

    public void setFilterBXPIBeaconUUID(String uuid) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_IBEACON_UUID.getParamsKey(), 2);
        if (TextUtils.isEmpty(uuid)) {
            data = new byte[5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) 0x00;
        } else {
            byte[] uuidBytes = MokoUtils.hex2bytes(uuid);
            int length = uuidBytes.length;
            data = new byte[length + 5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) length;
            for (int i = 0; i < uuidBytes.length; i++) {
                data[i + 5] = uuidBytes[i];
            }
        }
        response.responseValue = data;
    }

    public void setFilterBXPTagEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_TAG_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterBXPTagPrecise(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_TAG_PRECISE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterBXPTagReverse(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_TAG_REVERSE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterBXPTagRules(ArrayList<String> filterBXPTagRules) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_TAG_RULES.getParamsKey(), 2);
        if (filterBXPTagRules == null || filterBXPTagRules.size() == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) cmdBytes[0],
                    (byte) cmdBytes[1],
                    (byte) 0x00
            };
        } else {
            int length = 0;
            for (String mac : filterBXPTagRules) {
                length += 1;
                length += mac.length() / 2;
            }
            data = new byte[5 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) length;
            int index = 0;
            for (int i = 0, size = filterBXPTagRules.size(); i < size; i++) {
                String mac = filterBXPTagRules.get(i);
                byte[] macBytes = MokoUtils.hex2bytes(mac);
                int l = macBytes.length;
                data[5 + index] = (byte) l;
                index++;
                for (int j = 0; j < l; j++, index++) {
                    data[5 + index] = macBytes[j];
                }
            }
        }
        response.responseValue = data;
    }

    public void setFilterMkPirEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MK_PIR_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setFilterMkPirSensorDetectionStatus(@IntRange(from = 0, to = 2) int type) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MK_PIR_DETECTION_STATUS.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) type
        };
    }

    public void setFilterMkPirSensorSensitivity(@IntRange(from = 0, to = 3) int type) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MK_PIR_SENSOR_SENSITIVITY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) type
        };
    }

    public void setFilterMkPirDoorStatus(@IntRange(from = 0, to = 2) int type) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MK_PIR_DOOR_STATUS.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) type
        };
    }

    public void setFilterMkPirDelayResStatus(@IntRange(from = 0, to = 3) int type) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MK_PIR_DELAY_RES_STATUS.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) type
        };
    }

    public void setFilterMkPirMajorRange(@IntRange(from = 0, to = 65535) int min,
                                         @IntRange(from = 0, to = 65535) int max) {
        byte[] minBytes = MokoUtils.toByteArray(min, 2);
        byte[] maxBytes = MokoUtils.toByteArray(max, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MK_PIR_MAJOR.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x04,
                minBytes[0],
                minBytes[1],
                maxBytes[0],
                maxBytes[1]
        };
    }

    public void setFilterMkPirMinorRange(@IntRange(from = 0, to = 65535) int min,
                                         @IntRange(from = 0, to = 65535) int max) {
        byte[] minBytes = MokoUtils.toByteArray(min, 2);
        byte[] maxBytes = MokoUtils.toByteArray(max, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MK_PIR_MINOR.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x04,
                minBytes[0],
                minBytes[1],
                maxBytes[0],
                maxBytes[1]
        };
    }

    public void setFilterMkTofEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MK_TOF_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterMkTofRules(ArrayList<String> filterMkTofRules) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_MK_TOF_MFG_CODE.getParamsKey(), 2);
        if (filterMkTofRules == null || filterMkTofRules.size() == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) cmdBytes[0],
                    (byte) cmdBytes[1],
                    (byte) 0x00
            };
        } else {
            int length = 0;
            for (String mac : filterMkTofRules) {
                length += 1;
                length += mac.length() / 2;
            }
            data = new byte[5 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) length;
            int index = 0;
            for (int i = 0, size = filterMkTofRules.size(); i < size; i++) {
                String mac = filterMkTofRules.get(i);
                byte[] macBytes = MokoUtils.hex2bytes(mac);
                int l = macBytes.length;
                data[5 + index] = (byte) l;
                index++;
                for (int j = 0; j < l; j++, index++) {
                    data[5 + index] = macBytes[j];
                }
            }
        }
        response.responseValue = data;
    }

//    public void setFilterBXPSensorInfoEnable(@IntRange(from = 0, to = 1) int enable) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_SENSOR_INFO_ENABLE.getParamsKey(), 2);
//        response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0],
//                (byte) cmdBytes[1],
//                (byte) 0x01,
//                (byte) enable
//        };
//
//    }
//
//    public void setFilterBXPSensorInfoPrecise(@IntRange(from = 0, to = 1) int enable) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_SENSOR_INFO_PRECISE.getParamsKey(), 2);
//        response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0],
//                (byte) cmdBytes[1],
//                (byte) 0x01,
//                (byte) enable
//        };
//
//    }
//
//    public void setFilterBXPSensorInfoReverse(@IntRange(from = 0, to = 1) int enable) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_SENSOR_INFO_REVERSE.getParamsKey(), 2);
//        response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0],
//                (byte) cmdBytes[1],
//                (byte) 0x01,
//                (byte) enable
//        };
//
//    }
//
//    public void setFilterBXPSensorInfoRules(ArrayList<String> filterBXPTagRules) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_SENSOR_INFO_RULES.getParamsKey(), 2);
//        if (filterBXPTagRules == null || filterBXPTagRules.size() == 0) {
//            data = new byte[]{
//                    (byte) 0xED,
//                    (byte) 0x01,
//                    (byte) cmdBytes[0],
//                    (byte) cmdBytes[1],
//                    (byte) 0x00
//            };
//        } else {
//            int length = 0;
//            for (String mac : filterBXPTagRules) {
//                length += 1;
//                length += mac.length() / 2;
//            }
//            data = new byte[5 + length];
//            data[0] = (byte) 0xED;
//            data[1] = (byte) 0x01;
//            data[2] = (byte) cmdBytes[0];
//            data[3] = (byte) cmdBytes[1];
//            data[4] = (byte) length;
//            int index = 0;
//            for (int i = 0, size = filterBXPTagRules.size(); i < size; i++) {
//                String mac = filterBXPTagRules.get(i);
//                byte[] macBytes = MokoUtils.hex2bytes(mac);
//                int l = macBytes.length;
//                data[5 + index] = (byte) l;
//                index++;
//                for (int j = 0; j < l; j++, index++) {
//                    data[5 + index] = macBytes[j];
//                }
//            }
//        }
//        response.responseValue = data;
//    }

    public void setFilterBXPButtonEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_BUTTON_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterBXPButtonRules(@IntRange(from = 0, to = 1) int singleEnable,
                                        @IntRange(from = 0, to = 1) int doubleEnable,
                                        @IntRange(from = 0, to = 1) int longEnable,
                                        @IntRange(from = 0, to = 1) int abnormalEnable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_BUTTON_RULES.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x04,
                (byte) singleEnable,
                (byte) doubleEnable,
                (byte) longEnable,
                (byte) abnormalEnable,
        };

    }

    public void setFilterEddystoneUIDEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_EDDYSTONE_UID_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterEddystoneUIDNamespace(String namespace) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_EDDYSTONE_UID_NAMESPACE.getParamsKey(), 2);
        if (TextUtils.isEmpty(namespace)) {
            data = new byte[5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) 0x00;
        } else {
            byte[] dataBytes = MokoUtils.hex2bytes(namespace);
            int length = dataBytes.length;
            data = new byte[length + 5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) length;
            for (int i = 0; i < dataBytes.length; i++) {
                data[i + 5] = dataBytes[i];
            }
        }
        response.responseValue = data;
    }

    public void setFilterEddystoneUIDInstance(String instance) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_EDDYSTONE_UID_INSTANCE.getParamsKey(), 2);
        if (TextUtils.isEmpty(instance)) {
            data = new byte[5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) 0x00;
        } else {
            byte[] dataBytes = MokoUtils.hex2bytes(instance);
            int length = dataBytes.length;
            data = new byte[length + 5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) length;
            for (int i = 0; i < dataBytes.length; i++) {
                data[i + 5] = dataBytes[i];
            }
        }
        response.responseValue = data;
    }

    public void setFilterEddystoneUrlEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_EDDYSTONE_URL_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterEddystoneUrl(String url) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_EDDYSTONE_URL.getParamsKey(), 2);
        if (TextUtils.isEmpty(url)) {
            data = new byte[5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) 0x00;
        } else {
            byte[] dataBytes = url.getBytes();
            int length = dataBytes.length;
            data = new byte[length + 5];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) length;
            for (int i = 0; i < dataBytes.length; i++) {
                data[i + 5] = dataBytes[i];
            }
        }
        response.responseValue = data;
    }

    public void setFilterEddystoneTlmEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_EDDYSTONE_TLM_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterEddystoneTlmVersion(@IntRange(from = 0, to = 2) int version) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_EDDYSTONE_TLM_VERSION.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) version
        };

    }

    public void setFilterBXPAccEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_ACC.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterBXPTHEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_TH.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterBXPDeviceEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_BXP_DEVICE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterOtherEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_OTHER_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setFilterOtherRelationship(@IntRange(from = 0, to = 5) int relationship) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_OTHER_RELATIONSHIP.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) relationship
        };

    }

    public void setFilterOtherRules(ArrayList<String> filterOtherRules) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_OTHER_RULES.getParamsKey(), 2);
        if (filterOtherRules == null || filterOtherRules.size() == 0) {
            data = new byte[]{
                    (byte) 0xED,
                    (byte) 0x01,
                    (byte) cmdBytes[0],
                    (byte) cmdBytes[1],
                    (byte) 0x00
            };
        } else {
            int length = 0;
            for (String other : filterOtherRules) {
                length += 1;
                length += other.length() / 2;
            }
            data = new byte[5 + length];
            data[0] = (byte) 0xED;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) length;
            int index = 0;
            for (int i = 0, size = filterOtherRules.size(); i < size; i++) {
                String rule = filterOtherRules.get(i);
                byte[] ruleBytes = MokoUtils.hex2bytes(rule);
                int l = ruleBytes.length;
                data[5 + index] = (byte) l;
                index++;
                for (int j = 0; j < l; j++, index++) {
                    data[5 + index] = ruleBytes[j];
                }
            }
        }
        response.responseValue = data;
    }

    public void setGPSPosTimeoutL76(@IntRange(from = 30, to = 600) int timeout) {
        byte[] timeoutBytes = MokoUtils.toByteArray(timeout, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_POS_TIMEOUT_L76C.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                timeoutBytes[0],
                timeoutBytes[1]
        };

    }


    public void setGPSPDOPLimitL76(@IntRange(from = 25, to = 100) int limit) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_PDOP_LIMIT_L76C.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) limit,
        };

    }

    public void setGPSExtremeModeL76(@IntRange(from = 0, to = 1) int limit) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_EXTREME_MODE_L76C.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) limit,
        };

    }

    public void setVoltageReportEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_VOLTAGE_REPORT_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable,
        };

    }

    public void setOutdoorBleReportInterval(@IntRange(from = 1, to = 100) int interval) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_OUTDOOR_BLE_REPORT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) interval,
        };

    }

    public void setOutdoorGpsReportInterval(@IntRange(from = 1, to = 14400) int interval) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_OUTDOOR_GPS_REPORT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                intervalBytes[0],
                intervalBytes[1],
        };

    }

//    public void setGPSPosTimeout(@IntRange(from = 1, to = 5) int timeout) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_POS_TIMEOUT.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//                (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) timeout
//        };
//        
//    }
//
//    public void setGPSPosSatelliteThreshold(@IntRange(from = 4, to = 10) int threshold) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_POS_SATELLITE_THRESHOLD.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//            (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) threshold
//        };
//        
//    }
//
//    public void setGPSPosDataType(@IntRange(from = 0, to = 1) int type) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_POS_DATA_TYPE.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//            (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) type
//        };
//        
//    }
//
//    public void setGPSPosSystem(@IntRange(from = 0, to = 2) int type) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_POS_SYSTEM.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//            (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) type
//        };
//        
//    }
//
//    public void setGPSPosAutoEnable(@IntRange(from = 0, to = 1) int enable) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_POS_AUTONMOUS_AIDING_ENABLE.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//            (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) enable
//        };
//        
//    }
//
//    public void setGPSPosAuxiliaryLatLon(@IntRange(from = -9000000, to = 9000000) int lat, @IntRange(from = -18000000, to = 18000000) int lon) {
//        byte[] latBytes = MokoUtils.toByteArray(lat, 4);
//        byte[] lonBytes = MokoUtils.toByteArray(lon, 4);
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_POS_AUXILIARY_LAT_LON.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//            (byte)cmdBytes[1],
//                (byte) 0x08,
//                latBytes[0],
//                latBytes[1],
//                latBytes[2],
//                latBytes[3],
//                lonBytes[0],
//                lonBytes[1],
//                lonBytes[2],
//                lonBytes[3],
//        };
//        
//    }
//
//    public void setGPSPosEphemerisStartNotifyEnable(@IntRange(from = 0, to = 1) int enable) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_POS_EPHEMERIS_START_NOTIFY_ENABLE.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//            (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) enable
//        };
//        
//    }
//
//    public void setGPSPosEphemerisEndNotifyEnable(@IntRange(from = 0, to = 1) int enable) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_GPS_POS_EPHEMERIS_END_NOTIFY_ENABLE.getParamsKey(), 2);response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0], 
//            (byte)cmdBytes[1],
//                (byte) 0x01,
//                (byte) enable
//        };
//        
//    }

    public void setBlePosMechanism(@IntRange(from = 0, to = 1) int mechanism) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_BLE_POS_MECHANISM.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) mechanism
        };

    }

    public void setLoraRegion(@IntRange(from = 0, to = 13) int region) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_REGION.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) region
        };
    }

    public void setLoraUploadMode(@IntRange(from = 1, to = 2) int mode) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_MODE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) mode
        };
    }

    public void setLoraDevEUI(String devEui) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(devEui);
        int length = rawDataBytes.length;
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_DEV_EUI.getParamsKey(), 2);
        data = new byte[5 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) cmdBytes[0];
        data[3] = (byte) cmdBytes[1];
        data[4] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 5] = rawDataBytes[i];
        }
        response.responseValue = data;
    }

    public void setLoraAppEUI(String appEui) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(appEui);
        int length = rawDataBytes.length;
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_APP_EUI.getParamsKey(), 2);
        data = new byte[5 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) cmdBytes[0];
        data[3] = (byte) cmdBytes[1];
        data[4] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 5] = rawDataBytes[i];
        }
        response.responseValue = data;
    }

    public void setLoraAppKey(String appKey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(appKey);
        int length = rawDataBytes.length;
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_APP_KEY.getParamsKey(), 2);
        data = new byte[5 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) cmdBytes[0];
        data[3] = (byte) cmdBytes[1];
        data[4] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 5] = rawDataBytes[i];
        }
        response.responseValue = data;
    }

    public void setLoraDevAddr(String devAddr) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(devAddr);
        int length = rawDataBytes.length;
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_DEV_ADDR.getParamsKey(), 2);
        data = new byte[5 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) cmdBytes[0];
        data[3] = (byte) cmdBytes[1];
        data[4] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 5] = rawDataBytes[i];
        }
        response.responseValue = data;
    }

    public void setLoraAppSKey(String appSkey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(appSkey);
        int length = rawDataBytes.length;
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_APP_SKEY.getParamsKey(), 2);
        data = new byte[5 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) cmdBytes[0];
        data[3] = (byte) cmdBytes[1];
        data[4] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 5] = rawDataBytes[i];
        }
        response.responseValue = data;
    }

    public void setLoraNwkSKey(String nwkSkey) {
        byte[] rawDataBytes = MokoUtils.hex2bytes(nwkSkey);
        int length = rawDataBytes.length;
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_NWK_SKEY.getParamsKey(), 2);
        data = new byte[5 + length];
        data[0] = (byte) 0xED;
        data[1] = (byte) 0x01;
        data[2] = (byte) cmdBytes[0];
        data[3] = (byte) cmdBytes[1];
        data[4] = (byte) length;
        for (int i = 0; i < length; i++) {
            data[i + 5] = rawDataBytes[i];
        }
        response.responseValue = data;
    }

//    public void setLoraMessageType(@IntRange(from = 0, to = 1) int type) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_MESSAGE_TYPE.getParamsKey(), 2);
//        response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0],
//                (byte) cmdBytes[1],
//                (byte) 0x01,
//                (byte) type
//        };
//    }

    public void setLoraCH(int ch1, int ch2) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_CH.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                (byte) ch1,
                (byte) ch2
        };
    }

    public void setLoraDR(int dr1) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_DR.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) dr1
        };
    }

    public void setLoraUplinkStrategy(int adr, int number, int dr1, int dr2) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_UPLINK_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x04,
                (byte) adr,
                (byte) number,
                (byte) dr1,
                (byte) dr2
        };
    }


    public void setLoraDutyCycleEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_DUTYCYCLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setLoraTimeSyncInterval(@IntRange(from = 0, to = 255) int interval) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_TIME_SYNC_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) interval
        };
    }

    public void setLoraNetworkInterval(@IntRange(from = 0, to = 255) int interval) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_NETWORK_CHECK_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) interval
        };
    }

    public void setLoraAdrAckLimit(@IntRange(from = 1, to = 255) int interval) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_ADR_ACK_LIMIT.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) interval
        };
    }

    public void setLoraAdrAckDelay(@IntRange(from = 1, to = 255) int interval) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LORA_ADR_ACK_DELAY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) interval
        };
    }

//    public void setDownLinkPosStrategy(@IntRange(from = 0, to = 3) int strategy) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_DOWN_LINK_POS_STRATEGY.getParamsKey(), 2);
//        response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0],
//                (byte) cmdBytes[1],
//                (byte) 0x01,
//                (byte) strategy
//        };
//
//    }

    public void setAlarmEnable(@IntRange(from = 0, to = 1) int enable, @IntRange(from = 0, to = 1) int exitEnable, int type) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_ENABLE_1.getParamsKey(), 2);
        if (type == 1)
            cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_ENABLE_2.getParamsKey(), 2);
        if (type == 2)
            cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_ENABLE_3.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                (byte) enable,
                (byte) exitEnable
        };
    }

    public void setBuzzerEnable(@IntRange(from = 0, to = 2) int enable, int type) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_BUZZER_ENABLE_1.getParamsKey(), 2);
        if (type == 1)
            cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_BUZZER_ENABLE_2.getParamsKey(), 2);
        if (type == 2)
            cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_BUZZER_ENABLE_3.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setExitAlarmDuration(@IntRange(from = 10, to = 15) int duration, int type) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_EXIT_ALARM_DURATION_1.getParamsKey(), 2);
        if (type == 1)
            cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_EXIT_ALARM_DURATION_2.getParamsKey(), 2);
        if (type == 2)
            cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_EXIT_ALARM_DURATION_3.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) duration
        };
    }

    public void setAlarmReportInterval(@IntRange(from = 1, to = 1440) int interval, int type) {
        byte[] intervalBytes = MokoUtils.toByteArray(interval, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_REPORT_INTERVAL_1.getParamsKey(), 2);
        if (type == 1)
            cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_REPORT_INTERVAL_2.getParamsKey(), 2);
        if (type == 2)
            cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_REPORT_INTERVAL_3.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                (byte) intervalBytes[0],
                (byte) intervalBytes[1]
        };
    }

    public void setAccWakeupCondition(@IntRange(from = 1, to = 20) int threshold,
                                      @IntRange(from = 1, to = 10) int duration) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ACC_WAKEUP_CONDITION.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                (byte) threshold,
                (byte) duration
        };

    }

    public void setAccMotionCondition(@IntRange(from = 10, to = 250) int threshold,
                                      @IntRange(from = 1, to = 50) int duration) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ACC_MOTION_CONDITION.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                (byte) threshold,
                (byte) duration
        };

    }

    public void setShockDetectionEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_SHOCK_DETECTION_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }

    public void setShockThreshold(@IntRange(from = 10, to = 255) int threshold) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_SHOCK_THRESHOLD.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) threshold
        };

    }

    public void setShockReportInterval(@IntRange(from = 3, to = 255) int interval) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_SHOCK_REPORT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) interval
        };

    }

    public void setShockTimeout(@IntRange(from = 1, to = 20) int timeout) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_SHOCK_TIMEOUT.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) timeout
        };

    }

    public void setManDownDetectionEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MAN_DOWN_DETECTION_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };

    }


    public void setManDownDetectionTimeout(@IntRange(from = 1, to = 120) int timeout) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MAN_DOWN_DETECTION_TIMEOUT.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) timeout
        };

    }


    public void setManDownPosStrategy(@IntRange(from = 0, to = 6) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MAN_DOWN_DETECTION_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };
    }

    public void setManDownReportInterval(@IntRange(from = 10, to = 600) int interval) {
        byte[] bytes = MokoUtils.toByteArray(interval, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MAN_DOWN_DETECTION_REPORT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                bytes[0],
                bytes[1]
        };
    }

//    public void setManDownIdleReset() {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_MAN_DOWN_IDLE_RESET.getParamsKey(), 2);
//        response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0],
//                (byte) cmdBytes[1],
//                (byte) 0x00
//        };
//    }

    public void setAlarmType(@IntRange(from = 0, to = 2) int type) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_TYPE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) type
        };
    }

    public void setAlarmExitTime(@IntRange(from = 5, to = 15) int time) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_EXIT_TIME.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) time
        };
    }

    public void setAlarmAlertTriggerType(@IntRange(from = 0, to = 4) int type) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_ALERT_TRIGGER_TYPE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) type
        };
    }

    public void setAlarmAlertPosStrategy(@IntRange(from = 0, to = 3) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_ALERT_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };
    }

    public void setAlarmAlertNotifyEnable(int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_ALERT_NOTIFY_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };
    }

    //
    public void setAlarmSosTriggerType(@IntRange(from = 0, to = 4) int type) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_SOS_TRIGGER_TYPE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) type
        };
    }

    public void setAlarmSosPosStrategy(@IntRange(from = 0, to = 3) int strategy) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_SOS_POS_STRATEGY.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) strategy
        };
    }

    public void setAlarmSosReportInterval(@IntRange(from = 10, to = 600) int interval) {
        byte[] bytes = MokoUtils.toByteArray(interval, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_SOS_REPORT_INTERVAL.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                bytes[0],
                bytes[1]
        };
    }

    public void setAlarmSosNotifyEnable(int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ALARM_SOS_NOTIFY_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setTempMonitorEnable(int enable, int alarmEnable) {
        enable = enable | (alarmEnable << 1);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_TEMP_MONITOR_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setTempSampleRate(@IntRange(from = 1, to = 3600) int rate) {
        byte[] bytes = MokoUtils.toByteArray(rate, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_TEMP_SAMPLE_RATE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                bytes[0],
                bytes[1]
        };
    }

    public void setTempAlarmThreshold(int min, int max) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_TEMP_ALARM_THRESHOLD.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                (byte) min,
                (byte) max,
        };
    }

    public void setLightMonitorEnable(int enable, int alarmEnable) {
        enable = enable | (alarmEnable << 1);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LIGHT_MONITOR_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };
    }

    public void setLightSampleRate(@IntRange(from = 1, to = 3600) int rate) {
        byte[] bytes = MokoUtils.toByteArray(rate, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LIGHT_SAMPLE_RATE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                bytes[0],
                bytes[1]
        };
    }

    public void setLightAlarmThreshold(int threshold) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_LIGHT_ALARM_THRESHOLD.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) threshold
        };
    }

//    public void setActiveStateCountEnable(@IntRange(from = 0, to = 1) int enable) {
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ACTIVE_STATE_COUNT_ENABLE.getParamsKey(), 2);
//        response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0],
//                (byte) cmdBytes[1],
//                (byte) 0x01,
//                (byte) enable
//        };
//
//    }
//
//    public void setActiveStateTimeout(@IntRange(from = 1, to = 86400) int timeout) {
//        byte[] timeoutBytes = MokoUtils.toByteArray(timeout, 4);
//        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_ACTIVE_STATE_TIMEOUT.getParamsKey(), 2);
//        response.responseValue = data = new byte[]{
//                (byte) 0xED,
//                (byte) 0x01,
//                (byte) cmdBytes[0],
//                (byte) cmdBytes[1],
//                (byte) 0x04,
//                timeoutBytes[0],
//                timeoutBytes[1],
//                timeoutBytes[2],
//                timeoutBytes[3],
//        };
//
//    }

    public void readStorageData(@IntRange(from = 1, to = 65535) int time) {
        byte[] rawDataBytes = MokoUtils.toByteArray(time, 2);
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_READ_STORAGE_DATA.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x02,
                rawDataBytes[0],
                rawDataBytes[1]
        };
    }

    public void clearStorageData() {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_CLEAR_STORAGE_DATA.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x00
        };
    }

    public void setSyncEnable(@IntRange(from = 0, to = 1) int enable) {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_SYNC_ENABLE.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x01,
                (byte) enable
        };
    }


    public void setBatteryReset() {
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_BATTERY_RESET.getParamsKey(), 2);
        response.responseValue = data = new byte[]{
                (byte) 0xED,
                (byte) 0x01,
                (byte) cmdBytes[0],
                (byte) cmdBytes[1],
                (byte) 0x00
        };
    }

    public void setFilterNameRules(ArrayList<String> filterNameRules) {
        int length = 0;
        for (String name : filterNameRules) {
            length += 1;
            length += name.length();
        }
        dataBytes = new byte[length];
        int index = 0;
        for (int i = 0, size = filterNameRules.size(); i < size; i++) {
            String name = filterNameRules.get(i);
            byte[] nameBytes = name.getBytes();
            int l = nameBytes.length;
            dataBytes[index] = (byte) l;
            index++;
            for (int j = 0; j < l; j++, index++) {
                dataBytes[index] = nameBytes[j];
            }
        }
        dataLength = dataBytes.length;
        if (dataLength != 0) {
            if (dataLength % DATA_LENGTH_MAX > 0) {
                packetCount = dataLength / DATA_LENGTH_MAX + 1;
            } else {
                packetCount = dataLength / DATA_LENGTH_MAX;
            }
        } else {
            packetCount = 1;
        }
        remainPack = packetCount - 1;
        packetIndex = 0;
        delayTime = DEFAULT_DELAY_TIME + 500 * packetCount;
        byte[] cmdBytes = MokoUtils.toByteArray(ParamsKeyEnum.KEY_FILTER_NAME_RULES.getParamsKey(), 2);
        if (packetCount > 1) {
            data = new byte[DATA_LENGTH_MAX + 7];
            data[0] = (byte) 0xEE;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) packetCount;
            data[5] = (byte) packetIndex;
            data[6] = (byte) DATA_LENGTH_MAX;
            for (int i = 0; i < DATA_LENGTH_MAX; i++, dataOrigin++) {
                data[i + 7] = dataBytes[dataOrigin];
            }
        } else {
            data = new byte[dataLength + 7];
            data[0] = (byte) 0xEE;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) packetCount;
            data[5] = (byte) packetIndex;
            data[6] = (byte) dataLength;
            for (int i = 0; i < dataLength; i++) {
                data[i + 7] = dataBytes[i];
            }
        }
        response.responseValue = data;
    }

    private int packetCount;
    private int packetIndex;
    private int remainPack;
    private int dataLength;
    private int dataOrigin;
    private byte[] dataBytes;
    private static final int DATA_LENGTH_MAX = 176;

    @Override
    public boolean parseValue(byte[] value) {
        final int header = value[0] & 0xFF;
        if (header == 0xED)
            return true;
        final int cmd = MokoUtils.toInt(Arrays.copyOfRange(value, 2, 4));
        final int result = value[5] & 0xFF;
        if (result == 1) {
            remainPack--;
            packetIndex++;
            if (remainPack >= 0) {
                assembleRemainData(cmd);
                return false;
            }
            return true;
        }
        return false;
    }

    private void assembleRemainData(int cmd) {
        int length = dataLength - dataOrigin;
        byte[] cmdBytes = MokoUtils.toByteArray(cmd, 2);
        if (length > DATA_LENGTH_MAX) {
            data = new byte[DATA_LENGTH_MAX + 7];
            data[0] = (byte) 0xEE;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) packetCount;
            data[5] = (byte) packetIndex;
            data[6] = (byte) DATA_LENGTH_MAX;
            for (int i = 0; i < DATA_LENGTH_MAX; i++, dataOrigin++) {
                data[i + 7] = dataBytes[dataOrigin];
            }
        } else {
            data = new byte[length + 7];
            data[0] = (byte) 0xEE;
            data[1] = (byte) 0x01;
            data[2] = (byte) cmdBytes[0];
            data[3] = (byte) cmdBytes[1];
            data[4] = (byte) packetCount;
            data[5] = (byte) packetIndex;
            data[6] = (byte) length;
            for (int i = 0; i < length; i++, dataOrigin++) {
                data[i + 7] = dataBytes[dataOrigin];
            }
        }
        response.responseValue = data;
        LoRaLW013SBMokoSupport.getInstance().sendDirectOrder(this);
    }
}
