package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ManualMoreInfo extends AppCompatActivity {

    HashMap<String, Object> doctor_note_data;

    HashMap<String, Object> appointment;

    ArrayList<HashMap<String, Object>> medicationList = new ArrayList();

    ArrayList<HashMap<String, Object>> infoList = new ArrayList<>();

    EditText m1, where1, phone1;
    EditText m2, where2, phone2;
    EditText m3, where3, phone3;
    EditText m4, where4, phone4;

    View hline0, hline1, hline2, hline3, hline4;

    TextView c1, c2, c3;
    TextView c4, c5, c6;
    TextView c7, c8, c9;
    TextView c10, c11, c12;

    Button addBtn, delBtn;

    int itemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_more_info);

        addBtn = findViewById(R.id.add_btn);
        delBtn = findViewById(R.id.del);

        m1 = findViewById(R.id.input1);
        where1 = findViewById(R.id.input1b);
        phone1 = findViewById(R.id.input1c);

        m2 = findViewById(R.id.input2);
        where2 = findViewById(R.id.input2b);
        phone2 = findViewById(R.id.input2c);

        m3 = findViewById(R.id.input3);
        where3 = findViewById(R.id.input3b);
        phone3 = findViewById(R.id.input3c);

        m4 = findViewById(R.id.input4);
        where4 = findViewById(R.id.input4b);
        phone4 = findViewById(R.id.input4c);

        c1 = findViewById(R.id.for1);
        c2 = findViewById(R.id.go_to1);
        c3 = findViewById(R.id.call1);

        c4 = findViewById(R.id.for2);
        c5 = findViewById(R.id.go_to2);
        c6 = findViewById(R.id.call2);

        c7 = findViewById(R.id.for3);
        c8 = findViewById(R.id.go_to3);
        c9 = findViewById(R.id.call3);

        c10 = findViewById(R.id.for4);
        c11 = findViewById(R.id.go_to4);
        c12 = findViewById(R.id.call4);

        hline0 = findViewById(R.id.hline0);
        hline1 = findViewById(R.id.hline1);
        hline2 = findViewById(R.id.hline2);
        hline3 = findViewById(R.id.hline3);
        hline4 = findViewById(R.id.hline4);


        // handle the intent called from the previous page.
        // callingActivityIntent is from the previous page.
        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
            doctor_note_data = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("curr_doctor_note_data");
            appointment = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("appointment");
            medicationList = (ArrayList<HashMap<String, Object>>) callingActivityIntent.getSerializableExtra("medicationList");
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCount == 4) {
                    addBtn.setEnabled(false);
                    Toast.makeText(ManualMoreInfo.this, "You can only add four items at one form.", Toast.LENGTH_SHORT).show();
                } else {
                    if (itemCount == 1) {
                        // check if empty.
                        if (TextUtils.isEmpty(m1.getText().toString())) {
                            m1.setError("Cannot be blank.");
                            Toast.makeText(ManualMoreInfo.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (itemCount == 2) {
                        if (TextUtils.isEmpty(m2.getText().toString())) {
                            m2.setError("Cannot be blank.");
                            Toast.makeText(ManualMoreInfo.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (itemCount == 3) {
                        if (TextUtils.isEmpty(m3.getText().toString())) {
                            m3.setError("Cannot be blank.");
                            Toast.makeText(ManualMoreInfo.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    itemCount++;

                    if (itemCount == 1) {
                        hline0.setVisibility(View.VISIBLE);
                        hline1.setVisibility(View.VISIBLE);
                        m1.setVisibility(View.VISIBLE);
                        where1.setVisibility(View.VISIBLE);
                        phone1.setVisibility(View.VISIBLE);

                        c1.setVisibility(View.VISIBLE);
                        c2.setVisibility(View.VISIBLE);
                        c3.setVisibility(View.VISIBLE);

                    } else if (itemCount == 2) {
                        hline2.setVisibility(View.VISIBLE);
                        m2.setVisibility(View.VISIBLE);
                        where2.setVisibility(View.VISIBLE);
                        phone2.setVisibility(View.VISIBLE);

                        c4.setVisibility(View.VISIBLE);
                        c5.setVisibility(View.VISIBLE);
                        c6.setVisibility(View.VISIBLE);

                    } else if (itemCount == 3) {
                        hline3.setVisibility(View.VISIBLE);
                        m3.setVisibility(View.VISIBLE);
                        where3.setVisibility(View.VISIBLE);
                        phone3.setVisibility(View.VISIBLE);

                        c7.setVisibility(View.VISIBLE);
                        c8.setVisibility(View.VISIBLE);
                        c9.setVisibility(View.VISIBLE);

                    } else if (itemCount == 4) {
                        hline4.setVisibility(View.VISIBLE);
                        m4.setVisibility(View.VISIBLE);
                        where4.setVisibility(View.VISIBLE);
                        phone4.setVisibility(View.VISIBLE);

                        c10.setVisibility(View.VISIBLE);
                        c11.setVisibility(View.VISIBLE);
                        c12.setVisibility(View.VISIBLE);
                    }
                }
                delBtn.setEnabled(true);
                // Toast.makeText(ManualMedication.this, Integer.toString(itemCount) , Toast.LENGTH_SHORT).show();
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCount == 0) {
                    delBtn.setEnabled(false);
                    Toast.makeText(ManualMoreInfo.this, "You haven't enter the medication.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    itemCount--;
                    if (itemCount == 3) {
                        hline4.setVisibility(View.INVISIBLE);
                        m4.setVisibility(View.INVISIBLE);
                        where4.setVisibility(View.INVISIBLE);
                        phone4.setVisibility(View.INVISIBLE);
                        c10.setVisibility(View.INVISIBLE);
                        c11.setVisibility(View.INVISIBLE);
                        c12.setVisibility(View.INVISIBLE);

                    } else if (itemCount == 2) {
                        hline3.setVisibility(View.INVISIBLE);
                        m3.setVisibility(View.INVISIBLE);
                        where3.setVisibility(View.INVISIBLE);
                        phone3.setVisibility(View.INVISIBLE);
                        c7.setVisibility(View.INVISIBLE);
                        c8.setVisibility(View.INVISIBLE);
                        c9.setVisibility(View.INVISIBLE);

                    } else if (itemCount == 1) {
                        hline2.setVisibility(View.INVISIBLE);
                        m2.setVisibility(View.INVISIBLE);
                        where2.setVisibility(View.INVISIBLE);
                        phone2.setVisibility(View.INVISIBLE);
                        c4.setVisibility(View.INVISIBLE);
                        c5.setVisibility(View.INVISIBLE);
                        c6.setVisibility(View.INVISIBLE);

                    } else if (itemCount == 0) {
                        hline0.setVisibility(View.INVISIBLE);
                        hline1.setVisibility(View.INVISIBLE);
                        m1.setVisibility(View.INVISIBLE);
                        where1.setVisibility(View.INVISIBLE);
                        phone1.setVisibility(View.INVISIBLE);
                        c1.setVisibility(View.INVISIBLE);
                        c2.setVisibility(View.INVISIBLE);
                        c3.setVisibility(View.INVISIBLE);

                    }
                }
                addBtn.setEnabled(true);
            }
        });
    }

    public void onClick_next_page(View view) {

        if (itemCount > 0) {
            infoList.clear();

            // check if empty.
            if (TextUtils.isEmpty(m1.getText().toString())) {
                m1.setError("cannot be blank.");
                return;
            }

            if (TextUtils.isEmpty(where1.getText().toString()) && TextUtils.isEmpty(phone1.getText().toString())) {
                where1.setError("go to and phone sections cannot both be blank.");
                return;
            }

            HashMap<String, Object> f_i1 = new HashMap<>();
            f_i1.put("purpose", m1.getText().toString());

            if (TextUtils.isEmpty(where1.getText().toString())) {
                f_i1.put("who", "");
            } else {
                f_i1.put("who", where1.getText().toString());
            }


            if (TextUtils.isEmpty(phone1.getText().toString())) {
                f_i1.put("phone", "");
            } else {
                f_i1.put("phone", phone1.getText().toString());
            }

            infoList.add(f_i1);

            if (itemCount > 1) {
                // check if empty.
                if (TextUtils.isEmpty(m2.getText().toString())) {
                    m2.setError("cannot be blank.");
                    return;
                }

                if (TextUtils.isEmpty(where2.getText().toString()) && TextUtils.isEmpty(phone2.getText().toString())) {
                    where2.setError("go to and phone sections cannot both be blank.");
                    return;
                }

                HashMap<String, Object> f_i2 = new HashMap<>();
                f_i2.put("purpose", m2.getText().toString());

                if (TextUtils.isEmpty(where2.getText().toString())) {
                    f_i2.put("who", "");
                } else {
                    f_i2.put("who", where2.getText().toString());
                }


                if (TextUtils.isEmpty(phone2.getText().toString())) {
                    f_i2.put("phone", "");
                } else {
                    f_i2.put("phone", phone2.getText().toString());
                }

                infoList.add(f_i2);

                if (itemCount > 2) {
                    // check if empty.
                    if (TextUtils.isEmpty(m3.getText().toString())) {
                        m3.setError("cannot be blank.");
                        return;
                    }

                    if (TextUtils.isEmpty(where3.getText().toString()) && TextUtils.isEmpty(phone3.getText().toString())) {
                        where3.setError("go to and phone sections cannot both be blank.");
                        return;
                    }

                    HashMap<String, Object> f_i3 = new HashMap<>();
                    f_i3.put("purpose", m3.getText().toString());

                    if (TextUtils.isEmpty(where3.getText().toString())) {
                        f_i3.put("who", "");
                    } else {
                        f_i3.put("who", where3.getText().toString());
                    }


                    if (TextUtils.isEmpty(phone3.getText().toString())) {
                        f_i3.put("phone", "");
                    } else {
                        f_i3.put("phone", phone3.getText().toString());
                    }

                    infoList.add(f_i3);

                    if (itemCount > 3) {
                        // check if empty.
                        if (TextUtils.isEmpty(m4.getText().toString())) {
                            m4.setError("cannot be blank.");
                            return;
                        }

                        if (TextUtils.isEmpty(where4.getText().toString()) && TextUtils.isEmpty(phone4.getText().toString())) {
                            where4.setError("go to and phone sections cannot both be blank.");
                            return;
                        }

                        HashMap<String, Object> f_i4 = new HashMap<>();
                        f_i4.put("purpose", m4.getText().toString());

                        if (TextUtils.isEmpty(where4.getText().toString())) {
                            f_i4.put("who", "");
                        } else {
                            f_i4.put("who", where4.getText().toString());
                        }


                        if (TextUtils.isEmpty(phone4.getText().toString())) {
                            f_i4.put("phone", "");
                        } else {
                            f_i4.put("phone", phone4.getText().toString());
                        }

                        infoList.add(f_i4);
                    }
                }
            }
        }
        
        Intent intent = new Intent(this, ManualOwnNotes.class);
        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        // send list of medications.
        intent.putExtra("medicationList", medicationList);

        // feelingList is a list of feeling and instruction HashMap.
        doctor_note_data.put("more_info", infoList);

        intent.putExtra("appointment", appointment);
        startActivity(intent);
    }
}
