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

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class TaskModel extends RealmObject {

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

    @PrimaryKey
    private String tid;
    private String name;
    private String pid;
    private String entry_id;
    private String entry_name;
    private String created_at;
    private String updated_at;
    private double pos;
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
    @Ignore
    private List<?> labels;
    /**
     * todo_id :
     * name :
     * checked : 0
     * pos : 65535
     */

    private RealmList<TodosEntity> todos;
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

    private RealmList<WatchersEntity> watchers;
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

    private RealmList<MembersEntity> members;
    @Ignore
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

    public double getPos() {
        return pos;
    }

    public void setPos(double pos) {
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

    public void setTodos(RealmList<TodosEntity> todos) {
        this.todos = todos;
    }

    public List<WatchersEntity> getWatchers() {
        return watchers;
    }

    public void setWatchers(RealmList<WatchersEntity> watchers) {
        this.watchers = watchers;
    }

    public List<MembersEntity> getMembers() {
        return members;
    }

    public void setMembers(RealmList<MembersEntity> members) {
        this.members = members;
    }

    public List<?> getFiles() {
        return files;
    }

    public void setFiles(List<?> files) {
        this.files = files;
    }
}
