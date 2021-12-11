package com.example.bullt.Search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullt.Data.ItemData;
import com.example.bullt.ListItems.RecyclerAdapter;
import com.example.bullt.R;
import com.example.bullt.Activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchActivity extends AppCompatActivity implements OnItemClick1 {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    //검색기록 리사이클뷰
    RecyclerView SearchRecyclerView;
    private RecyclerSearchAdapter RsAdapter;
    ArrayList<String> s_list;


    public static Context mContext; // Context 변수 선언
    public String a; // Adapter 로 전달할 변수 선언
    //옷,바지,신발,스웨터,패딩,액세서리
    Button clothes_btn,pants_btn,shoes_btn,sweater_btn,padding_btn,etc_btn;
    // ArrayList -> Json으로 변환
    ImageButton back_ib;
    private static String SETTINGS_PLAYER_JSON;

    //아이템 리사이클뷰
    RecyclerView recyclerView3;
    private RecyclerAdapter adapter3;
    public static final String PREFERENCES_NAME = "rebuild_preference";
    //SearPre
    SearchView searchView;
    ArrayList<ItemData> list;

    FirebaseDatabase database;
    DatabaseReference myRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //검색기록 1.검색바 아이디 연결,2.검색어 리사이클뷰 연결,3.recyclerview 레이아웃 LinearLayout 설정,4.ArrayList로 검색어 저장 5.s_list 검색기록 불러오기
        searchView = findViewById(R.id.SearchView1);
        SearchRecyclerView = findViewById(R.id.search_recyclerView);
        SearchRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        s_list = new ArrayList<>();
        try {
            String userUid = firebaseAuth.getCurrentUser().getUid();
            SETTINGS_PLAYER_JSON = userUid;
        }catch(Exception e){SETTINGS_PLAYER_JSON = "123";}
        s_list = getStringArrayPref(getApplicationContext(),SETTINGS_PLAYER_JSON);
        //검색한 자료
//       1.검색결과 리사이클뷰 연결,3.recyclerview 레이아웃 GridLayout 설정,4.ArrayList로 이미지 정보 저장
        recyclerView3 = findViewById(R.id.SearchResult_recycelrView);
        recyclerView3.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        list = new ArrayList<>();
        Setinit();
        setSearchView();
    }

    //  테이블레이아웃으로 만든 버튼 설정
    private void Setinit(){

        back_ib = findViewById(R.id.back_ib);
        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        clothes_btn = findViewById(R.id.clothes_btn);

        clothes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("옷");
            }
        });
        pants_btn = findViewById(R.id.pants_btn);

        pants_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("팬츠");
            }
        });
        shoes_btn = findViewById(R.id.shoes_btn);

        shoes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("신발");
            }
        });
        sweater_btn = findViewById(R.id.sweater_btn);
        sweater_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("스웨터");
            }
        });

        padding_btn = findViewById(R.id.padding_btn);
        padding_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("패딩");
            }
        });
        etc_btn = findViewById(R.id.etc_btn);
        etc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("그외");
            }
        });

    }


    private void FireBaseDataInit(String input){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //파이어베이스 realtime 변수
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
//      데이터 가져오기
        getData(input);
    }
    private void setSearchView(){

        RsAdapter = new RecyclerSearchAdapter(getApplicationContext(),s_list,this);

        //TextFocus를 바꿀때
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                        @Override
                        public boolean onClose() {
                            return true;
                        }
                    });
                }
            }
        });
        //      searchview 텍스트 포커스 할때
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    searchView.setQuery("",true);
                    SearchRecyclerView.setVisibility(View.VISIBLE);
                    SearchRecyclerView.setAdapter(RsAdapter);
                }
            }
        });
//      searchview 텍스트 내용 담기
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //          텍스트 입력 완료
            @Override
            public boolean onQueryTextSubmit(String query) {

                getData(query);
                if(s_list.size()==0){
                    s_list.add(query);
                }
                else {
                    boolean tf = false;
                    for (int i = 0; i < s_list.size(); i++) {
                        //검색기록이 있으면
                        if (s_list.get(i).equals(query)) {
                            tf = true;
                            break;
                        }
                    }
                    if(!tf) s_list.add(query);
                }
                Toast.makeText(getApplicationContext(), list.size()+"개 검색결과가 있습니다.", Toast.LENGTH_SHORT).show();
                adapter3.notifyDataSetChanged();
                RsAdapter.notifyDataSetChanged();

                //입력완료하면 포커스 해제,아이템 목록 안보임
                searchView.clearFocus();
                SearchRecyclerView.setVisibility(View.GONE);
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }

    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }

        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }

        editor.apply();
    }

    private ArrayList getStringArrayPref(Context context, String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList urls = new ArrayList();

        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    @Override
    public void onPause() {
        super.onPause();
        setStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON, s_list);
    }
    private void getData(String query){
        adapter3 = new RecyclerAdapter(getApplicationContext(),list,1);
        recyclerView3.setAdapter(adapter3);
        myRef.child("ListItem").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    ItemData itemData = ds.getValue(ItemData.class);
                    String[] titles = itemData.getTitle().split(" ");
                    String[] contents = itemData.getContent().split(" ");
//                              검색한 입력어 titles와 contents 비교
                    if(Arrays.asList(titles).contains(query)||Arrays.asList(contents).contains(query)||itemData.getSearch().contains(query)){
                        list.add(itemData);
                    }
                }
                adapter3.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //에러가 날때 작동

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(String value) {
        searchView.setQuery(value,true);
    }
}
