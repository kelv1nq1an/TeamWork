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
package me.fattycat.kun.teamwork.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWAccessToken;
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.TWRetrofit;
import me.fattycat.kun.teamwork.model.UserTeamListModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamFragment extends BaseFragment {
    private static final String TAG = "TW_TeamFragment";

    @Bind(R.id.fragment_team_spinner)
    Spinner mTeamSpinner;
    @Bind(R.id.fragment_team_avatar)
    CircleImageView mTeamAvatar;
    @Bind(R.id.fragment_team_member)
    RecyclerView mTeamMemberRecyclerView;

    private List<UserTeamListModel> mUserTeamList = new ArrayList<>();
    private ArrayAdapter<String> mUserTeamListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team, container, false);
        ButterKnife.bind(this, rootView);

        initTeamSelectSpinner();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getUserTeamList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void getUserTeamList() {
        TWApi.UserTeamListService userTeamListService = TWRetrofit.createService(TWApi.UserTeamListService.class, TWAccessToken.getAccessToken());
        Call<List<UserTeamListModel>> userTeamsListCall = userTeamListService.getUserTeams();

        userTeamsListCall.enqueue(new Callback<List<UserTeamListModel>>() {
            @Override
            public void onResponse(Call<List<UserTeamListModel>> call, Response<List<UserTeamListModel>> response) {
                if (response.body() != null) {
                    mUserTeamList = response.body();

                    updateSpinnerData();
                }
            }

            @Override
            public void onFailure(Call<List<UserTeamListModel>> call, Throwable t) {
                // FIXME: 16/3/22 user team list on failure
            }
        });
    }

    private void updateSpinnerData() {
        mUserTeamListAdapter.clear();
        for (UserTeamListModel model : mUserTeamList) {
            mUserTeamListAdapter.add(model.getName());
        }
        mUserTeamListAdapter.notifyDataSetChanged();
    }

    private void initTeamSelectSpinner() {
        mUserTeamListAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item);
        mUserTeamListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUserTeamListAdapter.add("正在加载团队列表");
        mTeamSpinner.setAdapter(mUserTeamListAdapter);

        mTeamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mUserTeamList.size() > 0) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
