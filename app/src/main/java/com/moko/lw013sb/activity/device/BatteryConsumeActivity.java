package com.moko.lw013sb.activity.device;


import android.os.Bundle;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw013sb.activity.BaseActivity;
import com.moko.lw013sb.databinding.Lw013ActivityBatteryConsumeBinding;
import com.moko.lib.loraui.dialog.AlertMessageDialog;
import com.moko.support.lw013sb.LoRaLW013SBMokoSupport;
import com.moko.support.lw013sb.OrderTaskAssembler;
import com.moko.support.lw013sb.entity.OrderCHAR;
import com.moko.support.lw013sb.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatteryConsumeActivity extends BaseActivity {

    private Lw013ActivityBatteryConsumeBinding mBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013ActivityBatteryConsumeBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        showSyncingProgressDialog();
        mBind.tvAdvTimes.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getBatteryInfo());
            orderTasks.add(OrderTaskAssembler.getBatteryInfoAll());
            orderTasks.add(OrderTaskAssembler.getBatteryInfoLast());
            LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        if (!MokoConstants.ACTION_CURRENT_DATA.equals(action))
            EventBus.getDefault().cancelEventDelivery(event);
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderCHAR) {
                    case CHAR_PARAMS:
                        if (value.length >= 5) {
                            int header = value[0] & 0xFF;// 0xED
                            int flag = value[1] & 0xFF;// read or write
                            int cmd = MokoUtils.toInt(Arrays.copyOfRange(value, 2, 4));
                            if (header != 0xED)
                                return;
                            ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[4] & 0xFF;
                            if (flag == 0x01) {
                                // write
                                int result = value[5] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_BATTERY_RESET:
                                        if (result == 1) {
                                            AlertMessageDialog dialog = new AlertMessageDialog();
                                            dialog.setMessage("Reset Successfully！");
                                            dialog.setConfirm("OK");
                                            dialog.setCancelGone();
                                            dialog.show(getSupportFragmentManager());
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read

                                switch (configKeyEnum) {
                                    case KEY_BATTERY_INFO:
                                        if (length == 64) {
                                            int runtime = MokoUtils.toInt(Arrays.copyOfRange(value, 5, 9));
                                            mBind.tvRuntime.setText(String.format("%d s", runtime));
                                            int advTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 9, 13));
                                            mBind.tvAdvTimes.setText(String.format("%d times", advTimes));
                                            int redDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 13, 17));
                                            mBind.tvRedDuration.setText(String.format("%d s", redDuration));
                                            int greenDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 17, 21));
                                            mBind.tvGreenDuration.setText(String.format("%d s", greenDuration));
                                            int blueDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 21, 25));
                                            mBind.tvBlueDuration.setText(String.format("%d s", blueDuration));
                                            int buzzerNormalDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 25, 29));
                                            mBind.tvBuzzerNormalDuration.setText(String.format("%d s", buzzerNormalDuration));
                                            int buzzerAlarmDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 29, 33));
                                            mBind.tvBuzzerAlarmDuration.setText(String.format("%d s", buzzerAlarmDuration));
                                            int event1TriggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 33, 37));
                                            mBind.tvEvent1TriggerDuration.setText(String.format("%d times", event1TriggerTimes));
                                            int event1PayloadTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 37, 41));
                                            mBind.tvEvent1PayloadTimes.setText(String.format("%d times", event1PayloadTimes));
                                            int event2TriggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 41, 45));
                                            mBind.tvEvent2TriggerDuration.setText(String.format("%d times", event2TriggerTimes));
                                            int event2PayloadTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 45, 49));
                                            mBind.tvEvent2PayloadTimes.setText(String.format("%d times", event2PayloadTimes));
                                            int event3TriggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 49, 53));
                                            mBind.tvEvent3TriggerDuration.setText(String.format("%d times", event3TriggerTimes));
                                            int event3PayloadTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 53, 57));
                                            mBind.tvEvent3PayloadTimes.setText(String.format("%d times", event3PayloadTimes));
                                            int loraTransmissionTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 57, 61));
                                            mBind.tvLoraTransmissionTimes.setText(String.format("%d times", loraTransmissionTimes));
                                            int loraPower = MokoUtils.toInt(Arrays.copyOfRange(value, 61, 65));
                                            mBind.tvLoraPower.setText(String.format("%d s", loraPower));
                                            String batteryConsumeStr = MokoUtils.getDecimalFormat("0.###").format(MokoUtils.toInt(Arrays.copyOfRange(value, 65, 69)) * 0.001f);
                                            mBind.tvBatteryConsume.setText(String.format("%s mAH", batteryConsumeStr));
                                        }
                                        break;
                                    case KEY_BATTERY_INFO_ALL:
                                        if (length == 64) {
                                            int runtime = MokoUtils.toInt(Arrays.copyOfRange(value, 5, 9));
                                            mBind.tvRuntimeAll.setText(String.format("%d s", runtime));
                                            int advTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 9, 13));
                                            mBind.tvAdvTimesAll.setText(String.format("%d times", advTimes));
                                            int redDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 13, 17));
                                            mBind.tvRedDurationAll.setText(String.format("%d s", redDuration));
                                            int greenDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 17, 21));
                                            mBind.tvGreenDurationAll.setText(String.format("%d s", greenDuration));
                                            int blueDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 21, 25));
                                            mBind.tvBlueDurationAll.setText(String.format("%d s", blueDuration));
                                            int buzzerNormalDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 25, 29));
                                            mBind.tvBuzzerNormalDurationAll.setText(String.format("%d s", buzzerNormalDuration));
                                            int buzzerAlarmDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 29, 33));
                                            mBind.tvBuzzerAlarmDurationAll.setText(String.format("%d s", buzzerAlarmDuration));
                                            int event1TriggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 33, 37));
                                            mBind.tvEvent1TriggerDurationAll.setText(String.format("%d times", event1TriggerTimes));
                                            int event1PayloadTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 37, 41));
                                            mBind.tvEvent1PayloadTimesAll.setText(String.format("%d times", event1PayloadTimes));
                                            int event2TriggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 41, 45));
                                            mBind.tvEvent2TriggerDurationAll.setText(String.format("%d times", event2TriggerTimes));
                                            int event2PayloadTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 45, 49));
                                            mBind.tvEvent2PayloadTimesAll.setText(String.format("%d times", event2PayloadTimes));
                                            int event3TriggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 49, 53));
                                            mBind.tvEvent3TriggerDurationAll.setText(String.format("%d times", event3TriggerTimes));
                                            int event3PayloadTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 53, 57));
                                            mBind.tvEvent3PayloadTimesAll.setText(String.format("%d times", event3PayloadTimes));
                                            int loraTransmissionTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 57, 61));
                                            mBind.tvLoraTransmissionTimesAll.setText(String.format("%d times", loraTransmissionTimes));
                                            int loraPower = MokoUtils.toInt(Arrays.copyOfRange(value, 61, 65));
                                            mBind.tvLoraPowerAll.setText(String.format("%d s", loraPower));
                                            String batteryConsumeStr = MokoUtils.getDecimalFormat("0.###").format(MokoUtils.toInt(Arrays.copyOfRange(value, 65, 69)) * 0.001f);
                                            mBind.tvBatteryConsumeAll.setText(String.format("%s mAH", batteryConsumeStr));
                                        }
                                        break;
                                    case KEY_BATTERY_INFO_LAST:
                                        if (length == 64) {
                                            int runtime = MokoUtils.toInt(Arrays.copyOfRange(value, 5, 9));
                                            mBind.tvRuntimeLast.setText(String.format("%d s", runtime));
                                            int advTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 9, 13));
                                            mBind.tvAdvTimesLast.setText(String.format("%d times", advTimes));
                                            int redDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 13, 17));
                                            mBind.tvRedDurationLast.setText(String.format("%d s", redDuration));
                                            int greenDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 17, 21));
                                            mBind.tvGreenDurationLast.setText(String.format("%d s", greenDuration));
                                            int blueDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 21, 25));
                                            mBind.tvBlueDurationLast.setText(String.format("%d s", blueDuration));
                                            int buzzerNormalDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 25, 29));
                                            mBind.tvBuzzerNormalDurationLast.setText(String.format("%d s", buzzerNormalDuration));
                                            int buzzerAlarmDuration = MokoUtils.toInt(Arrays.copyOfRange(value, 29, 33));
                                            mBind.tvBuzzerAlarmDurationLast.setText(String.format("%d s", buzzerAlarmDuration));
                                            int event1TriggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 33, 37));
                                            mBind.tvEvent1TriggerDurationLast.setText(String.format("%d times", event1TriggerTimes));
                                            int event1PayloadTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 37, 41));
                                            mBind.tvEvent1PayloadTimesLast.setText(String.format("%d times", event1PayloadTimes));
                                            int event2TriggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 41, 45));
                                            mBind.tvEvent2TriggerDurationLast.setText(String.format("%d times", event2TriggerTimes));
                                            int event2PayloadTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 45, 49));
                                            mBind.tvEvent2PayloadTimesLast.setText(String.format("%d times", event2PayloadTimes));
                                            int event3TriggerTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 49, 53));
                                            mBind.tvEvent3TriggerDurationLast.setText(String.format("%d times", event3TriggerTimes));
                                            int event3PayloadTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 53, 57));
                                            mBind.tvEvent3PayloadTimesLast.setText(String.format("%d times", event3PayloadTimes));
                                            int loraTransmissionTimes = MokoUtils.toInt(Arrays.copyOfRange(value, 57, 61));
                                            mBind.tvLoraTransmissionTimesLast.setText(String.format("%d times", loraTransmissionTimes));
                                            int loraPower = MokoUtils.toInt(Arrays.copyOfRange(value, 61, 65));
                                            mBind.tvLoraPowerLast.setText(String.format("%d s", loraPower));
                                            String batteryConsumeStr = MokoUtils.getDecimalFormat("0.###").format(MokoUtils.toInt(Arrays.copyOfRange(value, 65, 69)) * 0.001f);
                                            mBind.tvBatteryConsumeLast.setText(String.format("%s mAH", batteryConsumeStr));
                                        }
                                        break;
                                }
                            }
                        }
                        break;
                }
            }
        });
    }

    public void onBatteryReset(View view) {
        if (isWindowLocked())
            return;
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning！");
        dialog.setMessage("Are you sure to reset battery?");
        dialog.setConfirm("OK");
        dialog.setOnAlertConfirmListener(() -> {
            showSyncingProgressDialog();
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.setBatteryReset());
            orderTasks.add(OrderTaskAssembler.getBatteryInfo());
            orderTasks.add(OrderTaskAssembler.getBatteryInfoAll());
            orderTasks.add(OrderTaskAssembler.getBatteryInfoLast());
            LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        });
        dialog.show(getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void onBack(View view) {
        backHome();
    }

    @Override
    public void onBackPressed() {
        backHome();
    }

    private void backHome() {
        finish();
    }
}
