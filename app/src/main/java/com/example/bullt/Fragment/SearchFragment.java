package com.example.bullt.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullt.Data.ItemData;
import com.example.bullt.ListItems.RecyclerAdapter;
import com.example.bullt.R;
import com.example.bullt.Search.RecyclerSearchAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class SearchFragment extends Fragment {
    private View view;

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

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_search_fragment, container, false);
        //검색기록 1.검색바 아이디 연결,2.검색어 리사이클뷰 연결,3.recyclerview 레이아웃 LinearLayout 설정,4.ArrayList로 검색어 저장 5.s_list 검색기록 불러오기
        searchView = view.findViewById(R.id.SearchView1);
        SearchRecyclerView = view.findViewById(R.id.search_recyclerView);
        SearchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        s_list = new ArrayList<>();
        try {
            String userUid = firebaseAuth.getCurrentUser().getUid();
            SETTINGS_PLAYER_JSON = userUid;
        }catch(Exception e){SETTINGS_PLAYER_JSON = "123";}
        s_list = getStringArrayPref(getContext(),SETTINGS_PLAYER_JSON);
        //검색한 자료
//       1.검색결과 리사이클뷰 연결,3.recyclerview 레이아웃 GridLayout 설정,4.ArrayList로 이미지 정보 저장
        recyclerView3 = view.findViewById(R.id.SearchResult_recycelrView);
        recyclerView3.setLayoutManager(new GridLayoutManager(getContext(),3));
        list = new ArrayList<>();
        Setinit();
        setSearchView();
        return view;
    }

//  테이블레이아웃으로 만든 버튼 설정
    private void Setinit(){
        clothes_btn = view.findViewById(R.id.clothes_btn);

        clothes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("옷");
            }
        });
        pants_btn = view.findViewById(R.id.pants_btn);

        pants_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("팬츠");
            }
        });
        shoes_btn = view.findViewById(R.id.shoes_btn);

        shoes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("신발");
           }
        });
        sweater_btn = view.findViewById(R.id.sweater_btn);
        sweater_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("스웨터");
            }
        });

        padding_btn = view.findViewById(R.id.padding_btn);
        padding_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("패딩");
            }
        });
        etc_btn = view.findViewById(R.id.etc_btn);
        etc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseDataInit("그외");
            }
        });

        // TODO: 2021-12-04 SharedPreferences를 사용하여 검색기록중에 하나를 누르면 그내용을 searchView.setQuery해주기
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
//      searchview 텍스트 포커스 할때
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    searchView.setQuery("",true);
                    SearchRecyclerView.setVisibility(View.VISIBLE);
                    RsAdapter = new RecyclerSearchAdapter(getContext(),s_list);
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
                Toast.makeText(getContext(), list.size()+"개 검색결과가 있습니다.", Toast.LENGTH_SHORT).show();
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
        setStringArrayPref(getContext(), SETTINGS_PLAYER_JSON, s_list);
    }
    private void getData(String query){
        adapter3 = new RecyclerAdapter(getContext(),list,1);
        recyclerView3.setAdapter(adapter3);
        myRef = database.getReference();
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
    public void onDestroyView() {
        super.onDestroyView();
    }
}
