package kaiser.airqualityapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
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
                Intent intent = new Intent(MainActivity.this, BLEActivity.class);
                startActivity(intent);
            }
        });

        list_EndDevice = new ArrayList<>();

        if(initNode()){
            // 초기화 성공
            Toast.makeText(this,"Success initNode",Toast.LENGTH_LONG).show();
        }else{
            // 초기화 실패
            Toast.makeText(this,"Failure initNode",Toast.LENGTH_LONG).show();
        }
        updateNode();
    }

    private void printNode() {
        for (int i = 0; i < list_EndDevice.size(); i++){
            EndDevice temp = list_EndDevice.get(i);
            Toast.makeText(this, "ID: "+temp.getID()+"\nLatitude: "+temp.getLatitude()+"\nLongitude"+temp.getLongitude(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // ArrayList에 Node정보를 저장
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
        firebaseDatabaseRef.child("/registedNode/").child("00").setValue(new EndDevice("red", "00", 5, 36.367829, 127.341439));
        firebaseDatabaseRef.child("/registedNode/").child("01").setValue(new EndDevice("red", "01", 5, 36.366659, 127.343222));
        firebaseDatabaseRef.child("/registedNode/").child("02").setValue(new EndDevice("red", "02", 5, 36.366096, 127.344004));
        firebaseDatabaseRef.child("/registedNode/").child("03").setValue(new EndDevice("red", "03", 5, 36.365996, 127.345361));
        firebaseDatabaseRef.child("/registedNode/").child("03").setValue(new EndDevice("red", "04", 5, 36.367239, 127.343408));
        firebaseDatabaseRef.child("/registedNode/").child("03").setValue(new EndDevice("red", "05", 5, 36.367930, 127.343891));


        firebaseDatabaseRef.child("/registedNode/").child("10").setValue(new EndDevice("red", "10", 5, 36.368845, 127.341549));
        firebaseDatabaseRef.child("/registedNode/").child("11").setValue(new EndDevice("red", "11", 5, 36.368781, 127.342555));
        firebaseDatabaseRef.child("/registedNode/").child("12").setValue(new EndDevice("red", "12", 5, 36.368616, 127.344055));
        firebaseDatabaseRef.child("/registedNode/").child("13").setValue(new EndDevice("red", "13", 5, 36.368442, 127.345749));

        firebaseDatabaseRef.child("/registedNode/").child("20").setValue(new EndDevice("red", "20", 5, 36.369843, 127.341054));
        firebaseDatabaseRef.child("/registedNode/").child("21").setValue(new EndDevice("red", "21", 5, 36.370309, 127.342816));
        firebaseDatabaseRef.child("/registedNode/").child("22").setValue(new EndDevice("red", "22", 5, 36.370447, 127.343805));
        firebaseDatabaseRef.child("/registedNode/").child("23").setValue(new EndDevice("red", "23", 5, 36.370227, 127.344396));
        firebaseDatabaseRef.child("/registedNode/").child("24").setValue(new EndDevice("red", "24", 5, 36.369303, 127.345931));

        return true;
    }

}