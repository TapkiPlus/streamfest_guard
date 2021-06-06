package ru.streamfest.guard;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import java.util.HashMap;

import ru.streamfest.guard.model.TicketDetails;
import ru.streamfest.guard.model.TicketLine;

public class TicketLineAdapter extends ArrayAdapter<TicketLine> {

    private final Context context;

    private static final int ENTRY_ALLOWED = 0;
    private static final int ENTRY_FORBIDDEN_NO_SUCH_TICKET = 1;
    private static final int ENTRY_FORBIDDEN_ENTRY_ATTEMPTS_EXCEEDED = 2;
    private static final int ENTRY_FORBIDDEN_ALREADY_ENTRERED_TODAY = 3;

    static class ViewHolder {
        private TextView firstLine;
        private TextView secondLine;
        private ImageView icon;
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
            mViewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            mViewHolder.position = position;
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        final TicketLine data = getItem(position);
        TicketDetails details = data.getDetails();
        if (details != null) {
            mViewHolder.firstLine.setText(details.getOrderId());
        }
        if (data.getStatus() == ENTRY_ALLOWED) {
            mViewHolder.firstLine.setTextColor(Color.GREEN);
            mViewHolder.secondLine.setTextColor(Color.GREEN);
            mViewHolder.icon.setImageResource(android.R.drawable.ic_input_add);
        } else {
            mViewHolder.firstLine.setTextColor(Color.RED);
            mViewHolder.secondLine.setTextColor(Color.RED);
            switch (data.getStatus()) {
                case ENTRY_FORBIDDEN_NO_SUCH_TICKET:
                    final String err = "No such QR code!";
                    mViewHolder.secondLine.setText(err);
                    break;
                case ENTRY_FORBIDDEN_ALREADY_ENTRERED_TODAY:
                    final StringBuilder sb = new StringBuilder().append("Already cleared today!");
                    if (details != null && details.getCheckinLast() != null) {
                        sb.append(" At ").append(details.getCheckinLast());
                    }
                    mViewHolder.secondLine.setText(sb.toString());
                    break;
                case ENTRY_FORBIDDEN_ENTRY_ATTEMPTS_EXCEEDED:
                    final StringBuilder sb1 = new StringBuilder().append("Maximum entry count reached!");
                    if (details != null) {
                        sb1.append(" Attempted: ").append(details.getCheckinCount())
                           .append(" Allowed: ").append(details.getDaysQty());
                    }
                    mViewHolder.secondLine.setText(sb1);
                    break;
            }
            mViewHolder.icon.setImageResource(android.R.drawable.ic_delete);
        }
        return convertView;
    }
}