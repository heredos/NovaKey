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

package viviano.cantu.novakey.core.actions;

import android.view.inputmethod.ExtractedText;

import viviano.cantu.novakey.core.controller.Controller;
import viviano.cantu.novakey.core.NovaKeyService;
import viviano.cantu.novakey.core.model.Model;

/**
 * Created by Viviano on 6/15/2016.
 */
public class RenameSelectionAction implements Action<Void> {

    private boolean mMoveRight;


    public RenameSelectionAction(boolean moveRight) {
        mMoveRight = moveRight;
    }


    /**
     * Moves the caret, or grows/shrinks the selection while keeping the anchor
     * fixed. In cursor mode 0 this acts like a left/right arrow key: any existing
     * selection is dropped and the caret moved. In modes -1/1 only the start/end
     * is moved and, when it crosses the anchor, the mode flips so the anchor
     * stays in place.
     */
    @Override
    public Void trigger(NovaKeyService ime, Controller control, Model model) {
        ExtractedText et = ime.getExtractedText();
        if (et == null || et.text == null)
            return null;

        int len = et.text.length();
        int s = et.selectionStart, e = et.selectionEnd;
        int d = mMoveRight ? 1 : -1;

        switch (model.getCursorMode()) {
            case 0://arrow key
                int caret = s != e ? (d > 0 ? e : s) : s;
                caret = clamp(caret + d, 0, len);
                ime.setSelection(caret, caret);
                break;
            case -1://move start, anchor is end
                s += d;
                if (s > e) {
                    ime.setSelection(e, s);
                    model.setCursorMode(1);
                } else {
                    ime.setSelection(clamp(s, 0, len), clamp(e, 0, len));
                }
                break;
            case 1://move end, anchor is start
                e += d;
                if (e < s) {
                    ime.setSelection(e, s);
                    model.setCursorMode(-1);
                } else {
                    ime.setSelection(clamp(s, 0, len), clamp(e, 0, len));
                }
                break;
        }
        return null;
    }


    private int clamp(int value, int min, int max) {
        return value < min ? min : value > max ? max : value;
    }
}
