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
import android.widget.Spinner;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import br.com.gpaengenharia.R;
import br.com.gpaengenharia.beans.Equipe;
import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.classes.Utils;
import br.com.gpaengenharia.classes.xmls.XmlEquipe;
import br.com.gpaengenharia.classes.xmls.XmlTarefasPessoais;

/**
 * Activity de gerenciamento de projetosPessoais
 */
public class AtvProjeto extends FragmentActivity implements Utils.DatePickerFragment.Listener, AdapterView.OnItemSelectedListener {
    private EditText EdtVencimento;
    private Spinner SpnEquipe;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_projeto);
        Utils.contexto = this;
        EdtVencimento = (EditText) findViewById(R.id.EDTvencimento);
        EdtVencimento.setInputType(0);
        SpnEquipe = (Spinner) findViewById(R.id.SPNequipe);
        if (SpnEquipe.getAdapter() == null) {
            //busca Equipes via webservice e set no Spinner
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
                this.grava();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean grava(){
        String nome = ((EditText) findViewById(R.id.EDTprojeto)).getText().toString();
        //int responsavel = ((Spinner) findViewById(R.id.SPNresponsavel)).getSelectedItem();
        Projeto projeto = new Projeto(Parcel.obtain());

        return false;
    }

    /** setado diretamente na propriedade OnClick do BTNnovoResponsavel */
    public void onClickBtnNovoResponsavel(View v){
        startActivity(new Intent(this, AtvUsuarios.class));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}