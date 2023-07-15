package com.example.qlks_android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<Room> roomList;

    private String mParam1;
    private String mParam2;
    public HomeActivity() {

    }
    public static HomeActivity newInstance(String param1, String param2) {
        HomeActivity fragment = new HomeActivity();
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
    public class Room {
        private String code;
        private int status;

        public Room(String code, int status) {
            this.code = code;
            this.status = status;
        }

        public String getCode() {
            return code;
        }

        public int getStatus() {
            return status;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm2, container, false);
        String json = loadJSONFromAsset(requireContext(), "KhachSan.json");

        roomList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String code = jsonObject.getString("Code");
                int status = jsonObject.getInt("Status");
                roomList.add(new Room(code, status));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GridLayout mainGrid = view.findViewById(R.id.mainGrid);

        for (int i = 0; i < roomList.size(); i++) {
            createCardView(mainGrid, roomList.get(i), view);
        }

        Button total = view.findViewById(R.id.totalRoom);
        Button empty = view.findViewById(R.id.emptyRoom);
        Button activeRoom = view.findViewById(R.id.activeRoom);
        Button maintainRoom = view.findViewById(R.id.maintainRoom);

        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGridLayout(mainGrid, roomList, view);
            }
        });

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Room> emptyRooms = new ArrayList<>();
                for (Room room : roomList) {
                    if (room.getStatus() == -1) {
                        emptyRooms.add(room);
                    }
                }
                updateGridLayout(mainGrid, emptyRooms, view);
            }
        });

        activeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Room> activeRooms = new ArrayList<>();
                for (Room room : roomList) {
                    if (room.getStatus() == 1) {
                        activeRooms.add(room);
                    }
                }
                updateGridLayout(mainGrid, activeRooms, view);
            }
        });

        maintainRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Room> maintainRooms = new ArrayList<>();
                for (Room room : roomList) {
                    if (room.getStatus() == 0) {
                        maintainRooms.add(room);
                    }
                }
                updateGridLayout(mainGrid, maintainRooms, view);
            }
        });

        return view;
    }
    private void updateGridLayout(GridLayout mainGrid, List<Room> filteredRoomList, View view) {
        mainGrid.removeAllViews();

        for (int i = 0; i < filteredRoomList.size(); i++) {
            createCardView(mainGrid, filteredRoomList.get(i), view);
        }
    }
    @SuppressLint("ResourceAsColor")
    private void createCardView(GridLayout gridLayout, Room room, View layout) {
        CardView cardView = new CardView(requireContext());
        int cardWidth = getResources().getDimensionPixelSize(R.dimen.card_width);
        int cardHeight = getResources().getDimensionPixelSize(R.dimen.card_height);
        int cardMargin = getResources().getDimensionPixelSize(R.dimen.card_margin);
        int cardCornerRadius = getResources().getDimensionPixelSize(R.dimen.card_corner_radius);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = cardWidth;
        layoutParams.height = cardHeight;
        layoutParams.setMargins(cardMargin, cardMargin, cardMargin, cardMargin);
        layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        cardView.setLayoutParams(layoutParams);

        cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        cardView.setCardElevation(getResources().getDimension(R.dimen.card_elevation));
        cardView.setRadius(cardCornerRadius);
        if (room.getStatus() == -1) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        } else if (room.getStatus() == 1) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_200));
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_200));
        }

        LinearLayout linearLayout = new LinearLayout(requireContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        linearLayoutParams.setMargins(cardMargin, cardMargin, cardMargin, cardMargin);
        linearLayout.setLayoutParams(linearLayoutParams);

        ImageView imageView = new ImageView(requireContext());
        imageView.setImageResource(R.drawable.room);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView textView = new TextView(requireContext());
        textView.setText(room.getCode());
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        cardView.addView(textView);
        linearLayout.addView(imageView);

        cardView.addView(linearLayout);
        gridLayout.addView(cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(room.getStatus() == 1) {
                    Toast.makeText(getActivity(), "Phòng không khả dụng lúc này!", Toast.LENGTH_SHORT).show();
                    return;
                } else if(room.getStatus() == 0) {
                    Toast.makeText(getActivity(), "Phòng không khả dụng lúc này!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showNameInputDialog(room);
            }

            private void showNameInputDialog(Room room) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Nhập vào họ và tên:");

                final EditText input = new EditText(requireContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();
                        showReservationConfirmationDialog(room, name);
                    }
                    private void showReservationConfirmationDialog(Room room, String name) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Xác nhận đặt phòng");
                        builder.setMessage("Số phòng: " + room.getCode() + "\nHọ và tên: " + name);
                        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = input.getText().toString().trim();
                                if (!name.isEmpty()) {
                                    Toast.makeText(requireContext(), "Đã đặt phòng thành công: " + name, Toast.LENGTH_SHORT).show();
                                    cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_200));
                                } else {
                                    Toast.makeText(requireContext(), "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });

                builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
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
