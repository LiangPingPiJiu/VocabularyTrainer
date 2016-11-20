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


package com.tobias.vocabulary_trainer.data;

import static android.provider.BaseColumns._ID;
import static com.tobias.vocabulary_trainer.Constants.BOOK;
import static com.tobias.vocabulary_trainer.Constants.CHARACTER;
import static com.tobias.vocabulary_trainer.Constants.DATABASE_NAME;
import static com.tobias.vocabulary_trainer.Constants.DATABASE_VERSION;
import static com.tobias.vocabulary_trainer.Constants.LESSON;
import static com.tobias.vocabulary_trainer.Constants.LEVEL;
import static com.tobias.vocabulary_trainer.Constants.PHONETIC_SCRIPT;
import static com.tobias.vocabulary_trainer.Constants.TABLE_NAME;
import static com.tobias.vocabulary_trainer.Constants.TRANSLATION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import com.tobias.vocabulary_trainer.Settings;
import com.tobias.vocabulary_trainer.utils.Conversion;

public class VocabularyData extends SQLiteOpenHelper
{
	
	public final static String VOCABULARIES_LIST="VocabularyTrainerBackup.txt";

	/** Constructor - Create a helper object for the Events database */
	public VocabularyData(Context ctx)
	{
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ BOOK + " INTEGER,"
				+ LESSON + " INTEGER,"
				+ CHARACTER + " TEXT NOT NULL,"
				+ PHONETIC_SCRIPT + " TEXT NOT NULL,"
				+ TRANSLATION + " TEXT NOT NULL,"
				+ LEVEL + " INTEGER"
				+		");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,
	int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public void deleteDatabase(SQLiteDatabase db)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public String getWhereClause(SharedPreferences settings)
    {
    	StringBuilder builder = new StringBuilder("");

    	int bookBeginIndex = settings.getInt(Settings.PREF_BOOK_BEGIN, 0);
		int bookEndIndex = settings.getInt(Settings.PREF_BOOK_END, 0);
		int lessonBeginIndex = settings.getInt(Settings.PREF_LESSON_BEGIN, 0);
		int lessonEndIndex = settings.getInt(Settings.PREF_LESSON_END, 0);
		int levelBeginIndex = settings.getInt(Settings.PREF_LEVEL_BEGIN, 0);
		int levelEndIndex = settings.getInt(Settings.PREF_LEVEL_END, 4);

    	// Get the data represented by the indexes and add it to the WHERE Clause
		Vector<Integer> available_books;
		Vector<Integer> available_books_end;
		Vector<Integer> available_lessons;
		Vector<Integer> available_lessons_end;

		Vector<Integer> levels = new Vector<Integer>();

		levels.add(new Integer(-2));
		levels.add(new Integer(-1));
		levels.add(new Integer(0));
		levels.add(new Integer(1));
		levels.add(new Integer(2));

		available_books = getBooks();
		available_lessons = getLessons(available_books.get(bookBeginIndex));
		available_books_end = Conversion.toVector(available_books.subList(bookBeginIndex, available_books.size()));

		if(bookEndIndex == 0)
		{
			available_lessons_end = Conversion.toVector(available_lessons.subList(lessonBeginIndex, available_lessons.size()));
		}
		else
		{
			available_lessons_end = getLessons(available_books_end.get(bookEndIndex));
		}
		Vector<Integer> levelsEnd;
		levelsEnd = Conversion.toVector(levels.subList(levelBeginIndex, levels.size()));

		builder.append(BOOK + " = " + available_books.get(bookBeginIndex)
		        + " and " + LESSON  + " >= " +  available_lessons.get(lessonBeginIndex)
		        + " and " + levels.get(levelBeginIndex) + " <= "+ LEVEL
		        + " and " + levelsEnd.get(levelEndIndex) + " >= " + LEVEL);
		        if(bookEndIndex == 0)
		        {
		            //if book begin and book end are the same
		        	builder.append(" and " + LESSON  + " <= " +  available_lessons_end.get(lessonEndIndex));
		        }

		builder.append(" or " + BOOK + " > " + available_books.get(bookBeginIndex)
		        + " and " + BOOK + " < " + available_books_end.get(bookEndIndex)
				+ " and " + levels.get(levelBeginIndex) + " <= "+ LEVEL
				+ " and " + levelsEnd.get(levelEndIndex) + " >= " + LEVEL);

		if(bookEndIndex > 0)
		{
			//if book begin and book end are not the same
			builder.append(" or " + BOOK + " = " + available_books_end.get(bookEndIndex)
			        + " and " + LESSON  + " <= " + available_lessons_end.get(lessonEndIndex)
			        + " and " + levels.get(levelBeginIndex) + " <= "+ LEVEL
			        + " and " + levelsEnd.get(levelEndIndex) + " >= " + LEVEL);
		}
		return builder.toString();
    }

	/**
	 * @return a sorted array of all available books from database
	 */
	public Vector<Integer> getBooks()
	{
		boolean moreBooks;
		Vector<Integer> books = new Vector<Integer>();
		String[] from = {BOOK};
		SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.query(TABLE_NAME, from, null, null, null,
    			null, BOOK);

    	cursor.moveToFirst();
    	moreBooks = false;
    	if (cursor.getCount() > 0) {
    		moreBooks = true;
    	}
    	while (moreBooks) {
    		if (books.contains(new Integer(cursor.getInt(0))) == false) {
    			books.add(new Integer(cursor.getInt(0)));
    		}
    		moreBooks = cursor.moveToNext();
    	}
    	Collections.sort(books);
    	cursor.close();
		return books;
	}

    public Vector<Integer> getLessons(int book)
	{
    	boolean moreLessons;
    	Vector<Integer> lessons = new Vector<Integer>();
		String[] from = {LESSON, BOOK};
		SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.query(TABLE_NAME, from, BOOK + " like " + book, null, null,
    			null, BOOK);

    	cursor.moveToFirst();
    	moreLessons = false;
    	if (cursor.getCount() > 0) {
    		moreLessons = true;
    	}
    	while (moreLessons) {
    		if (lessons.contains(new Integer(cursor.getInt(0))) == false) {
    			lessons.add(new Integer(cursor.getInt(0)));
    		}
    		moreLessons = cursor.moveToNext();
    	}
    	Collections.sort(lessons);
    	cursor.close();
		return lessons;
	}

	public void addVocabulary(int book, int lesson, String character, String phonetic_script, String translation, int level)
	{
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BOOK, book);
		values.put(LESSON, lesson);
		values.put(CHARACTER, character);
		values.put(PHONETIC_SCRIPT, phonetic_script);
		values.put(TRANSLATION, translation);
		values.put(LEVEL, level);
		db.insertOrThrow(TABLE_NAME, null, values);
	}

