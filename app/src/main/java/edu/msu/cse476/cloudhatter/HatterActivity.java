package edu.msu.cse476.cloudhatter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class HatterActivity extends AppCompatActivity {

    private static final String PARAMETERS = "parameters";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hatter);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        /*
         * Set up the spinner
         */

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hats_spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        getSpinner().setAdapter(adapter);
        getSpinner().setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int pos, long id) {
                getHatterView().setHat(pos);
                getColorButton().setEnabled(pos == HatterView.HAT_CUSTOM);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
        /*
         * Restore any state
         */
        if(savedInstanceState != null) {
            getHatterView().getFromBundle(PARAMETERS, savedInstanceState);

        }
        /**
         * Ensure the user interface is up to date
         */
        updateUI();
    }
    /**
     * The hatter view object
     */
    private HatterView getHatterView() {
        return (HatterView) findViewById(R.id.hatterView);
    }

    /**
     * The color select button
     */
    private Button getColorButton() {
        return (Button)findViewById(R.id.buttonColor);
    }

    /**
     * The feather checkbox
     */
    private CheckBox getFeatherCheck() {
        return (CheckBox)findViewById(R.id.checkFeather);
    }

    /**
     * The hat choice spinner
     */
    private Spinner getSpinner() {
        return (Spinner) findViewById(R.id.spinnerHat);
    }

    /**
     * Called when it is time to create the options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hatter, menu);
        return true;
    }
    /**
     * Handle options menu selections
     * @param item Menu item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        //
        //  Note we are using nested if instead of a switch statement
        //  since in gradle version 5 resource id's are not final
        //
        if (itemId == R.id.menu_about) {
            AboutDlg dlg = new AboutDlg();
            dlg.show(getSupportFragmentManager(), "About");
            return true;
        } else if(itemId == R.id.menu_reset) {
            getHatterView().reset();
            return true;
        } else if (itemId == R.id.menu_load) {
            LoadDlg dlg2 = new LoadDlg();
            dlg2.show(getSupportFragmentManager(), "load");
            return true;
        } else if (itemId == R.id.menu_save) {
            SaveDlg dlg3 = new SaveDlg();
            dlg3.show(getSupportFragmentManager(), "save");
            return true;
        } else if (itemId == R.id.menu_delete) {
                DeleteDlg dlg4 = new DeleteDlg();
                dlg4.show(getSupportFragmentManager(), "Delete");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Ensure the user interface components match the current state
     */
    public void updateUI() {
        getSpinner().setSelection(getHatterView().getHat());
        getFeatherCheck().setChecked(getHatterView().getFeather());
        getColorButton().setEnabled(getHatterView().getHat() == HatterView.HAT_CUSTOM);
    }

    public void setUri(Uri uri) {
        getHatterView().setImageUri(uri);
    }

    ActivityResultLauncher<Intent> pickImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        setUri(imageUri);
                    }
                }
            });

    public ActivityResultLauncher<Intent> pickColorResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        int color = data.getIntExtra(ColorSelectActivity.COLOR, Color.BLACK);
                        getHatterView().setColor(color);
                    }
                }
            });

    /**
     * Handle the color select button
     * @param view Button view
     */
    public void onColor(View view) {
        // Get a picture from the gallery
        Intent intent = new Intent(this, ColorSelectActivity.class);
        //startActivityForResult(intent, SELECT_COLOR);
        pickColorResultLauncher.launch(intent);
    }

    /**
     * Handle a Picture button press
     */
    public void onPicture(View view) {
        // Bring up the picture selection dialog box
        PictureDlg dialog = new PictureDlg();
        dialog.show(getSupportFragmentManager(), null);
    }

    public void onFeather(View view) {
        getHatterView().setFeather(getFeatherCheck().isChecked());
        updateUI();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getHatterView().putToBundle(PARAMETERS, outState);
    }
}