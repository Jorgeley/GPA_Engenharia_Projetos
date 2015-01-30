package br.com.gpaengenharia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import br.com.gpaengenharia.classes.Utils;
import br.com.gpaengenharia.R;

/*
 Tela de Login
  */
public class AtvLogin extends Activity{
    //TODO: implementar comunicação com webservice
    public static boolean ErroWebservice = false; //status webservice
    private AutoCompleteTextView TxtEmail;
    private EditText EdtSenha;
    private ProgressBar PrgLogin;
    private Button BtnLogin;
    private LoginTask AtaskLogin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_login);
        Utils.contexto = this;
        this.TxtEmail = (AutoCompleteTextView) findViewById(R.id.email);
        this.EdtSenha = (EditText) findViewById(R.id.password);
        this.BtnLogin = (Button) findViewById(R.id.email_sign_in_button);
        this.PrgLogin = (ProgressBar) findViewById(R.id.login_progress);
    }

    public void onClickLogin(View v) {
        String login = this.TxtEmail.getText().toString();
        String senha = this.EdtSenha.getText().toString();
        Utils.barraProgresso(this, this.PrgLogin, true);
        this.AtaskLogin = new LoginTask(login, senha);
        this.AtaskLogin.execute((Void) null);
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
            Utils.barraProgresso(AtvLogin.this, PrgLogin, false);
            if (successo) {
                startActivity(new Intent(AtvLogin.this, AtvPrincipal.class));
            } else {
                //TODO: implementar erro de login
            }
        }

        @Override
        protected void onCancelled() {
            AtaskLogin = null;
            Utils.barraProgresso(AtvLogin.this, PrgLogin, false);
        }
    }

}