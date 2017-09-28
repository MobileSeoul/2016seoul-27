package soongsil.ourbycicle;

/**
 * Created by SueWoon on 10/30/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class JoinActivity extends AppCompatActivity {
    /* 텍스트 필드 */
    EditText IDtextfield, PWtextfield, CFtextfield, NNtextfield;
    String id, pwd, pwdcf, nickname, joinAddress, checkAddress;

    /* 버튼 */
    Button joinButton, checkButton;
    Context cont;

    // 아이디 중복 확인을 했는지
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        IDtextfield = (EditText) findViewById(R.id.IDtextfield);   // ID 입력 필드
        PWtextfield = (EditText) findViewById(R.id.PWtextfield);   // 비밀번호 입력 필드
        CFtextfield = (EditText) findViewById(R.id.CFtextfield);   // 비밀번호 입력 필드
        NNtextfield = (EditText) findViewById(R.id.NNtextfield);   // 비밀번호 입력 필드
        joinButton = (Button) findViewById(R.id.joinButton);    // 회원가입 버튼
        checkButton = (Button) findViewById(R.id.checkButton);    // 회원가입 버튼
        flag = 0;

        /* 회원가입 동작 */
        // 쓰레드 생성
        joinAddress = "http://116.33.179.48:50/join.php";
        checkAddress = "http://116.33.179.48:50/dup_check.php";

        // 중복 확인 버튼 클릭 (쓰레드 시작) - flag를 다시 초기화하는 걸 해야한다...
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = IDtextfield.getText().toString();

                CheckTask task = new CheckTask();
                task.execute();
            }
        });

        // 회원가입 버튼 클릭 (쓰레드 시작)
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = IDtextfield.getText().toString();
                pwd = PWtextfield.getText().toString();
                pwdcf = CFtextfield.getText().toString();
                nickname = NNtextfield.getText().toString();

                if(pwd.equals(pwdcf)){
                    JoinTask task = new JoinTask();
                    task.execute();
                } else {
                    Toast.makeText(JoinActivity.this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    class CheckTask extends AsyncTask<String, Void, Integer> {
        // 중복 확인(CHECK) 클릭시 실행되는 task
        Context mContext = JoinActivity.this;
        ProgressDialog mDialog;
        final int OK = 1;
        final int DUP = 2;
        final int ERROR = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //걍 있어보이게 하는거임ㅋ
            mDialog = ProgressDialog.show(mContext,null,"중복확인중..잠시만 기다려주세요.",true,false);
        }
        @Override
        protected Integer doInBackground(String... params) {
            try {
                // 서버와 연결
                URL url = new URL(checkAddress);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                //보낼 메세지 (중복 확인 할 id)
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(out);
                String info = "id=" + id;    // 보낼 메세지 : id=$id
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
                    // 서버와 통신 시도 - 아이디가 중복이 아닌지
                    if (read.equals("OK")) {
                        return OK;
                    } else if (read.equals("DUP")){
                        return DUP;
                    } else {
                        return ERROR;
                    }
                }
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
                    Toast.makeText(mContext, "사용할 수 있는 ID입니다.", Toast.LENGTH_LONG).show();
                    flag = 1;
                    break;
                case DUP:
                    Toast.makeText(mContext, "이미 가입된 ID입니다.", Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    Toast.makeText(mContext,"서버에서 이상한 응답이....",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(mContext,"?? 이건 무슨에러람",Toast.LENGTH_SHORT).show();
            }
        }
    }

    class JoinTask extends AsyncTask<String, Void, Integer> {
        // 회원가입 클릭시 실행되는 task
        Context mContext = JoinActivity.this;
        ProgressDialog mDialog;
        final int OK = 1;
        final int FAIL = 2;
        final int ERROR = 0;

        @Override
        protected Integer doInBackground(String... params) {
            try {
                // 서버와 연결
                URL url = new URL(joinAddress);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                //보낼 메세지 (중복 확인 할 id)
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(out);
                String info = "id=" + id + "&pwd=" + pwd + "&nickname=" + nickname; // 보낼 메세지 : id=$id & pwd=$pwd & nickname=$nickname
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
                        if(flag == 1) return OK;
                        else return FAIL;
                    } else {
                        return ERROR;
                    }
                }
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
            switch (result) {
                case OK:
                    Toast.makeText(mContext, "회원가입 성공", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case FAIL:
                    Toast.makeText(mContext, "아이디 중복 확인을 해주세요.", Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    Toast.makeText(mContext, "서버에서 이상한 응답이....", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(mContext, "??이건 무슨에러람", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

