package app.dev.sigtivity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import app.dev.sigtivity.R;
import app.dev.sigtivity.domain.EventDetail;

/**
 * Created by Ravi on 1/12/2016.
 */
public class UserEventListAdapter extends BaseAdapter {
    private List<EventDetail> eventDetails;
    private Context context;

    public UserEventListAdapter(Context context, List<EventDetail> eventDetails){
        this.context = context;
        this.eventDetails = eventDetails;
    }

    @Override
    public int getCount() {
        return eventDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.template_profile_event_row, parent, false);
        TextView eventTitle = (TextView) view.findViewById(R.id.txtViewEventTitle);
        TextView eventDate = (TextView)view.findViewById(R.id.txtViewEventDate);
        EventDetail detail = eventDetails.get(position);
        eventTitle.setText(detail.getEventName());
        eventDate.setText(detail.getEventDate().toString());
        view.setId(detail.getEventId());
        return view;
    }
}
