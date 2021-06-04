package ru.streamfest.guard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.UUID;

public class QRHandler {

    private final Context context;
    private RequestQueue queue;
    private ArrayAdapter<String> adapter;
    private AlertDialog dialog;

    private String BASE_URL ="http://sf.tagobar.ru";

    public QRHandler(Context context, ListView view) {
        this.context = context;
        this.adapter = new ArrayAdapter<String>(context, R.layout.activity_main);
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
    public void req(String UNUSED_CODE) {
        final String qrCode = UUID.randomUUID().toString();
        final String url = BASE_URL + "/api/checkin?code=" + qrCode;
        final StringRequest req = new StringRequest(Request.Method.GET, url,
            response -> {
                // Display the first 500 characters of the response string.
                adapter.add(response);
            },
            error -> {
                final String msg = "The server is not available right now: " + error.getLocalizedMessage();
                QRHandler.this.dialog.setMessage(msg);
                QRHandler.this.dialog.show();
            }
        );

        // Add the request to the RequestQueue.
        queue.add(req);
    }

}
