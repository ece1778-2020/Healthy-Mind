package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ManualMedication extends AppCompatActivity {

    Button addBtn, delBtn;
    Context context;

    View hline2, hline3, hline4;

    TextView name2, name3, name4;

    int medicationCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_medication);
        addBtn = (Button) findViewById(R.id.add_m_btn);
        delBtn = (Button) findViewById(R.id.del);

        hline2 = findViewById(R.id.hline2);
        hline3 = findViewById(R.id.hline4);
        hline4 = findViewById(R.id.hline6);

        name2 = findViewById(R.id.n1);
        name3 = findViewById(R.id.n2);
        name4 = findViewById(R.id.n3);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medicationCount == 4) {
                    addBtn.setEnabled(false);
                    Toast.makeText(ManualMedication.this, "You can only add four medications at one form.", Toast.LENGTH_SHORT).show();
                } else {
                    medicationCount++;
                    if (medicationCount == 2) {
                        hline2.setVisibility(View.VISIBLE);
                        name2.setVisibility(View.VISIBLE);

                    } else if (medicationCount == 3) {
                        hline3.setVisibility(View.VISIBLE);
                        name3.setVisibility(View.VISIBLE);

                    } else if (medicationCount == 4) {
                        hline4.setVisibility(View.VISIBLE);
                        name4.setVisibility(View.VISIBLE);

                    }
                }

                Toast.makeText(ManualMedication.this, Integer.toString(medicationCount) , Toast.LENGTH_SHORT).show();
            }
        });


        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medicationCount == 1) {
                    delBtn.setEnabled(false);
                    Toast.makeText(ManualMedication.this, "You should enter at least one medication.", Toast.LENGTH_SHORT).show();
                } else {
                    medicationCount--;
                }

                Toast.makeText(ManualMedication.this, Integer.toString(medicationCount) , Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void onClick_next_page(View view) {
        Intent intent = new Intent(this, ManualFeel.class);
        startActivity(intent);
    }
}
