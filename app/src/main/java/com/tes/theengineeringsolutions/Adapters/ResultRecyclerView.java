package com.tes.theengineeringsolutions.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.tes.theengineeringsolutions.Models.LocalTestDatabase;
import com.tes.theengineeringsolutions.R;

import java.util.HashMap;
import java.util.List;

public class ResultRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //this int id which is used to determine the layout type
    //where 0 is for event
    private static final int VIEW_TYPE_EVENT = 0;
    //where 1 is for empty view
    private static final int VIEW_TYPE_EMPTY = 1;

    private HashMap<Integer, Integer> questionAnswered;

    private LayoutInflater inflater;

    //context
    private Context context;
    //list of list to show in recycler view
    private List<LocalTestDatabase> testList;

    public ResultRecyclerView(Context context, List<LocalTestDatabase> testList, HashMap<Integer, Integer> questionAnswered) {
        this.testList = testList;
        this.context = context;
        this.questionAnswered = questionAnswered;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //based on the viewType the holder will hold the view
        RecyclerView.ViewHolder holder;
        //viewType == 1
        if (viewType == VIEW_TYPE_EMPTY) {
            //empty view is passed to the view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_layout, parent, false);
            //then holder will hold the EmptyLayout view
            holder = new ResultRecyclerView.EmptyLayout(view);
        } else {
            //if viewType == 1
            //then view is assign the card view

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_question, parent, false);
            //holder will be assign the TestCardViewHolder

            holder = new ResultRecyclerView.TestCardViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //holder - holder will the view which is passed by onCreateViewHolder
        //position - position is the position of the card
        //if holder is of type empty then do nothing
        if (holder.getItemViewType() == VIEW_TYPE_EMPTY) {
            ResultRecyclerView.EmptyLayout emptyLayout = (ResultRecyclerView.EmptyLayout) holder;
        } //if holder is of type event then do as according
        //all internal view is populated
        else {
            ResultRecyclerView.TestCardViewHolder testCardViewHolder = (ResultRecyclerView.TestCardViewHolder) holder;
            String question = testList.get(position).getQuestions();
            int questionNumber = testList.get(position).getQuestionNo();
            int answer = testList.get(position).getAnswer();

            testCardViewHolder.setCardView(questionNumber, question, answer);

            if (questionAnswered.containsKey(questionNumber-1)) {
                if (answer == questionAnswered.get(questionNumber-1)) {
                    testCardViewHolder.isCorrect.setText("correct");
                    testCardViewHolder.isCorrect.setTextColor(context.getColor(R.color.subTitle));
                    Log.e("user answered questions: ", answer+"");
                    testCardViewHolder.wrongAnswer.setVisibility(View.GONE);
                } else {
                    testCardViewHolder.isCorrect.setText("wrong");
                    testCardViewHolder.isCorrect.setTextColor(context.getColor(R.color.CeriseRed));
                    testCardViewHolder.wrongAnswer.setText(setChipChoice(position, questionAnswered.get(questionNumber-1)));
                }
            }

            testCardViewHolder.correctAnswer.setText(setChipChoice(position, answer));
        }
    }

    private String setChipChoice(int position, int option) {
        LocalTestDatabase localTestDatabase = testList.get(position);
        switch (option) {
            case 1:
                return localTestDatabase.getChoice1();
            case 2:
                return localTestDatabase.getChoice2();
            case 3:
                return localTestDatabase.getChoice3();
            case 4:
                return localTestDatabase.getChoice4();
            default:
                return -1 + "";
        }
    }

    @Override
    public int getItemCount() {
        //if list is empty then return 1
        // here 1 means exactly 1 card is created
        // 1 card is to show empty layout
        if (testList.size() == 0) return 1;
            //if list is not empty then
            //size of the list return
            //based on the number of item present
            // for each item card is created
            // for ex list size == 10 then 10 card is created
        else return testList.size();
    }

    @Override //this will return view type to show
    public int getItemViewType(int position) {
        //based list size
        //if list size is 0 this means that list is empty
        // there for return empty view
        //this view is passed to onCreateView
        if (testList.size() == 0) {
            return VIEW_TYPE_EMPTY;
        }//else return the card view
        else {
            return VIEW_TYPE_EVENT;
        }
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
        TextView textViewQuestion;
        TextView textViewQuestionNumber;
        TextView isCorrect;
        Chip correctAnswer;
        Chip wrongAnswer;

        public TestCardViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.cardView_tv_question);
            textViewQuestionNumber = itemView.findViewById(R.id.cardview_tv_question_no);
            isCorrect = itemView.findViewById(R.id.cardView_tv_correct);
            correctAnswer = itemView.findViewById(R.id.cardview_cv_correct_answer);
            wrongAnswer = itemView.findViewById(R.id.cardview_cv_wrong_answer);
            //linking the view with ids
        }


        public void setCardView(int questionNumber, String question, int answer) {
            textViewQuestionNumber.setText(questionNumber + "");
            textViewQuestion.setText(question);
        }
    }
}
