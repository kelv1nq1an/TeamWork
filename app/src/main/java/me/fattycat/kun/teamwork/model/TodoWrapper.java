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

public class TodoWrapper {
    public String todoId;
    public String taskId;
    public String projectId;
    public String todoName;

    public TodoWrapper(String todoId, String taskId, String projectId, String todoName) {
        this.todoId = todoId;
        this.taskId = taskId;
        this.projectId = projectId;
        this.todoName = todoName;
    }
}
