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

import me.fattycat.kun.teamwork.model.AccessToken;
import me.fattycat.kun.teamwork.model.AccessTokenBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class TWApi {
    public static final String BASE_URL_OAUTH = "https://open.worktile.com/oauth2/";
    public static final String BASE_URL_COMMON = "https://api.worktile.com/";

    public static String OAUTHURL = TWApi.BASE_URL_OAUTH
            + "authorize?client_id=" + TWSecret.CLIENT_ID
            + "&redirect_uri=" + TWSecret.REDIRECT_URI;

    public interface AccessTokenService {
        @Headers("Content-Type:application/json")
        @POST("oauth2/access_token")
        Call<AccessToken> getAccessToken(@Body AccessTokenBody accessTokenBody);
    }
}
