package br.com.gpaengenharia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import br.com.gpaengenharia.classes.Utils;
import br.com.gpaengenharia.R;

// Tela de Login
public class AtvLogin extends Activity implements OnClickListener {
    //TODO: implementar comunicação com webservice
    public static boolean ErroWebservice = false; //status webservice
    private AutoCompleteTextView TxtEmail;
    private EditText EdtSenha;
    private ProgressBar PrgLogin;
    private Button BtnLogin;
    private LoginTask AtaskLogin = null;
    private Utils utils = new Utils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_login);
        TxtEmail = (AutoCompleteTextView) findViewById(R.id.email);
        EdtSenha = (EditText) findViewById(R.id.password);
        BtnLogin = (Button) findViewById(R.id.email_sign_in_button);
        PrgLogin = (ProgressBar) findViewById(R.id.login_progress);
        BtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String login = TxtEmail.getText().toString();
        String senha = EdtSenha.getText().toString();
        utils.barraProgresso(PrgLogin,true);
        AtaskLogin = new LoginTask(login, senha);
        AtaskLogin.execute((Void) null);
    }

    /**
     * Faz o login em segundo plano
     */
    public class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String login;
        private final String senha;

        LoginTask(String login, String senha) {
            this.login = login;
            this.senha = senha;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: implementar login real.
            try {
                // Simulando um login
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean successo) {
            AtaskLogin = null;
            utils.barraProgresso(PrgLogin, false);
            if (successo) {
                startActivity(new Intent(AtvLogin.this, AtvPrincipal.class));
            } else {
                //TODO: implementar erro de login
            }
        }

        @Override
        protected void onCancelled() {
            AtaskLogin = null;
            utils.barraProgresso(PrgLogin, false);
        }
    }

}