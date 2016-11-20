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

import static android.provider.BaseColumns._ID;
import static com.tobias.vocabulary_trainer.Constants.BOOK;
import static com.tobias.vocabulary_trainer.Constants.CHARACTER;
import static com.tobias.vocabulary_trainer.Constants.LESSON;
import static com.tobias.vocabulary_trainer.Constants.LEVEL;
import static com.tobias.vocabulary_trainer.Constants.PHONETIC_SCRIPT;
import static com.tobias.vocabulary_trainer.Constants.TABLE_NAME;
import static com.tobias.vocabulary_trainer.Constants.TRANSLATION;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.tobias.vocabulary_trainer.data.VocabularyData;
import com.tobias.vocabulary_trainer.utils.Conversion;
import com.tobias.vocabulary_trainer.utils.GeneralUtils;

public class Settings extends Activity
implements android.widget.AdapterView.OnItemSelectedListener
{	
	private VocabularyData vocabularies;
	Vector<Integer> available_books;
	String[] available_books_string;
	Vector<Integer> available_books_end;
	String[] available_books_end_string;
	Vector<Integer> available_lessons;
	String[] available_lessons_string;
	Vector<Integer> available_lessons_end;
	String[] available_lessons_end_string;
	int selected_spinner_lesson_begin = 0;
	int selected_spinner_book_begin = 0;
	int selected_spinner_book_end = 0;
	int selected_spinner_level_begin = 0;
	Spinner spinnerLanguages;
	Spinner spinnerBookBegin;
	Spinner spinnerBookEnd;
	Spinner spinnerLessonBegin;
	Spinner spinnerLessonEnd;
	Spinner spinnerLevelBegin;
	Spinner spinnerLevelEnd;
	Spinner spinnerLearnMode;
	int lastPositionSpinnerLanguages;
	int lastPositionSpinnerBookBegin;
	int lastPositionSpinnerBookEnd;
	int lastPositionSpinnerLessonBegin;
	int lastPositionSpinnerLessonEnd;
	int lastPositionSpinnerLevelBegin;
	int lastPositionSpinnerLevelEnd;
	int lastPositionSpinnerLearnMode;
	int languagesIndex;
	
	Vector<Integer> levels;

	public static final String USER_PREFERENCE = "USER_PREFERENCES";
	public static final String TEMP_USER_PREFERENCE = "TEMP_USER_PREFERENCES";
	public static final String PREF_LANGUAGES = "PREF_PREF_LANGUAGES";
	public static final String PREF_BOOK_BEGIN = "PREF_BOOK_BEGIN";
	public static final String PREF_BOOK_END = "PREF_BOOK_END";
	public static final String PREF_LESSON_BEGIN = "PREF_LESSON_BEGIN";
	public static final String PREF_LESSON_END = "PREF_LESSON_END";
	public static final String PREF_LEVEL_BEGIN = "PREF_LEVEL_BEGIN";
	public static final String PREF_LEVEL_END = "PREF_LEVEL_END";
	public static final String PREF_LEARN_MODE = "PREF_LEARN_MODE";
	public static final String PREF_LANGUAGE_UPDATED = "PREF_LANGUAGE_UPDATED";
	public static final String PREF_RESULT_OK = "PREF_RESULT_OK";
	SharedPreferences prefs;
	SharedPreferences tempPrefs;
	private static String[] FROM = { _ID, BOOK, LESSON, CHARACTER, PHONETIC_SCRIPT, TRANSLATION, LEVEL };
    private static String ORDER_BY = _ID;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        GeneralUtils.setsLanguage(this);
        setContentView(R.layout.settings_layout);
        prefs = getSharedPreferences(USER_PREFERENCE, Activity.MODE_PRIVATE);
        tempPrefs = getSharedPreferences(TEMP_USER_PREFERENCE, Activity.MODE_PRIVATE);
        if(tempPrefs.getInt(PREF_RESULT_OK, 0) == 1)
        {
        	Settings.this.setResult(RESULT_OK);
        }
        else
        {
        	Settings.this.setResult(RESULT_CANCELED);
        }
        levels = new Vector<Integer>();
    	levels.add(new Integer(-2));
    	levels.add(new Integer(-1));
    	levels.add(new Integer(0));
    	levels.add(new Integer(1));
    	levels.add(new Integer(2));
        
        vocabularies = new VocabularyData(this);
        available_books = vocabularies.getBooks();
        available_books_string = Conversion.toStringArray(available_books);
        
        spinnerLanguages = (Spinner)findViewById(R.id.spinner_languages);
        spinnerBookBegin = (Spinner)findViewById(R.id.spinner_book_begin);
        spinnerBookEnd = (Spinner)findViewById(R.id.spinner_book_end);
        spinnerLessonBegin = (Spinner)findViewById(R.id.spinner_lesson_begin);
        spinnerLessonEnd = (Spinner)findViewById(R.id.spinner_lesson_end);
        spinnerLevelBegin = (Spinner)findViewById(R.id.spinner_level_begin);
        spinnerLevelEnd = (Spinner)findViewById(R.id.spinner_level_end);
        spinnerLearnMode = (Spinner)findViewById(R.id.spinner_learn_mode);
		final Intent moveVocabulariesIntent = new Intent(this, MoveVocabularies.class);
		final Intent warningIntent = new Intent(this, WarningReadVocabularies.class);
        final Intent updateIntent = new Intent(this, UpdateVocabularies.class);
        //if language has been updated, restore the temp settings
        //attention for later use: prefs = tempPrefs will not be reset in this onCreate()!
        if(tempPrefs.getInt(PREF_LANGUAGE_UPDATED, 0) == 1)
        {
        	prefs = tempPrefs;
        }
        populateSpinners();
        updateUIFromPreferences();
        if(prefs == tempPrefs)
        {
        	//set the PREF_LANGUAGE_UPDATED back to 0
    		Editor editor = tempPrefs.edit();
    		editor.putInt(PREF_LANGUAGE_UPDATED, 0);
    		editor.commit();
        }
        //set lastPositions of the Spinners
    	lastPositionSpinnerLanguages = spinnerLanguages.getSelectedItemPosition();
    	lastPositionSpinnerBookBegin = spinnerBookBegin.getSelectedItemPosition();
    	lastPositionSpinnerBookEnd = spinnerBookEnd.getSelectedItemPosition();
    	lastPositionSpinnerLessonBegin = spinnerLessonBegin.getSelectedItemPosition();
    	lastPositionSpinnerLessonEnd = spinnerLessonEnd.getSelectedItemPosition();
    	lastPositionSpinnerLevelBegin = spinnerLevelBegin.getSelectedItemPosition();
    	lastPositionSpinnerLevelEnd = spinnerLevelEnd.getSelectedItemPosition();
    	lastPositionSpinnerLearnMode = spinnerLearnMode.getSelectedItemPosition();

    	final Button OkButton = (Button) findViewById(R.id.OkButton);
        OkButton.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        		savePreferences();
        		Settings.this.setResult(RESULT_OK);
        		finish();

        	}
        });
        final Button CancelButton = (Button) findViewById(R.id.CancelButton);
        CancelButton.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        		finish();
        	}
        });
        final Button updateVocabulariesButton = (Button) findViewById(R.id.updateVocabularies);
        updateVocabulariesButton.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        		//open an alert box, asking for server data
            	startActivity(updateIntent);
        	}
        });
		final Button moveVocabulariesButton = (Button) findViewById(R.id.moveVocabularies);
		moveVocabulariesButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				savePreferences();
				//open an alert box with the MoveVocabularies-intent
				startActivity(moveVocabulariesIntent);
			}
		});
        final Button readVocabulariesButton = (Button) findViewById(R.id.readVocabularies);
        readVocabulariesButton.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        		//open an alert box warning
            	startActivity(warningIntent);
        	}
        });
        final Button writeVocabulariesButton = (Button) findViewById(R.id.writeVocabularies);
        writeVocabulariesButton.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        		writeVocabularies();
        	}
        });
        vocabularies.close();
	} //end on_create
	
	@Override
	public void onResume()
	{
		super.onResume();
	}

	private void updateUIFromPreferences()
	{
		languagesIndex = GeneralUtils.returnsLanguageIndexFromPreferences(this, prefs);
		int bookBeginIndex = prefs.getInt(PREF_BOOK_BEGIN, 0);
		int bookEndIndex = prefs.getInt(PREF_BOOK_END, 0);
		int lessonBeginIndex = prefs.getInt(PREF_LESSON_BEGIN, 0);
		int lessonEndIndex = prefs.getInt(PREF_LESSON_END, 0);
		int levelBeginIndex = prefs.getInt(PREF_LEVEL_BEGIN, 2);
		int levelEndIndex = prefs.getInt(PREF_LEVEL_END, 0);
		int learnModeIndex = prefs.getInt(PREF_LEARN_MODE, 0);
		spinnerLanguages.setSelection(languagesIndex);
		spinnerBookBegin.setSelection(bookBeginIndex);
		spinnerBookEnd.setSelection(bookEndIndex);
		spinnerLessonBegin.setSelection(lessonBeginIndex);
		spinnerLessonEnd.setSelection(lessonEndIndex);
		spinnerLevelBegin.setSelection(levelBeginIndex);
		spinnerLevelEnd.setSelection(levelEndIndex);
		spinnerLearnMode.setSelection(learnModeIndex);
	}
	
	private void savePreferences()
	{
		int languagesIndex = spinnerLanguages.getSelectedItemPosition();
		int bookBeginIndex = spinnerBookBegin.getSelectedItemPosition();
		int bookEndIndex = spinnerBookEnd.getSelectedItemPosition();
		int lessonBeginIndex = spinnerLessonBegin.getSelectedItemPosition();
		int lessonEndIndex = spinnerLessonEnd.getSelectedItemPosition();
		int levelBeginIndex = spinnerLevelBegin.getSelectedItemPosition();
		int levelEndIndex = spinnerLevelEnd.getSelectedItemPosition();
		int learnModeIndex = spinnerLearnMode.getSelectedItemPosition();
		Editor editor = prefs.edit();
		editor.putInt(PREF_LANGUAGES, languagesIndex);
		editor.putInt(PREF_BOOK_BEGIN, bookBeginIndex);
		editor.putInt(PREF_BOOK_END, bookEndIndex);
		editor.putInt(PREF_LESSON_BEGIN, lessonBeginIndex);
		editor.putInt(PREF_LESSON_END, lessonEndIndex);
		editor.putInt(PREF_LEVEL_BEGIN, levelBeginIndex);
		editor.putInt(PREF_LEVEL_END, levelEndIndex);
		editor.putInt(PREF_LEARN_MODE, learnModeIndex);
		editor.commit();
	}

	public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
	{
		if(parent == spinnerLanguages)
		{
	    	if(lastPositionSpinnerLanguages == position)
	    	{
	    		System.out.println("spinnerLanguages ungewollt");
	    	}
	    	else
	    	{
	    		System.out.println("spinnerLanguages gewollt");
				lastPositionSpinnerLanguages = position;
	    		String oldLanguage = getBaseContext().getResources().getConfiguration().locale.getLanguage();
		    	String languageToLoad  = (getResources().getStringArray(R.array.languagecodes))[position];
		    	if(!oldLanguage.equals(languageToLoad))
		    	{
		    		Locale locale = new Locale(languageToLoad);
		    		Locale.setDefault(locale);
		        	Configuration config = new Configuration();
		        	config.locale = locale;
		        	getBaseContext().getResources().updateConfiguration(config, 
		        		getBaseContext().getResources().getDisplayMetrics());
		        	//save only the settings of the languages to prefs
		        	Editor editor = prefs.edit();
		    		editor.putInt(PREF_LANGUAGES, position);
		    		editor.commit();
		    		//say that the language was updated and RESULT is OK
		    		editor = tempPrefs.edit();
		    		editor.putInt(PREF_LANGUAGE_UPDATED, 1);
		    		editor.putInt(PREF_RESULT_OK, 1);
		    		editor.commit();
		    		//the variable prefs should refer to the tmpPrefs object
		        	//to not modify the settings but to save the actual state
		        	//for the time after the refresh()
		        	prefs = tempPrefs;
		        	savePreferences();
		    		GeneralUtils.refresh(this);
		    	}
	    	}
		}
		else if(parent.equals(spinnerLevelBegin))
		{
			if(lastPositionSpinnerLevelBegin == position)
	    	{
				System.out.println("spinnerLevelBegin ungewollt");
	    	}
	    	else
	    	{
	    		System.out.println("spinnerLevelBegin gewollt");
	    		lastPositionSpinnerLevelBegin = position;
				//Populate the level_end_spinner
	    		populateLevelEndSpinner(position);
	    	}
		}
		else if(parent.equals(spinnerLessonBegin))
		{		
			if(lastPositionSpinnerLessonBegin == position)
	    	{
				System.out.println("spinnerLessonBegin ungewollt");
	    	}
	    	else
	    	{
	    		System.out.println("spinnerLessonBegin gewollt");
	    		lastPositionSpinnerLessonBegin = position;
	    		selected_spinner_book_begin = spinnerBookBegin.getSelectedItemPosition();
				selected_spinner_book_end = spinnerBookEnd.getSelectedItemPosition();
				// Populate the lesson_end_spinner
				populateLessonEndSpinner(selected_spinner_book_begin,
						selected_spinner_book_end, position);
	    	}
		}
		else if(parent.equals(spinnerBookBegin))
		{
			if(lastPositionSpinnerBookBegin== position)
	    	{
				System.out.println("spinnerBookBegin ungewollt");
	    	}
	    	else
	    	{
	    		System.out.println("spinnerBookBegin gewollt");
				lastPositionSpinnerBookBegin = position;
				selected_spinner_book_end = spinnerBookEnd.getSelectedItemPosition();
				selected_spinner_lesson_begin = spinnerLessonBegin.getSelectedItemPosition();
				// Populate the lesson_begin_spinner
				populateLessonBeginSpinner(position);
				
				// Populate the book_end_spinner
				populateBookEndSpinner(position);
				
				// Populate the lesson_end_spinner
				populateLessonEndSpinner(position,
						selected_spinner_book_end, selected_spinner_lesson_begin);
	    	}
		}
		else if(parent.equals(spinnerBookEnd))
		{		
			if(lastPositionSpinnerBookEnd == position)
	    	{
				System.out.println("spinnerBookEnd ungewollt");
	    	}
	    	else
	    	{
	    		System.out.println("spinnerBookEnd gewollt");
				lastPositionSpinnerBookEnd = position;
				selected_spinner_book_begin = spinnerBookBegin.getSelectedItemPosition();
				selected_spinner_lesson_begin = spinnerLessonBegin.getSelectedItemPosition();
				// Populate the lesson_end_spinner
				populateLessonEndSpinner(selected_spinner_book_begin,
						position, selected_spinner_lesson_begin);
	    	}
		}
	}
	
	public void onNothingSelected(AdapterView<?> parent) 
	{
			//do nothing
	}
		
	private void populateLanguageSpinner()
	{
		spinnerLanguages.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> lAdapter;
		lAdapter = ArrayAdapter.createFromResource(this,
				R.array.languages,
				android.R.layout.simple_spinner_item);
		lAdapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		spinnerLanguages.setAdapter(lAdapter);
	}
	
	private void populateBookBeginSpinner()
	{
		spinnerBookBegin.setOnItemSelectedListener(this);
		ArrayAdapter<String> bb = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				available_books_string);
		bb.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		spinnerBookBegin.setAdapter(bb);
	}
	
	private void populateLessonBeginSpinner(int selected_spinner_book_begin)
	{
		// Populate the lesson_begin_spinner
		available_lessons = vocabularies.getLessons(
				available_books.get(selected_spinner_book_begin).intValue());
        available_lessons_string = Conversion.toStringArray(available_lessons);
		spinnerLessonBegin.setOnItemSelectedListener(this);
		ArrayAdapter<String> lb = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				available_lessons_string);
		lb.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		spinnerLessonBegin.setAdapter(lb);
	}
	
	private void populateBookEndSpinner(int selected_spinner_book_begin)
	{
		// Populate the book_end_spinner
		available_books_end = Conversion.toVector(available_books.subList(
				selected_spinner_book_begin, available_books.size()));
		available_books_end_string = Conversion.toStringArray(available_books_end);
		spinnerBookEnd.setOnItemSelectedListener(this);
		ArrayAdapter<String> be = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				available_books_end_string);
		be.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		spinnerBookEnd.setAdapter(be);
	}
	
	private void populateLessonEndSpinner(int selected_spinner_book_begin,
			int selected_spinner_book_end, int selected_spinner_lesson_begin)
	{
		//wenn entweder beide oder nur das letzte buch geaendert werden: immer letzte lektion anzeigen
		//wenn nur lessonBegin geaendert wird, ggf. beibehalten
//		int selected_spinner_level_end = spinnerLevelEnd.getSelectedItemPosition();
//		int number_of_items = spinnerLevelEnd.getCount();
//		int position_from_back = number_of_items - selected_spinner_level_end;
		
		// Populate the lesson_end_spinner
		if(selected_spinner_book_end == 0)
		{
			//book begin and book end are the same
			available_lessons = vocabularies.getLessons(available_books.get(
					selected_spinner_book_begin));
			available_lessons_end = Conversion.toVector(available_lessons.subList(
					selected_spinner_lesson_begin, available_lessons.size()));
		}
		else
		{
			//book begin and book end are different
			available_books_end = Conversion.toVector(available_books.subList(
					selected_spinner_book_begin, available_books.size()));
			available_lessons_end = vocabularies.getLessons(available_books_end.get(
					selected_spinner_book_end));
		}
		available_lessons_end_string = Conversion.toStringArray(available_lessons_end);
		spinnerLessonEnd.setOnItemSelectedListener(this);
		ArrayAdapter<String> le = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				available_lessons_end_string);
		le.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		spinnerLessonEnd.setAdapter(le);
		
