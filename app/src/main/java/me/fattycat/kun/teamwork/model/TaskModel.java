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

import java.util.List;

public class TaskModel {

    /**
     * name :
     * pid :
     * tid :
     * entry_id :
     * entry_name :
     * created_at :
     * updated_at :
     * pos : 65535
     * labels : []
     * todos : [{"todo_id":"","name":"","checked":0,"pos":65535}]
     * badges : {"expire_date":"","comment_count":0,"todo_checked_count":0,"todo_count":2,"file_count":0}
     * watchers : [{"uid":"","name":"","display_name":"","email":"","avatar":"","desc":"","status":3,"online":0}]
     * members : [{"uid":"","name":"","display_name":"","email":"","avatar":"","desc":"","status":3,"online":0}]
     * completed : 0
     * completed_date :
     * expire_date :
     * desc :
     * project : {"pid":"","name":"","pic":"","bg":""}
     * files : []
     */

    private String name;
    private String pid;
    private String tid;
    private String entry_id;
    private String entry_name;
    private String created_at;
    private String updated_at;
    private int pos;
    /**
     * expire_date :
     * comment_count : 0
     * todo_checked_count : 0
     * todo_count : 2
     * file_count : 0
     */

    private BadgesEntity badges;
    private int completed;
    private String completed_date;
    private String expire_date;
    private String desc;
    /**
     * pid :
     * name :
     * pic :
     * bg :
     */

    private ProjectEntity project;
    private List<?> labels;
    /**
     * todo_id :
     * name :
     * checked : 0
     * pos : 65535
     */

    private List<TodosEntity> todos;
    /**
     * uid :
     * name :
     * display_name :
     * email :
     * avatar :
     * desc :
     * status : 3
     * online : 0
     */

    private List<WatchersEntity> watchers;
    /**
     * uid :
     * name :
     * display_name :
     * email :
     * avatar :
     * desc :
     * status : 3
     * online : 0
     */

    private List<MembersEntity> members;
    private List<?> files;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getEntry_id() {
        return entry_id;
    }

    public void setEntry_id(String entry_id) {
        this.entry_id = entry_id;
    }

    public String getEntry_name() {
        return entry_name;
    }

    public void setEntry_name(String entry_name) {
        this.entry_name = entry_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public BadgesEntity getBadges() {
        return badges;
    }

    public void setBadges(BadgesEntity badges) {
        this.badges = badges;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public String getCompleted_date() {
        return completed_date;
    }

    public void setCompleted_date(String completed_date) {
        this.completed_date = completed_date;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public List<?> getLabels() {
        return labels;
    }

    public void setLabels(List<?> labels) {
        this.labels = labels;
    }

    public List<TodosEntity> getTodos() {
        return todos;
    }

    public void setTodos(List<TodosEntity> todos) {
        this.todos = todos;
    }

    public List<WatchersEntity> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<WatchersEntity> watchers) {
        this.watchers = watchers;
    }

    public List<MembersEntity> getMembers() {
        return members;
    }

    public void setMembers(List<MembersEntity> members) {
        this.members = members;
    }

    public List<?> getFiles() {
        return files;
    }

    public void setFiles(List<?> files) {
        this.files = files;
    }

    public static class BadgesEntity {
        private String expire_date;
        private int comment_count;
        private int todo_checked_count;
        private int todo_count;
        private int file_count;

        public String getExpire_date() {
            return expire_date;
        }

        public void setExpire_date(String expire_date) {
            this.expire_date = expire_date;
        }

        public int getComment_count() {
            return comment_count;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
        }

        public int getTodo_checked_count() {
            return todo_checked_count;
        }

        public void setTodo_checked_count(int todo_checked_count) {
            this.todo_checked_count = todo_checked_count;
        }

        public int getTodo_count() {
            return todo_count;
        }

        public void setTodo_count(int todo_count) {
            this.todo_count = todo_count;
        }

        public int getFile_count() {
            return file_count;
        }

        public void setFile_count(int file_count) {
            this.file_count = file_count;
        }
    }

    public static class ProjectEntity {
        private String pid;
        private String name;
        private String pic;
        private String bg;

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
    }

    public static class TodosEntity {
        private String todo_id;
        private String name;
        private int checked;
        private int pos;

        public String getTodo_id() {
            return todo_id;
        }

        public void setTodo_id(String todo_id) {
            this.todo_id = todo_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getChecked() {
            return checked;
        }

        public void setChecked(int checked) {
            this.checked = checked;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }
    }

    public static class WatchersEntity {
        private String uid;
        private String name;
        private String display_name;
        private String email;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

    public static class MembersEntity {
        private String uid;
        private String name;
        private String display_name;
        private String email;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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
