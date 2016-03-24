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

import java.util.List;

import me.fattycat.kun.teamwork.model.AccessTokenBody;
import me.fattycat.kun.teamwork.model.AccessTokenModel;
import me.fattycat.kun.teamwork.model.EntryModel;
import me.fattycat.kun.teamwork.model.ProjectModel;
import me.fattycat.kun.teamwork.model.TaskModel;
import me.fattycat.kun.teamwork.model.TeamProjectModel;
import me.fattycat.kun.teamwork.model.UserProfileModel;
import me.fattycat.kun.teamwork.model.UserTeamListModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class TWApi {
    public static final String BASE_URL_OAUTH = "https://open.worktile.com/oauth2/";
    public static final String BASE_URL_COMMON = "https://api.worktile.com/";
    public static final String BASE_URL_SCHEME = "v1/";

    public static String OAUTHURL = TWApi.BASE_URL_OAUTH
            + "authorize?client_id=" + TWSecret.CLIENT_ID
            + "&redirect_uri=" + TWSecret.REDIRECT_URI;

    public interface AccessTokenService {
        @POST("oauth2/access_token")
        Call<AccessTokenModel> getAccessToken(@Body AccessTokenBody accessTokenBody);
    }

    public interface UserProfileService {
        @GET(BASE_URL_SCHEME + "user/profile")
        Call<UserProfileModel> getUserProfile();
    }

    public interface UserTeamListService {
        @GET(BASE_URL_SCHEME + "teams")
        Call<List<UserTeamListModel>> getUserTeams();
    }

    public interface TeamProjectListService {
        @GET(BASE_URL_SCHEME + "teams/{teamId}/projects")
        Call<List<TeamProjectModel>> getTeamProjectList(@Path("teamId") String teamId);
    }

    public interface AllProjectsService {
        @GET(BASE_URL_SCHEME + "projects")
        Call<List<ProjectModel>> getAllProjects();
    }

    public interface ProjectEntryListService {
        @GET(BASE_URL_SCHEME + "entries")
        Call<List<EntryModel>> getProjectEntryList(@Query("pid") String pid);
    }

    public interface TaskListService {
        @GET(BASE_URL_SCHEME + "tasks")
        Call<List<TaskModel>> getTaskList(@Query("pid") String pid);
    }
}
