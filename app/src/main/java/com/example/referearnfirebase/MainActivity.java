package com.example.referearnfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView sharetv = findViewById(R.id.sharetv);
        sharetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDynamicLink();
            }
        });

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                Uri deeplink = null;
                if (pendingDynamicLinkData != null) {
                    deeplink = pendingDynamicLinkData.getLink();
                }

                if (deeplink != null) {
                    String username = deeplink.getQueryParameter("username");
                    Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "DeepLink Null", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Unable to Capture Link", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void createDynamicLink() {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://example.com/"))
                .setDomainUriPrefix("https://rohitpage.page.link")
                // Open links with this app on Android
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.example.referearnfirebase")
                                .setMinimumVersion(25)
                                .build())
                //.setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        Log.e("main", "  Long refer " + dynamicLink.getUri());

        createReferlink("Rahul");

    }

    public void createReferlink(String user) {
        // manuall link
        String sharelinktext = "https://rohitpage.page.link/?" +
                "link=http://www.example.com/?username=" + user +
                "&apn=" + getPackageName() +
                "&st=" + "Download LPNT App & Get Rewarded" +
                "&sd=" + "Reward : 1 LPNT Token" +
                "&si=" + "https://static.news.bitcoin.com/wp-content/uploads/2021/02/8EHoA6aN-lpnt-is-live1280.jpg";

        Log.e("mainactivity", "sharelink - " + sharelinktext);
        // shorten the link
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                //.setLongLink(dynamicLink.getUri())    // enable it if using firebase method dynamicLink
                .setLongLink(Uri.parse(sharelinktext))  // manually
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("main ", "short link " + shortLink.toString());
                            // share app dialog
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);


                        } else {
                            // Error
                            // ...
                            Log.e("main", " error " + task.getException());

                        }
                    }
                });

    }
}