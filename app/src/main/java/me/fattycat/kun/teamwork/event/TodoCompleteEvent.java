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
package me.fattycat.kun.teamwork.event;

import me.fattycat.kun.teamwork.model.TodoWrapper;

public class TodoCompleteEvent {
    public TodoWrapper todoWrapper;
    public boolean isChecked;

    public TodoCompleteEvent(TodoWrapper todoWrapper, boolean isChecked) {
        this.todoWrapper = todoWrapper;
        this.isChecked = isChecked;
    }
}
