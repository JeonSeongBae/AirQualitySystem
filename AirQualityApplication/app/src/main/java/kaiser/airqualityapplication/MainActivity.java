package kaiser.airqualityapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ArrayList<EndDevice> list_EndDevice;
    private TextView textViewID;
    private EditText editTextID;
    private TextView textViewDensity;
    private EditText editTextDensity;
    private TextView textViewLatitude;
    private EditText editTextLatitude;
    private TextView textViewLongitude;
    private EditText editTextLongitude;
    private Button buttonSaveEndDevice;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseDatabaseRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference firebaseStorageRef;
    private Button buttonChoose;
    private Uri filePath;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_EndDevice = new ArrayList<EndDevice>();

        // TextView와 EditText 생성
        textViewID = findViewById(R.id.textViewID);
        editTextID = findViewById(R.id.editTextID);
        textViewDensity = findViewById(R.id.textViewDensity);
        editTextDensity = findViewById(R.id.editTextDensity);
        textViewLatitude = findViewById(R.id.textViewLatitude);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        textViewLongitude = findViewById(R.id.textViewLongitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        buttonSaveEndDevice = findViewById(R.id.buttonSaveEndDevice);
        buttonChoose = findViewById(R.id.buttonChoose);

        // 데이터베이스 Instance 생성
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabaseRef = firebaseDatabase.getReference();

        // 스토리지 Instance 생성
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseStorageRef = firebaseStorage.getReference();

        // Auth Instance 생성
        mAuth = FirebaseAuth.getInstance();

        // Button 클릭 기능 생성
        buttonSaveEndDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewEndDevice("공대 5호관", 45, 36.123456, 37.654321);
                //writeNewEndDeviceList();
                // createUser(editTextID.getText().toString(), editTextDensity.getText().toString());
            }
        });
        
        // Choose Button 클릭 기능 생성
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printNode();
            }
        });

        list_EndDevice = new ArrayList<>();
        initNode();
        updateNode();
    }

    private void printNode() {
        for (int i = 0; i < list_EndDevice.size(); i++){
            EndDevice temp = list_EndDevice.get(i);
            Toast.makeText(this, "ID: "+temp.getID()+"\nLatitude: "+temp.getLatitude()+"\nLongitude"+temp.getLongitude(),Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNode() {
        firebaseDatabaseRef.child("registedNode").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EndDevice a = dataSnapshot.getValue(EndDevice.class);
                list_EndDevice.add(a);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void writeNewEndDevice(String ID, double density, double latitude, double longitude) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        EndDevice endDevice = new EndDevice(null,ID, density, latitude, longitude);
        Map<String, Object> postValues = endDevice.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Node/" + endDevice.getID(), postValues);

        firebaseDatabaseRef.updateChildren(childUpdates);
    }


    // PIN 위치 표시
    private boolean initNode() {
        // 데이터 베이스에서 노드 정보 불러옴
        // 정보를 기반으로 class 생성
        firebaseDatabaseRef.child("/registedNode/").child("공대5호관").setValue(new EndDevice("red", "공대5호관", 41.0, 36.366002, 127.345320));
        firebaseDatabaseRef.child("/registedNode/").child("충대정문").setValue(new EndDevice("blue", "충대정문", 12.0, 36.366125, 127.343891));
        firebaseDatabaseRef.child("/registedNode/").child("공대1호관").setValue(new EndDevice("yellow", "공대1호관", 45.0, 36.367810, 127.341391));
        firebaseDatabaseRef.child("/registedNode/").child("교양관").setValue(new EndDevice("red", "교양관", 25.0, 36.368447, 127.345729));
        firebaseDatabaseRef.child("/registedNode/").child("충대후문").setValue(new EndDevice("blue", "충대후문", 21.0, 36.368837, 127.341544));

        return true;
    }

}