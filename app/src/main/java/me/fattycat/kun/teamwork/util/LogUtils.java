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

import android.util.Log;

import me.fattycat.kun.teamwork.App;

public class LogUtils {

    private LogUtils() {
    }

    public static void v(String tag, String msg) {
        if (App.sDebug) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (App.sDebug) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (App.sDebug) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (App.sDebug) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (App.sDebug) {
            Log.e(tag, msg);
        }
    }
}
