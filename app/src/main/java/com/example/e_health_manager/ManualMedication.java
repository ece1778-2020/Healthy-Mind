package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ManualMedication extends AppCompatActivity {

    Button addBtn, delBtn;
    Context context;

    View hline0, hline1, hline2, hline3, hline4;

    TextView name1, name2, name3, name4, m1, m2, m3, m0;

    TextView c1, c2, c3, c4;
    TextView c5, c6, c7, c8;
    TextView c9, c10, c11, c12;
    TextView c13, c14, c15, c16;

    EditText n1_input, n2_input, n3_input, n0_input;
    EditText m1_input, m2_input, m3_input, m0_input;

    CheckBox c1_input, c2_input, c3_input, c4_input;
    CheckBox c5_input, c6_input, c7_input, c8_input;
    CheckBox c9_input, c10_input, c11_input, c12_input;
    CheckBox c13_input, c14_input, c15_input, c16_input;

    int medicationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_medication);
        addBtn = (Button) findViewById(R.id.add_m_btn);
        delBtn = (Button) findViewById(R.id.del);

        hline0 = findViewById(R.id.hline0);
        hline1 = findViewById(R.id.hline);

        hline2 = findViewById(R.id.hline2);
        hline3 = findViewById(R.id.hline4);
        hline4 = findViewById(R.id.hline6);

        name1 = findViewById(R.id.medication_name);
        name2 = findViewById(R.id.n1);
        name3 = findViewById(R.id.n2);
        name4 = findViewById(R.id.n3);

        n0_input = findViewById(R.id.medication2_input);
        n1_input = findViewById(R.id.n1_input);
        n2_input = findViewById(R.id.n2_input);
        n3_input = findViewById(R.id.n3_input);

        m0_input = findViewById(R.id.medication_name_input);
        m1_input = findViewById(R.id.m1_input);
        m2_input = findViewById(R.id.m2_input);
        m3_input = findViewById(R.id.m3_input);

        m0 = findViewById(R.id.medication2);
        m1 = findViewById(R.id.m1);
        m2 = findViewById(R.id.m2);
        m3 = findViewById(R.id.m3);

        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        c4 = findViewById(R.id.c4);

        c5 = findViewById(R.id.c5);
        c6 = findViewById(R.id.c6);
        c7 = findViewById(R.id.c7);
        c8 = findViewById(R.id.c8);

        c9 = findViewById(R.id.c9);
        c10 = findViewById(R.id.c10);
        c11 = findViewById(R.id.c11);
        c12 = findViewById(R.id.c12);

        c13 = findViewById(R.id.c13);
        c14 = findViewById(R.id.c14);
        c15 = findViewById(R.id.c15);
        c16 = findViewById(R.id.c16);

        c1_input = findViewById(R.id.c1_input);
        c2_input = findViewById(R.id.c2_input);
        c3_input = findViewById(R.id.c3_input);
        c4_input = findViewById(R.id.c4_input);

        c5_input = findViewById(R.id.c5_input);
        c6_input = findViewById(R.id.c6_input);
        c7_input = findViewById(R.id.c7_input);
        c8_input = findViewById(R.id.c8_input);

        c9_input = findViewById(R.id.c9_input);
        c10_input = findViewById(R.id.c10_input);
        c11_input = findViewById(R.id.c11_input);
        c12_input = findViewById(R.id.c12_input);

        c13_input = findViewById(R.id.c13_input);
        c14_input = findViewById(R.id.c14_input);
        c15_input = findViewById(R.id.c15_input);
        c16_input = findViewById(R.id.c16_input);


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
                        n1_input.setVisibility(View.VISIBLE);
                        m1.setVisibility(View.VISIBLE);
                        m1_input.setVisibility(View.VISIBLE);
                        c5.setVisibility(View.VISIBLE);
                        c6.setVisibility(View.VISIBLE);
                        c7.setVisibility(View.VISIBLE);
                        c8.setVisibility(View.VISIBLE);

                        c5_input.setVisibility(View.VISIBLE);
                        c6_input.setVisibility(View.VISIBLE);
                        c7_input.setVisibility(View.VISIBLE);
                        c8_input.setVisibility(View.VISIBLE);

                    } else if (medicationCount == 3) {
                        hline3.setVisibility(View.VISIBLE);
                        name3.setVisibility(View.VISIBLE);
                        n2_input.setVisibility(View.VISIBLE);
                        m2.setVisibility(View.VISIBLE);
                        m2_input.setVisibility(View.VISIBLE);
                        c9.setVisibility(View.VISIBLE);
                        c10.setVisibility(View.VISIBLE);
                        c11.setVisibility(View.VISIBLE);
                        c12.setVisibility(View.VISIBLE);

                        c9_input.setVisibility(View.VISIBLE);
                        c10_input.setVisibility(View.VISIBLE);
                        c11_input.setVisibility(View.VISIBLE);
                        c12_input.setVisibility(View.VISIBLE);

                    } else if (medicationCount == 4) {
                        hline4.setVisibility(View.VISIBLE);
                        name4.setVisibility(View.VISIBLE);
                        n3_input.setVisibility(View.VISIBLE);
                        m3.setVisibility(View.VISIBLE);
                        m3_input.setVisibility(View.VISIBLE);
                        c13.setVisibility(View.VISIBLE);
                        c14.setVisibility(View.VISIBLE);
                        c15.setVisibility(View.VISIBLE);
                        c16.setVisibility(View.VISIBLE);

                        c13_input.setVisibility(View.VISIBLE);
                        c14_input.setVisibility(View.VISIBLE);
                        c15_input.setVisibility(View.VISIBLE);
                        c16_input.setVisibility(View.VISIBLE);
                    } else if (medicationCount == 1) {
                        hline0.setVisibility(View.VISIBLE);
                        hline1.setVisibility(View.VISIBLE);
                        m0.setVisibility(View.VISIBLE);
                        m0_input.setVisibility(View.VISIBLE);
                        n0_input.setVisibility(View.VISIBLE);
                        name1.setVisibility(View.VISIBLE);

                        c1.setVisibility(View.VISIBLE);
                        c2.setVisibility(View.VISIBLE);
                        c3.setVisibility(View.VISIBLE);
                        c4.setVisibility(View.VISIBLE);

                        c1_input.setVisibility(View.VISIBLE);
                        c2_input.setVisibility(View.VISIBLE);
                        c3_input.setVisibility(View.VISIBLE);
                        c4_input.setVisibility(View.VISIBLE);
                    }
                }
                delBtn.setEnabled(true);
                // Toast.makeText(ManualMedication.this, Integer.toString(medicationCount) , Toast.LENGTH_SHORT).show();
            }
        });


        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medicationCount == 0) {
                    delBtn.setEnabled(false);
                    Toast.makeText(ManualMedication.this, "You haven't enter the medication.", Toast.LENGTH_SHORT).show();
                } else {
                    medicationCount--;
                    if (medicationCount == 3) {
                        hline4.setVisibility(View.INVISIBLE);
                        name4.setVisibility(View.INVISIBLE);
                        n3_input.setVisibility(View.INVISIBLE);
                        m3.setVisibility(View.INVISIBLE);
                        m3_input.setVisibility(View.INVISIBLE);
                        c13.setVisibility(View.INVISIBLE);
                        c14.setVisibility(View.INVISIBLE);
                        c15.setVisibility(View.INVISIBLE);
                        c16.setVisibility(View.INVISIBLE);

                        c13_input.setVisibility(View.INVISIBLE);
                        c14_input.setVisibility(View.INVISIBLE);
                        c15_input.setVisibility(View.INVISIBLE);
                        c16_input.setVisibility(View.INVISIBLE);
                    } else if (medicationCount == 2) {
                        hline3.setVisibility(View.INVISIBLE);
                        name3.setVisibility(View.INVISIBLE);
                        n2_input.setVisibility(View.INVISIBLE);
                        m2.setVisibility(View.INVISIBLE);
                        m2_input.setVisibility(View.INVISIBLE);
                        c9.setVisibility(View.INVISIBLE);
                        c10.setVisibility(View.INVISIBLE);
                        c11.setVisibility(View.INVISIBLE);
                        c12.setVisibility(View.INVISIBLE);

                        c9_input.setVisibility(View.INVISIBLE);
                        c10_input.setVisibility(View.INVISIBLE);
                        c11_input.setVisibility(View.INVISIBLE);
                        c12_input.setVisibility(View.INVISIBLE);

                    } else if (medicationCount == 1) {
                        hline2.setVisibility(View.INVISIBLE);
                        name2.setVisibility(View.INVISIBLE);
                        n1_input.setVisibility(View.INVISIBLE);
                        m1.setVisibility(View.INVISIBLE);
                        m1_input.setVisibility(View.INVISIBLE);
                        c5.setVisibility(View.INVISIBLE);
                        c6.setVisibility(View.INVISIBLE);
                        c7.setVisibility(View.INVISIBLE);
                        c8.setVisibility(View.INVISIBLE);

                        c5_input.setVisibility(View.INVISIBLE);
                        c6_input.setVisibility(View.INVISIBLE);
                        c7_input.setVisibility(View.INVISIBLE);
                        c8_input.setVisibility(View.INVISIBLE);

                    } else if (medicationCount == 0) {
                        hline0.setVisibility(View.INVISIBLE);
                        hline1.setVisibility(View.INVISIBLE);
                        m0.setVisibility(View.INVISIBLE);
                        m0_input.setVisibility(View.INVISIBLE);
                        n0_input.setVisibility(View.INVISIBLE);
                        name1.setVisibility(View.INVISIBLE);

                        c1.setVisibility(View.INVISIBLE);
                        c2.setVisibility(View.INVISIBLE);
                        c3.setVisibility(View.INVISIBLE);
                        c4.setVisibility(View.INVISIBLE);

                        c1_input.setVisibility(View.INVISIBLE);
                        c2_input.setVisibility(View.INVISIBLE);
                        c3_input.setVisibility(View.INVISIBLE);
                        c4_input.setVisibility(View.INVISIBLE);
                    }
                }
                addBtn.setEnabled(true);
                // Toast.makeText(ManualMedication.this, Integer.toString(medicationCount) , Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void onClick_next_page(View view) {
        Intent intent = new Intent(this, ManualFeel.class);
        startActivity(intent);
    }
}
