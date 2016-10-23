//[Vocabulary Trainer]
//Copyright (C) [2012]  [Tobias Endrikat]
//
//This program is free software;
//you can redistribute it and/or modify it under the terms of the
//GNU General Public License as published by the Free Software Foundation;
//either version 3 of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
//or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, see <http://www.gnu.org/licenses/>.

package com.tobias.vocabulary_trainer.utils;

import java.util.Locale;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import com.tobias.vocabulary_trainer.R;

public class GeneralUtils
{
	public static final String USER_PREFERENCE = "USER_PREFERENCES";
	public static final String PREF_LANGUAGES = "PREF_PREF_LANGUAGES";
	static SharedPreferences prefs;

	public static void refresh(Activity activity)
    {
    	int actualScreenOrientation = activity.getRequestedOrientation();
    	if(actualScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    	{
    		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	}
    	if(actualScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    	{
    		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	}
    	if(actualScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    			&& actualScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    	{
    		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	}
    	activity.setRequestedOrientation(actualScreenOrientation);
    }
	public static int returnsLanguageIndexFromPreferences(Activity activity, SharedPreferences prefs)
	{
		//set the languageIndex to the Index saved in the prefs,
		//if not available, set it to the index of "en"
		//if also not available set it to 0
		String savedLanguage = activity.getBaseContext().getResources().getConfiguration().locale.getLanguage();
		String[] languagecodes = (activity.getResources().getStringArray(R.array.languagecodes));
		int languageToSet = 0;
		for(int i=0;i<languagecodes.length;i++)
		{
			if(languagecodes[i].equals("en"))
			{
				languageToSet = i;
			}
			if(languagecodes[i].equals(savedLanguage))
			{
				languageToSet = i;
				break;
			}
		}
		int languagesIndex = prefs.getInt(PREF_LANGUAGES, languageToSet);
		return languagesIndex;
	}
	public static void setsLanguage(Activity activity)
	{
		String oldLanguage = activity.getBaseContext().getResources().getConfiguration().locale.getLanguage();
		prefs = activity.getSharedPreferences(USER_PREFERENCE, Activity.MODE_PRIVATE);
		int languagesIndex = GeneralUtils.returnsLanguageIndexFromPreferences(activity, prefs);
		String languageToLoad  = (activity.getResources().getStringArray(R.array.languagecodes))[languagesIndex];
	    Locale locale = new Locale(languageToLoad); 
	    Locale.setDefault(locale);
	    Configuration config = new Configuration();
	    config.locale = locale;
	    activity.getBaseContext().getResources().updateConfiguration(config, 
	    		activity.getBaseContext().getResources().getDisplayMetrics());
	    if(!oldLanguage.equals(languageToLoad))
    	{
        	refresh(activity);
        }
	}
}
