package edu.msu.cse476.cloudhatter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import android.widget.Toast;

import java.io.IOException;

import edu.msu.cse476.cloudhatter.Cloud.Cloud;
public class SaveDlg extends DialogFragment {

    private AlertDialog dlg;

    /**
     * Create the dialog box
     * @param savedInstanceState The saved instance bundle
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle("Save to Cloud");

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.name_dlg, null);
        builder.setView(view);

        // Add an OK button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText editName = (EditText)dlg.findViewById(R.id.editName);
                save(editName.getText().toString());
            }
        });

        // Add a cancel button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel just closes the dialog box
            }
        });

        // Create the dialog box
        dlg = builder.create();
        return dlg;
    }

    /**
     * Actually save the hatting
     * @param name name to save it under
     */
    private void save(final String name) {
        if (!(getActivity() instanceof HatterActivity)) {
            return;
        }
        final HatterActivity activity = (HatterActivity) getActivity();
        final HatterView view = (HatterView) activity.findViewById(R.id.hatterView);
        final Cloud cloud = new Cloud();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Cloud cloud = new Cloud();
                boolean ok;
                try {
                    ok = cloud.saveToCloud(name, view);
                } catch (IOException e) {
                    e.printStackTrace();
                    ok = false;
                }
                if(!ok) {
                    /*
                     * If we fail to save, display a toast
                     */
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                             Toast.makeText(activity, R.string.save_fail, Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }
        }).start();

    }

}
