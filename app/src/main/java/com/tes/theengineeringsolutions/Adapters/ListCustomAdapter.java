package com.tes.theengineeringsolutions.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.airbnb.lottie.L;
import com.tes.theengineeringsolutions.Models.UserDataModel;
import com.tes.theengineeringsolutions.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ListCustomAdapter extends ArrayAdapter<UserDataModel> {

    private final int resource;
    private List<UserDataModel> userDataModels;
    
    private Context context;
    
    private List<Integer> dataList;
    
    public ListCustomAdapter(Context context, int resource, List<UserDataModel> userDataModels) {
        super(context, resource, userDataModels);
        this.context = context;
        this.resource = resource;
        this.userDataModels = userDataModels;

        dataList = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(resource, null, false);

        TextView itemName = view.findViewById(R.id.list_item_tv_user_name);

        itemName.setText(userDataModels.get(position).getName());

        return view;
    }
}
