package just.cse.mahfuz.quizsolving;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapterNotification extends BaseAdapter {
    Context c;
    ArrayList<ModelNotification> notifications;

    public ListViewAdapterNotification(Context c, ArrayList<ModelNotification> notifications) {
        this.c = c;
        this.notifications = notifications;
    }
    @Override
    public int getCount() {return notifications.size();}
    @Override
    public Object getItem(int i) {return notifications.get(i);}
    @Override
    public long getItemId(int i) {return i;}
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.notification_row,viewGroup,false);
        }

        TextView timestamp=view.findViewById(R.id.timestamp);
        TextView msg=view.findViewById(R.id.msg);

        final ModelNotification modelNotification = (ModelNotification) this.getItem(i);

        timestamp.setText(modelNotification.getTimestamp());
        msg.setText(modelNotification.getMessage());
        return view;
    }
}