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

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.*;
import android.provider.Settings;
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

public class StatusBar extends SettingsPreferenceFragment implements
    Preference.OnPreferenceChangeListener {

    private static final String CLOCK_DATE_AUTO_HIDE_HDUR = "status_bar_clock_auto_hide_hduration";
    private static final String CLOCK_DATE_AUTO_HIDE_SDUR = "status_bar_clock_auto_hide_sduration";

    private CustomSeekBarPreference mHideDuration;
    private CustomSeekBarPreference mShowDuration;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.zen_hub_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mHideDuration = (CustomSeekBarPreference) findPreference(CLOCK_DATE_AUTO_HIDE_HDUR);
        int hideVal = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_CLOCK_AUTO_HIDE_HDURATION, 60, UserHandle.USER_CURRENT);
        mHideDuration.setValue(hideVal);
        mHideDuration.setOnPreferenceChangeListener(this);

        mShowDuration = (CustomSeekBarPreference) findPreference(CLOCK_DATE_AUTO_HIDE_SDUR);
        int showVal = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_CLOCK_AUTO_HIDE_SDURATION, 5, UserHandle.USER_CURRENT);
        mShowDuration.setValue(showVal);
        mShowDuration.setOnPreferenceChangeListener(this);
        
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mHideDuration) {
            int value = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.STATUS_BAR_CLOCK_AUTO_HIDE_HDURATION, value, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mShowDuration) {
            int value = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.STATUS_BAR_CLOCK_AUTO_HIDE_SDURATION, value, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ZENX_SETTINGS;
    }

}
