package com.example.wastewise;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class AvailableQuestsActivity extends AppCompatActivity {
    LinearLayout quests_container;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_available_quests);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        quests_container = findViewById(R.id.quests_container);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        db.collection("quests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String title = document.getString("questTitle");
                    String description = document.getString("description");
                    int points = document.getLong("points").intValue();
                    String questId = document.getId();
                    int maxTakers = document.getLong("maxQuestTakers").intValue();
                    int currentTakers = document.getLong("currentQuestTakers").intValue();
                    HashMap<String, Object> materials = (HashMap<String, Object>) document.get("materials");

                    View availableQuest = getLayoutInflater().inflate(R.layout.availablequest_overview_layout, quests_container, false);

                    TextView questTitle = availableQuest.findViewById(R.id.questTitle_tv);
                    TextView questDescription = availableQuest.findViewById(R.id.questDescription_tv);
                    TextView questPoints = availableQuest.findViewById(R.id.questPoints_tv);

                    questTitle.setText(title);
                    questDescription.setText(description);
                    questPoints.setText(String.valueOf(points));

                    availableQuest.setOnClickListener(v -> {
                        View availableQuestDetails = getLayoutInflater().inflate(R.layout.availablequest_details_layout, null);

                        TextView questTitleDetails = availableQuestDetails.findViewById(R.id.questTitle_tv);
                        TextView questDescriptionDetails = availableQuestDetails.findViewById(R.id.questDescription_tv);
                        TextView questPointsDetails = availableQuestDetails.findViewById(R.id.questPoints_tv);
                        TextView questIdDetails = availableQuestDetails.findViewById(R.id.questId_tv);
                        LinearLayout materialsContainer = availableQuestDetails.findViewById(R.id.materials_container);
                        ImageView questQr = availableQuestDetails.findViewById(R.id.questQr_img);
                        TextView progressNum = availableQuestDetails.findViewById(R.id.progressNum_tv);
                        LinearProgressIndicator linearProgress = availableQuestDetails.findViewById(R.id.linearProgress_bar);

                        questTitleDetails.setText(title);
                        questDescriptionDetails.setText(description);
                        questPointsDetails.setText(String.valueOf(points));
                        questIdDetails.setText(questId);
                        progressNum.setText(String.valueOf(currentTakers) + "/" + String.valueOf(maxTakers));
                        linearProgress.setMax(maxTakers);
                        linearProgress.setProgress(currentTakers);

                        for (Map.Entry<String, Object> entry : materials.entrySet()) {
                            String material = entry.getKey();
                            int quantity = ((Long) entry.getValue()).intValue();
                            View materialView = getLayoutInflater().inflate(R.layout.material_item_layout, null);
                            TextView materialTextView = materialView.findViewById(R.id.material_item);
                            materialTextView.setText("- " + material + ": " + quantity);
                            materialsContainer.addView(materialView);
                        }

                        MultiFormatWriter writer = new MultiFormatWriter();
                        try{
                            BitMatrix matrix = writer.encode(questId, BarcodeFormat.QR_CODE, 800, 800);
                            BarcodeEncoder encoder = new BarcodeEncoder();
                            Bitmap bitmap = encoder.createBitmap(matrix);

                            questQr.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        FrameLayout overlayContainer = findViewById(R.id.overlay_container);
                        overlayContainer.addView(availableQuestDetails);
                        overlayContainer.setVisibility(View.VISIBLE);

                        View modalBackground = availableQuestDetails.findViewById(R.id.modal_background);
                        modalBackground.setOnClickListener(v1 -> {
                            overlayContainer.setVisibility(View.GONE);
                            overlayContainer.removeAllViews(); // clears the modal
                        });

                    });

                    quests_container.addView(availableQuest);
                };
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());
    }
}