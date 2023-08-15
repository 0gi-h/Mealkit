package com.example.last_last_cap;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.last_last_cap.R;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {

    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> data;

    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        searchView = view.findViewById(R.id.searchView);
        listView = view.findViewById(R.id.listView);
        data = new ArrayList<>();
        data.add("오늘의 랜덤 음식");
        data.add("김치찌개");
        data.add("된장찌개");
        data.add("제육 볶음");
        data.add("오징어볶음");
        data.add("볶음김치");
        data.add("참치김밥");
        data.add("우동");
        data.add("돈까스");
        data.add("계란찜");
        data.add("간장계란밥");
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("검색어를 입력하시오");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(removeSpaces(newText), new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if (count == 0) {
                            // 필터링 결과가 없을 때의 동작
                        }
                    }
                });
                return true;
            }

            private String removeSpaces(String text) {
                return text.replaceAll(" ", "");
            }

        });

        return view;
    }

}
