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

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ProjectModel extends RealmObject {
    /**
     * pid : eda54f766b7f457ea7cdb5a93c6d892e
     * name : Worktile 开发
     * team_id : cds4ls3f5c4ee8
     * desc :
     * archived : 0
     * pic : icon-github
     * bg : #5d8d0b
     * visibility : 2
     * is_star : 0
     * pos : 65536.5
     * member_count : 20
     * curr_role : 1
     * permission : 31
     */
    @PrimaryKey
    private String pid;
    private String name;
    private String team_id;
    private String desc;
    private int archived;
    private String pic;
    private String bg;
    private int visibility;
    private int is_star;
    private double pos;
    private int member_count;
    private int curr_role;
    private int permission;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getArchived() {
        return archived;
    }

    public void setArchived(int archived) {
        this.archived = archived;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getIs_star() {
        return is_star;
    }

    public void setIs_star(int is_star) {
        this.is_star = is_star;
    }

    public double getPos() {
        return pos;
    }

    public void setPos(double pos) {
        this.pos = pos;
    }

    public int getMember_count() {
        return member_count;
    }

    public void setMember_count(int member_count) {
        this.member_count = member_count;
    }

    public int getCurr_role() {
        return curr_role;
    }

    public void setCurr_role(int curr_role) {
        this.curr_role = curr_role;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }
}
