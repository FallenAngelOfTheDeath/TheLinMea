/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.testing;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.non.adapter.ViewPagerAdapter;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.testing.models.Word;
import com.fallenangel.linmea._modulus.testing.models.WordsStat;

import java.util.ArrayList;

public class TestResultActivity extends SuperAppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setTitle("Test result");

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_dots);
        tabLayout.setupWithViewPager(mViewPager, true);
        mViewPager.addOnPageChangeListener(this);


        Testing tResult = (Testing) getIntent().getParcelableExtra(
                QuestionGenerator.class.getCanonicalName());
        Bundle extra = getIntent().getBundleExtra("TESTING_WORDS_EXTRA");
        ArrayList<Word> testingWords = (ArrayList<Word>) extra.getSerializable("TESTING_WORDS");
        String mainStats = "Question counter: " + tResult.getStats().getQCounter()+ "\n"
                + "\n"
                + "Correct answers: " + tResult.getStats().getTrueCounter() + "\n"
                + "Mistakes: " + tResult.getStats().getFalseCounter() + "\n";

        String resultByWords = "";
        for (Word item:testingWords) {
            resultByWords = resultByWords + "\nWord: " + item.getWord()
                    + "\nnumber of repetitions: " + item.getWordRepetitionCounter()
                    + "\nnumber of correct answers: " + item.getCurreectCounter()
                    + "\nnumber of wrong answers: " + item.getMistakCounter() + "\n";
        }

        String resultByQuestions = "";
        //for (WordsStat item:tResult.getStats().getWordsStat()) {
        for (int i = 0; i < tResult.getStats().getWordsStat().size(); i++) {
            WordsStat item = tResult.getStats().getWordsStat().get(i);

            resultByQuestions = resultByQuestions + "\n" + "Question #" + (i + 1) + ": " + item.getItem().getAnswer()
                + "\nYour answer: " + item.getUrAnsw()
                + "\nCorrect answer: " + item.getItem().getcA() + "\n";
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MainStats.newInstance(mainStats.trim(), "Testing result:"), null);
        adapter.addFragment(MainStats.newInstance(resultByWords.trim(), "Testing result by words:"), null);
        adapter.addFragment(MainStats.newInstance(resultByQuestions.trim(), "Testing result by questions:"), null);
        mViewPager.setAdapter(adapter);


        Button close = (Button) findViewById(R.id.close_test_result);
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class MainStats extends Fragment{
        private TextView mainStats, title;
        public static MainStats newInstance(String mainStatsStr, String titleStr) {
            MainStats mainStats = new MainStats();
            Bundle args = new Bundle();
            args.putString("MAIN_STATS", mainStatsStr);
            args.putString("TITLE", titleStr);
            mainStats.setArguments(args);
            return mainStats;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.main_stats_fragment, container, false);
            mainStats = (TextView) view.findViewById(R.id.main_stats);
            title = (TextView) view.findViewById(R.id.testing_result_title);
            mainStats.setText(getArguments().getString("MAIN_STATS"));
            title.setText(getArguments().getString("TITLE"));
            return view;
        }


    }
}
