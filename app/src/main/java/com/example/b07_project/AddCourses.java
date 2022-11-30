package com.example.b07_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCourses#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AddCourses extends Fragment {

    View addCourseView;
    private Button addCourseBtn;
    private EditText inputTitle, inputCode, inputSessions, inputPrereqs;
    private FirebaseDatabase fbDatabase;
    private DatabaseReference dbReference;

    /*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddCourses() {
        // Required empty public constructor
    }
    */

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCourses.
     */

    // TODO: Rename and change types and number of parameters

    /*
    public static AddCourses newInstance(String param1, String param2) {
        AddCourses fragment = new AddCourses();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    */

    private void attachFields(){
        addCourseBtn = addCourseView.findViewById(R.id.addCourseBtn);
        inputTitle = addCourseView.findViewById(R.id.inputTitle);
        inputCode = addCourseView.findViewById(R.id.inputCode);
        inputSessions = addCourseView.findViewById(R.id.inputSessions);
        inputPrereqs = addCourseView.findViewById(R.id.inputPrereqs);
        fbDatabase = FirebaseDatabase.getInstance();
        dbReference = fbDatabase.getReference("Courses"); //have to reference to child still

        addCourseBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addNewCourse(addCourseView);
            }
        });
    }

    public void addNewCourse(View courseView){
        String courseName = inputTitle.getText().toString();
        String courseCode = inputCode.getText().toString();
        String offeringSessions = inputSessions.getText().toString();
        String prereqs = inputPrereqs.getText().toString();
        String[] prereqArr = trimAll(prereqs.split(","));
        String[] offeringArr = lowerAll(trimAll(offeringSessions.split(",")));
        if (courseName.isEmpty()) {
            inputTitle.setError("Enter Valid Course Title");
        }

        if(courseCode.isEmpty()){
            inputCode.setError("Enter Proper Course Code");
        }

        if (offeringSessions.isEmpty()) {
            inputSessions.setError("Enter Valid Course Sessions");
        }
        else{

            dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean allPrereqsValid = true;
                    boolean allOfferingsValid = true;
                    for (int i = 0; i < prereqArr.length; i++){
                        if(!snapshot.hasChild(prereqArr[i].toUpperCase())){
                            allPrereqsValid = false;
                        }
                    }
                    if(snapshot.hasChild(courseCode)) {
                        inputCode.setError("This Course Already Exists");
                    }
                    else if (!allPrereqsValid){
                        inputPrereqs.setError("Prerequisite Course(s) Does Not Exist");
                    }
                    else if(duplicates(prereqArr)){
                        inputPrereqs.setError("Cannot Have Duplicate Prerequisites");
                    }
                    else if(duplicates(offeringArr)){
                        inputSessions.setError("Cannot Have Duplicate Offering Sessions");
                    }
                    else{
                        Course newCourse = new Course(courseName, courseCode, offeringSessions, prereqs);
                        dbReference.child(
                                courseCode).setValue(newCourse).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //Sends you back to main activity to login again
                                    Toast.makeText(getActivity(), "Course Addition Successful", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getActivity(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError error){
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            });


        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addCourseView = inflater.inflate(R.layout.fragment_add_courses, container, false);
        attachFields();
        return addCourseView;
    }

    boolean duplicates (final String[] zipcodelist)
    {
        Set<String> lump = new HashSet<String>();
        for (String i : zipcodelist)
        {
            if(lump.contains(i)) return true;
            lump.add(i);
        }
        return false;
    }

    String[] trimAll(String[] arr){
        for (int i = 0; i<arr.length; i++){
            arr[i] = arr[i].trim().toLowerCase();
        }
        return arr;
    }
    String[] lowerAll(String[] arr){
        for (int i = 0; i<arr.length;i++){
            arr[i] = arr[i].toLowerCase();
        }
        return arr;
    }
}