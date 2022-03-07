package edu.msu.cse476.cloudhatter;

import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Dialog box that allows a user to input a URL for
 * an image or choose an image from the chooser.
 */
public class PictureDlg extends DialogFragment {
    
    /**
     * Retain the last entered URL so we can just backspace to change the 
     * image if we like.
     */
    private static String lastUrl = "http://www.cse.msu.edu/~dennisp/izzo.jpg";

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(final Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.url_dlg_heading);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.url_dlg, null))
               // Add action buttons
               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       // This doesn't actually do anything because I 
                       // replace it with code that is included when the
                       // dialog box is shown.
                   }
               })
               
               .setNeutralButton(R.string.gallery, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       // Pass the selection process off to the gallery
                       Intent intent = new Intent();
                       intent.setType("image/*");
                       intent.setAction(Intent.ACTION_GET_CONTENT);
                       HatterActivity myActivity = (HatterActivity) getActivity();

                       assert myActivity != null;
                       myActivity.pickImageResultLauncher.launch(intent);//startActivityForResult(intent, HatterActivity.SELECT_PICTURE);

                   }
               })
        
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel just closes the dialog box
                    }
                });
        
        // Create the dialog box
        final AlertDialog dlg = builder.create();
        
        // By default the Ok button dismisses the dialog box. I want to
        // do error checking first, so this captures the click message
        // and replaces the regular code with my code that can check
        // before dismissing
        dlg.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                
                final EditText urlText = dlg.findViewById(R.id.editUrl);
                if(bundle == null) {
                    urlText.setText(lastUrl);
                    urlText.selectAll();
                }
                
                Button b = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // Do we have a valid URL?
                        try {
                            URL url = new URL(urlText.getText().toString());
                            Uri uri = Uri.parse(url.toExternalForm());
                            lastUrl = url.toExternalForm();
                            dlg.dismiss();
                            if(getActivity() instanceof HatterActivity) {
                                ((HatterActivity)getActivity()).setUri(uri);
                            }
                        } catch (MalformedURLException e) {
                            // If invalid, force the user to try again
                            urlText.requestFocus();
                            urlText.selectAll();
                        }
                    }
                        
                });
            }
        });
        
        return dlg;
    }
}
