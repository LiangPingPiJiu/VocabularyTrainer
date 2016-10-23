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


//cursor.close() alle 2x auskommentiert

package com.tobias.vocabulary_trainer;

import static android.provider.BaseColumns._ID;
import static com.tobias.vocabulary_trainer.Constants.BOOK;
import static com.tobias.vocabulary_trainer.Constants.CHARACTER;
import static com.tobias.vocabulary_trainer.Constants.LESSON;
import static com.tobias.vocabulary_trainer.Constants.LEVEL;
import static com.tobias.vocabulary_trainer.Constants.PHONETIC_SCRIPT;
import static com.tobias.vocabulary_trainer.Constants.TRANSLATION;

import java.io.File;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tobias.vocabulary_trainer.data.VocabularyData;
import com.tobias.vocabulary_trainer.utils.GeneralUtils;

public class VocabularyTrainer extends Activity
{
	boolean draw = true;
	String character, phonetic_script, translation;
	int current_vocabulary_id;
	TextView PhoneticScriptTextView;
	private static String[] FROM = { _ID, BOOK, LESSON, CHARACTER, PHONETIC_SCRIPT, TRANSLATION, LEVEL };
    private static String ORDER_BY = _ID;
    private static String WHERE = "";
	public VocabularyData vocabularies;
	SharedPreferences prefs;
	SharedPreferences tempPrefs;
	
	private Vector<Integer> vocList = new Vector<Integer>();
	
	int numberOfVocabularies;
	private static final int SHOW_SETTINGS = 1;
	Cursor cursor;
	Cursor testCursor;
	boolean readVocabularies;
	boolean onlyWatch = false;
	Intent noVocabularies;
	Intent noVocabulariesList;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	GeneralUtils.setsLanguage(this);
        setContentView(R.layout.main);
        final Intent settingsIntent = new Intent(this, Settings.class);
        vocabularies = new VocabularyData(this);
        noVocabularies = new Intent(this, ErrorNoVocabularies.class);
        noVocabulariesList = new Intent(this, ErrorNoVocabulariesList.class);
        PhoneticScriptTextView = (TextView) findViewById(R.id.phonetic_script);
        prefs = getSharedPreferences(Settings.USER_PREFERENCE, Activity.MODE_PRIVATE);
        tempPrefs = getSharedPreferences(Settings.TEMP_USER_PREFERENCE, Activity.MODE_PRIVATE);
        onlyWatch = onlyWatch();
        
        final File databaseFile = new File(Environment.getDataDirectory() +
        		"/data/com.tobias.vocabulary_trainer/databases", "vocabularyData.db");
        if(databaseFile.exists() == false)
        {
        	File f = new File(Environment.getExternalStorageDirectory(),VocabularyData.VOCABULARIES_LIST);
        	if(f.exists() == false)
        	{
            	startActivity(noVocabulariesList);
            	finish();
        	}
        	else
        	{
        		vocabularies.readVocabularies(f, true);
        	}
        }
        if(databaseFile.exists() == true)
        {
        	WHERE = this.vocabularies.getWhereClause(prefs);
            cursor = vocabularies.getVocabularies(FROM, WHERE, ORDER_BY);
            testCursor = vocabularies.getVocabularies(FROM, WHERE, ORDER_BY);
            numberOfVocabularies = cursor.getCount();
            //fill the vocabularyList
            vocList = new Vector<Integer>();
            for(int i = 0; i < numberOfVocabularies; i++)
            {
            	vocList.add(new Integer(i));
            }
            current_vocabulary_id = (int) (Math.random()*(numberOfVocabularies)); // random integer between 0 and numberOfVocabularies, eig. muesste es (numberOfVocabularies + 1) heissen, ka warum, 1.0 scheint bei Math.random doch includiert zu sein
            
            //set first translationTextView    	
            if(numberOfVocabularies > 0)
            {
            	translation = vocabularies.getTranslation(cursor, current_vocabulary_id);
            	TextView TranslationTextView = (TextView) findViewById(R.id.translation);
            	TranslationTextView.setText(translation);
            	if(onlyWatch == true)
            	{
            		//change Button titles
                	TextView button01text = (TextView) findViewById(R.id.Button01);
                	button01text.setText(R.string.Button_continue);
                	
                	TextView button02text = (TextView) findViewById(R.id.Button02);
                	button02text.setText(R.string.Button_delete);
                	
            		//set the phoneticScript and the Character, too
            		//show Character
                	character = vocabularies.getCharacter(cursor, current_vocabulary_id);
                	TextView CharacterTextView = (TextView) findViewById(R.id.character);
                	CharacterTextView.setText(character);
                	
                	//show phonetic script
                    phonetic_script = vocabularies.getPhoneticScript(cursor, current_vocabulary_id);
                	TextView PhoneticScriptTextView = (TextView) findViewById(R.id.phonetic_script);
                	PhoneticScriptTextView.setText(phonetic_script);
            	}
            }
            else
            {
            	//open an alert box and start the Settings activity
    			startActivityForResult(settingsIntent, SHOW_SETTINGS);
    			startActivity(noVocabularies);
            }
        }
        
