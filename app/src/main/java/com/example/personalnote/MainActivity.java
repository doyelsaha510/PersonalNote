package com.example.personalnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalnote.auth.Login;
import com.example.personalnote.auth.Register;
import com.example.personalnote.model.Note;
import com.example.personalnote.note.AddNote;
import com.example.personalnote.note.EditNote;
import com.example.personalnote.note.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.dimorinny.showcasecard.position.BottomCenter;
import ru.dimorinny.showcasecard.position.Center;
import ru.dimorinny.showcasecard.position.TopLeftToolbar;
import ru.dimorinny.showcasecard.position.TopRightToolbar;
import ru.dimorinny.showcasecard.step.ShowCaseStep;
import ru.dimorinny.showcasecard.step.ShowCaseStepDisplayer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView noteLists;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;
    FloatingActionButton fab;
    Button n, l;

    boolean switchmode = true;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
         editor= sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if (firstStart) {
            showTooltip();
        }

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
    /*    n = findViewById(R.id.night);
        l = findViewById(R.id.light);
        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
*/
        Query query = fStore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);
        // query notes > uuid > mynotes

        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();


        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {
                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                final int code = getRandomColor();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code, null));
                }
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();


                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("code", code);
                        i.putExtra("noteId", docId);
                        v.getContext().startActivity(i);
                    }
                });

                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            menu.setGravity(Gravity.END);
                        }
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent i = new Intent(v.getContext(), EditNote.class);
                                i.putExtra("title", note.getTitle());
                                i.putExtra("content", note.getContent());
                                i.putExtra("noteId", docId);
                                startActivity(i);
                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference docRef = fStore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // note deleted
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error in Deleting Note.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });

                        menu.show();

                    }
                });


            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);

                return new NoteViewHolder(view);
            }
        };


        noteLists = findViewById(R.id.notelist);
        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        fab = findViewById(R.id.addNoteFloat);
        nav_view.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteLists.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
        noteLists.setAdapter(noteAdapter);

        View headerView = nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

        if (user.isAnonymous()) {
            userEmail.setVisibility(View.GONE);
            username.setText("Temporary User");
        } else {
            userEmail.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int count = noteAdapter.getItemCount();
                //Toast.makeText(MainActivity.this, count, Toast.LENGTH_SHORT).show();
                if (count > 3 && user.isAnonymous()) {
                    syncnotealert();
                } else {
                    startActivity(new Intent(view.getContext(), AddNote.class));
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.addNote:
                final int count = noteAdapter.getItemCount();
                //Toast.makeText(MainActivity.this, count, Toast.LENGTH_SHORT).show();
                if (count > 3 && user.isAnonymous()) {
                    syncnotealert();
                } else {
                    startActivity(new Intent(this, AddNote.class));
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                }
                break;

            case R.id.sync:
                if (user.isAnonymous()) {
                    startActivity(new Intent(this, Register.class));
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                } else {
                    Toast.makeText(this, "Your Are Connected.", Toast.LENGTH_SHORT).show();
                }
                break;

            /*case R.id.logout:
                checkUser();
                break;*/

            case R.id.nighttheme:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("isDarkModeOn", true);
                editor.apply();
                Toast.makeText(this, "switching", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;

            case R.id.daytheme:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("isDarkModeOn", false);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
            default:
                Toast.makeText(this, "Coming soon.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void showTooltip() {
        new ShowCaseStepDisplayer.Builder(MainActivity.this)

                .addStep(new ShowCaseStep(new BottomCenter(), "Add Your first note"))
                .addStep(new ShowCaseStep(new TopRightToolbar(), "settings here you can choose color of notes, change avatar and many more "))
                .addStep(new ShowCaseStep(new TopLeftToolbar(), "profile section where you can sync notes ,add new notes, and many more"))
                .build().start();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void checkUser() {
        // if user is real or not
        if (user.isAnonymous()) {
            displayAlert();
        } else {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), Splash.class));
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure ?")
                .setMessage("You are logged in with Temporary Account. Logging out will Delete All the notes.")
                .setPositiveButton("Sync Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ToDO: delete all the notes created by the Anon user


                        // TODO: delete the anon user

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //startActivity(new Intent(getApplicationContext(), Splash.class));
                                //overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                                finish();
                            }
                        });
                    }
                });

        warning.show();
    }

    private void syncnotealert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Exhausted Temporary notes")
                .setMessage("Because you are logged in with temporary account you only have access to create 10 notes\n" +
                        "If you don't want to loose your notes then sync all your notes.")
                .setPositiveButton("Sync Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //fab.setEnabled(false);
                    }
                }).setCancelable(false);
        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {

            Toast.makeText(this, "Settings Menu is Clicked.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent;
        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }

    private int getRandomColor() {

        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

}