package com.example.a16adventure.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.EventAdapter;
import com.example.a16adventure.models.Event;
import com.example.a16adventure.models.EventDataManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends BaseActivity {

    private ImageView btnBack, btnFilter;
    private EditText edtSearch;
    private ChipGroup chipGroupMonths;
    private RecyclerView rvEvents;
    private EventAdapter adapter;
    private List<Event> fullList;
    private List<Event> displayList;

    // Trạng thái bộ lọc
    private int selectedMonth = 0; // 0 = Tất cả
    private String searchQuery = "";
    private List<String> selectedLocations = new ArrayList<>();
    private String selectedTimeStatus = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
        edtSearch = findViewById(R.id.edtSearch);
        chipGroupMonths = findViewById(R.id.chipGroupMonths);
        rvEvents = findViewById(R.id.rvEvents);

        btnBack.setOnClickListener(v -> finish());

        // Load Data
        fullList = EventDataManager.getMockEvents();
        displayList = new ArrayList<>(fullList);
        adapter = new EventAdapter(this, displayList);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(adapter);

        setupChips();
        setupSearch();
        setupFilterBottomSheet();
    }

    private void setupChips() {
        LayoutInflater inflater = LayoutInflater.from(this);
        
        // Chip Tất cả
        Chip chipAll = (Chip) inflater.inflate(R.layout.item_chip_filter, chipGroupMonths, false);
        chipAll.setText("Tất cả");
        chipAll.setId(View.generateViewId());
        chipGroupMonths.addView(chipAll);

        // Các Chip tháng
        for (int i = 1; i <= 12; i++) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_chip_filter, chipGroupMonths, false);
            chip.setText("Tháng " + i);
            chip.setId(View.generateViewId());
            chipGroupMonths.addView(chip);
        }

        chipGroupMonths.check(chipAll.getId());

        chipGroupMonths.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip selectedChip = findViewById(checkedIds.get(0));
            String text = selectedChip.getText().toString();
            
            if (text.equals("Tất cả")) {
                selectedMonth = 0;
            } else {
                try {
                    selectedMonth = Integer.parseInt(text.replace("Tháng ", ""));
                } catch (Exception e) {
                    selectedMonth = 0;
                }
            }
            applyFilters();
        });
    }

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterBottomSheet() {
        btnFilter.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_filter_bottom_sheet, null);
            bottomSheetDialog.setContentView(view);

            Spinner spinnerTime = view.findViewById(R.id.spinnerTime);
            String[] times = {"Tất cả", "Sắp diễn ra", "Đang diễn ra", "Đã kết thúc"};
            ArrayAdapter<String> adapterTime = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, times);
            spinnerTime.setAdapter(adapterTime);

            for (int i = 0; i < times.length; i++) {
                if (times[i].equals(selectedTimeStatus)) {
                    spinnerTime.setSelection(i);
                    break;
                }
            }

            CheckBox cbDoSon = view.findViewById(R.id.cbDoSon);
            CheckBox cbCatBa = view.findViewById(R.id.cbCatBa);
            CheckBox cbKienAn = view.findViewById(R.id.cbKienAn);
            CheckBox cbTrungTam = view.findViewById(R.id.cbTrungTam);

            cbDoSon.setChecked(selectedLocations.contains("Đồ Sơn"));
            cbCatBa.setChecked(selectedLocations.contains("Cát Bà"));
            cbKienAn.setChecked(selectedLocations.contains("Kiến An"));
            cbTrungTam.setChecked(selectedLocations.contains("Trung tâm"));

            MaterialButton btnApply = view.findViewById(R.id.btnApplyFilter);

            btnApply.setOnClickListener(v2 -> {
                selectedTimeStatus = spinnerTime.getSelectedItem().toString();
                selectedLocations.clear();
                if (cbDoSon.isChecked()) selectedLocations.add("Đồ Sơn");
                if (cbCatBa.isChecked()) selectedLocations.add("Cát Bà");
                if (cbKienAn.isChecked()) selectedLocations.add("Kiến An");
                if (cbTrungTam.isChecked()) selectedLocations.add("Trung tâm");

                applyFilters();
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();
        });
    }

    private void applyFilters() {
        displayList.clear();
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US);
        java.util.Date now = new java.util.Date();

        for (Event e : fullList) {
            // 1. Lọc theo thanh tìm kiếm
            boolean matchSearch = e.getName().toLowerCase().contains(searchQuery) || e.getLocation().toLowerCase().contains(searchQuery);
            
            // 2. Lọc theo Chip tháng
            boolean matchMonth = (selectedMonth == 0) || (e.getMonthLimit() == selectedMonth);

            // 3. Lọc theo Checkbox địa điểm
            boolean matchLocation = selectedLocations.isEmpty();
            if (!matchLocation) {
                for (String loc : selectedLocations) {
                    if (e.getLocation().contains(loc)) {
                        matchLocation = true;
                        break;
                    }
                }
            }

            // 4. Lọc theo thời gian diễn ra
            boolean matchTimeStatus = true;
            if (!selectedTimeStatus.equals("Tất cả")) {
                try {
                    java.util.Date eventDate = sdf.parse(e.getDate());
                    long diff = eventDate.getTime() - now.getTime();
                    long days = diff / (1000 * 60 * 60 * 24);

                    if (selectedTimeStatus.equals("Sắp diễn ra")) {
                        matchTimeStatus = days > 0;
                    } else if (selectedTimeStatus.equals("Đã kết thúc")) {
                        matchTimeStatus = days < 0;
                    } else if (selectedTimeStatus.equals("Đang diễn ra")) {
                        matchTimeStatus = days == 0;
                    }
                } catch (Exception ex) {
                    // Nếu lỗi parse date, ưu tiên bỏ qua lọc thời gian
                }
            }

            // Phải thỏa mãn TẤT CẢ các điều kiện (AND logic)
            if (matchSearch && matchMonth && matchLocation && matchTimeStatus) {
                displayList.add(e);
            }
        }
        adapter.updateList(displayList);
    }
}