        //buttons
        final Button button01 = (Button) findViewById(R.id.Button01);
        button01.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
        		if(onlyWatch == true)
            	{
            		vocList.remove(new Integer(current_vocabulary_id));
                	if(vocList.isEmpty())
                	{
                		cursor = vocabularies.getVocabularies(FROM, WHERE, ORDER_BY);
                		numberOfVocabularies = cursor.getCount();
                        if(numberOfVocabularies == 0)
                        {
                        	//open an alert box and start the Settings activity
                        	startActivityForResult(settingsIntent, SHOW_SETTINGS);
                        	startActivity(noVocabularies);
                        }
                        //refill the vocabularyList
                        vocList = new Vector<Integer>();
                        for(int i = 0; i < numberOfVocabularies; i++)
                        {
                        	vocList.add(new Integer(i));
                        }
                	}
                	if(numberOfVocabularies > 0)
                	{
                		current_vocabulary_id = (int) (Math.random()*(numberOfVocabularies));
                    	while(vocList.contains(current_vocabulary_id) == false)
                    	{
                    		current_vocabulary_id = (int) (Math.random()*(numberOfVocabularies));
                    	}
                	}
                	//delete canvas
            		DrawCanvas drawCanvas = (DrawCanvas) findViewById(R.id.DrawCanvas);
            		drawCanvas.dotsList.first = null;
            		drawCanvas.invalidate();
                
            		if(numberOfVocabularies > 0)
                	{
                    	cursor.moveToPosition(current_vocabulary_id);
                    	translation = vocabularies.getTranslation(cursor, current_vocabulary_id);
                    	TextView TranslationTextView = (TextView) findViewById(R.id.translation);                    	
                    	TranslationTextView.setText(translation);
                    	//show Character
                    	character = vocabularies.getCharacter(cursor, current_vocabulary_id);
                    	TextView CharacterTextView = (TextView) findViewById(R.id.character);
                    	CharacterTextView.setText(character);
                    	//show phonetic script
                        phonetic_script = vocabularies.getPhoneticScript(cursor, current_vocabulary_id);
                    	TextView PhoneticScriptTextView = (TextView) findViewById(R.id.phonetic_script);
                    	PhoneticScriptTextView.setText(phonetic_script);
                	}
            	}//end onlyWatch
            	else
            	{
            		if(draw == true) //Bottom click "ready"
                    {
                    	//change Button titles
                    	TextView button01text = (TextView) findViewById(R.id.Button01);
                    	button01text.setText(R.string.Button_right);
                    	
                    	TextView button02text = (TextView) findViewById(R.id.Button02);
                    	button02text.setText(R.string.Button_false);
                    	
                    	//show Character
                    	character = vocabularies.getCharacter(cursor, current_vocabulary_id);
                    	TextView CharacterTextView = (TextView) findViewById(R.id.character);
                    	CharacterTextView.setText(character);
                    	
                    	//show phonetic script
                        phonetic_script = vocabularies.getPhoneticScript(cursor, current_vocabulary_id);
                    	TextView PhoneticScriptTextView = (TextView) findViewById(R.id.phonetic_script);
                    	PhoneticScriptTextView.setText(phonetic_script);
                    	
                    	draw = false;
                    }
                    else //Bottom click "right"
                    {
                    	//raise vocabulary level if current level < 2
                    	vocabularies.raiseVocabularyLevel(cursor, current_vocabulary_id);
                    	vocList.remove(new Integer(current_vocabulary_id));
                    	if(vocList.isEmpty())
                    	{
                    		cursor = vocabularies.getVocabularies(FROM, WHERE, ORDER_BY);
                            numberOfVocabularies = cursor.getCount();
                            if(numberOfVocabularies == 0)
                            {
                            	//open an alert box and start the Settings activity
                            	startActivityForResult(settingsIntent, SHOW_SETTINGS);
                            	startActivity(noVocabularies);
                            }
                            //refill the vocabularyList
                            vocList = new Vector<Integer>();
                            for(int i = 0; i < numberOfVocabularies; i++)
                            {
                            	vocList.add(new Integer(i));
                            }
                    	}
                    	if(numberOfVocabularies > 0)
                    	{
                    		current_vocabulary_id = (int) (Math.random()*(numberOfVocabularies));
                        	while(vocList.contains(current_vocabulary_id) == false)
                        	{
                        		current_vocabulary_id = (int) (Math.random()*(numberOfVocabularies));
                        	}
                    	}
                    	//delete canvas
                		DrawCanvas drawCanvas = (DrawCanvas) findViewById(R.id.DrawCanvas);
                		drawCanvas.dotsList.first = null;
                		drawCanvas.invalidate();
                    	
                    	//change Button titles
                    	TextView button01text = (TextView) findViewById(R.id.Button01);
                    	button01text.setText(R.string.Button_ready);
                    	
                    	TextView button02text = (TextView) findViewById(R.id.Button02);
                    	button02text.setText(R.string.Button_delete);
                    	
                    	//show Character
                    	character = "";
                    	TextView CharacterTextView = (TextView) findViewById(R.id.character);
                    	CharacterTextView.setText(character);
                    	
                    	//show phonetic script
                    	phonetic_script = "";
                    	TextView PhoneticScriptTextView = (TextView) findViewById(R.id.phonetic_script);
                    	PhoneticScriptTextView.setText(phonetic_script);
                    	
                    	if(numberOfVocabularies > 0)
                    	{
                    		//show translation
                    		translation = vocabularies.getTranslation(cursor, current_vocabulary_id);
                        	TextView TranslationTextView = (TextView) findViewById(R.id.translation);
                        	TranslationTextView.setText(translation);
                    	}
                    	draw = true;
                    }
            	}
            }
        });
        
        final Button button02 = (Button) findViewById(R.id.Button02);
        button02.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // Perform action on click
            	if(onlyWatch == true)
            	{
            		//delete canvas
            		DrawCanvas drawCanvas = (DrawCanvas) findViewById(R.id.DrawCanvas);
            		drawCanvas.dotsList.first = null;
            		drawCanvas.invalidate();
            	}
            	else
            	{
            		if(draw == true) //Bottom click "delete"
                	{
                		//delete canvas
                		DrawCanvas drawCanvas = (DrawCanvas) findViewById(R.id.DrawCanvas);
                		drawCanvas.dotsList.first = null;
                		drawCanvas.invalidate();
                	}
                	else //Bottom click "false"
                	{
                		//lower vocabulary level if current level > -2
                    	vocabularies.lowerVocabularyLevel(cursor, current_vocabulary_id);
                    	vocList.remove(new Integer(current_vocabulary_id));
                    	if(vocList.isEmpty())
                    	{
                    		cursor = vocabularies.getVocabularies(FROM, WHERE, ORDER_BY);
                            numberOfVocabularies = cursor.getCount();
                            if(numberOfVocabularies == 0)
                            {
                            	//open an alert box and start the Settings activity
                            	startActivityForResult(settingsIntent, SHOW_SETTINGS);
                            	startActivity(noVocabularies);
                            }
                            //refill the vocabularyList
                            vocList = new Vector<Integer>();
                            for(int i = 0; i < numberOfVocabularies; i++)
                            {
                            	//vocabularyList.addAtBegin(i);
                            	vocList.add(new Integer(i));
                            }
                    	}
                    	if(numberOfVocabularies > 0)
                    	{
                    		current_vocabulary_id = (int) (Math.random()*(numberOfVocabularies));
                        	while(vocList.contains(current_vocabulary_id) == false)
                        	{
                        		current_vocabulary_id = (int) (Math.random()*(numberOfVocabularies));
                        	}
                    	}

                		//delete canvas
                		DrawCanvas drawCanvas = (DrawCanvas) findViewById(R.id.DrawCanvas);
                		drawCanvas.dotsList.first = null;
                		drawCanvas.invalidate();
                		
                		//change Button titles
                    	TextView button01text = (TextView) findViewById(R.id.Button01);
                    	button01text.setText(R.string.Button_ready);
                    	
                    	TextView button02text = (TextView) findViewById(R.id.Button02);
                    	button02text.setText(R.string.Button_delete);
                    	
                    	//show Character
                    	character = "";
                    	TextView CharacterTextView = (TextView) findViewById(R.id.character);
                    	CharacterTextView.setText(character);
                    	
                    	//show phonetic script
                    	phonetic_script = "";
                    	TextView PhoneticScriptTextView = (TextView) findViewById(R.id.phonetic_script);
                    	PhoneticScriptTextView.setText(phonetic_script);
                    	
                    	if(numberOfVocabularies > 0)
                    	{
                    		//show translation
                    		translation = vocabularies.getTranslation(cursor, current_vocabulary_id);
                    		TextView TranslationTextView = (TextView) findViewById(R.id.translation);
                    		TranslationTextView.setText(translation);
                    	}
                    	draw = true;
                	}
            	}
            }
        });
