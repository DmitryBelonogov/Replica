package com.nougust3.diary.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends MvpAppCompatActivity {

    private TextView inboxCount;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fab;

    private long notebookId;
    private long noteId;

    //private SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

    //public SharedPreferences getPrefs() {
        //return sp;
    //}

    public void initFAB() {
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditorActivity.class);
                intent.putExtra("creation5", noteId);
                intent.putExtra("notebook", notebookId);
                startActivityForResult(intent, 1);
            }
        });
    }

    public FloatingActionButton getFAB() {
        return fab;
    }

    public void initNavigation() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigation);

        inboxCount = (TextView) navigationView.getMenu().getItem(0).getActionView();
        inboxCount.setGravity(Gravity.CENTER_VERTICAL);
        inboxCount.setTextColor(ContextCompat.getColor(getContext(), R.color.navigation_text));

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if(item.getItemId() == R.id.actionInboxItem) {
                            Intent intent = new Intent(getContext(), NotesActivity.class);
                            intent.putExtra("notebookId", 0L);
                            startActivityForResult(intent, 1);
                        }
                        else if(item.getItemId() == R.id.actionTrashItem) {
                            Intent intent = new Intent(getContext(), TrashActivity.class);
                            startActivityForResult(intent, 1);
                        }
                        else if(item.getItemId() == R.id.actionNotebooksItem) {
                            Intent intent = new Intent(getContext(), NotebooksActivity.class);
                            startActivityForResult(intent, 1);
                        }
                        else if(item.getItemId() == R.id.actionSettingsItem) {
                            Intent intent = new Intent(getContext(), SettingsActivity.class);
                            startActivity(intent);
                        }

                        return false;
                    }
                });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void updateCounter() {
        inboxCount.setText(DBHelper.getInstance().getInboxSize());
    }

    public Context getContext() {
        return getApplicationContext();
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNotebookId(long notebookId) {
        this.notebookId = notebookId;
    }

    public long getNotebookId() {
        return notebookId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
    }
}