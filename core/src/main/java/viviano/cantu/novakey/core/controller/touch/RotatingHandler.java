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
import viviano.cantu.novakey.core.elements.MainElement;

/**
 * Created by Viviano on 6/15/2016.
 */
public abstract class RotatingHandler implements TouchHandler {

    protected float currX, currY;
    protected int currArea, prevArea;
    protected int currSector = -1, prevSector = -1;


    /**
     * Freezes area & sector tracking to the given position so a
     * mid-gesture hand off neither emits a phantom center cross nor
     * swallows the first genuine rotation (the prevSector == -1 guard)
     *
     * @param x       current finger x position
     * @param y       current finger y position
     * @param control controller used for context
     */
    @Override
    public void begin(float x, float y, Controller control) {
        currX = x;
        currY = y;
        currArea = MainElement.getArea(currX, currY, control.getModel());
        prevArea = currArea;
        currSector = MainElement.getSector(currX, currY, control.getModel());
        prevSector = currSector;
    }


    /**
     * Handles the logic given a touch event and
     * a view
     *
     * @param event   current touch event
     * @param control
     * @return true to continue action, false otherwise
     */
    @Override
    public boolean handle(MotionEvent event, Controller control) {
        currX = event.getX(0);
        currY = event.getY(0);
        currArea = MainElement.getArea(currX, currY, control.getModel());
        currSector = MainElement.getSector(currX, currY, control.getModel());

        boolean result = true;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                result = result & onDown(currX, currY, currArea, control);
                prevArea = currArea;
                prevSector = currSector;
                break;
            case MotionEvent.ACTION_MOVE:
                result = result & onMove(currX, currY, control);
                if (currArea != prevArea) {
                    if (currArea == 0)
                        result = result & onCenterCross(true, control);
                    else if (prevArea == 0)
                        result = result & onCenterCross(false, control);
                }
                prevArea = currArea;
                if (currSector != prevSector && prevSector != -1 &&
                        currSector >= 1 && currSector <= 5 &&
                        prevSector >= 1 && prevSector <= 5) {//if not initial handling
                    //shortest signed delta over the 5 cycle, in [-2, 2];
                    //a fast swipe landing several sectors over counts every step
                    int delta = currSector - prevSector;
                    if (delta > 2)
                        delta -= 5;
                    if (delta < -2)
                        delta += 5;
                    boolean clockwise = delta < 0;
                    for (int i = 0; i < Math.abs(delta); i++)
                        result = result & onRotate(clockwise, currArea == 0, control);
                }
                prevSector = currSector;
                break;
            case MotionEvent.ACTION_UP:
                result = result & onUp(control);
                break;
        }
        return result;
    }


    /**
     * Override this to specify onDown behaviour
     *
     * @param x          current x position
     * @param y          current y position
     * @param area       current area
     * @param controller controller used for context
     */
    protected boolean onDown(float x, float y, int area, Controller controller) {
        return true;
    }


    /**
     * Called when the user enters or exits the inner circle.
     * Call unrelated to onMove()
     *
     * @param entered    true if event was triggered by entering the
     *                   inner circle, false if was triggered by exit
     * @param controller provides context
     */
    protected abstract boolean onCenterCross(boolean entered, Controller controller);


    /**
     * Called for every move event so that the handler can update
     * display properly. Called before onRotate()
     *
     * @param x          current finger x position
     * @param y          current finger y position
     * @param controller provides context
     */
    protected boolean onMove(float x, float y, Controller controller) {
        return true;
    }


    /**
     * Called when the touch listener detects that there
     * has been a cross, either in sector or range
     *
     * @param clockwise  true if rotation is clockwise, false otherwise
     * @param inCenter   if finger position is currently in the center
     * @param controller provides context
     */
    protected abstract boolean onRotate(boolean clockwise, boolean inCenter, Controller controller);


    /**
     * Called when the user lifts finger, typically this
     * method expects a finalized action to be triggered
     * like typing a character
     *
     * @param controller provides context
     */
    protected abstract boolean onUp(Controller controller);
}
