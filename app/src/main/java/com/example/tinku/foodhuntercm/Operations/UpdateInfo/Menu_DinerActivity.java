package com.example.tinku.foodhuntercm.Operations.UpdateInfo;

/* Import appropriate libraries */
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.tinku.foodhuntercm.Exceptions.AppException;
import com.example.tinku.foodhuntercm.Operations.JoinCommunity.LoginActivity;
import com.example.tinku.foodhuntercm.R;

/* Menu options screen for diner */
public class Menu_DinerActivity extends AppCompatActivity implements View.OnClickListener {

    /* Variable for handling diner menu activity */
    Button Aboutus,Updateinfo, Search;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         /* Set the content view to Diner menu */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_diner);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Aboutus = (Button)findViewById(R.id.aboutus);
        Updateinfo = (Button) findViewById(R.id.updateinfo);
        Search = (Button) findViewById(R.id.customsearch);

        try {
            Button btnLogout  = (Button)findViewById(R.id.logout);
            if(btnLogout == null){
                throw  new AppException(1, "Missing information");
            }

            btnLogout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                /* On click of register, start an Intent to switch to register user screen */
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
            });
        }
        catch(AppException e){
            e.genericexceptionfix();
        }
    }

    /* On click of the view, jump to appropriate fragments */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutus:
                FragmentManager FM= getFragmentManager();
                FragmentTransaction FT= FM.beginTransaction();
                AboutUsFragment Fone=new AboutUsFragment();
                FT.add(R.id.customexps2,Fone);
                FT.commit();
                break;
            case R.id.updateinfo:
                Intent intent = new Intent(getApplicationContext(), DinerUploadActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.customsearch:
                FragmentManager FM3= getFragmentManager();
                Bundle bundle= new Bundle();
                bundle.putString("username",username);
                FragmentTransaction FT3= FM3.beginTransaction();
                SearchOptionFragment F3=new SearchOptionFragment();
                F3.setArguments(bundle);
                FT3.add(R.id.customexps,F3);
                FT3.commit();
                break;
        }
    }
}
