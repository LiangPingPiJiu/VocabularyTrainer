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


package com.tobias.vocabulary_trainer;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns
{
	public static final String DATABASE_NAME = "vocabularyData.db" ;
	public static final int DATABASE_VERSION = 12;
	public static final String TABLE_NAME = "vocabulary";
	// Columns in the Events database
	public static final String BOOK = "book";
	public static final String LESSON = "lesson";
	public static final String CHARACTER = "character";
	public static final String PHONETIC_SCRIPT = "phonetic_script";
	public static final String TRANSLATION = "translation";
	public static final String LEVEL = "level";
}