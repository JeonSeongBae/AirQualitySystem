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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ArrayList<EndDevice> list_EndDevice;
    private BLE ble;
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
    private String key;
    private FirebaseStorage firebaseStorage;
    private StorageReference firebaseStorageRef;
    private Button buttonChoose;
    private Button buttonUpload;
    private ImageView imageViewUpload;
    private Uri filePath;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        buttonUpload = findViewById(R.id.buttonUpload);
        imageViewUpload = findViewById(R.id.imageViewUpload);

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
                //writeNewEndDevice(editTextID.getText().toString(), editTextDensity.getText().toString(), editTextLatitude.getText().toString(), editTextLongitude.getText().toString());
                createUser(editTextID.getText().toString(), editTextDensity.getText().toString());
            }
        });
        
        // Choose Button 클릭 기능 생성
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Select Image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });
        
        // Upload Button 클릭 기능 생성
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        list_EndDevice = new ArrayList<>();
        initNode();
        ble = new BLE();
        ble.connectedID();
        updateNode();
        synchronizeTime();

        connect_BLE();

        writeData(receiveData());
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d("MainActivity", "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageViewUpload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //upload the file
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".jpg";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://lg01-ba3b9.appspot.com").child(filename);

            //올라가거라...
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeNewEndDevice(String ID, String density, String latitude, String longitude) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        EndDevice endDevice = new EndDevice(null,ID, Double.valueOf(density).doubleValue(), Double.valueOf(latitude).doubleValue(), Double.valueOf(longitude).doubleValue());
        Map<String, Object> postValues = endDevice.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Node/" + "공대 5호관", postValues);
        // childUpdates.put("/user-posts/" + key, postValues);

        firebaseDatabaseRef.updateChildren(childUpdates);
    }

    // 앱 투 DB
    private void writeData(double data) {
        // data를 블루투스 연결된 ID로 update
        // 핸들링된 정보를 write
    }
    //엔드디바이스 투 앱
    private double receiveData() {
        // byte로 받은 정보를 가공해서 double로 저장
        byte data = 0;
        // 처리해서 double 형태로 바꿔서 리턴
        return 0.0;
    }

    private EndDevice readData(String ID){
        EndDevice endDevice = new EndDevice(null,"ID", 50, 0.0, 0.0);
        return null;
    }

    private void connect_BLE() {
        // 연결되어있지 않을경우 검색하여 연결한다.
        BLE ble = new BLE();
        ble.stop_BLE();
    }

    private void synchronizeTime() {
    }

    // DB 투 앱
    // PIN 위치 표시
    private void updateNode() {
        // DB내용을 통해 어플리케이션 업데이팅
        // list_EndDevice에 들어있는 노드들을 반복하여 updating
        EndDevice a = list_EndDevice.get(0);
        decideColor(a);
        mapPIN(a);
    }

    // PIN 위치 표시
    private boolean initNode() {
        // 데이터 베이스에서 노드 정보 불러옴
        // 정보를 기반으로 class 생성
        EndDevice endDevice = new EndDevice(null,"ID", 50,0.0,0.0);
        decideColor(endDevice);
        mapPIN(endDevice);
        list_EndDevice.add(endDevice);
        return true;
    }

    private void mapPIN(EndDevice a) {
        // 지도 api에 PIN을 찍어주는 메소드
    }

    // 데이터 핸들링
    private void decideColor(EndDevice a) {
        // 알고리즘을 통해 농도로 색상 결정
        a.setColor("red");
    }
}