//        vocabularies.close();
        
//        cursor.close();
        
    } //end OnCreate
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    }
    
    private boolean onlyWatch()
    {
    	boolean onlyWatch = false;
    	int learnModeIndex = prefs.getInt(Settings.PREF_LEARN_MODE, 0);
    	if(learnModeIndex == 0)
    	{
    		onlyWatch = true;
    	}
    	return onlyWatch;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	super.onCreateOptionsMenu(menu);
    	menu.add(getString(R.string.menu_settings));
    	menu.add(getString(R.string.menu_info));
    	menu.add(getString(R.string.menu_statistics));
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	String title = item.getTitle().toString();
    	{
    		if(title.equals(getString(R.string.menu_settings)))
    		{
    			Intent i = new Intent(this, Settings.class);
    			startActivityForResult(i, SHOW_SETTINGS);
    			return true;
    		}
    		if(title.equals(getString(R.string.menu_info)))
    		{
    			Intent i = new Intent(this, Info.class);
    			startActivity(i);
    			return true;
    		}
    		if(title.equals(getString(R.string.menu_statistics)))
    		{	
    			Intent i = new Intent(this, Statistics.class);
    			startActivity(i);
    			return true;
    		}
    	}
    	return false;
    }  

    protected void onActivityResult(int requestCode, int resultCode,
            Intent data)
    {
        if (requestCode == SHOW_SETTINGS)
        {
            if (resultCode == RESULT_OK)
            {
            	System.out.println("OK");
            	//reset the RESULT of Settings
	    		Editor editor = tempPrefs.edit();
	    		editor.putInt(Settings.PREF_RESULT_OK, 0);
	    		editor.commit();
            	GeneralUtils.refresh(this);
            }
            else if (resultCode == RESULT_CANCELED)
            {
            	System.out.println("CANCELD");
            }
        }
    }
}