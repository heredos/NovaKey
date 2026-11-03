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

package viviano.cantu.novakey.core.elements.keyboards.overlays;

import android.graphics.Canvas;
import android.view.MotionEvent;

import viviano.cantu.novakey.core.actions.Action;
import viviano.cantu.novakey.core.actions.RenameLineAction;
import viviano.cantu.novakey.core.actions.VibrateAction;
import viviano.cantu.novakey.core.controller.Controller;
import viviano.cantu.novakey.core.controller.touch.SelectingHandler;
import viviano.cantu.novakey.core.controller.touch.TouchHandler;
import viviano.cantu.novakey.core.model.MainDimensions;
import viviano.cantu.novakey.core.model.Model;
import viviano.cantu.novakey.core.model.Settings;
import viviano.cantu.novakey.core.utils.drawing.Icons;
import viviano.cantu.novakey.core.view.themes.MasterTheme;
import viviano.cantu.novakey.core.view.themes.board.BoardTheme;

/**
 * Overlay shown while the line cursor is active: same interaction as
 * {@link CursorOverlay} (rotating steps the caret, dipping into the center
 * opens the clipboard menu and toggles the cursor mode on the way out)
 * but every rotation step moves the cursor one line down or up, vibrating
 * as it goes
 */
public class LineCursorOverlay implements OverlayElement {

    private final TouchHandler mHandler;


    public LineCursorOverlay() {
        mHandler = new SelectingHandler(step(true), step(false));
    }


    /**
     * @param down true for the clockwise (down) step,
     *             false for the counter-clockwise (up) step
     * @return action vibrating and moving the cursor one line
     */
    private static Action<Void> step(boolean down) {
        return (ime, control, model) -> {
            control.fire(new VibrateAction(Settings.vibrateLevel));
            control.fire(new RenameLineAction(down));
            return null;
        };
    }


    @Override
    public void draw(Model model, MasterTheme theme, Canvas canvas) {
        MainDimensions d = model.getMainDimensions();
        BoardTheme board = theme.getBoardTheme();

        int cursorCode = model.getCursorMode();
        //same icons as the horizontal cursor overlay, turned 90 degrees so
        //the axis reads down/up and the two overlays never get confused
        canvas.save();
        canvas.rotate(90, d.getX(), d.getY());
        board.drawItem(Icons.cursors, d.getX(), d.getY(),
                d.getSmallRadius(), canvas);
        if (cursorCode >= 0)
            board.drawItem(Icons.cursorLeft, d.getX(), d.getY(),
                    d.getSmallRadius(), canvas);
        if (cursorCode <= 0)
            board.drawItem(Icons.cursorRight, d.getX(), d.getY(),
                    d.getSmallRadius(), canvas);
        canvas.restore();
    }


    @Override
    public boolean handle(MotionEvent event, Controller control) {
        return mHandler.handle(event, control);
    }
}