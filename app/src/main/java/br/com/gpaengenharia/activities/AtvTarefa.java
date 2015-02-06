package br.com.gpaengenharia.activities;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import br.com.gpaengenharia.R;
import br.com.gpaengenharia.classes.Utils;
import br.com.gpaengenharia.classes.Utils.DatePickerFragment;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableLayout;
import android.widget.TableRow;

/** Mostra os dados da tarefa */
public class AtvTarefa extends FragmentActivity implements DatePickerFragment.Listener, OnItemSelectedListener{
    private EditText EdtTarefa;
    private EditText EdtDescricao;
    private EditText EdtDialogo;
    private EditText EdtVencimento;
    private Spinner SpnResponsavel;
    private Spinner SpnProjeto;
    private String[] responsavel = new String[]{ "responsável" };//arrayString do spinner responsavel
    private String[] projeto = new String[]{ "projeto" };//arraytring do spinner projeto

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_tarefa);
        Utils.contexto = this;
        EdtTarefa = (EditText) findViewById(R.id.EDTtarefa);
        EdtDescricao = (EditText) findViewById(R.id.EDTdescricao);
        EdtDialogo = (EditText) findViewById(R.id.EDTdialogo);
        EdtVencimento = (EditText) findViewById(R.id.EDTvencimento);
        EdtVencimento.setInputType(0);
        SpnResponsavel = (Spinner) findViewById(R.id.SPNresponsavel);
        SpnResponsavel.setAdapter(Utils.setAdaptador(this, responsavel));
        SpnProjeto = (Spinner) findViewById(R.id.SPNprojeto);
        SpnProjeto.setAdapter(Utils.setAdaptador(this, projeto));
        //caso usuário seja adminnistrador, adiciona botões de administração no layout
        if (AtvLogin.usuario.getPerfil() == "adm")
            addBotoes();
    }

    @Override
    public void getData(String data) {
        EdtVencimento.setText(data);
    }

    /** setado diretamente na propriedade OnClick do EDTvencimento */
    public void mostraDatePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getFragmentManager(), "datePicker");
    }

    /** adiciona botões addProjeto e addResponsavel ao layout  */
    private void addBotoes(){
        //cria botões
        ImageButton BtnAddProjeto = new ImageButton(this);
        BtnAddProjeto.setImageResource(android.R.drawable.ic_menu_add);
        BtnAddProjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AtvTarefa.this, AtvProjeto.class));
            }
        });
        ImageButton BtnAddResponsavel = new ImageButton(this);
        BtnAddResponsavel.setImageResource(android.R.drawable.ic_menu_add);
        BtnAddResponsavel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AtvTarefa.this, AtvUsuarios.class));
            }
        });
        //adiciona no layout
        TableRow TrSpinners = (TableRow) findViewById(R.id.TRspinners);
        TrSpinners.addView(BtnAddProjeto, 1);
        TrSpinners.addView(BtnAddResponsavel, 3);
        //adapta os outros views para mesclar as células do TableLayout
        TableRow.LayoutParams params4 = new TableRow.LayoutParams();
        params4.span = 4;
        EdtTarefa.setLayoutParams(params4);
        EdtDescricao.setLayoutParams(params4);
        EdtDialogo.setLayoutParams(params4);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams();
        params2.span = 2;
        EdtVencimento.setLayoutParams(params2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.atv_tarefa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}