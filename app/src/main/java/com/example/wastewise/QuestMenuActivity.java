package com.example.wastewise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuestMenuActivity extends AppCompatActivity {
    ImageButton backBtn, availableQuests_btn, completedQuests_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quest_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        availableQuests_btn = findViewById(R.id.availableQuests_btn);
        completedQuests_btn = findViewById(R.id.completedQuests_btn);

        availableQuests_btn.setOnClickListener(v -> {
            Intent intent = new Intent(this.getApplicationContext(), AvailableQuestsActivity.class);
            startActivity(intent);
        });

        completedQuests_btn.setOnClickListener(v -> {
            Toast.makeText(this, "Completed quests coming soon!", Toast.LENGTH_SHORT).show();
        });

        backBtn.setOnClickListener(v -> finish());

    }
}