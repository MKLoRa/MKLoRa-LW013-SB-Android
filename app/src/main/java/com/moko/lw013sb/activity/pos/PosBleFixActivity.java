package com.moko.lw013sb.activity.pos;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw013sb.R;
import com.moko.lw013sb.activity.BaseActivity;
import com.moko.lw013sb.activity.filter.FilterAdvNameActivity;
import com.moko.lw013sb.activity.filter.FilterMacAddressActivity;
import com.moko.lw013sb.activity.filter.FilterRawDataSwitchActivity;
import com.moko.lw013sb.databinding.Lw013ActivityPosBleBinding;
import com.moko.lw013sb.dialog.BottomDialog;
import com.moko.lw013sb.utils.ToastUtils;
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

public class PosBleFixActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {


    private Lw013ActivityPosBleBinding mBind;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private ArrayList<String> mRelationshipValues;
    private int mRelationshipSelected;
    private ArrayList<String> mBleFixMechanismValues;
    private int mBleFixMechanismSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013ActivityPosBleBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);

        mRelationshipValues = new ArrayList<>();
        mRelationshipValues.add("Null");
        mRelationshipValues.add("Only MAC");
        mRelationshipValues.add("Only ADV Name");
        mRelationshipValues.add("Only Raw Data");
        mRelationshipValues.add("ADV Name&Raw Data");
        mRelationshipValues.add("MAC&ADV Name&Raw Data");
        mRelationshipValues.add("ADV Name | Raw Data");
        mBleFixMechanismValues = new ArrayList<>();
        mBleFixMechanismValues.add("Time Priority");
        mBleFixMechanismValues.add("RSSI Priority");
        mBind.sbRssiFilter.setOnSeekBarChangeListener(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        mBind.etPosTimeout.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getBlePosTimeout());
            orderTasks.add(OrderTaskAssembler.getBlePosNumber());
            orderTasks.add(OrderTaskAssembler.getBlePosMechanism());
            orderTasks.add(OrderTaskAssembler.getFilterRSSI());
            orderTasks.add(OrderTaskAssembler.getFilterRelationship());
            LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
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
                                    case KEY_BLE_POS_TIMEOUT:
                                    case KEY_BLE_POS_MAC_NUMBER:
                                    case KEY_BLE_POS_MECHANISM:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_FILTER_RELATIONSHIP:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(PosBleFixActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            ToastUtils.showToast(this, "Save Successfully！");
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_BLE_POS_TIMEOUT:
                                        if (length > 0) {
                                            int number = value[5] & 0xFF;
                                            mBind.etPosTimeout.setText(String.valueOf(number));
                                        }
                                        break;
                                    case KEY_BLE_POS_MAC_NUMBER:
                                        if (length > 0) {
                                            int number = value[5] & 0xFF;
                                            mBind.etMacNumber.setText(String.valueOf(number));
                                        }
                                        break;
                                    case KEY_BLE_POS_MECHANISM:
                                        if (length > 0) {
                                            int mechanism = value[5] & 0xFF;
                                            mBleFixMechanismSelected = mechanism;
                                            mBind.tvBleFixMechanism.setText(mBleFixMechanismValues.get(mechanism));
                                        }
                                        break;
                                    case KEY_FILTER_RSSI:
                                        if (length > 0) {
                                            final int rssi = value[5];
                                            int progress = rssi + 127;
                                            mBind.sbRssiFilter.setProgress(progress);
                                            mBind.tvRssiFilterTips.setText(getString(R.string.rssi_filter, rssi));
                                        }
                                        break;
                                    case KEY_FILTER_RELATIONSHIP:
                                        if (length > 0) {
                                            int relationship = value[5] & 0xFF;
                                            mRelationshipSelected = relationship;
                                            mBind.tvFilterRelationship.setText(mRelationshipValues.get(relationship));
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

    public void onSave(View view) {
        if (isWindowLocked())
            return;
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Para error!");
        }
    }

    private boolean isValid() {
        final String posTimeoutStr = mBind.etPosTimeout.getText().toString();
        if (TextUtils.isEmpty(posTimeoutStr))
            return false;
        final int posTimeout = Integer.parseInt(posTimeoutStr);
        if (posTimeout < 1 || posTimeout > 10) {
            return false;
        }
        final String numberStr = mBind.etMacNumber.getText().toString();
        if (TextUtils.isEmpty(numberStr))
            return false;
        final int number = Integer.parseInt(numberStr);
        if (number < 1 || number > 15) {
            return false;
        }
        return true;

    }


    private void saveParams() {
        final String posTimeoutStr = mBind.etPosTimeout.getText().toString();
        final String numberStr = mBind.etMacNumber.getText().toString();
        final int posTimeout = Integer.parseInt(posTimeoutStr);
        final int number = Integer.parseInt(numberStr);
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setBlePosTimeout(posTimeout));
        orderTasks.add(OrderTaskAssembler.setBlePosNumber(number));
        orderTasks.add(OrderTaskAssembler.setBlePosMechanism(mBleFixMechanismSelected));
        orderTasks.add(OrderTaskAssembler.setFilterRSSI(mBind.sbRssiFilter.getProgress() - 127));
        orderTasks.add(OrderTaskAssembler.setFilterRelationship(mRelationshipSelected));
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dismissSyncProgressDialog();
                            finish();
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            // 注销广播
            unregisterReceiver(mReceiver);
        }
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
        setResult(RESULT_OK);
        finish();
    }

    public void onBleFixMechanism(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mBleFixMechanismValues, mBleFixMechanismSelected);
        dialog.setListener(value -> {
            mBleFixMechanismSelected = value;
            mBind.tvBleFixMechanism.setText(mBleFixMechanismValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onFilterRelationship(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mRelationshipValues, mRelationshipSelected);
        dialog.setListener(value -> {
            mRelationshipSelected = value;
            mBind.tvFilterRelationship.setText(mRelationshipValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onFilterByMac(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, FilterMacAddressActivity.class);
        startActivity(intent);
    }

    public void onFilterByName(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, FilterAdvNameActivity.class);
        startActivity(intent);
    }

    public void onFilterByRawData(View view) {
        if (isWindowLocked())
            return;
        Intent intent = new Intent(this, FilterRawDataSwitchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        int rssi = progress - 127;
        mBind.tvRssiFilterValue.setText(String.format("%ddBm", rssi));
        mBind.tvRssiFilterTips.setText(getString(R.string.rssi_filter, rssi));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
