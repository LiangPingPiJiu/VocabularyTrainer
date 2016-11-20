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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.text.InputType;

import com.tobias.vocabulary_trainer.data.VocabularyData;

public class UpdateVocabularies extends Activity
{
	public static final String USER_PREFERENCE = "USER_PREFERENCES";
	public static final String PREF_SERVER_ADDRESSE = "PREF_SERVER_ADDRESSE";
	public static final String PREF_SERVER_USERNAME = "PREF_SERVER_USERNAME";
	public static final String PREF_SERVER_PASSWORD = "PREF_SERVER_PASSWORD";
	public static final String PREF_SERVER_PORT = "PREF_SERVER_PORT";
	public static final String PREF_SERVER_FILE = "PREF_SERVER_FILE";
	public static final String PREF_LOCAL_FILE = "PREF_LOCAL_FILE";
	private VocabularyData vocabularies;
	SharedPreferences prefs;
	EditText serverAdresseEditText;
	EditText serverUsernameEditText;
	EditText serverPasswordEditText;
	EditText serverPortEditText;
	EditText serverFileEditText;
	EditText localFileEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_vocabularies_layout);
		prefs = getSharedPreferences(USER_PREFERENCE, Activity.MODE_PRIVATE);
		serverAdresseEditText = (EditText)findViewById(R.id.server_adresse_edit_text);
		serverUsernameEditText = (EditText)findViewById(R.id.server_username_edit_text);
		serverPasswordEditText = (EditText)findViewById(R.id.server_password_edit_text);
		serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
		serverFileEditText = (EditText)findViewById(R.id.server_file_edit_text);
		localFileEditText = (EditText)findViewById(R.id.local_file_edit_text);
		
		vocabularies = new VocabularyData(this);
		System.out.println("before updateUIFromPreferences();");
		updateUIFromPreferences();
		System.out.println("after updateUIFromPreferences();");
		
		final Button ServerOkButton = (Button) findViewById(R.id.server_ok);
		ServerOkButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	System.out.println("ServerOKButton pressed");
            	savePreferences();
            	InputStream in;
            	String serverAdresse = serverAdresseEditText.getText().toString();
        		String serverUsername = serverUsernameEditText.getText().toString();
        		String serverPassword = serverPasswordEditText.getText().toString();
        		int serverPort = Integer.parseInt(serverPortEditText.getText().toString());
        		String serverFile = serverFileEditText.getText().toString();
            	
            	FTPClient ftp;
            	ftp = new FTPClient();
            	try
                {
                  int reply;
                  System.out.println("try to connect to ftp server");
                  ftp.connect(serverAdresse, serverPort);
                  System.out.print(ftp.getReplyString());
                  // After connection attempt, you should check the reply code to verify
                  // success.
                  reply = ftp.getReplyCode();
                  if(FTPReply.isPositiveCompletion(reply))
                  {
                	  System.out.println("connected to ftp server");
                  }
                  else
                  {
                	  ftp.disconnect();
                      System.out.println("FTP server refused connection.");  
                  }
                  
                  // transfer files
                  System.out.println("try to login");
                  ftp.login(serverUsername, serverPassword);
                  System.out.println("current working directory: " + ftp.printWorkingDirectory());
                  System.out.println("try to start downloading");
                  in = ftp.retrieveFileStream(serverFile);
                  // files transferred
                  //write to database and textfile on sdcard
                  vocabularies.readVocabularies(in, false);
                  ftp.logout();
                }
                catch(IOException e)
                {
                  e.printStackTrace();
                }
                finally
                {
                  if(ftp.isConnected())
                  {
                    try
                    {
                      ftp.disconnect();
                    }
                    catch(IOException ioe)
                    {
                      // do nothing
                    }
                  }
                }
//            	settings.populateSpinners();
            	finish();
            }
        });
		final Button LocalFileOkButton = (Button) findViewById(R.id.local_file_ok);
		LocalFileOkButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	File f = new File(Environment.getExternalStorageDirectory(),
            			localFileEditText.getText().toString());
            	savePreferences();
            	vocabularies.readVocabularies(f, false);
//            	settings.populateSpinners();
            	finish();
            }
        });
        final Button CancelButton = (Button) findViewById(R.id.Cancel);
        CancelButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	finish();
            }
        });
        vocabularies.close();
	}	//end on_create
	private void updateUIFromPreferences()
	{
		System.out.println("called updateUIFromPreferences();");
		String serverAdresse = prefs.getString(PREF_SERVER_ADDRESSE,
				getString(R.string.serverExampleAddress));
		String serverUsername = prefs.getString(PREF_SERVER_USERNAME,
				getString(R.string.serverExampleUsername));
		String serverPassword = prefs.getString(PREF_SERVER_PASSWORD,
				getString(R.string.serverExamplePassword));
		String serverPort = Integer.toString(prefs.getInt(PREF_SERVER_PORT,
				21));
		String serverFile = prefs.getString(PREF_SERVER_FILE,
				getString(R.string.serverExampleFilePath));
		String localFile = prefs.getString(PREF_LOCAL_FILE,
				getString(R.string.localeExampleFilePath));
		System.out.println("updateUIFromPreferences();: einstellungen eingelesen");
		serverAdresseEditText.setText(serverAdresse);
		serverUsernameEditText.setText(serverUsername);
		serverPasswordEditText.setText(serverPassword);
		serverPortEditText.setText(serverPort);
		serverFileEditText.setText(serverFile);
		localFileEditText.setText(localFile);
	}
	private void savePreferences()
	{
		Editor editor = prefs.edit();
		String serverAdresse = serverAdresseEditText.getText().toString();
		String serverUsername = serverUsernameEditText.getText().toString();
		String serverPassword = serverPasswordEditText.getText().toString();
		int serverPort = Integer.parseInt(serverPortEditText.getText().toString());
		String serverFile = serverFileEditText.getText().toString();
		String localFile = localFileEditText.getText().toString();
		editor.putString(PREF_SERVER_ADDRESSE, serverAdresse);
		editor.putString(PREF_SERVER_USERNAME, serverUsername);
		editor.putString(PREF_SERVER_PASSWORD, serverPassword);
		editor.putInt(PREF_SERVER_PORT, serverPort);
		editor.putString(PREF_SERVER_FILE, serverFile);
		editor.putString(PREF_LOCAL_FILE, localFile);
		editor.commit();
	}
}