	/**
	 * reads text-file and saves the containing vocabularies in the database
	 * @param f	text-file with vocabularies
	 * @param delete_old_database	true if existing database should be overwritten
     * @return	true if succeed, otherwise false
     */
	public boolean readVocabularies(File f, boolean delete_old_database)
	{
		try
		{
			FileInputStream fin = new FileInputStream(f);
			return readVocabularies(fin, delete_old_database);
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * reads InputStream and saves the containing vocabularies in the database
	 * @param in	FileInputStream
	 * @param delete_old_database	true if existing database should be overwritten
     * @return	true if succeed, otherwise false
     */
	public boolean readVocabularies(InputStream in, boolean delete_old_database)
	{
		boolean success;
		success = false;
		try
		{
			if (in!=null)
			{
				if(delete_old_database)
				{
					//first delete old database to avoid double vocabularies
					SQLiteDatabase db = getWritableDatabase();
					deleteDatabase(db);
				}
				InputStreamReader tmp = new InputStreamReader(in);
				BufferedReader reader = new BufferedReader(tmp);
				String str;
				int index_begin;
				int index_end;
				int counter = 0;
				String temp;
				//database values
				int book = 0;
				int lesson = 0;
				String character = "";
				String phonetic_script = "";
				String translation = "";
				int level = 0;
				while ((str = reader.readLine()) != null)
				{
					if(!str.startsWith("//"))
					{
						index_begin = 0;
						counter = 0;
						while(str.length() > index_begin)
						{
							index_end = str.indexOf("|", index_begin);
							if(index_end != -1) //-1 means there is no "|"
							{
								temp = str.substring(index_begin, index_end);
	            				temp.trim(); //leider keine wirkung!?
	            				index_begin = index_end + 1;
							}
							else //last element in this row
							{
								temp = str.substring(index_begin);
								temp.trim(); //leider keine wirkung!?
								index_begin = str.length();
							}
							if(counter == 0) // if book
							{
								book = Integer.parseInt(temp);
							}
							if(counter == 1) // if lesson
							{
								lesson = Integer.parseInt(temp);
							}
							if(counter == 2) // if character
							{
								character = temp;
							};
							if(counter == 3) // if phonetic_script
							{
								phonetic_script = temp;
							}
							if(counter == 4) // if translation
							{
								translation = temp;
							}
							if(counter == 5) // if level
							{
								level = Integer.parseInt(temp);
							}
							counter++;
						}
						addVocabulary(book, lesson, character, phonetic_script,	translation, level);
						//update vocabularies_list.txt
						if(!delete_old_database)
						{
							File fileToBeUpdated = new File(Environment.getExternalStorageDirectory(),
									"vocabularies_list.txt");
							addVocabulariesToTextFile(fileToBeUpdated, str);
						}
					}
				}
				in.close();
				success = true;
			}
		}
		catch (Exception e)
		{
			success = false;
			System.out.println("error while io access: " + e.getMessage());
		}

		return success;
	}

	public boolean addVocabulariesToTextFile(File fileToBeUpdated, String updateString)
	{
		//doesn't sort the vocabularies, just add them to the end of the existing list
		boolean success = false;
				try
				{
					//append to fileToBeUpdated
					FileOutputStream fileOS = new FileOutputStream(fileToBeUpdated, true);
					OutputStreamWriter out =
						new OutputStreamWriter(fileOS);
					out.write(updateString);
					out.write("\n");
					out.close();
				}
				catch (Throwable t)
				{
					//do nothing yet
				}

		return success;
	}

 	public Cursor getVocabularies(String[] from, String where, String order_by)
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, from, where, null, null,
				null, order_by);
		return cursor;
	}

