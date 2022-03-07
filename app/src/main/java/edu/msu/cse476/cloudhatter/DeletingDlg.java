package edu.msu.cse476.cloudhatter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.app.DialogFragment;
import androidx.fragment.app.DialogFragment;
import android.widget.Toast;

import edu.msu.cse476.cloudhatter.Cloud.Cloud;

/**
 * Created by Alireza on 10/16/2016.
 */
public class DeletingDlg extends DialogFragment{
    private final static String ID = "id";
    private final static String NAME = "name";

    private String name;
    private String catId;

    /**
     * Save the id we are deleting in case we have to try again
     */
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString(ID,  catId);
        bundle.putString(NAME, name);
    }

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        if(bundle != null) {
            catId = bundle.getString(ID);
            name = bundle.getString(NAME);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.deleting);

        String message = getString(R.string.delete_sure) + " " + name + "?";
        builder.setMessage(message);

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Just close dialog box, we are done
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                delete();
            }
        });

        // Create the dialog box
        return builder.create();
    }

    private void delete() {
        final HatterView view = (HatterView)getActivity().findViewById(R.id.hatterView);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // Create a cloud object and get the XML
                Cloud cloud = new Cloud();
                if(!cloud.deleteFromCloud(catId)) {
                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), R.string.delete_fail, Toast.LENGTH_SHORT).show();
                        }

                    });
                }

            }

        }).start();
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
