/*
 * TeamWork
 * Copyright (C) 2015  FattycatR
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program;  if not, see <http://www.gnu.org/licenses/>.
 */
package me.fattycat.kun.teamwork.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    public static Context sContext;

    private ToastUtils() {
    }

    public static void register(Context context) {
        sContext = context.getApplicationContext();
    }

    private static void check() {
        if (sContext == null) {
            throw new NullPointerException("Must register ToastUtils in your application class");
        }
    }

    public static void showShort(int resId) {
        check();
        Toast.makeText(sContext, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(String message) {
        check();
        Toast.makeText(sContext, message, Toast.LENGTH_SHORT).show();
    }


    public static void showLong(int resId) {
        check();
        Toast.makeText(sContext, resId, Toast.LENGTH_LONG).show();
    }


    public static void showLong(String message) {
        check();
        Toast.makeText(sContext, message, Toast.LENGTH_LONG).show();
    }
}
