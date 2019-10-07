/*
 * Copyright (C) 2013 Android Open Kang Project
 *           (C) 2017 faust93 at monumentum@gmail.com
 *           (C) 2020 ZenxOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zenx.zen.hub.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.view.View;

import android.content.Intent;
import android.util.Log;
import android.net.ConnectivityManager;

import com.zenx.zen.hub.R;
import com.zenx.support.preferences.SystemSettingSeekBarPreference;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

public class ScreenStateToggles extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "ScreenStateToggles";
    private static final String SCREEN_STATE_TOGGLES_ENABLE = "screen_state_toggles_enable_key";
    private static final String SCREEN_STATE_TOGGLES_TWOG = "screen_state_toggles_twog";
    private static final String SCREEN_STATE_TOGGLES_GPS = "screen_state_toggles_gps";
    private static final String SCREEN_STATE_TOGGLES_MOBILE_DATA = "screen_state_toggles_mobile_data";
    private static final String SCREEN_STATE_ON_DELAY = "screen_state_on_delay";
    private static final String SCREEN_STATE_OFF_DELAY = "screen_state_off_delay";
    private static final String SCREEN_STATE_CATGEGORY_LOCATION = "screen_state_toggles_location_key";
    private static final String SCREEN_STATE_CATGEGORY_MOBILE_DATA = "screen_state_toggles_mobile_key";

    private Context mContext;

    private SwitchPreference mEnableScreenStateToggles;
    private SwitchPreference mEnableScreenStateTogglesTwoG;
    private SwitchPreference mEnableScreenStateTogglesGps;
    private SwitchPreference mEnableScreenStateTogglesMobileData;
    private SystemSettingSeekBarPreference mSecondsOffDelay;
    private SystemSettingSeekBarPreference mSecondsOnDelay;
    private PreferenceCategory mMobileDateCategory;
    private PreferenceCategory mLocationCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = (Context) getActivity();

        addPreferencesFromResource(R.xml.screen_state_toggles);
        ContentResolver resolver = getActivity().getContentResolver();

        mEnableScreenStateToggles = (SwitchPreference) findPreference(
                SCREEN_STATE_TOGGLES_ENABLE);

        int enabled = Settings.Secure.getIntForUser(resolver,
                Settings.Secure.START_SCREEN_STATE_SERVICE, 0, UserHandle.USER_CURRENT);

        mEnableScreenStateToggles.setChecked(enabled != 0);
        mEnableScreenStateToggles.setOnPreferenceChangeListener(this);

        mSecondsOffDelay = (SystemSettingSeekBarPreference) findPreference(SCREEN_STATE_OFF_DELAY);
        int offd = Settings.System.getIntForUser(resolver,
                Settings.System.SCREEN_STATE_OFF_DELAY, 0, UserHandle.USER_CURRENT);
        mSecondsOffDelay.setValue(offd);
        mSecondsOffDelay.setOnPreferenceChangeListener(this);

        mSecondsOnDelay = (SystemSettingSeekBarPreference) findPreference(SCREEN_STATE_ON_DELAY);
        int ond = Settings.System.getIntForUser(resolver,
                Settings.System.SCREEN_STATE_ON_DELAY, 0, UserHandle.USER_CURRENT);
        mSecondsOnDelay.setValue(ond);
        mSecondsOnDelay.setOnPreferenceChangeListener(this);

        mMobileDateCategory = (PreferenceCategory) findPreference(
                SCREEN_STATE_CATGEGORY_MOBILE_DATA);
        mLocationCategory = (PreferenceCategory) findPreference(
                SCREEN_STATE_CATGEGORY_LOCATION);

        mEnableScreenStateTogglesTwoG = (SwitchPreference) findPreference(
                SCREEN_STATE_TOGGLES_TWOG);

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (!cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE)){
            getPreferenceScreen().removePreference(mEnableScreenStateTogglesTwoG);
        } else {
            mEnableScreenStateTogglesTwoG.setChecked((
                Settings.System.getIntForUser(resolver,
                Settings.System.SCREEN_STATE_TWOG, 0, UserHandle.USER_CURRENT) == 1));
            mEnableScreenStateTogglesTwoG.setOnPreferenceChangeListener(this);
        }

        mEnableScreenStateTogglesMobileData = (SwitchPreference) findPreference(
                SCREEN_STATE_TOGGLES_MOBILE_DATA);

        if (!cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE)){
            getPreferenceScreen().removePreference(mEnableScreenStateTogglesMobileData);
        } else {
            mEnableScreenStateTogglesMobileData.setChecked((
                Settings.System.getIntForUser(resolver,
                Settings.System.SCREEN_STATE_MOBILE_DATA, 0, UserHandle.USER_CURRENT) == 1));
            mEnableScreenStateTogglesMobileData.setOnPreferenceChangeListener(this);
        }

        // Only enable these controls if this user is allowed to change location
        // sharing settings.
        final UserManager um = (UserManager) getActivity().getSystemService(Context.USER_SERVICE);
        boolean isLocationChangeAllowed = !um.hasUserRestriction(UserManager.DISALLOW_SHARE_LOCATION);

        // TODO: check if gps is available on this device?
        mEnableScreenStateTogglesGps = (SwitchPreference) findPreference(
                SCREEN_STATE_TOGGLES_GPS);

        if (!isLocationChangeAllowed){
            getPreferenceScreen().removePreference(mEnableScreenStateTogglesGps);
            mEnableScreenStateTogglesGps = null;
        } else {
            mEnableScreenStateTogglesGps.setChecked((
                Settings.System.getIntForUser(resolver,
                Settings.System.SCREEN_STATE_GPS, 0, UserHandle.USER_CURRENT) == 1));
            mEnableScreenStateTogglesGps.setOnPreferenceChangeListener(this);
        }

        mMobileDateCategory.setEnabled(enabled != 0);
        mLocationCategory.setEnabled(enabled != 0);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mEnableScreenStateToggles) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putIntForUser(resolver,
                    Settings.Secure.START_SCREEN_STATE_SERVICE, value ? 1 : 0, UserHandle.USER_CURRENT);

            Intent service = (new Intent())
                .setClassName("com.android.systemui", "com.android.systemui.zenx.screenstate.ScreenStateService");
            if (value) {
                getActivity().stopService(service);
                getActivity().startService(service);
            } else {
                getActivity().stopService(service);
            }

            mMobileDateCategory.setEnabled(value);
            mLocationCategory.setEnabled(value);
            return true;
        } else if (preference == mEnableScreenStateTogglesTwoG) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.SCREEN_STATE_TWOG, value ? 1 : 0, UserHandle.USER_CURRENT);

            Intent intent = new Intent("android.intent.action.SCREEN_STATE_SERVICE_UPDATE");
            mContext.sendBroadcast(intent);
            return true;
        } else if (preference == mEnableScreenStateTogglesGps) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.SCREEN_STATE_GPS, value ? 1 : 0, UserHandle.USER_CURRENT);

            Intent intent = new Intent("android.intent.action.SCREEN_STATE_SERVICE_UPDATE");
            mContext.sendBroadcast(intent);
            return true;
        } else if (preference == mEnableScreenStateTogglesMobileData) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.SCREEN_STATE_MOBILE_DATA, value ? 1 : 0, UserHandle.USER_CURRENT);

            Intent intent = new Intent("android.intent.action.SCREEN_STATE_SERVICE_UPDATE");
            mContext.sendBroadcast(intent);
            return true;
        } else if (preference == mSecondsOffDelay) {
            int delay = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.SCREEN_STATE_OFF_DELAY, delay, UserHandle.USER_CURRENT);

            return true;
        } else if (preference == mSecondsOnDelay) {
            int delay = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.SCREEN_STATE_ON_DELAY, delay, UserHandle.USER_CURRENT);
            return true;
        }

        return false;
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.Secure.putIntForUser(resolver,
                Settings.Secure.START_SCREEN_STATE_SERVICE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.SCREEN_STATE_OFF_DELAY, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.SCREEN_STATE_ON_DELAY, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.SCREEN_STATE_TWOG, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.SCREEN_STATE_MOBILE_DATA, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.SCREEN_STATE_GPS, 0, UserHandle.USER_CURRENT);
    }

    private void restartService(){
        Intent service = (new Intent())
                .setClassName("com.android.systemui", "com.android.systemui.zenx.screenstate.ScreenStateService");
        getActivity().stopService(service);
        getActivity().startService(service);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ZENX_SETTINGS;
    }
}
