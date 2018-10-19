package com.example.minutecrazy.sqlit1;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private StudentAdapter mAdapter;
    private List<Student> studentList = new ArrayList<>();
    private RecyclerView syudentsRecyclerView;
    private Button addButton;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.button_add);
        syudentsRecyclerView = findViewById(R.id.re_std);

        db = new DatabaseHelper(this);
        studentList.addAll(db.getAllStudent());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStudentDialog(false,null,-1);
            }
        });

        mAdapter = new StudentAdapter(this,studentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        syudentsRecyclerView.setLayoutManager(mLayoutManager);
        syudentsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        syudentsRecyclerView.setAdapter(mAdapter);

        //recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener()

        syudentsRecyclerView.addOnItemTouchListener(new RecyclerTouchListner(getApplicationContext(),
                syudentsRecyclerView, new RecyclerTouchListner.ClickListener(){
            @Override
            public void onClick(View view,final int p) { }

            @Override
            public void onLongClick(View view, int p) {
                showActionDialog(p);
            }
        }));
    }


    private void createStudent(String id, String name, String track){
        db.insertStudent(id, name , track);
        Student student = db.getStudent(id);
        if (student != null){
            studentList.add(0,student);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateStudent(String id, String name, String track, int position){
        Student student = studentList.get(position);

        student.setId(id);
        student.setName(name);
        student.setTrack(track);

        db.updateStudent(student);

        studentList.set(position, student);
        mAdapter.notifyItemChanged(position);
    }

    private void deleteStudent(int position){
        db.deleteStudent(studentList.get(position));

        studentList.remove(position);
        mAdapter.notifyItemChanged(position);
    }

    private void showStudentDialog(final boolean shouldUpdate, final Student student, final int position){
        LayoutInflater layoutInflaterAndriod = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndriod.inflate(R.layout.student_dialog,null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputId = view.findViewById(R.id.edit_text_id);
        final EditText inputName = view.findViewById(R.id.edit_text_name);
        final EditText inputTrack = view.findViewById(R.id.edit_text_track);
        TextView dialogTitle = view.findViewById(R.id.text_view_title);
        dialogTitle.setText(!shouldUpdate ? "New Student" : "Edit student");

        if (shouldUpdate && student != null){
            inputId.setText(student.getId());
            inputName.setText(student.getName());
            inputTrack.setText(student.getTrack());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("cancle",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(inputId.getText().toString())){
                    Toast.makeText(MainActivity.this,"Enter ID!",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    alertDialog.dismiss();
                }

                if (shouldUpdate && student != null){
                    updateStudent(inputId.getText().toString(),
                            inputName.getText().toString(),
                            inputTrack.getText().toString(),
                            position);
                }else {
                    createStudent(inputId.getText().toString(),inputName.getText().toString(),inputTrack.getText().toString());
                }
            }
        });
    }


    private void showActionDialog(final int position){
        CharSequence colors[] = new CharSequence[]{"Edit","Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    showStudentDialog(true,studentList.get(position),position);
                }else {
                    deleteStudent(position);
                }
            }
        });
        builder.show();
    }
}
