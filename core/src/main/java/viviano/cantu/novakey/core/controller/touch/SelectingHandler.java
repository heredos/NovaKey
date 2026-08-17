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

import android.os.CountDownTimer;

import viviano.cantu.novakey.core.Clipboard;
import viviano.cantu.novakey.core.controller.Controller;
import viviano.cantu.novakey.core.actions.Action;
import viviano.cantu.novakey.core.actions.RenameSelectionAction;
import viviano.cantu.novakey.core.actions.SetOverlayAction;
import viviano.cantu.novakey.core.actions.ToggleCursorModeAction;
import viviano.cantu.novakey.core.actions.VibrateAction;
import viviano.cantu.novakey.core.model.Settings;

/**
 * Created by Viviano on 6/15/2016.
 */
public class SelectingHandler extends RotatingHandler {

    private final Action mRight, mLeft;

    private CountDownTimer mTimer;
    private boolean mMenuOpened;


    public SelectingHandler() {
        mRight = new RenameSelectionAction(true);
        mLeft = new RenameSelectionAction(false);
    }


    /**
     * Called when the user enters or exits the inner circle.
     * Call unrelated to onMove()
     *
     * @param entered    true if event was triggered by entering the
     *                   inner circle, false if was triggered by exit
     * @param controller
     */
    @Override
    protected boolean onCenterCross(boolean entered,
                                    Controller controller) {
        if (entered) {
            cancelTimer();
            mMenuOpened = false;
            mTimer = new CountDownTimer(Settings.longPressTime, Settings.longPressTime) {
                @Override
                public void onTick(long millisUntilFinished) {
                }


                @Override
                public void onFinish() {
                    controller.fire(new VibrateAction(Settings.vibrateLevel));
                    Clipboard.MENU.reset();
                    controller.fire(new SetOverlayAction(Clipboard.MENU));
                    controller.setHandler(Clipboard.MENU.getHandler());
                    mMenuOpened = true;
                }
            }.start();
        } else {
            cancelTimer();
            if (!mMenuOpened)
                controller.fire(new ToggleCursorModeAction());
        }
        return true;
    }


    private void cancelTimer() {
        if (mTimer != null)
            mTimer.cancel();
    }


    /**
     * Called for every move event so that the handler can update
     * display properly. Called before onRotate()
     *
     * @param x          current finger x position
     * @param y          current finger y position
     * @param controller
     */
    @Override
    protected boolean onMove(float x, float y, Controller controller) {
        //Do nothing
        return true;
    }


    /**
     * Called when the touch listener detects that there
     * has been a cross, either in sector or range
     *
     * @param clockwise  true if rotation is clockwise, false otherwise
     * @param inCenter   if finger position is currently in the center
     * @param controller
     */
    @Override
    protected boolean onRotate(boolean clockwise, boolean inCenter,
                               Controller controller) {
        System.out.println("Rotating: " + clockwise);
        if (!inCenter)
            controller.fire(clockwise ? mRight : mLeft);
        return true;
    }


    /**
     * Called when the user lifts finger, typically this
     * method expects a finalized action to be triggered
     * like typing a character
     *
     * @param controller
     */
    @Override
    protected boolean onUp(Controller controller) {
        cancelTimer();
        if (!mMenuOpened)
            controller.fire(new SetOverlayAction(controller.getModel().getKeyboard()));
        return false;
    }
}
