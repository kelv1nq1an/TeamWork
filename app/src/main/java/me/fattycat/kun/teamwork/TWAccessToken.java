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

public class TWAccessToken {
    public static boolean AUTHORIZED = true;
    public static boolean NOTAUTHORIZED = false;

    public static boolean sIsAuthorized;
    private static String sAccessToken;
    private static String sRefreshToken;

    private TWAccessToken() {
    }

    public static void init(boolean isAuthorized) {
        sIsAuthorized = isAuthorized;
        sAccessToken = null;
        sRefreshToken = null;
    }

    public static void init(boolean isAuthorized, String accessToken, String refreshToken) {
        sIsAuthorized = isAuthorized;
        sAccessToken = accessToken;
        sRefreshToken = refreshToken;
    }

    public static String getAccessToken() {
        return sAccessToken;
    }

    public static String getRefreshToken() {
        return sRefreshToken;
    }
}
