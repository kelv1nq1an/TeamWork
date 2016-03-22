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

public class UserTeamListModel {

    /**
     * team_id : b809bef616fd4faa994a84ac61f922a1
     * url : b809bef616fd4faa994a84ac61f922a1
     * name : 易成科技
     * desc : 易成科技
     * created_at : 2014-09-03T04:41:01.436Z
     * visibility : 1
     * create_by : {"uid":"679efdf3960d45a0b8679693098135ff","name":"gonglinjie","display_name":"龚林杰","avatar":"default_avatar.png","desc":"","status":3,"online":0}
     */

    private String team_id;
    private String url;
    private String name;
    private String desc;
    private String created_at;
    private int visibility;
    /**
     * uid : 679efdf3960d45a0b8679693098135ff
     * name : gonglinjie
     * display_name : 龚林杰
     * avatar : default_avatar.png
     * desc :
     * status : 3
     * online : 0
     */

    private CreateByEntity create_by;

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public CreateByEntity getCreate_by() {
        return create_by;
    }

    public void setCreate_by(CreateByEntity create_by) {
        this.create_by = create_by;
    }

    public static class CreateByEntity {
        private String uid;
        private String name;
        private String display_name;
        private String avatar;
        private String desc;
        private int status;
        private int online;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplay_name() {
            return display_name;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getOnline() {
            return online;
        }

        public void setOnline(int online) {
            this.online = online;
        }
    }
}
