package kaiser.airqualityapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
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
    private String key;

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

        // 데이터베이스 Instance 생성
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Button 클릭 기능 생성
        buttonSaveEndDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewEndDevice(editTextID.getText().toString(), editTextDensity.getText().toString(), editTextLatitude.getText().toString(), editTextLongitude.getText().toString());
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

    private void writeNewEndDevice(String ID, String density, String latitude, String longitude) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        EndDevice endDevice = new EndDevice(null,ID, Double.valueOf(density).doubleValue(), Double.valueOf(latitude).doubleValue(), Double.valueOf(longitude).doubleValue());
        Map<String, Object> postValues = endDevice.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Node/" + "공대 5호관", postValues);
        // childUpdates.put("/user-posts/" + key, postValues);

        firebaseDatabase.getReference().updateChildren(childUpdates);
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
