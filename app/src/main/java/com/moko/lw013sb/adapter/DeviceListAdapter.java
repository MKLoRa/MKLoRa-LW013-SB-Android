package com.moko.lw013sb.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw013sb.R;
import com.moko.lw013sb.entity.AdvInfo;

import java.util.Locale;

import androidx.core.content.ContextCompat;

public class DeviceListAdapter extends BaseQuickAdapter<AdvInfo, BaseViewHolder> {
    public DeviceListAdapter() {
        super(R.layout.lw013_list_item_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, AdvInfo item) {
        final String rssi = String.format(Locale.getDefault(), "%ddBm", item.rssi);
        helper.setText(R.id.tv_rssi, rssi);
        final String name = TextUtils.isEmpty(item.name) ? "N/A" : item.name;
        helper.setText(R.id.tv_name, name);
        helper.setText(R.id.tv_mac, String.format("MAC:%s", item.mac));

        final String intervalTime = item.intervalTime == 0 ? "<->N/A" : String.format(Locale.getDefault(), "<->%dms", item.intervalTime);
        helper.setText(R.id.tv_track_interval, intervalTime);
        helper.setText(R.id.tv_battery, item.lowPowerState == 0 ? "Full" : "Low");
        helper.setImageResource(R.id.iv_battery, item.lowPowerState == 0 ? R.drawable.ic_battery : R.drawable.lw013_low_battery);
        helper.setText(R.id.tv_tx_power, item.txPower == Integer.MAX_VALUE ? "Tx Power:N/AdBm" : String.format(Locale.getDefault(), "Tx Power:%ddBm", item.txPower));
        helper.setText(R.id.tv_battery_power, item.batteryPower < 0 ? "N/AV" : String.format(Locale.getDefault(), "%sV", MokoUtils.getDecimalFormat("0.###").format(item.batteryPower * 0.001f)));
        helper.setVisible(R.id.tv_connect, item.connectable);
        helper.addOnClickListener(R.id.tv_connect);
    }
}
