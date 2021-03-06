package com.tes.theengineeringsolutions.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.opencsv.CSVReader;
import com.tes.theengineeringsolutions.roomdatabase.LocalTestDatabaseDoa;
import com.tes.theengineeringsolutions.Models.QuestionModel;
import com.tes.theengineeringsolutions.Models.QuizContract;
import com.tes.theengineeringsolutions.roomdatabase.QuizDatabase;
import com.tes.theengineeringsolutions.R;
import com.tes.theengineeringsolutions.quiz.QuizActivity;
import com.tes.theengineeringsolutions.utils.SharedPrefsUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //this int id which is used to determine the layout type
    //where 0 is for event
    private static final int VIEW_TYPE_TESTVIEW = 1;
    //where 1 is for empty view
    private static final int VIEW_TYPE_EMPTY = 0;
    //where 2 is for result card view in result fragment
    private static final int VIEW_TYPE_RESULTVIEW = 2;
    //where 3 is for inbox card view in home fragment
    private static final int VIEW_TYPE_INBOXVIEW =3;

    private static final String TAG = "RECYLER VIEW ADAPER";

    public static HashMap<String, Boolean> colorMap = new HashMap<>();

    private FirebaseStorage firebaseStorage;
    private LayoutInflater inflater;
    private int currentView;
    //context
    private Context context;

    //list of list to show in recycler view
    private List<QuizContract> testList;

    public RecyclerViewAdapter() {}

    //constructor to populate list and context of Test Fragment
    public RecyclerViewAdapter(Context context, List<QuizContract> testList, int currentView) {
        this.testList = testList;
        this.context = context;
        this.currentView = currentView;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        colorMap.put("#F8C9B5", false);
        colorMap.put("#79CBE8", false);
        colorMap.put("#D1A38B", false);
        colorMap.put("#B4DDF6", false);
        colorMap.put("#FFD8D8", false);
        colorMap.put("#BDD8AF", false);

    }

    @NonNull
    @Override //RecyclerView holder holds the view
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //based on the viewType the holder will hold the view

        RecyclerView.ViewHolder holder;

        if (viewType == VIEW_TYPE_TESTVIEW) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_test, parent, false);
            holder = new TestCardViewHolder(view);

        } else if (viewType == VIEW_TYPE_RESULTVIEW) {

            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_result, parent, false);
            holder = new ResultCardViewHolder(view1);

        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_layout, parent, false);
            holder = new EmptyLayout(view);

        }
        return holder;
    }

    // BindViewHolder this method will bind the view with the internal will and used to fill the data in the view
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //holder - holder will the view which is passed by onCreateViewHolder
        //position - position is the position of the card
        //if holder is of type empty then do nothing
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_EMPTY:
                EmptyLayout emptyLayout = (EmptyLayout) holder;
                break;
            case VIEW_TYPE_TESTVIEW:
                TestCardViewHolder testCardViewHolder = (TestCardViewHolder) holder;
                QuizContract quizContract = testList.get(position);

                testCardViewHolder.setCardView(quizContract.getDisplay_name() + "",
                        quizContract.getSubject_code() + "",
                        quizContract.getDate() + "",
                        quizContract.getNumber_of_questions() + " questions",
                        quizContract.getTest_duration() + " mins",
                        quizContract.isQuizCompletedByUser);

                testCardViewHolder.mLockBtn.setOnClickListener(v -> {
                    if (testCardViewHolder.isFileExist())
                        testCardViewHolder.isTestCompleted();
                    else Toast.makeText(context, "download file first", Toast.LENGTH_SHORT).show();
                });

                testCardViewHolder.mDownloadBtn.setOnClickListener(v -> testCardViewHolder.downloadFile(quizContract.getFile_uri()));
                break;

            case VIEW_TYPE_RESULTVIEW:
                ResultCardViewHolder resultRecyclerView = (ResultCardViewHolder) holder;

                resultRecyclerView.setCardView(testList.get(position).getDisplay_name(), testList.get(position).getSubject_code(),
                        testList.get(position).getDate(), testList.get(position).getResult(),
                        testList.get(position).getPercentage(), testList.get(position).getCorrect(), testList.get(position).getIncorrect(), testList.get(position).getTotalQuestion(), testList.get(position).getColor());
                break;
        }
    }


    //get item count will return the number of items present int he list
    //based the number the view will number of cards
    @Override
    public int getItemCount() {
        //if list is empty then return 1
        // here 1 means exactly 1 card is created
        // 1 card is to show empty layout
        if (testList.size() == 0) {
            return 1;
        }
        //if list is not empty then
        //size of the list return
        //based on the number of item present
        // for each item card is created
        // for ex list size == 10 then 10 card is created
        else {
            return testList.size();
        }

    }

    @Override //this will return view type to show
    public int getItemViewType(int position) {
        //based list size
        //if list size is 0 this means that list is empty
        // there for return empty view
        //this view is passed to onCreateView
        if (testList.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else {
            switch (currentView) {
                case VIEW_TYPE_TESTVIEW:
                    return VIEW_TYPE_TESTVIEW;
                case VIEW_TYPE_RESULTVIEW:
                    return VIEW_TYPE_RESULTVIEW;
                case VIEW_TYPE_INBOXVIEW:
                    return VIEW_TYPE_INBOXVIEW;
            }
        }
        return 0;
    }

    //view of empty layout
    public class EmptyLayout extends RecyclerView.ViewHolder {

        public EmptyLayout(@NonNull View itemView) {
            super(itemView);
        }
    }

    //view of card layout
    public class TestCardViewHolder extends RecyclerView.ViewHolder {
        //views of the card view
        private TextView textViewDisplayName;
        private TextView texViewDate;
        private TextView textViewNoOfQuestion;
        private TextView textViewDuration;
        private TextView textViewSubjectCode;
        private TextView textViewIsCompleted;
        private ImageView mLockBtn;
        private ImageView mDownloadBtn;
        private ProgressBar progressBar;
        private AlertDialog alertDialog;

        public TestCardViewHolder(@NonNull View itemView) {
            super(itemView);
            //linking the view with ids
            textViewDisplayName = itemView.findViewById(R.id.card_tv_test_name);
            texViewDate = itemView.findViewById(R.id.card_tv_test_date);
            textViewNoOfQuestion = itemView.findViewById(R.id.card_tv_no_of_question);
            textViewIsCompleted = itemView.findViewById(R.id.card_tv_isCompleted);
            textViewDuration = itemView.findViewById(R.id.card_tv_duration);
            mLockBtn = itemView.findViewById(R.id.card_ib_lock);
            mDownloadBtn = itemView.findViewById(R.id.card_ib_download_test_file);
            progressBar = itemView.findViewById(R.id.card_pb_progress);
            textViewSubjectCode = itemView.findViewById(R.id.card_tv_test_unique_name);
        }

        void setCardView(String testTitle, String uniqueName, String date, String noOfQuestion, String testDuration, String isCompletedText) {
            textViewDisplayName.setText(testTitle);//set testTile with firestore
            textViewSubjectCode.setText(uniqueName);// e
            texViewDate.setText(date);//set date fetched form firestore
            textViewNoOfQuestion.setText(noOfQuestion);//set no questions
            textViewDuration.setText(testDuration);//set testDuration
            textViewIsCompleted.setText(isCompletedText);
        }

        void downloadFile(String url) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl(url);

            progressBar.setVisibility(View.VISIBLE);

            final File rootPath = new File("/data/data/com.tes.theengineeringsolutions/test_files/");
            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }

            final File localFile = new File(rootPath, textViewSubjectCode.getText().toString() + ".csv");
            if (localFile.exists()) {
                Toast.makeText(context, "file present", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else {
                storageReference.getFile(localFile).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "file downloaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "failed to download file", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        }


        void isTestCompleted() {
            progressBar.setVisibility(View.VISIBLE);
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getUid());
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null) {
                        if (snapshot.exists()) {
                            Map<String, Object> header = snapshot.getData();
                            Map<String, Boolean> data = (Map<String, Boolean>) header.get("test_completed");
                            if (data != null) {
                                Log.e(TAG, "data.get () - " + data.get(textViewSubjectCode.getText().toString()));
                                if (data.containsKey(textViewSubjectCode.getText().toString()) && data.get(textViewSubjectCode.getText().toString())) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, "test already completed", Toast.LENGTH_SHORT).show();
                                } else {
                                    showAlertDialog();
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, "enter password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                showAlertDialog();
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(context, "enter password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }

        void showAlertDialog() {
            View alertLayout = inflater.inflate(R.layout.alert_dialog_custom_view, null);
            final TextView mdisplayText = alertLayout.findViewById(R.id.alert_dialog_tv_display_text);
            final TextInputEditText mPassEditText = alertLayout.findViewById(R.id.alert_dialog_et_passEditText);
            final MaterialButton continueBtn = alertLayout.findViewById(R.id.alert_dialog_mb_continue);
            final MaterialButton cancelBtn = alertLayout.findViewById(R.id.alert_dialog_mb_cancel);
            final ProgressBar progressBar = alertLayout.findViewById(R.id.progressbar);
            final LottieAnimationView lottieAnimationView = alertLayout.findViewById(R.id.lottieLockAnimation);

            mdisplayText.setText(textViewDisplayName.getText().toString());

            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
            alert.setView(alertLayout);
            alert.setCancelable(false);

            alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            continueBtn.setOnClickListener(v -> {
                if (mPassEditText.getText().toString().equals("")) {
                    mPassEditText.setError("should not be empty");
                    mPassEditText.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                lottieAnimationView.setMinAndMaxProgress(.20F, .50F);
                lottieAnimationView.playAnimation();

                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Admin").document(textViewSubjectCode.getText().toString());
                documentReference.get().addOnSuccessListener(success -> {
                    Map<String, Object> data = success.getData();
                    Log.e(RecyclerViewAdapter.class.getName(), success.getData() + " ");
                    if (data != null && data.containsKey("password") && data.get("password") != null && data.get("password").equals(mPassEditText.getText().toString())) {
                        createTestTable();
                        lottieAnimationView.setMinAndMaxProgress(.60F, .80F);
                        lottieAnimationView.playAnimation();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(context, "wrong password", Toast.LENGTH_SHORT).show();
                        lottieAnimationView.setProgress(0);
                        lottieAnimationView.pauseAnimation();
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    lottieAnimationView.setProgress(0);
                    lottieAnimationView.pauseAnimation();
                    progressBar.setVisibility(View.GONE);
                });
            });
            cancelBtn.setOnClickListener(v -> alertDialog.dismiss());
            alertDialog.show();
        }

        LocalTestDatabaseDoa localTestDatabaseDoa;

        void createTestTable() {
            try {
                //this.getData.getpath();/data/data/com.tes.theengineeringsolutions/test_files/Gkgkueue03.csv
                CSVReader reader = new CSVReader(new FileReader("/data/data/com.tes.theengineeringsolutions/test_files/" + textViewSubjectCode.getText().toString() + ".csv"), ',');
                List<QuestionModel> tdb = new ArrayList<>();
                String[] rec = null;
                int questionNo = 1;
                while ((rec = reader.readNext()) != null) {
                    int correctAnswerChoice = 0;
                    try {
                        correctAnswerChoice = Integer.parseInt(rec[5]);
                    } catch (Exception xe) {
                        Toast.makeText(context, "File is corrupted: please upload file without question number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    QuestionModel test = new QuestionModel(questionNo++, rec[0] + "", rec[1] + "", rec[2] + "", rec[3] + "", rec[4] + " ", correctAnswerChoice, false);
                    tdb.add(test);
                }

                QuizDatabase quizDatabase = QuizDatabase.getInstance(context);
                localTestDatabaseDoa = quizDatabase.testDatabaseDoa();
                new DeleteAllLocalTestDatabase(localTestDatabaseDoa).execute();
                SharedPrefsUtils.clearAllPreference(context);

                for (QuestionModel questionModel : tdb) {
                    insert(questionModel);
                }

                setTestCompleted();
            } catch (Exception e) {
                Log.e(RecyclerViewAdapter.class.getName(), "exeption " + e.getMessage());
                e.printStackTrace();
            }
        }

        void setTestCompleted() {
            if (FirebaseAuth.getInstance().getUid() != null) {
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getUid());
                documentReference.get().addOnCompleteListener(task -> {
                    Map<String, Object> header = new HashMap<>();
                    Map<String, Boolean> data = new HashMap<>();
                    data.put(textViewSubjectCode.getText().toString(), true);
                    header.put("test_completed", data);
                    documentReference.set(header, SetOptions.merge()).addOnCompleteListener(task1 -> {
                        if (task.isSuccessful()) {
                            startQuizActivity();
                        } else {
                            Toast.makeText(context, "check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        }


        public void insert(QuestionModel questionModel) {
            new InsertLocalTestDatabase(localTestDatabaseDoa).execute(questionModel);
        }


        void startQuizActivity() {
            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("TEST_NAME", textViewDisplayName.getText().toString());
            intent.putExtra("TEST_CODE", textViewSubjectCode.getText().toString());
            intent.putExtra("TEST_DURATION", textViewDuration.getText().toString());
            intent.putExtra("TEST_TOTAL_QUESTION", textViewNoOfQuestion.getText().toString());
            alertDialog.cancel();
            context.startActivity(intent);
        }

        boolean isFileExist() {
            return new File("/data/data/com.tes.theengineeringsolutions/test_files/" + textViewSubjectCode.getText().toString() + ".csv").exists();
        }

    }

    private static class InsertLocalTestDatabase extends AsyncTask<QuestionModel, Void, Void> {

        private LocalTestDatabaseDoa databaseDao;

        private InsertLocalTestDatabase(LocalTestDatabaseDoa databaseDoa) {
            this.databaseDao = databaseDoa;
        }

        @Override
        protected Void doInBackground(QuestionModel... questionModels) {
            databaseDao.insert(questionModels[0]);
            return null;
        }
    }

    private static class DeleteAllLocalTestDatabase extends AsyncTask<Void, Void, Void> {

        private LocalTestDatabaseDoa databaseDao;

        private DeleteAllLocalTestDatabase(LocalTestDatabaseDoa databaseDoa) {
            this.databaseDao = databaseDoa;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            databaseDao.deleteAllQuestions();
            return null;
        }
    }

    //view of card layout
    public class ResultCardViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView textViewSubject;
        private TextView textViewSubjectCode;
        private TextView textViewDate;
        private TextView textViewResult;
        private TextView textViewPercent;
        private TextView textViewPercent2;
        private TextView textViewQuestionCorrect;
        private TextView textViewQuestionIncorrect;
        private TextView textViewWeekDay;
        private TextView textViewQuestionAnswered;
        private LinearLayout linearLayout;
        private View view;

        public ResultCardViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubject = itemView.findViewById(R.id.cardView_tv_subject);
            textViewSubjectCode = itemView.findViewById(R.id.cardView_tv_subject_code);
            textViewDate = itemView.findViewById(R.id.cardView_tv_date);
            textViewResult = itemView.findViewById(R.id.cardView_tv_pass);

            textViewPercent = itemView.findViewById(R.id.cardView_tv_percentage);
            textViewPercent2 = itemView.findViewById(R.id.cardView_tv_percentage2);
            textViewQuestionCorrect = itemView.findViewById(R.id.cardView_tv_correct);
            textViewQuestionIncorrect = itemView.findViewById(R.id.cardView_tv_incorrect);
            textViewWeekDay = itemView.findViewById(R.id.cardView_tv_week_day);
            textViewQuestionAnswered = itemView.findViewById(R.id.cardView_tv_answered);
            cardView = itemView.findViewById(R.id.result_card_view);
            linearLayout = itemView.findViewById(R.id.cardView_linearLayout);
            view = itemView.findViewById(R.id.view);
        }

        public void setCardView(String subject, String subjectCode, String date, String result, String percentage, String correct, String incorrect, String totalQuestions, String color) {
            textViewSubject.setText(subject);
            textViewSubjectCode.setText(subjectCode);
            textViewDate.setText(formattedDate(date));
            textViewWeekDay.setText(weekDate(date));
            textViewPercent.setText(percentage);
            textViewQuestionCorrect.setText("correct: " + correct + "/" + totalQuestions);
            textViewQuestionIncorrect.setText("incorrect: " + incorrect + "/" + totalQuestions);

            if (correct != null && incorrect != null && totalQuestions != null) {
                textViewQuestionAnswered.setText("answered: " + (Integer.parseInt(correct) + Integer.parseInt(incorrect)) + "/" + totalQuestions);
            }

            if (result != null && result.equals("pass")) {
                textViewResult.setText(result);
                textViewResult.setTextColor(context.getColor(R.color.green));
            }
            Log.e(TAG, color + "cccccccccccccccccccccccccccccccccccc");
            if (color != null) {
                cardView.setCardBackgroundColor(Color.parseColor(color));
            }

            if (color != null) {
                if (colorMap.containsKey(color)) {
                    textViewSubject.setTextColor(context.getColor(R.color.black));
                    textViewSubjectCode.setTextColor(context.getColor(R.color.black));
                    textViewDate.setTextColor(context.getColor(R.color.black));
                    textViewPercent.setTextColor(context.getColor(R.color.black));
                    textViewPercent2.setTextColor(context.getColor(R.color.black));
                    textViewQuestionCorrect.setTextColor(context.getColor(R.color.black));
                    textViewQuestionIncorrect.setTextColor(context.getColor(R.color.black));
                    textViewWeekDay.setTextColor(context.getColor(R.color.black));
                    textViewQuestionAnswered.setTextColor(context.getColor(R.color.black));
                    view.setBackgroundColor(context.getColor(R.color.black));
                    linearLayout.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.black));
                }
            }
        }

        private String formattedDate(String date) {
            Log.e(TAG, "date error " + date);
            if (date != null)
                return date.substring(4);
            else return "";
        }

        private String weekDate(String date) {
            if (date != null)
                return date.substring(0, 3);
            else return "";
        }
    }
}
