package com.example.bullt.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullt.ListItems.Data;
import com.example.bullt.ListItems.RecyclerAdapter;
import com.example.bullt.ListItems.RecyclerAdapter2;
import com.example.bullt.R;
import com.example.bullt.Search.RecyclerSearchAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class SearchFragment extends Fragment {
    private View view;
    String userEmail;
    //검색기록 리사이클뷰
    RecyclerView SearchRecyclerView;
    private RecyclerSearchAdapter RsAdapter;
    ArrayList<String> s_list;


    public static Context mContext; // Context 변수 선언
    public String a; // Adapter 로 전달할 변수 선언
    //옷,바지,신발,스웨터,패딩,액세서리
    Button clothes_btn,pants_btn,shoes_btn,sweater_btn,padding_btn,etc_btn;
    // ArrayList -> Json으로 변환
    private static final String SETTINGS_PLAYER_JSON = "settings_item_json";

    //아이템 리사이클뷰
    RecyclerView recyclerView3;
    private RecyclerAdapter adapter3;
    public static final String PREFERENCES_NAME = "rebuild_preference";
    //SearPre
    SearchView searchView;
    ArrayList<Data> list;
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
        s_list = getStringArrayPref(getContext(),SETTINGS_PLAYER_JSON);
        //검색한 자료
//       1.검색결과 리사이클뷰 연결,3.recyclerview 레이아웃 GridLayout 설정,4.ArrayList로 이미지 정보 저장
        recyclerView3 = view.findViewById(R.id.SearchResult_recycelrView);
        recyclerView3.setLayoutManager(new GridLayoutManager(getContext(),3));
        list = new ArrayList<>();
        FireBaseDataInit();
        Setinit();

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
    }

    private void FireBaseDataInit(String input){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //파이어베이스 realtime 변수
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        adapter3 = new RecyclerAdapter(getContext(),list);
        recyclerView3.setAdapter(adapter3);

        myRef.child("ListItem").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("aaa","실패");
                }else{
                    //이전 검색 초기화
                    list.clear();
//                          데이터 가져오기
                    Map<String, Object> map = (Map<String, Object>) task.getResult().getValue();
                    for (String keys : map.keySet()) {
                        Map<String, String> child = (Map<String, String>) map.get(keys);

                        //상표명,내용,가격,주소,이미지 주소,데이터베이스 이미지 이름,count
                        String title = String.valueOf(child.get("title"));
                        String content = String.valueOf(child.get("content"));
                        String price =String.valueOf(child.get("price"))+"원";
                        String ref = String.valueOf(child.get("ref"));
                        String image_id = String.valueOf(child.get("id"));
                        int count = Integer.parseInt(String.valueOf(child.get("count")));
                        StorageReference storageRef = storage.getReference(child.get("ImagePath"));

                        //원하는 아이템찾기....

                        if(input.equals(String.valueOf(child.get("search")))){
                            Data item = new Data(title,content,price,ref,storageRef.getPath(),image_id,false,count);
                            Log.e("ccc",title);
                            Log.e("ccc",content);
                            list.add(item);
                        }
                        else{
                            Log.e("ccc",input.contains(title)+"");
                            Log.e("ccc",content);
                        }
                    }
                    Toast.makeText(getContext(), list.size()+"개 검색결과가 있습니다.", Toast.LENGTH_SHORT).show();
                    adapter3.notifyDataSetChanged();
                }
            }
        });
    }
    private void FireBaseDataInit(){
        FirebaseStorage storage = FirebaseStorage.getInstance();


        //파이어베이스 realtime 변수
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

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

                adapter3 = new RecyclerAdapter(getContext(),list);
                recyclerView3.setAdapter(adapter3);

                RsAdapter = new RecyclerSearchAdapter(getContext(),s_list);
                SearchRecyclerView.setAdapter(RsAdapter);

                myRef.child("ListItem").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(!task.isSuccessful()){
                            Log.e("aaa","실패");
                        }else{
                            //이전 검색 초기화
                            list.clear();
//                          데이터 가져오기
                            Map<String, Object> map = (Map<String, Object>) task.getResult().getValue();
                            for (String keys : map.keySet()) {
                                Map<String, String> child = (Map<String, String>) map.get(keys);

                                //상표명,내용,가격,주소,이미지 주소,데이터베이스 이미지 이름,count
                                String title = String.valueOf(child.get("title"));
                                String content = String.valueOf(child.get("content"));
                                String price =String.valueOf(child.get("price"))+"원";
                                String ref = String.valueOf(child.get("ref"));
                                String image_id = String.valueOf(child.get("id"));
                                int count = Integer.parseInt(String.valueOf(child.get("count")));
                                StorageReference storageRef = storage.getReference(child.get("ImagePath"));
//                              원하는 아이템찾기....
                                String[] titles = title.split(" ");
                                String[] contents = content.split(" ");
//                              검색한 입력어 titles와 contents 비교
                                if(Arrays.asList(titles).contains(query)||Arrays.asList(contents).contains(query)||String.valueOf(child.get("search")).contains(query)){
                                    Data item = new Data(title,content,price,ref,storageRef.getPath(),image_id,false,count);
                                    Log.e("ccc",title);
                                    Log.e("ccc",content);
                                    list.add(item);
                                }
                                else{
                                    Log.e("ccc",query.contains(title)+"");
                                    Log.e("ccc",content);
                                }


                            }
                            //중복 검색 거르기
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
                        }
                    }
                });
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

}
