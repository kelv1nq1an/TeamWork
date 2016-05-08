/*
 * TeamWork
 * Copyright (C) 2016  FattycatR
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
package me.fattycat.kun.teamwork.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.event.LogoutEvent;

public class SettingActivity extends BaseActivity {
    private static final String TAG = "TW_SettingActivity";
    @Bind(R.id.setting_toolbar)
    Toolbar mSettingToolbar;
    @Bind(R.id.setting_logout)
    Button mSettingLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        setSupportActionBar(mSettingToolbar);
        mSettingToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mSettingToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSettingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAuthorization();
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                EventBus.getDefault().post(new LogoutEvent());
                finish();
            }
        });
    }
}
