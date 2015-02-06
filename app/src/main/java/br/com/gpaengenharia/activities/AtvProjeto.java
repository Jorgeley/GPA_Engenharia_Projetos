package br.com.gpaengenharia.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import br.com.gpaengenharia.R;
import br.com.gpaengenharia.classes.Utils;

public class AtvProjeto extends FragmentActivity implements Utils.DatePickerFragment.Listener, AdapterView.OnItemSelectedListener {
    private EditText EdtVencimento;
    private Spinner SpnResponsavel;
    private ImageButton BtnNovoResponsavel;
    private String[] responsavel = new String[]{ "responsável" };//arrayString do spinner responsavel


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_projeto);
        Utils.contexto = this;
        EdtVencimento = (EditText) findViewById(R.id.EDTvencimento);
        EdtVencimento.setInputType(0);
        SpnResponsavel = (Spinner) findViewById(R.id.SPNresponsavel);
        SpnResponsavel.setAdapter(Utils.setAdaptador(this, responsavel));
        BtnNovoResponsavel = (ImageButton) findViewById(R.id.BTNnovoresponsavel);
    }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.atv_projeto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

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