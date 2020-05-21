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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import androidx.preference.*;
import android.provider.Settings;
import com.android.internal.util.zenx.Utils;

import com.android.internal.logging.nano.MetricsProto; 
import com.android.settings.SettingsPreferenceFragment;

import com.zenx.zen.hub.R;
import com.zenx.support.preferences.SystemSettingSeekBarPreference;
import com.zenx.support.preferences.CustomSeekBarPreference;
import com.zenx.support.colorpicker.ColorPickerPreference;

public class QuickSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String QS_PANEL_ALPHA = "qs_panel_alpha";
    private static final String QS_HEADER_CLOCK_SIZE  = "qs_header_clock_size";
    static final int DEFAULT_STATUS_CLOCK_COLOR = 0xFFFFFFFF;
    private static final String QS_HEADER_CLOCK_FONT_STYLE  = "qs_header_clock_font_style";
    private static final String QS_HEADER_CLOCK_COLOR = "qs_header_clock_color";

    private SystemSettingSeekBarPreference mQsPanelAlpha;
    private SystemSettingSeekBarPreference mSysuiQqsCount;
    private CustomSeekBarPreference mQsClockSize;
    private ColorPickerPreference mClockColor;
    private ListPreference mClockFontStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.zen_hub_quicksettings);

        mQsPanelAlpha = (SystemSettingSeekBarPreference) findPreference(QS_PANEL_ALPHA);
        int qsPanelAlpha = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.QS_PANEL_BG_ALPHA, 255, UserHandle.USER_CURRENT);
        mQsPanelAlpha.setValue(qsPanelAlpha);
        mQsPanelAlpha.setOnPreferenceChangeListener(this);

        ContentResolver resolver = getActivity().getContentResolver();

        int value = Settings.Secure.getInt(resolver, Settings.Secure.QQS_COUNT, 6);
        mSysuiQqsCount = (SystemSettingSeekBarPreference) findPreference("sysui_qqs_count");
        mSysuiQqsCount.setValue(value);
        mSysuiQqsCount.setOnPreferenceChangeListener(this);

        mQsClockSize = (CustomSeekBarPreference) findPreference(QS_HEADER_CLOCK_SIZE);
        int qsClockSize = Settings.System.getInt(resolver,
                Settings.System.QS_HEADER_CLOCK_SIZE, 14);
                mQsClockSize.setValue(qsClockSize / 1);
        mQsClockSize.setOnPreferenceChangeListener(this);

        int intColor;
        String hexColor;

        mClockFontStyle = (ListPreference) findPreference(QS_HEADER_CLOCK_FONT_STYLE);
        int showClockFont = Settings.System.getInt(resolver,
                Settings.System.QS_HEADER_CLOCK_FONT_STYLE, 14);
        mClockFontStyle.setValue(String.valueOf(showClockFont));
        mClockFontStyle.setOnPreferenceChangeListener(this);

        mClockColor = (ColorPickerPreference) findPreference(QS_HEADER_CLOCK_COLOR);
        mClockColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                Settings.System.QS_HEADER_CLOCK_COLOR, DEFAULT_STATUS_CLOCK_COLOR);
        hexColor = String.format("#%08x", (0xFFFFFFFF & intColor));
        if (hexColor.equals("#ffffffff")) {
            mClockColor.setSummary(R.string.default_string);
        } else {
            mClockColor.setSummary(hexColor);
        }
        mClockColor.setNewPreviewColor(intColor);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQsPanelAlpha) {
            int bgAlpha = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_PANEL_BG_ALPHA, bgAlpha, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mSysuiQqsCount) {
            int val = (Integer) newValue;
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.QQS_COUNT, val, UserHandle.USER_CURRENT);
            return true;
        }  else if (preference == mQsClockSize) {
                int width = ((Integer)newValue).intValue();
                Settings.System.putInt(getContentResolver(),
                        Settings.System.QS_HEADER_CLOCK_SIZE, width);
                return true;
        } else if (preference == mClockColor) {
                String hex = ColorPickerPreference.convertToARGB(
                        Integer.valueOf(String.valueOf(newValue)));
                if (hex.equals("#ffffffff")) {
                    preference.setSummary(R.string.default_string);
                } else {
                    preference.setSummary(hex);
                }
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.QS_HEADER_CLOCK_COLOR, intHex);
                return true;
        } else if (preference == mClockFontStyle) {
                int showClockFont = Integer.valueOf((String) newValue);
                int index = mClockFontStyle.findIndexOfValue((String) newValue);
                Settings.System.putInt(getContentResolver(), Settings.System.
                    QS_HEADER_CLOCK_FONT_STYLE, showClockFont);
                mClockFontStyle.setSummary(mClockFontStyle.getEntries()[index]);
                return true;
            }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ZENX_SETTINGS;
    }
}
