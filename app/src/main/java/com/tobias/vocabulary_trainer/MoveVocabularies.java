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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.tobias.vocabulary_trainer.data.VocabularyData;

import static com.tobias.vocabulary_trainer.R.array.vocabulary_boxes;


public class MoveVocabularies extends Activity implements android.widget.AdapterView.OnItemSelectedListener
{
    private VocabularyData vocabularies;
    Spinner spinnerMoveTargetBox;
    int selectedTargetBox = 0;
    SharedPreferences prefs;
    private static String WHERE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_vocabularies_layout);

        vocabularies = new VocabularyData(this);
        spinnerMoveTargetBox = (Spinner)findViewById(R.id.move_target_box);

        populateSpinnerMoveTargetBox();


        final Button OkButton = (Button) findViewById(R.id.button_move_vocabularies_ok);
        OkButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                prefs = getSharedPreferences(Settings.USER_PREFERENCE, Activity.MODE_PRIVATE);
                WHERE = vocabularies.getWhereClause(prefs);
                //@debug:
                System.out.println("move vocabularies to new box:");
                System.out.println("WHERE: " + WHERE);
                System.out.println("selectedTargetBox: " + selectedTargetBox);
                vocabularies.moveSelectedVocabularies(WHERE, selectedTargetBox);

                finish();
            }
        });
        final Button CancelButton = (Button) findViewById(R.id.button_move_vocabularies_cancel);
        CancelButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });




    }


    private void populateSpinnerMoveTargetBox()
    {
        spinnerMoveTargetBox.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> lmAdapter;
        lmAdapter = ArrayAdapter.createFromResource(this, vocabulary_boxes , android.R.layout.simple_spinner_item);
        lmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoveTargetBox.setAdapter(lmAdapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long l)
    {
        if(parent == spinnerMoveTargetBox)
        {
            selectedTargetBox = position;
            System.out.println("Box Nummer " + selectedTargetBox + " ausgewaehlt.");
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}



