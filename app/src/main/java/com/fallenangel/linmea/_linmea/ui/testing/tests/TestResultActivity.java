package com.fallenangel.linmea._linmea.ui.testing.tests;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.model.Word;
import com.fallenangel.linmea._linmea.ui.testing.models.WordsStat;

import java.util.ArrayList;

public class TestResultActivity extends AppCompatActivity {

    private String TAG = "TestResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Testing tResult = (Testing) getIntent().getParcelableExtra(
                QuestionGenerator.class.getCanonicalName());

        Bundle extra = getIntent().getBundleExtra("TESTING_WORDS_EXTRA");
        ArrayList<Word> testingWords = (ArrayList<Word>) extra.getSerializable("TESTING_WORDS");

        TextView result = (TextView) findViewById(R.id.result);
        String resultStr = "Question counter: " + tResult.getStats().getQCounter()+ "\n"
                + "\n"
                + "Correct answers: " + tResult.getStats().getTrueCounter() + "\n"
                + "Mistakes: " + tResult.getStats().getFalseCounter() + "\n";

        for (Word item:testingWords) {
            resultStr = resultStr + "\nWord: " + item.getWord()
                    + "\nnumber of repetitions: " + item.getWordRepetitionCounter()
                    + "\nnumber of correct answers: " + item.getCurreectCounter()
                    + "\nnumber of wrong answers: " + item.getMistakCounter() + "\n";
        }

        resultStr = resultStr + "\nTesting detail";
        for (WordsStat item:tResult.getStats().getWordsStat()) {
            resultStr = resultStr + "\n" + "Question: " + item.getItem().getAnswer()
                + "\nYour answer: " + item.getUrAnsw()
                + "\nCorrect answer: " + item.getItem().getcA() + "\n";
        }



        result.setText(resultStr);
        Button close = (Button) findViewById(R.id.close_test_result);
        close.setOnClickListener(view -> finish());
    }

}
