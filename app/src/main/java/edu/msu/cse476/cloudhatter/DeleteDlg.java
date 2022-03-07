package edu.msu.cse476.cloudhatter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
//import android.app.DialogFragment;
import androidx.fragment.app.DialogFragment;

import edu.msu.cse476.cloudhatter.Cloud.Cloud;

public class DeleteDlg extends DialogFragment{

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.delete_fm_title);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.catalog_dlg, null);
        builder.setView(view)
                // Add a cancel button
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel just closes the dialog box
                    }
                });

        // Find the list view
        ListView list = (ListView)view.findViewById(R.id.listHattings);

        // Create the dialog box
        final AlertDialog dlg = builder.create();

        // Create a Cloud object and an adapter
        Cloud cloud = new Cloud();
        final Cloud.CatalogAdapter adapter = new Cloud.CatalogAdapter(list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // Get the id of the one we want to load
                String catId = adapter.getId(position);
                String name = adapter.getName(position);

                // Dismiss this dialog box
                dlg.dismiss();

                // Create and display the loading dialog box
                DeletingDlg deleteDlg = new DeletingDlg();
                deleteDlg.setCatId(catId);
                deleteDlg.setName(name);
                deleteDlg.show(getActivity().getSupportFragmentManager(), "Deleting");

            }

        });

        return dlg;
    }

}
