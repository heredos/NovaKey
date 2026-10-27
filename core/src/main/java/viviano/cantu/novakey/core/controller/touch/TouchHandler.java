/*
 * NovaKey - An alternative touchscreen input method
 * Copyright (C) 2019  Viviano Cantu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 *
 * Any questions about the program or source may be directed to <strellastudios@gmail.com>
 */

package viviano.cantu.novakey.core.controller.touch;

import android.view.MotionEvent;

import viviano.cantu.novakey.core.controller.Controller;

/**
 * Created by Viviano on 6/12/2016.
 */
public interface TouchHandler {

    /**
     * Initializes this handler when it is handed control mid-gesture,
     * i.e. without having received the gesture's ACTION_DOWN.
     * <p>
     * Implementations must freeze their area/sector tracking to the
     * given position so that the first move event after the hand off
     * does not produce a phantom cross, center cross, rotation or
     * long press timer. The position also serves as the origin ("index 0")
     * for menus opened by a long press.
     *
     * @param x       current finger x position
     * @param y       current finger y position
     * @param control controller used for context
     */
    default void begin(float x, float y, Controller control) {
    }


    /**
     * Handles the logic given a touch event and
     * a view
     *
     * @param event   current touch event
     * @param control controller used for context
     * @return true to continue action, false otherwise
     */
    boolean handle(MotionEvent event, Controller control);
}