//		number_of_items = spinnerLevelEnd.getCount();
//		if(number_of_items - position_from_back > 0)
//		spinnerLevelEnd.setSelection(number_of_items - position_from_back);
	}
		
	private void populateLevelBeginSpinner()
	{
		String[] stringLevels;
		stringLevels = Conversion.toStringArray(levels);
		spinnerLevelBegin.setOnItemSelectedListener(this);
		ArrayAdapter<String> levelBegin = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				stringLevels);
		levelBegin.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		spinnerLevelBegin.setAdapter(levelBegin);
	}
	
	private void populateLevelEndSpinner(int selected_spinner_level_begin)
	{
		int selected_spinner_level_end = spinnerLevelEnd.getSelectedItemPosition();
		int number_of_items = spinnerLevelEnd.getCount();
		int position_from_back = number_of_items - selected_spinner_level_end;
		
		Vector<Integer> levelsEnd;
		String[] stringLevelsEnd;
		levelsEnd = Conversion.toVector(levels.subList(selected_spinner_level_begin, levels.size()));
		stringLevelsEnd = Conversion.toStringArray(levelsEnd);
		//Populate the level_end_spinner
		spinnerLevelEnd.setOnItemSelectedListener(this);
		ArrayAdapter<String> levelEnd = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				stringLevelsEnd);
		levelEnd.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		spinnerLevelEnd.setAdapter(levelEnd);
		
		number_of_items = spinnerLevelEnd.getCount();
		if(number_of_items - position_from_back > 0)
		spinnerLevelEnd.setSelection(number_of_items - position_from_back);
	}
		
	private void populateLearnModeSpinner()
	{
		spinnerLearnMode.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> lmAdapter;
		lmAdapter = ArrayAdapter.createFromResource(this,
				R.array.learn_mode,
				android.R.layout.simple_spinner_item);
		lmAdapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		spinnerLearnMode.setAdapter(lmAdapter);
	}
	
	public void populateSpinners()
	{		
		// Populate the languages spinner
		populateLanguageSpinner();
		
		// Populate the book_begin_spinner
		populateBookBeginSpinner();
//		selected_spinner_book_begin = spinnerBookBegin.getSelectedItemPosition();
		selected_spinner_book_begin = prefs.getInt(PREF_BOOK_BEGIN, 0);
		
		//Populate the lesson_begin_spinner
		populateLessonBeginSpinner(selected_spinner_book_begin);
//		selected_spinner_lesson_begin = spinnerLessonBegin.getSelectedItemPosition();
		selected_spinner_lesson_begin = prefs.getInt(PREF_LESSON_BEGIN, 0);
		
		//Populate the book_end_spinner
		populateBookEndSpinner(selected_spinner_book_begin);
//		selected_spinner_book_end = spinnerBookEnd.getSelectedItemPosition();
		selected_spinner_book_end = prefs.getInt(PREF_BOOK_END, 0);
		
		//Populate the lesson_end_spinner
		populateLessonEndSpinner(selected_spinner_book_begin,
				selected_spinner_book_end, selected_spinner_lesson_begin);
		
		//Populate the level_begin_spinner
		populateLevelBeginSpinner();
//		selected_spinner_level_begin = spinnerLevelBegin.getSelectedItemPosition();
		selected_spinner_level_begin = prefs.getInt(PREF_LEVEL_BEGIN, 2);
		
		//Populate the level_end_spinner
		populateLevelEndSpinner(selected_spinner_level_begin);
		
		// Populate the learn_mode spinner
		populateLearnModeSpinner();
	}
		
	public void writeVocabularies() 
	{
		//writes vocabularyTrainerBackup.txt in the root directory of the SDCard
		try
		{
			File f = new File(Environment.getExternalStorageDirectory()+"/VocabularyTrainerBackup.txt");
			
			//tuncates file f
			FileOutputStream fileOS = new FileOutputStream(f);
			OutputStreamWriter out=
				new OutputStreamWriter(fileOS);
			out.write("//VocabularyTrainer Backup File\n"+
						"//\n"+
						"//to use this file to restore this backup\n"+
						"//rename it to vocabularies_list.txt\n"+
						"//and copy it to the root directory of the SDCard\n"+
						"//of your Android device\n"+
						"//\n"+
						"//one row stands for one vocabulary\n"+
						"//please don't leave any white space\n"+
						"//\n"+
						"//book|lesson|character|phonetic script|translation|level\n"+
						"//book, lesson and level have to be numeric values\n"+
						"//-2 < level < 2\n"+
						"//\n"+
						"//---------------------------------\n");
			
			SQLiteDatabase db = vocabularies.getReadableDatabase();
	    	Cursor cursor = db.query(TABLE_NAME, FROM, "", null, null,
	    			null, ORDER_BY);
	    	startManagingCursor(cursor);
		
	    	int id = 0;
	    	int columnIndex = 1;
	    	int prevBook = 0;
	    	int prevLesson = 0;
	    	while(id < cursor.getCount())
	    	{
	    		cursor.moveToPosition(id);
		    	if(prevBook != Integer.parseInt(cursor.getString(1))
		    			|| prevLesson != Integer.parseInt(cursor.getString(2)))
    			{
		    		out.write("//\n");
    			}
	    		prevBook = Integer.parseInt(cursor.getString(1));
		    	prevLesson = Integer.parseInt(cursor.getString(2));
		    	columnIndex = 1;
	    		while(columnIndex <= 6)
		    	{
		    		out.write(cursor.getString(columnIndex));
		    		if(columnIndex < 6)
		    		{
		    			out.write("|");
		    		}
		    		columnIndex++;
		    	}
		    	out.write("\n");
		    	id++;
	    	}
	    	
			out.close();
		}
		catch (Throwable t)
		{
			Toast
			.makeText(this, "Exception: "+t.toString(), 2000)
			.show();
		}
	}

	private void debugPrintSpinnerPositions()
	{
		System.out.println("@Debug: spinner-positions");
		System.out.println("spinnerLanguages: " + spinnerLanguages.getSelectedItemPosition());
		System.out.println("spinnerBookBegin: " + spinnerBookBegin.getSelectedItemPosition());
		System.out.println("spinnerBookEnd: " + spinnerBookEnd.getSelectedItemPosition());
		System.out.println("spinnerLessonBegin: " + spinnerLessonBegin.getSelectedItemPosition());
		System.out.println("spinnerLessonEnd: " + spinnerLessonEnd.getSelectedItemPosition());
		System.out.println("spinnerLevelBegin: " + spinnerLevelBegin.getSelectedItemPosition());
		System.out.println("spinnerLevelEnd: " + spinnerLevelEnd.getSelectedItemPosition());
		System.out.println("spinnerLearnMode: " + spinnerLearnMode.getSelectedItemPosition());
	}
}