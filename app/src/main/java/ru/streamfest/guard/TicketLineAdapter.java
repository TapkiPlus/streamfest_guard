package ru.streamfest.guard;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import java.util.Calendar;

import ru.streamfest.guard.model.TicketDetails;
import ru.streamfest.guard.model.TicketLine;

public class TicketLineAdapter extends ArrayAdapter<TicketLine> {

    private final Context context;

    private static final int GREEN  = 0xFF009900;
    private static final int RED    = 0xFF990000;

    private static final int ENTRY_ALLOWED = 0;
    private static final int ENTRY_FORBIDDEN_NO_SUCH_TICKET = 1;
    private static final int ENTRY_FORBIDDEN_ENTRY_ATTEMPTS_EXCEEDED = 2;
    private static final int ENTRY_FORBIDDEN_ALREADY_ENTRERED_TODAY = 3;

    static class ViewHolder {
        private TextView firstLine;
        private TextView secondLine;
        private View icon;
        private int position;
    }

    public TicketLineAdapter(Context context, @LayoutRes int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.visitor_item, parent, false);
            mViewHolder.firstLine = (TextView) convertView.findViewById(R.id.firstLine);
            mViewHolder.secondLine = (TextView) convertView.findViewById(R.id.secondLine);
            mViewHolder.icon = (View) convertView.findViewById(R.id.icon);
            mViewHolder.position = position;
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        final TicketLine data = getItem(position);
        TicketDetails details = data.getDetails();
        if (details != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(details.getOrderId())
                .append(" | ")
                .append(details.getOrderEmail());


            if (details.getStreamer() != null) {
                sb
                    .append(" | from ")
                    .append(details.getStreamer());
            }
            mViewHolder.secondLine.setText(sb.toString());
        }
        if (data.getStatus() == ENTRY_ALLOWED) {
            mViewHolder.firstLine.setTextColor(GREEN);
            mViewHolder.secondLine.setTextColor(GREEN);
            int type = data.getDetails().getTicketType();
            switch (type) {
                case 1:
                    final Calendar cal = Calendar.getInstance();
                    final int day = cal.get(Calendar.DAY_OF_WEEK);
                    switch (day) {
                        case Calendar.SATURDAY:
                            mViewHolder.icon.setBackgroundColor(context.getResources().getColor(R.color.colorYellow));
                            break;
                        case Calendar.SUNDAY:
                            mViewHolder.icon.setBackgroundColor(context.getResources().getColor(R.color.colorOrange));
                            break;
                    }
                    break;
                default:
                    mViewHolder.icon.setBackgroundColor(context.getResources().getColor(R.color.colorViolet));
                    break;
            }
            mViewHolder.firstLine.setText(R.string.access_granted);
        } else {
            mViewHolder.firstLine.setTextColor(RED);
            mViewHolder.secondLine.setTextColor(RED);
            switch (data.getStatus()) {
                case ENTRY_FORBIDDEN_NO_SUCH_TICKET:
                    mViewHolder.firstLine.setText(R.string.access_denied_no_such_code);
                    break;
                case ENTRY_FORBIDDEN_ALREADY_ENTRERED_TODAY:
                    final StringBuilder sb = new StringBuilder().append("Already cleared today!");
                    if (details != null && details.getCheckinLast() != null) {
                        int start = details.getCheckinLast().indexOf('T');
                        int end = details.getCheckinLast().indexOf('.');
                        if (start >= 0 && end >= 0 && start < end) {
                            String time = details.getCheckinLast().substring(start + 1, end);
                            sb.append(" At ").append(time);
                        }
                    }
                    mViewHolder.firstLine.setText(sb.toString());
                    break;
                case ENTRY_FORBIDDEN_ENTRY_ATTEMPTS_EXCEEDED:
                    final StringBuilder sb1 = new StringBuilder().append("Maximum entry count reached!");
                    if (details != null) {
                        sb1.append(" Attempted: ").append(details.getCheckinCount())
                           .append(" Type: ").append(details.getTicketType());
                    }
                    mViewHolder.firstLine.setText(sb1);
                    break;
            }
            mViewHolder.icon.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        }
        return convertView;
    }
}