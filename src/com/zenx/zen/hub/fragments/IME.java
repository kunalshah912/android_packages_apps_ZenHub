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

import android.app.AlertDialog; 
import android.app.Dialog; 
import android.content.Context; 
import android.content.DialogInterface; 
import android.content.Intent; 
import android.content.pm.PackageManager; 
import android.content.pm.ResolveInfo; 
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle; 
import android.app.DialogFragment; 
import android.os.Handler; 
import android.os.UserHandle;
import androidx.preference.*;
 
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;
import android.provider.Settings; 
import android.view.Gravity; 
import android.view.LayoutInflater; 
import android.view.View; 
import android.view.ViewGroup; 
import android.widget.AdapterView; 
import android.widget.BaseAdapter; 
import android.widget.ImageView; 
import android.widget.ListView; 
import android.widget.RadioButton; 
import android.widget.TextView; 
import android.widget.Toast; 
 
import java.util.ArrayList; 
import java.util.Collections; 
import java.util.Comparator; 
import java.util.HashMap; 
import java.util.Map; 
 
import com.android.internal.logging.nano.MetricsProto;

public class IME extends SettingsPreferenceFragment 
        implements Preference.OnPreferenceChangeListener { 
    
    public static final String TAG = "IME";

    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        addPreferencesFromResource(R.xml.zen_hub_ime);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return false;
    } 
 
    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ZENX_SETTINGS;
    }
 
} 
