package com.google.developer.colorvalue;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.developer.colorvalue.data.CardProvider;
import com.google.developer.colorvalue.service.CardService;
import com.google.developer.colorvalue.ui.ColorView;

import java.util.Objects;

public class CardActivity extends AppCompatActivity {

    private static final String TAG = CardActivity.class.getSimpleName();

    TextView hexTextView;
    TextView colorName;
    ColorView colorView;

    String hexCode;
    String cName;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        hexTextView = (TextView) findViewById(R.id.colorHexCodeTextView);
        colorName = (TextView) findViewById(R.id.colorNameTextView);
        colorView = (ColorView) findViewById(R.id.color);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            hexCode = Objects.requireNonNull(intent.getExtras()).getString("hexCode");
            cName = intent.getExtras().getString("colorName");
            id = intent.getExtras().getInt("id");
        }

        hexTextView.setText(hexCode);
        colorName.setText(cName);
        colorView.setBackgroundColor(Color.parseColor(hexCode));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the delete button menu
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Id: " + id);
        Log.d(TAG, "Uri for delete: " + ContentUris.withAppendedId(CardProvider.Contract.CONTENT_URI, id));
        Uri uri = ContentUris.withAppendedId(CardProvider.Contract.CONTENT_URI, id);

        switch (item.getItemId()) {
            case R.id.action_delete:
                if (id > 0) {
                    CardService.deleteCard(this, uri);
                    finish();
                } else {
                    Log.d(TAG, "Color cards was not delete");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