	public void raiseVocabularyLevel(Cursor cursor, int id)
	{
		SQLiteDatabase db = getWritableDatabase();
		cursor.moveToPosition(id);
		String ID = cursor.getString(0);
		String current_level_string = cursor.getString(6);
		int current_level = Integer.parseInt(current_level_string);
		if(current_level < 2)
		{
			db.execSQL("update " + TABLE_NAME + " set "
	    			+ LEVEL + " = " + (current_level + 1)
	    			+ " where " + _ID + " = " + ID);
		}
	}

	public void lowerVocabularyLevel(Cursor cursor, int id)
	{
		SQLiteDatabase db = getWritableDatabase();
		cursor.moveToPosition(id);
		String ID = cursor.getString(0);
		String current_level_string = cursor.getString(6);
		int current_level = Integer.parseInt(current_level_string);
		if(current_level > -2)
		{
			db.execSQL("update " + TABLE_NAME + " set "
	    			+ LEVEL + " = " + (current_level - 1)
	    			+ " where " + _ID + " = " + ID);
		}
	}

	public void moveSelectedVocabularies(String WHERE, int newBox)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("update " + TABLE_NAME + " set "
				+ LEVEL + " = " + (newBox)
				+ " where " + WHERE);
	}

	public String getCharacter(Cursor cursor, int id)
	{
		cursor.moveToPosition(id);
		return cursor.getString(3);
	}

	public String getPhoneticScript(Cursor cursor, int id)
	{
		cursor.moveToPosition(id);
		if(cursor.moveToPosition(id) == false)
		{
			return null;
		}
		return cursor.getString(4);
	}

	public String getTranslation(Cursor cursor, int id)
	{
		cursor.moveToPosition(id);
		return cursor.getString(5);
	}
}