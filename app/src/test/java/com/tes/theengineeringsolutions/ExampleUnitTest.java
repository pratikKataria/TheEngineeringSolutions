package com.tes.theengineeringsolutions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tes.theengineeringsolutions.Activities.MainActivity;

import org.junit.Test;

import java.util.Calendar;
import java.util.Map;

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
    }

    @Test
    public void getProgress() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document("z6ihi3np22gMdJRJGaI7117vQcE2");
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();
                    if (data != null && data.containsKey("test_progress")) {
                        Map<String, Object> month1 = (Map<String, Object>) data.get("test_progress");
                        for (int i = -6; i < 1; i++) {
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.MONTH, i);
                            int month = cal.get(Calendar.MONTH);
                            int year = cal.get(Calendar.YEAR);
                            String stringDate = month + "-" + year;
                            if (month1.containsKey(stringDate)) {
                                System.out.println(month1.get(stringDate));
                            }
                        }
                    }
                }
            }
        });
    }
}