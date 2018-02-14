/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.non;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 1/17/2018.
 */

public class Constant {
    public static final int DEBUG = 1;


    //Grammars
    public static final String GRAMMAR_CATEGORY = "GRAMMAR_CATEGORY";


    //MAIN DICT
    public static final String MAIN_WORD = "MAIN_WORD";
    public static final String MAIN_TRANSLATION = "MAIN_TRANSLATION";


    //DETAIL VIEW ACTIVITY MOD
    public static final String DETAIL_VIEW_MOD = "DETAIL_VIEW_MOD";
    public static final String ADD_MOD = "ADD_MOD";
    public static final String EDIT_MOD = "EDIT_MOD";


    public static final String WRONG_WORDS = "be - was, were - been - быть, являться\n" +
            "beat - beat - beaten - бить, колотить\n" +
            "become - became - become - становиться\n" +
            "begin - began - begun - начинать\n" +
            "bend - bent - bent - гнуть\n" +
            "bet - bet - bet - держать пари\n" +
            "bite - bit - bitten - кусать\n" +
            "blow - blew - blown - дуть, выдыхать\n" +
            "break - broke - broken - ломать, разбивать, разрушать\n" +
            "bring - brought - brought - приносить, привозить, доставлять\n" +
            "build - built - built - строить, сооружать\n" +
            "buy - bought - bought - покупать, приобретать\n" +
            "catch - caught - caught - ловить, поймать, схватить\n" +
            "choose - chose - chosen - выбирать, избирать\n" +
            "come - came - come - приходить, подходить\n" +
            "cost - cost - cost - стоить, обходиться\n" +
            "cut - cut - cut - резать, разрезать\n" +
            "deal - dealt - dealt - иметь дело, распределять\n" +
            "dig - dug - dug - копать, рыть\n" +
            "do - did - done - делать, выполнять\n" +
            "draw - drew - drawn - рисовать, чертить\n" +
            "drink - drank - drunk - пить\n" +
            "drive - drove - driven - ездить, подвозить\n" +
            "eat - ate - eaten - есть, поглощать, поедать\n" +
            "fall - fell - fallen - падать\n" +
            "feed - fed - fed - кормить\n" +
            "feel - felt - felt - чувствовать, ощущать\n" +
            "fight - fought - fought - драться, сражаться, воевать\n" +
            "find - found - found - находить, обнаруживать\n" +
            "fly - flew - flown - летать\n" +
            "forget - forgot - forgotten - забывать о (чём-либо)\n" +
            "forgive - forgave - forgiven - прощать\n" +
            "freeze - froze - frozen - замерзать, замирать\n" +
            "get - got - got - получать, добираться\n" +
            "give - gave - given - дать, подать, дарить\n" +
            "go - went - gone - идти, двигаться\n" +
            "grow - grew - grown - расти, вырастать\n" +
            "hang - hung - hung - вешать, развешивать, висеть\n" +
            "have - had - had - иметь, обладать\n" +
            "hear - heard - heard - слышать, услышать\n" +
            "hide - hid - hidden - прятать, скрывать\n" +
            "hit - hit - hit - ударять, поражать\n" +
            "hold - held - held - держать, удерживать, задерживать\n" +
            "hurt - hurt - hurt - ранить, причинять боль, ушибить\n" +
            "keep - kept - kept - хранить, сохранять, поддерживать\n" +
            "know - knew - known - знать, иметь представление\n" +
            "lay - laid - laid - класть, положить, покрывать\n" +
            "lead - led - led - вести за собой, сопровождать, руководить\n" +
            "leave - left - left - покидать, уходить, уезжать, оставлять\n" +
            "lend - lent - lent - одалживать, давать взаймы (в долг)\n" +
            "let - let - let - позволять, разрешать\n" +
            "lie - lay - lain - лежать\n" +
            "light - lit - lit - зажигать, светиться, освещать\n" +
            "lose - lost - lost - терять, лишаться, утрачивать\n" +
            "make - made - made - делать, создавать, изготавливать\n" +
            "mean - meant - meant - значить, иметь в виду, подразумевать\n" +
            "meet - met - met - встречать, знакомиться\n" +
            "pay - paid - paid - платить, оплачивать, рассчитываться\n" +
            "put - put - put - ставить, помещать, класть\n" +
            "read - read - read - читать, прочитать\n" +
            "ride - rode - ridden - ехать верхом, кататься\n" +
            "ring - rang - rung - звенеть, звонить\n" +
            "rise - rose - risen - восходить, вставать, подниматься\n" +
            "run - ran - run - бежать, бегать\n" +
            "say - said - said - говорить, сказать, произносить\n" +
            "see - saw - seen - видеть\n" +
            "seek - sought - sought - искать, разыскивать\n" +
            "sell - sold - sold - продавать, торговать\n" +
            "send - sent - sent - посылать, отправлять, отсылать\n" +
            "set - set - set - устанавливать, задавать, назначать\n" +
            "shake - shook - shaken - трясти, встряхивать\n" +
            "shine - shone - shone - светить, сиять, озарять\n" +
            "shoot - shot - shot - стрелять\n" +
            "show - showed - shown, showed - показывать\n" +
            "shut - shut - shut - закрывать, запирать, затворять\n" +
            "sing - sang - sung - петь, напевать\n" +
            "sink - sank - sunk - тонуть, погружаться\n" +
            "sit - sat - sat - сидеть, садиться\n" +
            "sleep - slept - slept - спать\n" +
            "speak - spoke - spoken - говорить, разговаривать, высказываться\n" +
            "spend - spent - spent - тратить, расходовать, проводить (время)\n" +
            "stand - stood - stood - стоять\n" +
            "steal - stole - stolen - воровать, красть\n" +
            "stick - stuck - stuck - втыкать, приклеивать\n" +
            "strike - struck - struck, stricken - ударять, бить, поражать\n" +
            "swear - swore - sworn - клясться, присягать\n" +
            "sweep - swept - swept - мести, подметать, смахивать\n" +
            "swim - swam - swum - плавать, плыть\n" +
            "swing - swung - swung - качаться, вертеться\n" +
            "take - took - taken - брать, хватать, взять\n" +
            "teach - taught - taught - учить, обучать\n" +
            "tear - tore - torn - рвать, отрывать\n" +
            "tell - told - told - рассказывать\n" +
            "think - thought - thought - думать, мыслить, размышлять\n" +
            "throw - threw - thrown - бросать, кидать, метать\n" +
            "understand - understood - understood - понимать, постигать\n" +
            "wake - woke - woken - просыпаться, будить\n" +
            "wear - wore - worn - носить (одежду)\n" +
            "win - won - won - победить, выиграть\n" +
            "write - wrote - written - писать, записывать";
    static String dtr = "В отличие от большинства глаголов английского языка, неправильные английские глаголы образуют формы прошедшего неопределенного времени и причастия прошедшего времени особым образом. Выделяют несколько основных способов их образования, которые, тем не менее, не описывают все возможные случаи. Изучить все неправильные глаголы английского языка возможно путем их непосредственного запоминания.";

    public static void fdgfdghfh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int cout = 0;
                String path = "grammar/categories/modal verbs/";
                DatabaseReference dbr = FirebaseDatabase.getInstance().getReference();

                String[] strar = WRONG_WORDS.split("\n");
                for (String sss:strar) {
                    cout++;
                    String[] t = sss.split(" - ");
//                    HashMap<Integer, String> tmp = new HashMap<>();
//                    tmp.put(0, "Infinitive: " + t[0]);
//                    tmp.put(1, "Past Simple: " + t[1]);
//                    tmp.put(2, "Past Participle: " + t[2]);
//                    tmp.put(3, "Translate: " + t[3]);

                    List<String> fdf = new ArrayList<>();
                    fdf.add(0,"Infinitive: " + t[0]);
                    fdf.add(1, "Past Simple: " + t[1]);
                    fdf.add(2, "Past Participle: " + t[2]);
                    fdf.add(3, "Translation: " + t[3]);

                    Log.i("Infinitive", "run: " + cout);
                //    dbr.child(path + t[0].toString() + "/examples/").setValue(fdf);
                //    dbr.child(path + t[0].toString() + "/description/").setValue(dtr);
                }
            }
        }).start();

    }


}
