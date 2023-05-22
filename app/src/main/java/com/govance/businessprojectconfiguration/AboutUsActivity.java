package com.govance.businessprojectconfiguration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AboutUsActivity extends AppCompatActivity {
ImageButton backButton;
MaterialButton emailButton;
MaterialButton whatsappButton;
MaterialButton callButton;
MaterialButton facebookButton;
MaterialButton twitterButton;
MaterialButton instagramButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initUI();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutUsActivity.this.onBackPressed();

            }
        });
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] feedBackEmail  =  new String[]{
                        "anchorallianceglobal@gmail.com"
                };
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, feedBackEmail);
                intent.putExtra(Intent.EXTRA_SUBJECT,"AAG Homes");
                intent.setDataAndType(Uri.parse("email"),"message/rfc822");
                startActivity(Intent.createChooser(intent,"Send feedback"));
            }
        });

        whatsappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=2347085348862")));

            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.putExtra(Intent.EXTRA_PHONE_NUMBER, "07085348862");
                intent.setData(Uri.parse("tel:07085348862"));
                startActivity(Intent.createChooser(intent,"Call"));

            }
        });
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=100090340867736")));
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/100811256318591")));

            }
        });
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/aaghomes?t=UBE1rd1DXOh9twqbvoFDbw&s=09")));

            }
        });
        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/aaghomesofficial?igshid=ZGUzMzM3NWJiOQ==")));

            }
        });

    }

    void initUI(){
        backButton = findViewById(R.id.backButtonId);
        emailButton = findViewById(R.id.emailUsActionButtonId);
        whatsappButton = findViewById(R.id.whatsappChatUsActionButtonId);
        callButton = findViewById(R.id.callUsActionButtonId);
        facebookButton = findViewById(R.id.facebookFollowUsActionButtonId);
        twitterButton = findViewById(R.id.twitterFollowUsActionButtonId);
        instagramButton = findViewById(R.id.instagramFollowUsActionButtonId);
    }

}