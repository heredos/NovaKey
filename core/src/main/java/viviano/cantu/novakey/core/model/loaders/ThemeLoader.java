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

package viviano.cantu.novakey.core.model.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;

import viviano.cantu.novakey.core.R;
import viviano.cantu.novakey.core.model.Settings;
import viviano.cantu.novakey.core.model.factories.ThemeFactory;
import viviano.cantu.novakey.core.view.themes.MasterTheme;

/**
 * Loads the {@link MasterTheme} used to draw the keyboard.
 *
 * <p>When the user has not customized the theme, the keyboard follows the
 * system's Material 3 dynamic color palette (wallpaper based on Android 12+,
 * Material 3 defaults on older versions).</p>
 */
public class ThemeLoader implements Loader<MasterTheme> {

    private final Context mContext;
    private final SharedPreferences mSharedPref;


    public ThemeLoader(Context context) {
        mContext = context;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }


    /**
     * Loads the element from where ever this interface saved it from
     *
     * @return a new loaded object
     */
    @Override
    public MasterTheme load() {
        String themeString = mSharedPref.getString(Settings.pref_theme, Settings.DEFAULT);
        MasterTheme theme = ThemeFactory.themeFromString(themeString);
        if (themeString.equals(Settings.DEFAULT))
            applyDynamicColors(theme);
        return theme;
    }


    /**
     * Applies the system's Material 3 dynamic color palette to the given theme.
     *
     * @param theme theme to color
     */
    private void applyDynamicColors(MasterTheme theme) {
        Context themed = new ContextThemeWrapper(mContext, R.style.NovaKeyTheme);
        int background = resolve(themed, com.google.android.material.R.attr.colorSurfaceContainerLow,
                0xFF616161);
        int lines = resolve(themed, com.google.android.material.R.attr.colorPrimary,
                0xFFF5F5F5);
        int text = resolve(themed, com.google.android.material.R.attr.colorOnSurface,
                0xFFF5F5F5);
        theme.setColors(background, lines, text);
    }


    private static int resolve(Context context, int attr, int fallback) {
        TypedValue value = new TypedValue();
        if (context.getTheme().resolveAttribute(attr, value, true))
            return value.data;
        return fallback;
    }


    /**
     * Saves the element to be loaded later
     *
     * @param theme object to be saved
     */
    @Override
    public void save(MasterTheme theme) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(Settings.pref_theme, ThemeFactory.stringFromTheme(theme));
        editor.commit();
    }
}