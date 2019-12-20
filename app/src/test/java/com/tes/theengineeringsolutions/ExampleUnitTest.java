package com.tes.theengineeringsolutions;

import com.tes.theengineeringsolutions.Activities.MainActivity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    int val;
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void quiz_test_result() {
        ExampleUnitTest ex = new ExampleUnitTest();
        ex.val = R.color.AliceBlue;

        assertEquals(2131099648, R.color.AliceBlue);
    }//2131099648
//    ?2131099737
//            2131099815
//            2131099685
//            2131099743
}