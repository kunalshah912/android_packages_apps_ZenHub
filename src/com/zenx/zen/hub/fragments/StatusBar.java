/*
 * Copyright (C) 2018 Zenx-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zenx.zen.hub.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import androidx.preference.*;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.TextUtils;
import android.provider.Settings;
import android.widget.EditText;
import com.android.settings.R;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import android.util.Log;
import android.content.Context;
import android.net.ConnectivityManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.android.internal.util.zenx.Utils;
import com.zenx.support.preferences.CustomSeekBarPreference;
import com.zenx.support.preferences.SystemSettingListPreference;
import com.zenx.support.preferences.SystemSettingMasterSwitchPreference;
import androidx.preference.ListPreference;

public class StatusBar extends SettingsPreferenceFragment implements
    Preference.OnPreferenceChangeListener {

    private static final String CLOCK_DATE_AUTO_HIDE_HDUR = "status_bar_clock_auto_hide_hduration";
    private static final String CLOCK_DATE_AUTO_HIDE_SDUR = "status_bar_clock_auto_hide_sduration";
    private static final String KEY_CARRIER_LABEL = "status_bar_show_carrier";
    private static final String STATUS_BAR_CLOCK = "status_bar_clock";
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";

    private CustomSeekBarPreference mHideDuration;
    private CustomSeekBarPreference mShowDuration;
    private SystemSettingListPreference mShowCarrierLabel;
    private SystemSettingMasterSwitchPreference mNetMonitor;
    private SystemSettingMasterSwitchPreference mStatusBarClockShow;
    private SystemSettingListPreference mStatusbarBatteryStyles;
    private SystemSettingListPreference mStatusbarBatteryPercentage;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.zen_hub_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mShowCarrierLabel = (SystemSettingListPreference) findPreference(KEY_CARRIER_LABEL);
        int showCarrierLabel = Settings.System.getInt(resolver,
        Settings.System.STATUS_BAR_SHOW_CARRIER, 1);
        CharSequence[] NonNotchEntries = { getResources().getString(R.string.show_carrier_disabled),
                getResources().getString(R.string.show_carrier_keyguard),
                getResources().getString(R.string.show_carrier_statusbar), getResources().getString(
                        R.string.show_carrier_enabled) };
        CharSequence[] NotchEntries = { getResources().getString(R.string.show_carrier_disabled),
                getResources().getString(R.string.show_carrier_keyguard) };
        CharSequence[] NonNotchValues = {"0", "1" , "2", "3"};
        CharSequence[] NotchValues = {"0", "1"};
        mShowCarrierLabel.setEntries(Utils.hasNotch(getActivity()) ? NotchEntries : NonNotchEntries);
        mShowCarrierLabel.setEntryValues(Utils.hasNotch(getActivity()) ? NotchValues : NonNotchValues);
        mShowCarrierLabel.setValue(String.valueOf(showCarrierLabel));
        mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntry());
        mShowCarrierLabel.setOnPreferenceChangeListener(this);
        
        mStatusBarClockShow = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_CLOCK);
        mStatusBarClockShow.setChecked((Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CLOCK, 1) == 1));
        mStatusBarClockShow.setOnPreferenceChangeListener(this);

        boolean isNetMonitorEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_STATE, 1, UserHandle.USER_CURRENT) == 1;
        mNetMonitor = (SystemSettingMasterSwitchPreference) findPreference("network_traffic_state");
        mNetMonitor.setChecked(isNetMonitorEnabled);
        mNetMonitor.setOnPreferenceChangeListener(this);
        mStatusbarBatteryStyles = (SystemSettingListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        mStatusbarBatteryStyles.setValue(String.valueOf(Settings.System.getInt(
        getContentResolver(), Settings.System.STATUS_BAR_BATTERY_STYLE, 107)));
        mStatusbarBatteryStyles.setSummary(mStatusbarBatteryStyles.getEntry());
        mStatusbarBatteryStyles.setOnPreferenceChangeListener(this);

        mStatusbarBatteryPercentage = (SystemSettingListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);
        mStatusbarBatteryPercentage.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0)));
                mStatusbarBatteryPercentage.setSummary(mStatusbarBatteryPercentage.getEntry());
                mStatusbarBatteryPercentage.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mShowCarrierLabel) {
            int value = Integer.parseInt((String) newValue);
            updateCarrierLabelSummary(value);
            return true;
        } else if (preference == mStatusBarClockShow) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CLOCK, value ? 1 : 0);
            return true;
		} else if (preference == mNetMonitor) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_STATE, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            mNetMonitor.setChecked(value);
            return true;
        } else if (preference == mStatusbarBatteryStyles) {
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_BATTERY_STYLE,
                    Integer.valueOf((String) newValue));
                    mStatusbarBatteryStyles.setValue(String.valueOf(newValue));
                    mStatusbarBatteryStyles.setSummary(mStatusbarBatteryStyles.getEntry());
            return true;
        } else if (preference == mStatusbarBatteryPercentage) {
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT,
                    Integer.valueOf((String) newValue));
                    mStatusbarBatteryPercentage.setValue(String.valueOf(newValue));
                    mStatusbarBatteryPercentage.setSummary(mStatusbarBatteryPercentage.getEntry());
            return true;
        }
        return false;
    }

    private void updateCarrierLabelSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Carrier Label disabled
            mShowCarrierLabel.setSummary(res.getString(R.string.show_carrier_disabled));
        } else if (value == 1) {
            mShowCarrierLabel.setSummary(res.getString(R.string.show_carrier_keyguard));
        } else if (value == 2) {
            mShowCarrierLabel.setSummary(res.getString(R.string.show_carrier_statusbar));
        } else if (value == 3) {
            mShowCarrierLabel.setSummary(res.getString(R.string.show_carrier_enabled));
        }
    }
    
    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ZENX_SETTINGS;
    }

}
