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

import com.zenx.zen.hub.preferences.Utils;
import com.zenx.support.preferences.CustomSeekBarPreference;
import com.zenx.support.preferences.SystemSettingListPreference;
import com.zenx.support.preferences.SystemSettingMasterSwitchPreference;
import com.zenx.support.preferences.SystemSettingSwitchPreference;
import androidx.preference.ListPreference;

public class StatusBar extends SettingsPreferenceFragment implements
    Preference.OnPreferenceChangeListener {

    private static final String CLOCK_DATE_AUTO_HIDE_HDUR = "status_bar_clock_auto_hide_hduration";
    private static final String CLOCK_DATE_AUTO_HIDE_SDUR = "status_bar_clock_auto_hide_sduration";
    private static final String KEY_CARRIER_LABEL = "status_bar_show_carrier";
    private static final String STATUS_BAR_CLOCK = "status_bar_clock";
    private static final String STATUS_BAR_LOGO = "status_bar_logo";
    private static final String SHOW_HD_ICON = "show_hd_icon";
    private static final String KEY_USE_OLD_MOBILETYPE = "use_old_mobiletype";
    private static final String KEY_NETWORK_TRAFFIC = "network_traffic_location";
    private static final String KEY_NETWORK_TRAFFIC_ARROW = "network_traffic_arrow";
    private static final String KEY_NETWORK_TRAFFIC_AUTOHIDE = "network_traffic_autohide_threshold";
    private static final String STATUSBAR_BATTERY_BAR = "statusbar_battery_bar";

    private CustomSeekBarPreference mHideDuration;
    private CustomSeekBarPreference mShowDuration;
    private SystemSettingListPreference mShowCarrierLabel;
    private SystemSettingMasterSwitchPreference mStatusBarClockShow;
    private SystemSettingMasterSwitchPreference mStatusBarLogo;
    private SwitchPreference mShowHDVolte;
    private boolean mConfigShowHDVolteIcon;
    private SwitchPreference mUseOldMobileType;
    private boolean mConfigUseOldMobileType;
    private ListPreference mNetworkTraffic;
    private SystemSettingSwitchPreference mNetworkTrafficArrow;
    private SystemSettingSeekBarPreference mNetworkTrafficAutohide;
    private SystemSettingMasterSwitchPreference mStatusbarBatteryBar;
    private ListPreference mBatteryStyle;
    private ListPreference mBatteryPercent;
    private int mBatteryPercentValue;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.zen_hub_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mShowCarrierLabel = (SystemSettingListPreference) findPreference(KEY_CARRIER_LABEL);
        int showCarrierLabel = Settings.System.getInt(resolver,
        Settings.System.STATUS_BAR_SHOW_CARRIER, 1);
        CharSequence[] NonNotchEntriesCarrier = { getResources().getString(R.string.show_carrier_disabled),
                getResources().getString(R.string.show_carrier_keyguard),
                getResources().getString(R.string.show_carrier_statusbar), getResources().getString(
                        R.string.show_carrier_enabled) };
        CharSequence[] NotchEntriesCarrier = { getResources().getString(R.string.show_carrier_disabled),
                getResources().getString(R.string.show_carrier_keyguard) };
        CharSequence[] NonNotchValuesCarrier = {"0", "1" , "2", "3"};
        CharSequence[] NotchValuesCarrier = {"0", "1"};
        mShowCarrierLabel.setEntries(Utils.hasNotch(getActivity()) ? NotchEntriesCarrier : NonNotchEntriesCarrier);
        mShowCarrierLabel.setEntryValues(Utils.hasNotch(getActivity()) ? NotchValuesCarrier : NonNotchValuesCarrier);
        mShowCarrierLabel.setValue(String.valueOf(showCarrierLabel));
        mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntry());
        mShowCarrierLabel.setOnPreferenceChangeListener(this);

        mNetworkTraffic = (ListPreference) findPreference(KEY_NETWORK_TRAFFIC);
        int networkTraffic = Settings.System.getInt(resolver,
        Settings.System.NETWORK_TRAFFIC_LOCATION, 2);
        CharSequence[] NonNotchEntries = { getResources().getString(R.string.network_traffic_disabled),
                getResources().getString(R.string.network_traffic_statusbar),
                getResources().getString(R.string.network_traffic_qs_header),
                getResources().getString(R.string.network_traffic_qs_footer) };
        CharSequence[] NotchEntries = { getResources().getString(R.string.network_traffic_disabled),
                getResources().getString(R.string.network_traffic_qs_header),
                getResources().getString(R.string.network_traffic_qs_footer) };
        CharSequence[] NonNotchValues = {"0", "1" , "2", "3"};
        CharSequence[] NotchValues = {"0", "2", "3"};
        mNetworkTraffic.setEntries(Utils.hasNotch(getActivity()) ? NotchEntries : NonNotchEntries);
        mNetworkTraffic.setEntryValues(Utils.hasNotch(getActivity()) ? NotchValues : NonNotchValues);
        mNetworkTraffic.setValue(String.valueOf(networkTraffic));
        mNetworkTraffic.setSummary(mNetworkTraffic.getEntry());
        mNetworkTraffic.setOnPreferenceChangeListener(this);

        mNetworkTrafficArrow = (SystemSettingSwitchPreference) findPreference(KEY_NETWORK_TRAFFIC_ARROW);
        mNetworkTrafficAutohide = (SystemSettingSeekBarPreference) findPreference(KEY_NETWORK_TRAFFIC_AUTOHIDE);
        updateNetworkTrafficPrefs(networkTraffic);

        mStatusBarClockShow = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_CLOCK);
        mStatusBarClockShow.setChecked((Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CLOCK, 1) == 1));
        mStatusBarClockShow.setOnPreferenceChangeListener(this);

        mStatusBarLogo = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_LOGO);
        mStatusBarLogo.setChecked((Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_LOGO, 0) == 1));
        mStatusBarLogo.setOnPreferenceChangeListener(this);

        mStatusbarBatteryBar = (SystemSettingMasterSwitchPreference) findPreference(STATUSBAR_BATTERY_BAR);
        mStatusbarBatteryBar.setChecked((Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR, 0) == 1));
                mStatusbarBatteryBar.setOnPreferenceChangeListener(this);

        mConfigShowHDVolteIcon = getResources().getBoolean(com.android.internal.R.bool.config_display_hd_volte);
        int useHDIcon = (mConfigShowHDVolteIcon ? 1 : 0);
        mShowHDVolte = (SwitchPreference) findPreference(SHOW_HD_ICON);
        mShowHDVolte.setChecked((Settings.System.getInt(resolver,
                Settings.System.SHOW_HD_ICON, useHDIcon) == 1));
        mShowHDVolte.setOnPreferenceChangeListener(this);

        mBatteryStyle = (ListPreference) findPreference("status_bar_battery_style");
        int batterystyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, 0, UserHandle.USER_CURRENT);
        mBatteryStyle.setValue(String.valueOf(batterystyle));
        mBatteryStyle.setSummary(mBatteryStyle.getEntry());
        mBatteryStyle.setOnPreferenceChangeListener(this);

        mBatteryPercent = (ListPreference) findPreference("status_bar_show_battery_percent");
        int batteryPercent = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0, UserHandle.USER_CURRENT);
        mBatteryPercent.setValue(String.valueOf(batteryPercent));
        mBatteryPercent.setSummary(mBatteryPercent.getEntry());
        mBatteryPercent.setOnPreferenceChangeListener(this);
        mBatteryPercent.setEnabled(batterystyle != 4 && batterystyle != 5);

        mConfigUseOldMobileType = getResources().getBoolean(com.android.internal.R.bool.config_useOldMobileIcons);
        int useOldMobileIcons = (!mConfigUseOldMobileType ? 0 : 1);
        mUseOldMobileType = (SwitchPreference) findPreference(KEY_USE_OLD_MOBILETYPE);
        mUseOldMobileType.setChecked((Settings.System.getInt(resolver,
                Settings.System.USE_OLD_MOBILETYPE, useOldMobileIcons) == 1));
        mUseOldMobileType.setOnPreferenceChangeListener(this);
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
		} else if (preference == mStatusBarLogo) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_LOGO, value ? 1 : 0);
            return true;
        } else if (preference == mShowHDVolte) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SHOW_HD_ICON, value ? 1 : 0);
            return true;
        } else if (preference == mUseOldMobileType) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.USE_OLD_MOBILETYPE, value ? 1 : 0);
            return true;
        } else if (preference == mNetworkTraffic) {
            int networkTraffic = Integer.valueOf((String) newValue);
            int index = mNetworkTraffic.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_LOCATION, networkTraffic);
            mNetworkTraffic.setSummary(mNetworkTraffic.getEntries()[index]);
            updateNetworkTrafficPrefs(networkTraffic);
            return true;
        } else if (preference == mStatusbarBatteryBar) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR, value ? 1 : 0);
            return true;
    	} else if (preference == mBatteryStyle) {
            int batterystyle = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_STYLE, batterystyle,
                UserHandle.USER_CURRENT);
            int index = mBatteryStyle.findIndexOfValue((String) newValue);
            mBatteryStyle.setSummary(mBatteryStyle.getEntries()[index]);
            mBatteryPercent.setEnabled(batterystyle != 4 && batterystyle != 5);
            return true;
        } else if (preference == mBatteryPercent) {
            int batteryPercent = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, batteryPercent,
                    UserHandle.USER_CURRENT);
            int index = mBatteryPercent.findIndexOfValue((String) newValue);
            mBatteryPercent.setSummary(mBatteryPercent.getEntries()[index]);
            return true;
		}
        return false;
    }

    private void updateNetworkTrafficPrefs(int networkTraffic) {
        if (mNetworkTraffic != null) {
            if (networkTraffic == 0) {
                mNetworkTrafficArrow.setEnabled(false);
                mNetworkTrafficAutohide.setEnabled(false);
            } else {
                mNetworkTrafficArrow.setEnabled(true);
                mNetworkTrafficAutohide.setEnabled(true);
            }
        }
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
