package com.example.qlks_android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ServiceActivity extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    Float money = Float.valueOf(0);
    private CheckBox doUong1CheckBox;
    private CheckBox doUong2CheckBox;
    private CheckBox doUong3CheckBox;
    private CheckBox doAn1CheckBox;
    private TextView price_foods;
    private TextView infoTextView;
    private Button btn_Oder;

    public ServiceActivity() {
    }

    public static ServiceActivity newInstance(String param1, String param2) {
        ServiceActivity fragment = new ServiceActivity();
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
        View view = inflater.inflate(R.layout.fm3, container, false);

        doUong1CheckBox = view.findViewById(R.id.DoUong1);
        doUong2CheckBox = view.findViewById(R.id.DoUong2);
        doUong3CheckBox = view.findViewById(R.id.DoUong3);
        doAn1CheckBox = view.findViewById(R.id.DoAn1);
        infoTextView = view.findViewById(R.id.info);
        price_foods = view.findViewById(R.id.price_foods);
        btn_Oder = view.findViewById(R.id.btn_oder);

        doUong1CheckBox.setOnCheckedChangeListener(this::onCheckBoxChanged);
        doUong2CheckBox.setOnCheckedChangeListener(this::onCheckBoxChanged);
        doUong3CheckBox.setOnCheckedChangeListener(this::onCheckBoxChanged);
        doAn1CheckBox.setOnCheckedChangeListener(this::onCheckBoxChanged);

        btn_Oder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Xác nhận đặt hàng: " + money + " VND");
                builder.setMessage("Nhập mã phòng:");

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Đặt hàng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String roomCode = input.getText().toString().trim();
                        if (roomCode.isEmpty()) {
                            Toast.makeText(getActivity(), "Vui lòng nhập mã phòng", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String json = loadJSONFromAsset(requireContext(), "KhachSan.json");

                        try {
                            JSONArray jsonArray = new JSONArray(json);
                            boolean isRoomCodeExist = false;
                            boolean isStatusOne = false;

                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(j);
                                String code = jsonObject.getString("Code");
                                int status = jsonObject.getInt("Status");

                                if (code.equals(roomCode)) {
                                    isRoomCodeExist = true;
                                    if (status == 1) {
                                        isStatusOne = true;
                                    }
                                    break;
                                }
                            }

                            if (isRoomCodeExist) {
                                if (isStatusOne) {
                                    Toast.makeText(getActivity(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                                    doUong1CheckBox.setChecked(false);
                                    doUong2CheckBox.setChecked(false);
                                    doUong3CheckBox.setChecked(false);
                                    doAn1CheckBox.setChecked(false);

                                    infoTextView.setText("");
                                    price_foods.setText("");
                                } else {
                                    Toast.makeText(getActivity(), "Không thể đặt hàng cho phòng này!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Không thể đặt hàng cho phòng này!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
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

    @SuppressLint("SetTextI18n")
    private void onCheckBoxChanged(CompoundButton buttonView, boolean isChecked) {
        String item = "";
        int price = 0;

        switch (buttonView.getId()) {
            case R.id.DoUong1:
                price = 8000;
                item = "Nước Khoáng Aquafina (8.000đ)";
                break;
            case R.id.DoUong2:
                price = 10000;
                item = "Cocacola Chai (10.000đ)";
                break;
            case R.id.DoUong3:
                price = 15000;
                item = "Trà Đào (15.000đ)";
                break;
            case R.id.DoAn1:
                price = 50000;
                item = "Combo Đồ Ăn (50.000đ)";
                break;
        }

        if (isChecked) {
            money += price;
        } else {
            money -= price;
        }

        if (isChecked) {
            String currentText = infoTextView.getText().toString();
            if (!currentText.isEmpty()) {
                currentText += "\n";
            }
            currentText += item;
            infoTextView.setText(currentText);
            price_foods.setText("Đơn giá: " + money + "VND");
        } else {
            String currentText = infoTextView.getText().toString();
            String updatedText = currentText.replace(item, "");
            updatedText = updatedText.replace("\n\n", "\n");
            infoTextView.setText(updatedText);
            price_foods.setText("Đơn giá: " + money + "VND");
        }
    }
}