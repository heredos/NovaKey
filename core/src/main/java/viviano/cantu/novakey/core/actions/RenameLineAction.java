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

import android.view.KeyEvent;
import android.view.inputmethod.ExtractedText;

import viviano.cantu.novakey.core.controller.Controller;
import viviano.cantu.novakey.core.NovaKeyService;
import viviano.cantu.novakey.core.model.Model;

/**
 * Vertical twin of {@link RenameSelectionAction}: moves the caret, or the
 * moving end of the selection while keeping the anchor fixed, one line
 * down/up. In cursor mode 0 this acts like a down/up arrow key: any existing
 * selection is dropped and the caret moved (column preserved, clamped at the
 * document edges). In modes -1/1 only the start/end is moved and, when it
 * crosses the anchor, the mode flips so the anchor stays in place.
 */
public class RenameLineAction implements Action<Void> {

    private final boolean mDown;


    public RenameLineAction(boolean down) {
        mDown = down;
    }


    /**
     * Called when the action is triggered
     * Actual logic for the action goes here
     *  @param ime
     * @param control
     * @param model
     */
    @Override
    public Void trigger(NovaKeyService ime, Controller control, Model model) {
        int d = mDown ? 1 : -1;
        ExtractedText et = ime.getExtractedText();

        if (et == null || et.text == null) {
            // Editor without text extraction (e.g. Termux, whose inherited
            // BaseInputConnection also silently ignores setSelection): key
            // events are the only channel that reaches the editor, and they
            // move the caret one line natively. The selection bracket modes
            // (-1/1) aren't supported there and degrade to a plain caret move
            ime.sendDownUpKeyEvents(mDown
                    ? KeyEvent.KEYCODE_DPAD_DOWN : KeyEvent.KEYCODE_DPAD_UP);
            return null;
        }

        String text = et.text.toString();
        int s = et.selectionStart, e = et.selectionEnd;
        int len = text.length();

        switch (model.getCursorMode()) {
            case 0://arrow key
                int caret = s != e ? (d > 0 ? e : s) : s;
                caret = lineStep(text, caret, d);
                ime.setSelection(caret, caret);
                break;
            case -1://move start, anchor is end
                int ns = lineStep(text, s, d);
                if (ns > e) {
                    ime.setSelection(e, ns);
                    model.setCursorMode(1);
                } else {
                    ime.setSelection(clamp(ns, 0, len), clamp(e, 0, len));
                }
                break;
            case 1://move end, anchor is start
                int ne = lineStep(text, e, d);
                if (ne < s) {
                    ime.setSelection(ne, s);
                    model.setCursorMode(-1);
                } else {
                    ime.setSelection(clamp(s, 0, len), clamp(ne, 0, len));
                }
                break;
        }
        return null;
    }


    /**
     * Moves the given position one line down (d &gt; 0) or up, preserving
     * its column and clamping at the first/last line. Positions inside a
     * line without a neighbor in the wanted direction stay put at the
     * start/end of that line
     *
     * @param text full text around the cursor
     * @param pos  current caret position inside text
     * @param d    1 to move down, -1 to move up
     * @return the new caret position
     */
    private static int lineStep(String text, int pos, int d) {
        pos = clamp(pos, 0, text.length());
        int start = pos > 0 ? text.lastIndexOf('\n', pos - 1) + 1 : 0;
        int end = text.indexOf('\n', pos);
        if (end < 0)
            end = text.length();
        int column = pos - start;

        if (d > 0) {
            if (end >= text.length())
                return text.length();
            int nextStart = end + 1;
            int nextEnd = text.indexOf('\n', nextStart);
            if (nextEnd < 0)
                nextEnd = text.length();
            return Math.min(nextStart + column, nextEnd);
        } else {
            if (start == 0)
                return 0;
            int prevEnd = start - 1;
            int prevStart = prevEnd > 0
                    ? text.lastIndexOf('\n', prevEnd - 1) + 1 : 0;
            return Math.min(prevStart + column, prevEnd);
        }
    }


    private static int clamp(int value, int min, int max) {
        return value < min ? min : value > max ? max : value;
    }
}