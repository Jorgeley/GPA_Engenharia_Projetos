package br.com.gpaengenharia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import br.com.gpaengenharia.beans.Usuario;
import br.com.gpaengenharia.classes.Utils;
import br.com.gpaengenharia.R;
import br.com.gpaengenharia.classes.WebService;
import br.com.gpaengenharia.classes.xmls.XmlTarefasPessoais;

/**
 * Activity inicial, Tela de Login
 */
public class AtvLogin extends Activity{
    public static Usuario usuario; //objeto global
    //TODO: implementar comunicação com webservice
    public Usuario respostaWebservice; //status webservice
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

    /**
     * setado diretamente na propriedade OnClick do BTNlogin
     * @param v
     */
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
            if (login=="adm"){
                Log.i("login", "logando localmente como Adm sem webservice");
                XmlTarefasPessoais xmlTarefasPessoais = new XmlTarefasPessoais(AtvLogin.this);
                xmlTarefasPessoais.criaXmlProjetosPessoaisTeste();
                return true;
            }else {
                //TODO nao enviar senhas sem segurança
                usuario = WebService.login(login, senha);//login via webservice
                if (usuario != null) {
                    /**
                     * TODO nao fazer o download do arquivo se ele ja existir e estar atualizado
                     * if (new File("tarefasPessoais.xml").exists())
                     */
                    XmlTarefasPessoais xmlTarefasPessoais = new XmlTarefasPessoais(AtvLogin.this);
                    //baixa o XML de tarefas pessoais via werbservice e cria o arquivo localmente
                    try {
                        xmlTarefasPessoais.criaXmlProjetosPessoaisWebservice(usuario.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else
                    return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean successo) {
            AtaskLogin = null;
            Utils.barraProgresso(AtvLogin.this, PrgLogin, false);
            if (successo) {
                /* OUT OF MEMORY!!!
                WebService.tarefas(usuario.getId());*/
                Toast.makeText(AtvLogin.this, "Bem vindo "+String.valueOf("["+usuario.getPerfil()+"]"+usuario.getNome()), Toast.LENGTH_LONG).show();
                if (usuario.getPerfil().equals("adm"))
                    startActivity(new Intent(AtvLogin.this, AtvAdministrador.class));
                else
                    startActivity(new Intent(AtvLogin.this, AtvColaborador.class));
            } else {
                Toast.makeText(AtvLogin.this, "Usuário ou senha inválidos", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            AtaskLogin = null;
            Utils.barraProgresso(AtvLogin.this, PrgLogin, false);
        }
    }

}