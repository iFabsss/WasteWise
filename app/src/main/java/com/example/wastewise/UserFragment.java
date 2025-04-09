package com.example.wastewise;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private TextView username, uid, email;
    private ImageView userQr;
    private ImageButton logoutBtn;
    private RelativeLayout confirmLogoutForm;
    private Button noLogoutBtn, confirmLogoutBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        username = view.findViewById(R.id.username_tv);
        uid = view.findViewById(R.id.uid_tv);
        email = view.findViewById(R.id.email_tv);
        userQr = view.findViewById(R.id.userQr_iv);
        confirmLogoutForm = view.findViewById(R.id.confirmLogout_form);
        noLogoutBtn = view.findViewById(R.id.noLogout_btn);
        confirmLogoutBtn = view.findViewById(R.id.confirmLogout_Btn);

        String uidStr = user.getUid();
        uid.setText(uidStr);

        db.collection("users")
                .document(uidStr)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    username.setText(documentSnapshot.getString("username"));
                    email.setText(documentSnapshot.getString("email"));
                });

        MultiFormatWriter writer = new MultiFormatWriter();
        try{
            BitMatrix matrix = writer.encode(uidStr, BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);

            userQr.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(v -> {
            confirmLogoutForm.setVisibility(View.VISIBLE);

            noLogoutBtn.setOnClickListener(v1 -> {
                confirmLogoutForm.setVisibility(View.GONE);
            });

            confirmLogoutBtn.setOnClickListener(v1 -> {
                Intent intent = new Intent(getActivity(), StartupActivity.class);
                startActivity(intent);
                mAuth.signOut();
                getActivity().finishAffinity();
            });
        });

        // Inflate the layout for this fragment
        return view;
    }
}