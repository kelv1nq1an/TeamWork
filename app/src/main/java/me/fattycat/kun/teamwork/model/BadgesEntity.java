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
package me.fattycat.kun.teamwork.model;

import io.realm.RealmObject;

public class BadgesEntity extends RealmObject {
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