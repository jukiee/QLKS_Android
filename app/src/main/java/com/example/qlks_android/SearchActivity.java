package com.example.qlks_android;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SearchActivity extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText findNameEditText;
    private EditText findIdEditText;
    private Button findButton;
    private TextView infoTextView;

    private String mParam1;
    private String mParam2;

    public SearchActivity() {
    }

    public static SearchActivity newInstance(String param1, String param2) {
        SearchActivity fragment = new SearchActivity();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm1, container, false);

        findNameEditText = view.findViewById(R.id.FindName);
        findIdEditText = view.findViewById(R.id.FindId);
        findButton = view.findViewById(R.id.btn_find);
        infoTextView = view.findViewById(R.id.info);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAndDisplayInfo();
            }
        });

        return view;
    }
    private void searchAndDisplayInfo() {
        String name = findNameEditText.getText().toString();
        String roomId = findIdEditText.getText().toString();

        String info = searchInJson(name, roomId);

        infoTextView.setText(info);
    }

    private String searchInJson(String name, String roomId) {
        String json = loadJSONFromAsset(requireContext(), "KhachSan.json");

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String roomCode = jsonObject.getString("Code");
                String nameGuest = jsonObject.getJSONObject("Guest").getString("FullName");
                int status = jsonObject.getInt("Status");

                if (nameGuest.equals(name.trim()) || roomCode.equals(roomId)) {
                    if (status == -1) {
                        return "THÔNG TIN ĐÃ TÌM THẤY:\n\n" + "Phòng " + roomCode + " đang trống.";
                    } else if (status == 1) {
                        String fullName = jsonObject.getJSONObject("Guest").getString("FullName");
                        int roomMoney = jsonObject.getJSONObject("Guest").getInt("RoomMoney");

                        return "THÔNG TIN ĐÃ TÌM THẤY:\n\n" +
                                "Họ tên: " + fullName + "\n" +
                                "Mã phòng: " + roomCode + "\n" +
                                "Tình trạng: Đang thuê\n" +
                                "Tiền phòng: " + roomMoney;
                    } else if (status == 0) {
                        return "THÔNG TIN ĐÃ TÌM THẤY:\n" + "Phòng " + roomCode + " đang trong quá trình bảo trì.";
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "THÔNG TIN ĐÃ TÌM THẤY:\n\n" + "Không tìm thấy thông tin phù hợp.";
    }

    private String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}