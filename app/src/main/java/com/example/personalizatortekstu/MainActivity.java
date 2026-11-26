package com.example.personalizatortekstu;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ImageView bgImage;
    private LinearLayout rootLayout;
    private LinearLayout panel;
    private TextView tvText;
    private Button btnChangeText;
    private Button btnChangeColor;
    private Button btnChangeBg;
    private SwitchCompat switchNight;
    private TextView tvAlphaLabel;
    private SeekBar seekAlpha;
    private TextView tvAlphaLayout;
    private SeekBar seekAlphaL;
    private final int[] images = {R.drawable.tlo1, R.drawable.tlo2, R.drawable.tlo3};
    private int imageIndex = 0;

    private String[] texts;
    private int textIndex = 0;
    private int[] colors;
    private int colorIndex = 0;
    private int textAlphaPercent = 100;
    private final int[] styles = {Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, Typeface.BOLD_ITALIC};
    private int styleIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bgImage = findViewById(R.id.bgImage);
        rootLayout = findViewById(R.id.rootLayout);
        panel = findViewById(R.id.panel);


        tvText = findViewById(R.id.tvText);
        btnChangeText = findViewById(R.id.btnChangeText);
        btnChangeColor = findViewById(R.id.btnChangeColor);
        btnChangeBg = findViewById(R.id.btnChangeBg);
        switchNight = findViewById(R.id.switchNight);
        tvAlphaLabel = findViewById(R.id.tvAlphaLabel);
        seekAlpha = findViewById(R.id.seekAlpha);
        tvAlphaLayout = findViewById(R.id.tvAlphaLayout);
        seekAlphaL = findViewById(R.id.seekAlphaL);

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        texts = getResources().getStringArray(R.array.texts);
        colors = new int[]{
                ContextCompat.getColor(this, R.color.text_color),
                ContextCompat.getColor(this, R.color.text_opt2),
                ContextCompat.getColor(this, R.color.text_opt3)
        };

        if (savedInstanceState != null) {
            imageIndex = savedInstanceState.getInt("imageIndex", 0);
            textIndex = savedInstanceState.getInt("textIndex", 0);
            styleIndex = savedInstanceState.getInt("styleIndex", 0);
            textAlphaPercent = savedInstanceState.getInt("textAlphaPercent", 100);
        }

        SharedPreferences prefs = getSharedPreferences("PersonalizatorPrefs", MODE_PRIVATE);
        colorIndex = prefs.getInt("colorIndex", 0);

        bgImage.setImageResource(images[imageIndex]);
        tvText.setText(texts[textIndex]);
        tvText.setTypeface(null, styles[styleIndex]);
        applyTextColor();

        btnChangeText.setOnClickListener(v -> {
            textIndex = (textIndex + 1) % texts.length;
            styleIndex = (styleIndex + 1) % styles.length;
            tvText.setText(texts[textIndex]);
            tvText.setTypeface(null, styles[styleIndex]);
        });

            btnChangeColor.setOnClickListener(v -> {
                colorIndex = (colorIndex + 1) % colors.length;
                applyTextColor();
                SharedPreferences p = getSharedPreferences("PersonalizatorPrefs", MODE_PRIVATE);
                p.edit().putInt("colorIndex", colorIndex).apply();
            });

        btnChangeBg.setOnClickListener(v -> {
            imageIndex = (imageIndex + 1) % images.length;
            bgImage.setImageResource(images[imageIndex]);
        });

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switchNight.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);

        switchNight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        seekAlpha.setProgress(textAlphaPercent);
        tvAlphaLabel.setText(getString(R.string.tv_alpha_label_format, textAlphaPercent));

        seekAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textAlphaPercent = progress;
                applyTextColor();
                tvAlphaLabel.setText(getString(R.string.tv_alpha_label_format, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekAlphaL.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int cardBgColor = ContextCompat.getColor(MainActivity.this, R.color.card_bg);
                int origAlpha = Color.alpha(cardBgColor);
                int panelAlpha = origAlpha * progress / 100;
                int newPanelColor = ColorUtils.setAlphaComponent(cardBgColor, panelAlpha);
                panel.setBackgroundColor(newPanelColor);
                tvAlphaLayout.setText(getString(R.string.tv_alpha_layout_format, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("imageIndex", imageIndex);
        outState.putInt("textIndex", textIndex);
        outState.putInt("styleIndex", styleIndex);
        outState.putInt("textAlphaPercent", textAlphaPercent);
    }



    private void applyTextColor() {
        int baseColor = colors[colorIndex];
        int alpha = textAlphaPercent * 255 / 100;
        int finalColor = ColorUtils.setAlphaComponent(baseColor, alpha);
        tvText.setTextColor(finalColor);
    }
}