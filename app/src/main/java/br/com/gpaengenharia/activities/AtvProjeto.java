package br.com.gpaengenharia.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.widget.AdapterView.OnItemSelectedListener;

import br.com.gpaengenharia.beans.Usuario;
import br.com.gpaengenharia.classes.Utils.DatePickerFragment.Listener;
import br.com.gpaengenharia.R;
import br.com.gpaengenharia.beans.Equipe;
import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.classes.Utils;
import br.com.gpaengenharia.classes.WebService;
import br.com.gpaengenharia.classes.xmls.XmlEquipe;
import br.com.gpaengenharia.classes.xmls.XmlProjeto;

/**
 * Activity de gerenciamento de projetosPessoais
 */
public class AtvProjeto extends FragmentActivity implements Listener, OnItemSelectedListener{
    private EditText EdtVencimento;
    private Spinner SpnEquipe;
    private ProgressBar PrgProjeto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_projeto);
        Utils.contexto = this;
        EdtVencimento = (EditText) findViewById(R.id.EDTvencimento);
        EdtVencimento.setInputType(0);
        PrgProjeto = (ProgressBar) findViewById(R.id.PRGprojeto);
        if (AtvLogin.usuario.getPerfil().equals("adm")) {
            SpnEquipe = (Spinner) findViewById(R.id.SPNequipe);
            SpnEquipe.setOnItemSelectedListener(this);
            if (SpnEquipe.getAdapter() == null) {
                /**
                 * busca Equipes via webservice e set no Spinner
                 * TODO atualizar essa lista quando houver novas equipes
                 */
                new AsyncTask<Void, Void, List<Equipe>>() {

                    @Override
                    protected List<Equipe> doInBackground(Void... voids) {
                        List<Equipe> equipes = null;
                        try {
                            File arquivo = new File(AtvProjeto.this.getFilesDir() + "/" + XmlEquipe.getNomeArquivoXML());
                            XmlEquipe xmlEquipe = new XmlEquipe(AtvProjeto.this);
                            if (!arquivo.exists())
                                xmlEquipe.criaXmlEquipesWebservice(false);
                            equipes = xmlEquipe.leXmlEquipes();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return equipes;
                    }

                    @Override
                    protected void onPostExecute(final List<Equipe> equipes) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SpnEquipe.setAdapter(new ArrayAdapter<>(AtvProjeto.this, android.R.layout.simple_spinner_item, equipes));
                            }
                        });
                    }
                }.execute();
            }
        }else{ //tira Spinner de equipes para usuarios comuns
            TableRow TrEquipes = (TableRow) findViewById(R.id.TRequipes);
            TrEquipes.setVisibility(View.GONE);
        }
    }

    /**
     * retorna a data do datePicker
     * @param data
     */
    @Override
    public void getData(String data) {
        EdtVencimento.setText(data);
    }

    /** setado diretamente na propriedade OnClick do EDTvencimento */
    public void mostraDatePicker(View v) {
        DialogFragment newFragment = new Utils.DatePickerFragment();
        newFragment.show(this.getFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.atv_projeto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionbar_grava:
            case R.id.menu_grava:
                this.grava();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * grava projeto via webservice
     */
    private void grava(){
        String nome = ((EditText) findViewById(R.id.EDTprojeto)).getText().toString();
        String descricao = ((EditText) findViewById(R.id.EDTdescricao)).getText().toString();
        String vencimentoString = ((EditText) findViewById(R.id.EDTvencimento)).getText().toString();
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        Date vencimento = new Date();
        try {
            vencimento = formatoData.parse(vencimentoString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Projeto projeto = new Projeto(Parcel.obtain());
        projeto.setNome(nome);
        projeto.setDescricao(descricao);
        projeto.setVencimento(vencimento);
        if (this.equipe != null)
            projeto.setEquipe(this.equipe);
        else
            projeto.setUsuario(AtvLogin.usuario);
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected void onPreExecute() {
                Utils.barraProgresso(AtvProjeto.this, PrgProjeto, true);
            }
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean ok = WebService.gravaProjeto(projeto);
                if (ok) {
                    try {
                        XmlProjeto xmlProjeto = new XmlProjeto(AtvProjeto.this);
                        xmlProjeto.criaXmlProjetosWebservice(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return ok;
            }
            @Override
            protected void onPostExecute(Boolean ok) {
                if (ok) {
                    Toast.makeText(AtvProjeto.this, "Projeto Gravado", Toast.LENGTH_SHORT).show();
                    AtvProjeto.this.finish();
                }else
                    Toast.makeText(AtvProjeto.this, "Erro ao tentar gravar Projeto", Toast.LENGTH_SHORT).show();
                Utils.barraProgresso(AtvProjeto.this, PrgProjeto, false);
            }
        }.execute();
    }

    /** setado diretamente na propriedade OnClick do BTNnovoResponsavel */
    public void onClickBtnNovoResponsavel(View v){
        startActivity(new Intent(this, AtvUsuarios.class));
    }

    Equipe equipe;
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.equipe = (Equipe) adapterView.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}