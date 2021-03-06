package ru.streamfest.guard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import ru.streamfest.guard.model.TicketLine;

public class QRHandler {

    private final Gson gson = new Gson();
    private final Context context;
    private RequestQueue queue;
    private ArrayAdapter<TicketLine> adapter;
    private AlertDialog dialog;

    private String BASE_URL ="https://streamfest.ru";

    public QRHandler(Context context, ListView view) {
        this.context = context;
        this.adapter = new TicketLineAdapter(context, R.layout.activity_main);
        view.setAdapter(adapter);

        // alert if something goes wrong
        this.dialog = createDialog();

        // call queue
        this.queue = Volley.newRequestQueue(this.context);
    }

    private AlertDialog createDialog() {
        return new AlertDialog.Builder(context)
                .setTitle("Couldn't make a query")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.dismiss();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();
    }

    // Instantiate the RequestQueue.
    public void req(String qrCode) {
        final String url = BASE_URL + "/api/checkin?code=" + qrCode;
        final StringRequest req = new StringRequest(Request.Method.GET, url,
            response -> {
                try {
                    TicketLine tl = gson.fromJson(response, TicketLine.class);
                    adapter.insert(tl, 0);
                } catch (Throwable e) {
                    final String msg = "Cannot parse server response!";
                    QRHandler.this.dialog.setMessage(msg);
                    QRHandler.this.dialog.show();
                }
            },
            error -> {
                final StringBuilder bld = new StringBuilder();
                if (error.getLocalizedMessage() != null) {
                    bld.append(" Reason: ")
                       .append(error.getLocalizedMessage());
                }
            }
        );
        queue.add(req);
    }

}
