package com.example.bookandpostroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class favorites extends AppCompatActivity implements fragmentforhome33favorite.Listenerhome2, fragmentforhome33favorite.Favoritelistener {
    private fragmentforhome33favorite fragmentHome;
    private emptystatefragment fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);
        Toolbar tb = findViewById(R.id.tbmain);
        setSupportActionBar(tb);

        MyDatabaseHelper db = new MyDatabaseHelper(this);
        int result = db.isTableEmpty();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (result == 0) {
            // Table is empty - show empty state fragment
            fm = new emptystatefragment();
            transaction.replace(R.id.fragment_container, fm);
            android.util.Log.d("DEBUG", "Loaded empty state fragment");
        } else {
            // Table has data - show favorites fragment
            fragmentHome = new fragmentforhome33favorite();
            transaction.replace(R.id.fragment_container, fragmentHome);
            android.util.Log.d("DEBUG", "Loaded favorites fragment");
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Your back press logic here
                // Example: finish activity
                Intent i=new Intent(favorites.this,studentinfoandrooms.class);
                startActivity(i);
                finish();
            }
        });

        transaction.commit();
    }

    @Override
    public void favoriteclick(int position, String s) {
        android.util.Log.d("DEBUG", "favoriteclick called - position: " + position + ", item: " + s);

        MyDatabaseHelper db = new MyDatabaseHelper(this);
        boolean deleted = db.removeFavorite(s);

        if (deleted) {
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();

            if (fragmentHome != null) {
                android.util.Log.d("DEBUG", "Calling removeItemFromAdapter");
                fragmentHome.removeItemFromAdapter(position);

                // Check if this was the last favorite item
                int result = db.isTableEmpty();
                if (result == 0) {
                    // No more favorites - switch to empty state
                    switchToEmptyState();
                }
            } else {
                android.util.Log.e("DEBUG", "Fragment home is null, cannot remove item");
            }
        } else {
            Toast.makeText(this, "Error removing favorite", Toast.LENGTH_SHORT).show();
        }
    }

    private void switchToEmptyState() {
        android.util.Log.d("DEBUG", "Switching to empty state");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fm = new emptystatefragment();
        transaction.replace(R.id.fragment_container, fm);
        transaction.commit();

        // Clear the reference to the favorites fragment
        fragmentHome = null;
    }

    // Method to switch back to favorites fragment (call this when favorites are added)
    public void switchToFavoritesFragment() {
        android.util.Log.d("DEBUG", "Switching to favorites fragment");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragmentHome = new fragmentforhome33favorite();
        transaction.replace(R.id.fragment_container, fragmentHome);
        transaction.commit();

        // Clear the reference to the empty state fragment
        fm = null;
    }

    @Override
    public void itemclicked3(int position, String s) {
        Intent i = new Intent(favorites.this, roominfoandbookfinal2.class);
        i.putExtra(roominfoandbookfinal2.message, s);
        startActivity(i);
    }


}