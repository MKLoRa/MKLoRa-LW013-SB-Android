package com.moko.lw013sb.activity.filter;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw013sb.R;
import com.moko.lw013sb.activity.BaseActivity;
import com.moko.lw013sb.databinding.Lw013ActivityFilterMkTofBinding;
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

public class FilterMKTOFActivity extends BaseActivity {
    private Lw013ActivityFilterMkTofBinding mBind;

    private int mkTofEnableFlag;
    private ArrayList<String> filterMfgCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013ActivityFilterMkTofBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        filterMfgCode = new ArrayList<>();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>(8);
        orderTasks.add(OrderTaskAssembler.getMkTofEnable());
        orderTasks.add(OrderTaskAssembler.getMkTofMfgCode());
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 400)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 400)
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
                byte[] value = response.responseValue;
                if (orderCHAR == OrderCHAR.CHAR_PARAMS) {
                    if (value.length >= 5) {
                        int header = value[0] & 0xFF;// 0xED
                        int flag = value[1] & 0xFF;// read or write
                        int cmd = MokoUtils.toInt(Arrays.copyOfRange(value, 2, 4));
                        if (header != 0xED) return;
                        ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
                        if (configKeyEnum == null) return;
                        int length = value[4] & 0xFF;
                        if (flag == 0x01) {
                            // write
                            int result = value[5] & 0xFF;
                            switch (configKeyEnum) {
                                case KEY_FILTER_MK_TOF_ENABLE:
                                    mkTofEnableFlag = result;
                                    break;
                                case KEY_FILTER_MK_TOF_MFG_CODE:
                                    if (mkTofEnableFlag == 1 && result == 1) {
                                        ToastUtils.showToast(this, "Save Successfully！");
                                    } else {
                                        ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
                                    }
                                    break;
                            }
                        }
                        if (flag == 0x00) {
                            // read
                            switch (configKeyEnum) {
                                case KEY_FILTER_MK_TOF_ENABLE:
                                    if (length == 1) {
                                        int enable = value[5] & 0xFF;
                                        mBind.cbMkTof.setChecked(enable == 1);
                                    }
                                    break;
                                case KEY_FILTER_MK_TOF_MFG_CODE:
                                    if (length > 0) {
                                        filterMfgCode.clear();
                                        byte[] mfgCodeBytes = Arrays.copyOfRange(value, 5, 5 + length);
                                        for (int i = 0, l = mfgCodeBytes.length; i < l; ) {
                                            int idLength = mfgCodeBytes[i] & 0xFF;
                                            i++;
                                            filterMfgCode.add(MokoUtils.bytesToHexString(Arrays.copyOfRange(mfgCodeBytes, i, i + idLength)));
                                            i += idLength;
                                        }
                                        for (int i = 0, l = filterMfgCode.size(); i < l; i++) {
                                            String macAddress = filterMfgCode.get(i);
                                            View v = LayoutInflater.from(this).inflate(R.layout.lw013_item_tof_filter, mBind.llMfgCode, false);
                                            TextView title = v.findViewById(R.id.tv_mfg_code_title);
                                            EditText etMacAddress = v.findViewById(R.id.et_mfg_code);
                                            title.setText(String.format("Code %d", i + 1));
                                            etMacAddress.setText(macAddress);
                                            mBind.llMfgCode.addView(v);
                                        }
                                    }
                                    break;

                            }
                        }
                    }
                }
            }
        });
    }

    public void onSave(View view) {
        if (isWindowLocked()) return;
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Para error!");
        }
    }

    public void onAdd(View view) {
        if (isWindowLocked())
            return;
        int count = mBind.llMfgCode.getChildCount();
        if (count > 9) {
            ToastUtils.showToast(this, "You can set up to 10 filters!");
            return;
        }
        View v = LayoutInflater.from(this).inflate(R.layout.lw013_item_tof_filter, mBind.llMfgCode, false);
        TextView title = v.findViewById(R.id.tv_mfg_code_title);
        title.setText(String.format("Code %d", count + 1));
        mBind.llMfgCode.addView(v);
    }

    public void onDel(View view) {
        if (isWindowLocked())
            return;
        final int c = mBind.llMfgCode.getChildCount();
        if (c == 0) {
            ToastUtils.showToast(this, "There are currently no filters to delete");
            return;
        }
        int count = mBind.llMfgCode.getChildCount();
        if (count > 0) {
            mBind.llMfgCode.removeViewAt(count - 1);
        }
    }


    private void saveParams() {
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setFilterMkTofEnable(mBind.cbMkTof.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setFilterMkTofRules(filterMfgCode));
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private boolean isValid() {
        final int c = mBind.llMfgCode.getChildCount();
        filterMfgCode.clear();
        if (c > 0) {
            for (int i = 0; i < c; i++) {
                View v = mBind.llMfgCode.getChildAt(i);
                EditText etTagId = v.findViewById(R.id.et_mfg_code);
                final String macAddress = etTagId.getText().toString();
                if (TextUtils.isEmpty(macAddress)) {
                    return false;
                }
                int length = macAddress.length();
                if (length % 2 != 0 || length > 12) {
                    return false;
                }
                filterMfgCode.add(macAddress);
            }
        }
        return true;
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
        setResult(RESULT_OK);
        finish();
    }
}
