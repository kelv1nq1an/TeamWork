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
package me.fattycat.kun.teamwork;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.fattycat.kun.teamwork.util.ToastUtils;

public class App extends Application {
    public static Context sContext;
    public static boolean sDebug;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;
        sDebug = true;
        ToastUtils.register(sContext);

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("Teamwork.realm")
                .schemaVersion(1)
                .build();

        Realm.setDefaultConfiguration(config);
    }

}
