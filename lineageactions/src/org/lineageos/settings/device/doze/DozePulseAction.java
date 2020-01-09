/*
 * Copyright (c) 2015 The CyanogenMod Project
 * Copyright (c) 2018 The LineageOS Project
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

package org.lineageos.settings.device.doze;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.lineageos.settings.device.SensorAction;

public class DozePulseAction implements SensorAction, ScreenStateNotifier {
    private static final String TAG = "LineageActions";
    private static boolean sCanDoze = true;
    private static final int DELAY_BEFORE_FIRST_DOZE_IN_MS = 3000;
    private static final int DELAY_BETWEEN_DOZES_IN_MS = 10000;
    private final Context mContext;

    private long mLastDoze;
    private long mLastScreenOff;

    public DozePulseAction(Context context) {
        mContext = context;
    }

    @Override
    public void screenTurnedOn() {
    }

    @Override
    public void screenTurnedOff() {
        mLastScreenOff = System.currentTimeMillis();
    }

    public void action() {
         if (mayDoze()) {
            Log.d(TAG, "Sending doze.pulse intent");
            mContext.sendBroadcast(new Intent("com.android.systemui.doze.pulse"));
        }
    }

    public static boolean getCanDoze() {
        return sCanDoze;
    }

    public static void setCanDoze(boolean canDoze) {
        sCanDoze = canDoze;
    }

    public synchronized boolean mayDoze() {
        if (!sCanDoze) {
            Log.d(TAG, "Denying doze (stowed)");
            return false;
        }

        long now = System.currentTimeMillis();
        if ((now - mLastScreenOff) < DELAY_BEFORE_FIRST_DOZE_IN_MS) {
            Log.d(TAG, "Denying doze due to DELAY_BEFORE_FIRST_DOZE_IN_MS");
            return false;
        } else if ((now - mLastDoze) < DELAY_BETWEEN_DOZES_IN_MS) {
            Log.d(TAG, "Denying doze due to DELAY_BETWEEN_DOZES");
            return false;
        }
        Log.d(TAG, "Allowing doze");
        mLastDoze = now;
        return true;
    }
}
