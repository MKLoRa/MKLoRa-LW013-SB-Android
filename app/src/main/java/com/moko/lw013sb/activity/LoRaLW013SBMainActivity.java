package com.moko.lw013sb.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.elvishew.xlog.XLog;
import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lib.loraui.dialog.AlertMessageDialog;
import com.moko.lib.loraui.dialog.LoadingMessageDialog;
import com.moko.lib.loraui.dialog.PasswordDialog;
import com.moko.lib.loraui.dialog.ScanFilterDialog;
import com.moko.lib.loraui.utils.ToastUtils;
import com.moko.lw013sb.AppConstants;
import com.moko.lw013sb.BuildConfig;
import com.moko.lw013sb.R;
import com.moko.lw013sb.activity.device.LogDataActivity;
import com.moko.lw013sb.adapter.DeviceListAdapter;
import com.moko.lw013sb.databinding.Lw013ActivityMainBinding;
import com.moko.lw013sb.entity.AdvInfo;
import com.moko.lw013sb.utils.AdvInfoAnalysisImpl;
import com.moko.lw013sb.utils.SPUtiles;
import com.moko.support.lw013sb.LoRaLW013SBMokoSupport;
import com.moko.support.lw013sb.MokoBleScanner;
import com.moko.support.lw013sb.OrderTaskAssembler;
import com.moko.support.lw013sb.callback.MokoScanDeviceCallback;
import com.moko.support.lw013sb.entity.DeviceInfo;
import com.moko.support.lw013sb.entity.OrderCHAR;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class LoRaLW013SBMainActivity extends BaseActivity implements MokoScanDeviceCallback, BaseQuickAdapter.OnItemChildClickListener {
    private Lw013ActivityMainBinding mBind;
    private boolean mReceiverTag = false;
    private ConcurrentHashMap<String, AdvInfo> beaconInfoHashMap;
    private ArrayList<AdvInfo> beaconInfos;
    private DeviceListAdapter adapter;
    private Animation animation = null;
    private MokoBleScanner mokoBleScanner;
    public Handler mHandler;
    private boolean isPasswordError;
    private boolean isVerifyEnable;

    public static String PATH_LOGCAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        // 初始化Xlog
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 优先保存到SD卡中
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                PATH_LOGCAT = getExternalFilesDir(null).getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "MKLoRa" : "LW013SB");
            } else {
                PATH_LOGCAT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "MKLoRa" : "LW013SB");
            }
        } else {
            // 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = getFilesDir().getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "MKLoRa" : "LW013SB");
        }
        LoRaLW013SBMokoSupport.getInstance().init(getApplicationContext());
        mSavedPassword = SPUtiles.getStringValue(this, AppConstants.SP_KEY_SAVED_PASSWORD_LW013_SB, "");
        beaconInfoHashMap = new ConcurrentHashMap<>();
        beaconInfos = new ArrayList<>();
        adapter = new DeviceListAdapter();
        adapter.replaceData(beaconInfos);
        adapter.setOnItemChildClickListener(this);
        adapter.openLoadAnimation();
        mBind.rvDevices.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_recycleview_divider));
        mBind.rvDevices.addItemDecoration(itemDecoration);
        mBind.rvDevices.setAdapter(adapter);
        mHandler = new Handler(Looper.getMainLooper());
        mokoBleScanner = new MokoBleScanner(this);
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!LoRaLW013SBMokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW013SBMokoSupport.getInstance().enableBluetooth();
        } else {
            if (animation == null) {
                startScan();
            }
        }
    }

    private void startScan() {
        if (!LoRaLW013SBMokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW013SBMokoSupport.getInstance().enableBluetooth();
            return;
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        mBind.ivRefresh.startAnimation(animation);
        advInfoAnalysis = new AdvInfoAnalysisImpl();
        mokoBleScanner.startScanDevice(this);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mokoBleScanner.stopScanDevice();
            }
        }, 1000 * 60);
    }


    private AdvInfoAnalysisImpl advInfoAnalysis;
    public String filterName;
    public int filterRssi = -127;

    @Override
    public void onStartScan() {
        beaconInfoHashMap.clear();
        new Thread(() -> {
            while (animation != null) {
                runOnUiThread(() -> {
                    adapter.replaceData(beaconInfos);
                    mBind.tvDeviceNum.setText(String.format("DEVICE(%d)", beaconInfos.size()));
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateDevices();
            }
        }).start();
    }

    @Override
    public void onScanDevice(DeviceInfo deviceInfo) {
        AdvInfo beaconInfo = advInfoAnalysis.parseDeviceInfo(deviceInfo);
        if (beaconInfo == null)
            return;
        beaconInfoHashMap.put(beaconInfo.mac, beaconInfo);
    }

    @Override
    public void onStopScan() {
        mBind.ivRefresh.clearAnimation();
        animation = null;
    }

    private void updateDevices() {
        beaconInfos.clear();
        if (!TextUtils.isEmpty(filterName) || filterRssi != -127) {
            ArrayList<AdvInfo> beaconInfosFilter = new ArrayList<>(beaconInfoHashMap.values());
            Iterator<AdvInfo> iterator = beaconInfosFilter.iterator();
            while (iterator.hasNext()) {
                AdvInfo beaconInfo = iterator.next();
                if (beaconInfo.rssi > filterRssi) {
                    if (TextUtils.isEmpty(filterName)) {
                        continue;
                    } else {
                        if (TextUtils.isEmpty(beaconInfo.name) && TextUtils.isEmpty(beaconInfo.mac)) {
                            iterator.remove();
                        } else if (TextUtils.isEmpty(beaconInfo.name) && beaconInfo.mac.toLowerCase().replaceAll(":", "").contains(filterName.toLowerCase())) {
                            continue;
                        } else if (TextUtils.isEmpty(beaconInfo.mac) && beaconInfo.name.toLowerCase().contains(filterName.toLowerCase())) {
                            continue;
                        } else if (!TextUtils.isEmpty(beaconInfo.name) && !TextUtils.isEmpty(beaconInfo.mac) && (beaconInfo.name.toLowerCase().contains(filterName.toLowerCase()) || beaconInfo.mac.toLowerCase().replaceAll(":", "").contains(filterName.toLowerCase()))) {
                            continue;
                        } else {
                            iterator.remove();
                        }
                    }
                } else {
                    iterator.remove();
                }
            }
            beaconInfos.addAll(beaconInfosFilter);
        } else {
            beaconInfos.addAll(beaconInfoHashMap.values());
        }
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Collections.sort(beaconInfos, (lhs, rhs) -> {
            if (lhs.rssi > rhs.rssi) {
                return -1;
            } else if (lhs.rssi < rhs.rssi) {
                return 1;
            }
            return 0;
        });
    }

    public void onRefresh(View view) {
        if (isWindowLocked())
            return;
        if (!LoRaLW013SBMokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW013SBMokoSupport.getInstance().enableBluetooth();
            return;
        }
        if (animation == null) {
            startScan();
        } else {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
    }

    public void onBack(View view) {
        back();
    }

    private void back() {
        if (animation != null) {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
        if (BuildConfig.IS_LIBRARY) {
            finish();
        } else {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setMessage(R.string.main_exit_tips);
            dialog.setOnAlertConfirmListener(() -> LoRaLW013SBMainActivity.this.finish());
            dialog.show(getSupportFragmentManager());
        }
    }

    public void onAbout(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, AboutActivity.class));
    }


    public void onFilter(View view) {
        if (isWindowLocked())
            return;
        if (animation != null) {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
        ScanFilterDialog scanFilterDialog = new ScanFilterDialog(this);
        scanFilterDialog.setFilterName(filterName);
        scanFilterDialog.setFilterRssi(filterRssi);
        scanFilterDialog.setOnScanFilterListener(new ScanFilterDialog.OnScanFilterListener() {
            @Override
            public void onDone(String filterName, int filterRssi) {
                LoRaLW013SBMainActivity.this.filterName = filterName;
                LoRaLW013SBMainActivity.this.filterRssi = filterRssi;
                if (!TextUtils.isEmpty(filterName) || filterRssi != -127) {
                    mBind.rlFilter.setVisibility(View.VISIBLE);
                    mBind.rlEditFilter.setVisibility(View.GONE);
                    StringBuilder stringBuilder = new StringBuilder();
                    if (!TextUtils.isEmpty(filterName)) {
                        stringBuilder.append(filterName);
                        stringBuilder.append(";");
                    }
                    if (filterRssi != -127) {
                        stringBuilder.append(String.format("%sdBm", filterRssi + ""));
                        stringBuilder.append(";");
                    }
                    mBind.tvFilter.setText(stringBuilder.toString());
                } else {
                    mBind.rlFilter.setVisibility(View.GONE);
                    mBind.rlEditFilter.setVisibility(View.VISIBLE);
                }
                if (isWindowLocked())
                    return;
                if (animation == null) {
                    startScan();
                }
            }
        });
        scanFilterDialog.setOnDismissListener(dialog -> {
            if (isWindowLocked())
                return;
            if (animation == null) {
                startScan();
            }
        });
        scanFilterDialog.show();
    }

    public void onFilterDelete(View view) {
        if (animation != null) {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
        mBind.rlFilter.setVisibility(View.GONE);
        mBind.rlEditFilter.setVisibility(View.VISIBLE);
        filterName = "";
        filterRssi = -127;
        if (isWindowLocked())
            return;
        if (animation == null) {
            startScan();
        }
    }

    private String mPassword;
    private String mSavedPassword;
    private int mDeviceType;

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (!LoRaLW013SBMokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW013SBMokoSupport.getInstance().enableBluetooth();
            return;
        }
        AdvInfo advInfo = (AdvInfo) adapter.getItem(position);
        if (advInfo != null && advInfo.connectable && !isFinishing()) {
            if (animation != null) {
                mHandler.removeMessages(0);
                mokoBleScanner.stopScanDevice();
            }
            isVerifyEnable = advInfo.verifyEnable;
            mDeviceType = advInfo.deviceType;
            if (!isVerifyEnable) {
                showLoadingProgressDialog();
                mBind.ivRefresh.postDelayed(() -> LoRaLW013SBMokoSupport.getInstance().connDevice(advInfo.mac), 500);
                return;
            }
            // show password
            final PasswordDialog dialog = new PasswordDialog(LoRaLW013SBMainActivity.this);
            dialog.setData(mSavedPassword);
            dialog.setOnPasswordClicked(new PasswordDialog.PasswordClickListener() {
                @Override
                public void onEnsureClicked(String password) {
                    if (!LoRaLW013SBMokoSupport.getInstance().isBluetoothOpen()) {
                        LoRaLW013SBMokoSupport.getInstance().enableBluetooth();
                        return;
                    }
                    XLog.i(password);
                    mPassword = password;
                    if (animation != null) {
                        mHandler.removeMessages(0);
                        mokoBleScanner.stopScanDevice();
                    }
                    showLoadingProgressDialog();
                    mBind.ivRefresh.postDelayed(() -> LoRaLW013SBMokoSupport.getInstance().connDevice(advInfo.mac), 500);
                }

                @Override
                public void onDismiss() {

                }
            });
            dialog.show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(() -> dialog.showKeyboard());
                }
            }, 200);
        }
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
                            if (animation != null) {
                                mHandler.removeMessages(0);
                                mokoBleScanner.stopScanDevice();
                                onStopScan();
                            }
                            break;
                        case BluetoothAdapter.STATE_ON:
                            if (animation == null) {
                                startScan();
                            }
                            break;
                    }
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        String action = event.getAction();
        if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
            mPassword = "";
            dismissLoadingProgressDialog();
            dismissLoadingMessageDialog();
            if (isPasswordError) {
                isPasswordError = false;
            } else {
                ToastUtils.showToast(LoRaLW013SBMainActivity.this, "Connection Failed");
            }
            if (animation == null) {
                startScan();
            }
        }
        if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
            dismissLoadingProgressDialog();
            if (!isVerifyEnable) {
                XLog.i("Success");
                Intent i = new Intent(LoRaLW013SBMainActivity.this, DeviceInfoActivity.class);
                startActivityForResult(i, AppConstants.REQUEST_CODE_DEVICE_INFO);
                return;
            }
            showLoadingMessageDialog();
            mHandler.postDelayed(() -> {
                // open password notify and set password
                List<OrderTask> orderTasks = new ArrayList<>();
                orderTasks.add(OrderTaskAssembler.setPassword(mPassword));
                LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
            }, 500);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
        }
        if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
        }
        if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
            OrderTaskResponse response = event.getResponse();
            OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
            int responseType = response.responseType;
            byte[] value = response.responseValue;
            switch (orderCHAR) {
                case CHAR_PASSWORD:
                    dismissLoadingMessageDialog();
                    if (value.length == 6) {
                        int header = value[0] & 0xFF;// 0xED
                        int flag = value[1] & 0xFF;// read or write
                        int cmd = MokoUtils.toInt(Arrays.copyOfRange(value, 2, 4));
                        if (header != 0xED)
                            return;
                        int length = value[4] & 0xFF;
                        if (flag == 0x01 && cmd == 0x0001 && length == 0x01) {
                            int result = value[5] & 0xFF;
                            if (1 == result) {
                                mSavedPassword = mPassword;
                                SPUtiles.setStringValue(LoRaLW013SBMainActivity.this, AppConstants.SP_KEY_SAVED_PASSWORD_LW013_SB, mSavedPassword);
                                XLog.i("Success");
                                Intent i = new Intent(LoRaLW013SBMainActivity.this, DeviceInfoActivity.class);
                                startActivityForResult(i, AppConstants.REQUEST_CODE_DEVICE_INFO);
                            }
                            if (0 == result) {
                                isPasswordError = true;
                                ToastUtils.showToast(LoRaLW013SBMainActivity.this, "Password Error");
                                LoRaLW013SBMokoSupport.getInstance().disConnectBle();
                            }
                        }
                    }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_DEVICE_INFO) {
            if (resultCode == RESULT_OK) {
                if (animation == null) {
                    startScan();
                }
            }
        }
    }

    private LoadingMessageDialog mLoadingMessageDialog;

    private void showLoadingMessageDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Verifying..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    private void dismissLoadingMessageDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        XLog.i("onNewIntent...");
        setIntent(intent);
        if (getIntent().getExtras() != null) {
            String from = getIntent().getStringExtra(AppConstants.EXTRA_KEY_FROM_ACTIVITY);
            if (LogDataActivity.TAG.equals(from)) {
                if (animation == null) {
                    startScan();
                }
            }
        }
    }
}
