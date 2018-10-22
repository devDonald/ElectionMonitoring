package com.donald.election_monitoring.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.donald.election_monitoring.CentralAdminHome;
import com.donald.election_monitoring.R;
import com.donald.election_monitoring.StateAdminHome;
import com.donald.election_monitoring.util.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

public class AdminLogin extends AppCompatActivity {
    private TextView m_display_error;
    private TextInputEditText mEmail, mPassword;
    private Button submit_login;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String email,password;
    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        m_display_error = findViewById(R.id.tv_error_message);
        mEmail = findViewById(R.id.et_admin_id);
        mPassword = findViewById(R.id.et_admin_password);
        submit_login = findViewById(R.id.admin_submit);

        mAuth = FirebaseAuth.getInstance();

        hud = KProgressHUD.create(AdminLogin.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Authenticating Admin...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setBackgroundColor(Color.GREEN)
                .setAutoDismiss(true);


        submit_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()){
                    email=mEmail.getText().toString().trim();
                    password = mPassword.getText().toString().trim();

                    if (!EmailValidator.getInstance().validate(email)){
                        m_display_error.setText("incorrect email, try again");
                    } else if (TextUtils.isEmpty(password)||password.length()<7){
                        m_display_error.setText("password length too short");
                    } else{
                        hud.show();

                        mAuth.signInWithEmailAndPassword(email,password)
                                .addOnCompleteListener(AdminLogin.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        hud.dismiss();
                                        if (task.isSuccessful()){
                                            if (password.length()==8){
                                                MDToast.makeText(getApplicationContext(),
                                                        "Login Successful!",
                                                        MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
                                                Intent intent = new Intent(AdminLogin.this,StateAdminHome.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);

                                            }else if (password.length()==10){

                                                Intent intent = new Intent(AdminLogin.this, CentralAdminHome.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        }else {
                                            m_display_error.setText("incorrect email/password");
                                        }
                                    }
                                });
                    }

                }else {
                    MDToast.makeText(getApplicationContext(),
                            "No network availabile, please try again!",
                            MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();

                }

            }
        });
    }

    //check network availability
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
