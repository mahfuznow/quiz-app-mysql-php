package just.cse.mahfuz.quizsolving;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    Context c;
    ArrayList<ModelHistory> histories;

    public ListViewAdapter(Context c, ArrayList<ModelHistory> histories) {
        this.c = c;
        this.histories = histories;
    }
    @Override
    public int getCount() {return histories.size();}
    @Override
    public Object getItem(int i) {return histories.get(i);}
    @Override
    public long getItemId(int i) {return i;}
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.history_row,viewGroup,false);
        }

        TextView timestamp=view.findViewById(R.id.timestamp);
        TextView type=view.findViewById(R.id.type);
        TextView number=view.findViewById(R.id.number);
        TextView amount=view.findViewById(R.id.amount);


        final ModelHistory modelHistory = (ModelHistory) this.getItem(i);

        timestamp.setText(modelHistory.getTimestamp());
        type.setText(modelHistory.getType());
        number.setText(modelHistory.getNumber());
        amount.setText(modelHistory.getAmount());

        return view;
    }
}