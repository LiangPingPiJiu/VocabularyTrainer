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

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.tobias.vocabulary_trainer.data.VocabularyData;

public class WarningReadVocabularies extends Activity
{
	private VocabularyData vocabularies;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warning_read_vocabularies_layout);
		
		vocabularies = new VocabularyData(this);
		
		final Button OkButton = (Button) findViewById(R.id.OK);
        OkButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	File f = new File(Environment.getExternalStorageDirectory(),VocabularyData.VOCABULARIES_LIST);
            	vocabularies.readVocabularies(f, true);
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
	}	
}