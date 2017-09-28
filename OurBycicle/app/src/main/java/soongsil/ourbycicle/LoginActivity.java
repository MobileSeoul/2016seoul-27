package soongsil.ourbycicle;

/**
 * Created by mandu on 16-10-14.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    /* 통신 초기화 작업 */
    String loginAddress;   // 서버 주소
    Activity activity;

    /* 텍스트 필드 */
    EditText IDtextfield, PWtextfield;
    String id, pwd;

    /* 체크박스 */
    CheckBox IDCheckBox, SaveCheckBox;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    /* 버튼 */
    Button loginButton, joinButton;
    Context cont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
         /* VIEW */
        IDtextfield = (EditText) findViewById(R.id.IDtextfield);   // ID 입력 필드
        PWtextfield = (EditText) findViewById(R.id.PWtextfield);   // 비밀번호 입력 필드
        loginButton = (Button) findViewById(R.id.loginButton);   // 로그인 버튼
        joinButton = (Button) findViewById(R.id.joinButton);    // 회원가입 버튼
        IDCheckBox = (CheckBox) findViewById(R.id.IDCheckBox);      // ID 저장 체크박스
        SaveCheckBox = (CheckBox) findViewById(R.id.SaveCheckBox);  // 자동 로그인 체크박스
        pref = getSharedPreferences("pref", 0);
        editor = pref.edit();
       // getActionBar().hide();
        // 조인 버튼 클릭
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(myIntent);
            }
        });

        /* 로그인 동작 */
        // 쓰레드 생성
        loginAddress = "http://116.33.179.48:50/login.php";

        // 체크박스 설정
        if(pref.getBoolean("autoLogin", true))
        {//자동 로그인 설정 되어있을 때
            id = pref.getString("id", "");
            pwd = pref.getString("pwd","");
            IDtextfield.setText(id);
            PWtextfield.setText(pwd);
            IDCheckBox.setChecked(true);
            SaveCheckBox.setChecked(true);
            LoginTask task = new LoginTask();
            task.execute();
        }
        else if(pref.getBoolean("saveID", true))
        {
            IDCheckBox.setChecked(true);
            IDtextfield.setText(pref.getString("id",""));
        }

        // 로그인 버튼 클릭 (쓰레드 시작)
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = IDtextfield.getText().toString();
                pwd = PWtextfield.getText().toString();
                LoginTask task = new LoginTask();
                task.execute();
            }
        });
    }

    class LoginTask extends AsyncTask<String,Void,Integer>{
        // 로그인 버튼 클릭시 실행되는 task
        Context mContext = LoginActivity.this;
        ProgressDialog mDialog;
        final int OK = 1;
        final int NON_USER = 2;
        final int WRONG_PWD = 3;
        final int ERROR = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //걍 있어보이게 하는거임ㅋ
            mDialog = ProgressDialog.show(mContext,null,"로그인중..잠시만 기다려주세요.",true,false);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                //연결
                URL url = new URL(loginAddress);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                //보낼 메세지 ( id와 password )
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(out);
                String info = "id=" + id + "&pwd="+pwd; // 보낼 메세지 : id=$id & pwd=$pwd
                writer.write(info);
                writer.flush();
                writer.close();
                // 서버로부터 답장 받음
                int res = urlConnection.getResponseCode();
                //해당 url에서 정보 읽어오기
                if (res == HttpURLConnection.HTTP_OK) {
                    //받은 메세지 읽기
                    String read = "";
                    InputStream test = urlConnection.getInputStream();
                    InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(in);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        read += line;
                    }
                    // 서버와 통신 시도 - 아이디랑 비밀번호가 맞는지
                    if (read.equals("OK")) {
                        return OK;
                    } else if(read.equals("NON_USER")){
                        // 없는 ID
                        return NON_USER;
                    } else if(read.equals("WRONG")){
                        // 틀린 password
                        return WRONG_PWD;
                    } else{
                        // 예외처리.. 서버에서 뭐라 보낸겨
                        return ERROR;
                    }
                }
                else
                    return ERROR;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //결과 출력
            super.onPostExecute(result);
            mDialog.dismiss();
            switch(result)
            {
                case OK:
                    Toast.makeText(mContext, "로그인 성공", Toast.LENGTH_LONG).show();
                    if(IDCheckBox.isChecked())
                    {//아이디 저장만 선택 시 아이디만 저장
                        editor.putString("id",id);
                        editor.putBoolean("saveID",true);
                        editor.putBoolean("autoLogin",false);
                        editor.commit();
                    }
                    if(SaveCheckBox.isChecked())
                    {//자동로그인 선택 시 id, pwd 저장
                        editor.putString("id",id);
                        editor.putString("pwd",pwd);
                        editor.putBoolean("saveID",true);
                        editor.putBoolean("autoLogin",true);
                        editor.commit();
                    }
                    //김수운
                    Intent mainInt = new Intent(LoginActivity.this, MainActivity.class);

                    //희연
                    mainInt.putExtra("id", id);

                    startActivityForResult(mainInt, 0);
                    break;
                case NON_USER:
                    Toast.makeText(mContext,"존재하지 않는 ID 입니다.",Toast.LENGTH_SHORT).show();
                    break;
                case WRONG_PWD:
                    Toast.makeText(mContext,"비밀번호를 다시 확인해주세요.",Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Toast.makeText(mContext,"서버에서 이상한 응답이....",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(mContext,"??이건 무슨에러람",Toast.LENGTH_SHORT).show();
            }
        }
    }
    // 김수운 - MainActivity가 종료되었을 때
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK: // LOGOUT 버튼 눌렀을 때
                SaveCheckBox.setChecked(false);
                editor.putBoolean("autoLogin", false);
                editor.remove("pwd");
                editor.commit();
                break;
            default:        // '뒤로가기' 버튼 눌렀을 때
                finish();
                break;
        }
    }
}