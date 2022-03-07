package edu.msu.cse476.cloudhatter;

import android.app.AlertDialog;
import android.app.Dialog;
//import android.app.DialogFragment;
import androidx.fragment.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import edu.msu.cse476.cloudhatter.Cloud.Cloud;
import edu.msu.cse476.cloudhatter.Cloud.Models.Hat;


public class LoadingDlg extends DialogFragment {

    /**
     * Id for the image we are loading
     */
    private String catId;

    /**
     * Create the dialog box
     */

    /**
     * Set true if we want to cancel
     */
    private volatile boolean cancel = false;

    private final static String ID = "id";

    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        if(bundle != null) {
            catId = bundle.getString(ID);
        }

        cancel = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.loading);

        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        cancel = true;
                    }
                });


        // Create the dialog box
        final AlertDialog dlg = builder.create();

        // Get a reference to the view we are going to load into
        final HatterView view = (HatterView)getActivity().findViewById(R.id.hatterView);

        /*
         * Create a thread to load the hatting from the cloud
         */
        new Thread(new Runnable() {

            @Override
            public void run() {
                // Create a cloud object and get the XML
                Cloud cloud = new Cloud();
                Hat hat = cloud.openFromCloud(catId);

                if(cancel) {
                    return;
                }

                // hat loaded successfully
                if(hat != null) {
                    view.loadHat(hat);
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            dlg.dismiss();
                            // Success!
                            if(getActivity() instanceof HatterActivity) {
                                ((HatterActivity)getActivity()).updateUI();
                            }
                        }
                    });
                    return;
                }

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        dlg.dismiss();
                        Toast.makeText(view.getContext(),
                                R.string.loading_fail,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
        return dlg;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    /**
     * Save the instance state
     */
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString(ID, catId);
    }

    /**
     * Called when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancel = true;
    }
}
