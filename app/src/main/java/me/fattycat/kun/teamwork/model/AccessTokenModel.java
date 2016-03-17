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
package me.fattycat.kun.teamwork.model;

public class AccessTokenModel {

    /**
     * access_token : ACCESS_TOKEN
     * expires_in : 1234
     * refresh_token : REFRESH_TOKEN
     * error_code : 100004
     * error_message : code已过期，请重新获取
     * request : /oauth2/access_token
     */

    private String access_token;
    private int expires_in;
    private String refresh_token;
    private int error_code;
    private String error_message;
    private String request;

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getAccess_token() {
        return access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public int getError_code() {
        return error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public String getRequest() {
        return request;
    }
}
