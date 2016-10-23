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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ErrorNoVocabulariesList extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.error_no_vocabularies_list_layout);
		
		final Button Okbutton = (Button) findViewById(R.id.OK);
        Okbutton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	finish();
            }
        });
	}
}