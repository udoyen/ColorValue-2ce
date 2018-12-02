package com.google.developer.colorvalue;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.developer.colorvalue.data.Card;
import com.google.developer.colorvalue.data.CardAdapter;
import com.google.developer.colorvalue.data.CardProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        CardAdapter.ListItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int CARD_LOADER_ID = 3;
    public Cursor mCursor;
    CardAdapter mCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

        getLoaderManager().initLoader(CARD_LOADER_ID, null, this);

        mCardAdapter = new CardAdapter(this);
        recycler.setAdapter(mCardAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addColorIntent = new Intent(MainActivity.this, AddCardActivity.class);
                startActivity(addColorIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                this,
                CardProvider.Contract.CONTENT_URI,
                null,
                null,
                null,
                null

        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        Log.d(TAG, "Cursor: " + mCursor.getCount());
        mCardAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCardAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(int clickedIndex) {
        Card card = mCardAdapter.getItem(clickedIndex);

        String hexCode = "";
        String colorName = "";
        int cId = 0;

        if (card != null) {
            hexCode = card.getHex();
            colorName = card.getName();
            cId = card.getID();
        }

        Intent intent = new Intent(this, CardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("hexCode", hexCode);
        intent.putExtra("colorName", colorName);
        intent.putExtra("id", cId);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Update the color cards after an addition
        getLoaderManager().initLoader(CARD_LOADER_ID, null, this);
        mCardAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getLoaderManager().initLoader(CARD_LOADER_ID, null, this);
        mCardAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().initLoader(CARD_LOADER_ID, null, this);
        mCardAdapter.notifyDataSetChanged();
    }
}
