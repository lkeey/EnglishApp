package com.example.englishapp;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

public class CheckingActivity extends AppCompatActivity {
        // 28 урок - анимация
    Dialog dialog;
    Dialog dialogEnd;

    final int COUNTER_WORDS = 2;

    public boolean statusCounter = true;
    public int numCounter = 0;
    public int numPassed = 0;
    public int counterTrueAnswer = 0;
    public int[] usedNum = new int[COUNTER_WORDS];
    public Button[] btnArray = {};
    public TextView[] tvArray = {};

    ArrayActivity array = new ArrayActivity(new String[]{"hi", "hello"}, new String[]{"привет", "здравствуйте"});
    Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_activity);

        Window window = getWindow();
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

//        Edit Fields
        TextView infoString = findViewById(R.id.infoString);
        infoString.setText("Level Type");

        TextView searchWord = findViewById(R.id.searchWord);

        btnArray = new Button[]{
                findViewById(R.id.btn1),
                findViewById(R.id.btn2),
                findViewById(R.id.btn3),
                findViewById(R.id.btn4),
                findViewById(R.id.btn5),
                findViewById(R.id.btn6),
                findViewById(R.id.btn7),
                findViewById(R.id.btn8),
                findViewById(R.id.btn9),
                findViewById(R.id.btn10),
        };

        tvArray = new TextView[] {
                findViewById(R.id.point1),
                findViewById(R.id.point2),
                findViewById(R.id.point3),
                findViewById(R.id.point4),
                findViewById(R.id.point5),
                findViewById(R.id.point6),
                findViewById(R.id.point7),
                findViewById(R.id.point8),
                findViewById(R.id.point9),
                findViewById(R.id.point10),
        };

//        New Dialog Window
        dialog = new Dialog(this);
//        Hide title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        choose layout
        dialog.setContentView(R.layout.previewdialog);
//        blur background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        not allow close by mobile button
        dialog.setCancelable(false);
//      show layout
        dialog.show();

        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(CheckingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {

                }
                dialog.dismiss();
            }
        });

        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(CheckingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {

                }
            }
        });

//      settings usedNum
        for(int num: usedNum) {
            num = COUNTER_WORDS + 1;
        }

//          setting the string that will need to be found
        numCounter = random.nextInt(2);
        searchWord.setText(array.ruWords[numCounter]);
        usedNum[0] = numCounter;

        for(int i = 0; i < COUNTER_WORDS; i++) {
            btnArray[i].setText(array.enWords[i]);

            int finalI = i;
            btnArray[i].setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if(motionEvent.getAction() == motionEvent.ACTION_DOWN) {
//                begin touch
                    for(int j = 0; j < COUNTER_WORDS; j++){
                        if(j != finalI) {
//                            block another buttons
                            btnArray[j].setEnabled(false);
                        }
                    }

                    } else if(motionEvent.getAction() == motionEvent.ACTION_UP){
//                raise finger

                        if(finalI == numCounter) {
                            counterTrueAnswer ++;
                        } else {
                            counterTrueAnswer --;
                        }

                        numPassed++;

//                        Toast.makeText(CheckingActivity.this, "counterTrueAnswer " + String.valueOf(counterTrueAnswer), Toast.LENGTH_SHORT).show();

                        for(int j = 0; j < numPassed; j++) {
                            tvArray[j].setBackgroundResource(R.drawable.style_points_done);
                        }

                        if(numPassed == COUNTER_WORDS) {
//                            exiting the game
//                            Toast.makeText(CheckingActivity.this, "Game End", Toast.LENGTH_SHORT).show();

                            if(counterTrueAnswer < 0) {
                                counterTrueAnswer = 0;
                            }
//                            SUCCESS or FAIL
                            if(counterTrueAnswer / numPassed * 100 > 75) {
                                statusCounter = true;
                                Toast.makeText(CheckingActivity.this, "Well done!!", Toast.LENGTH_LONG).show();
                            } else {
                                statusCounter = false;
                                Toast.makeText(CheckingActivity.this, "Oh, it should have been better to learn...", Toast.LENGTH_LONG).show();

                            }

                            dialogEnd = new Dialog(CheckingActivity.this);
//        Hide title
                            dialogEnd.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        choose layout
                            if(statusCounter) {
                                dialogEnd.setContentView(R.layout.dialog_end_success);
                            } else {
                                dialogEnd.setContentView(R.layout.dialog_end_fail);
                            }
//        blur background
                            dialogEnd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        not allow close by mobile button
                            dialogEnd.setCancelable(false);
//      show layout

                            TextView tv = dialogEnd.findViewById(R.id.description);
                            tv.setText("Your result is " + String.valueOf(counterTrueAnswer) + "/" + String.valueOf(COUNTER_WORDS));

                            dialogEnd.show();

                            Button btnCloseEnd = dialogEnd.findViewById(R.id.btnCloseEnd);
                            btnCloseEnd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                                    Increase experience

                                    Intent intent = new Intent(CheckingActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                    dialogEnd.dismiss();
                                }
                            });

                            Button btnContinueEnd = dialogEnd.findViewById(R.id.btnContinueEnd);
                            btnContinueEnd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(statusCounter) {
//                                        New level
                                        Intent intent = new Intent(CheckingActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
//                                        Lets go again

                                        numCounter = 0;
                                        numPassed = 0;
                                        counterTrueAnswer = 0;
                                        usedNum = new int[COUNTER_WORDS];

                                        for(int num: usedNum) {
                                            num = COUNTER_WORDS + 1;
                                        }

                                        numCounter = random.nextInt(2);
                                        searchWord.setText(array.ruWords[numCounter]);
                                        usedNum[0] = numCounter;

                                        for(TextView point: tvArray) {
                                            point.setBackgroundResource(R.drawable.style_status_progress);
                                        }

                                        for(int j = 0; j < COUNTER_WORDS; j++){
                                            btnArray[j].setEnabled(true);
                                        }

                                    }
                                    dialogEnd.dismiss();
                                }
                            });


                        } else {
                            numCounter = random.nextInt(2);

                            while (true) {
                                statusCounter = true;

//                                Toast.makeText(CheckingActivity.this, "NEW " + String.valueOf(numCounter), Toast.LENGTH_SHORT).show();
                                numCounter = random.nextInt(2);

                                for(int j = 0; j < COUNTER_WORDS - 1; j++){
//                                   if num has already used
//                                    Toast.makeText(CheckingActivity.this, "UsedNum " + String.valueOf(usedNum[j]), Toast.LENGTH_SHORT).show();
                                    if(numCounter == usedNum[j]){
//                                        Toast.makeText(CheckingActivity.this, String.valueOf(numCounter) + " NO " + String.valueOf(usedNum[j]), Toast.LENGTH_SHORT).show();
                                        statusCounter = false;
                                        break;
                                    }
                                }

                                if(statusCounter){
//                                    add to used num
                                    usedNum[numPassed] = numCounter;
                                    break;
                                }
                            }

                            searchWord.setText(array.ruWords[numCounter]);

                            for(int j = 0; j < COUNTER_WORDS; j++){
                                btnArray[j].setEnabled(true);
                            }

                        }
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(CheckingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {

        }
    }
}