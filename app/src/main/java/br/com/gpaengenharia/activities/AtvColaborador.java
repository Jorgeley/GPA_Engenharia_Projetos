package br.com.gpaengenharia.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import br.com.gpaengenharia.R;

public class AtvColaborador extends AtvBase{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_colaborador);
        this.setViews();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = super.criaMenu(menu);
        inflater.inflate(R.menu.atv_colaborador, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            Toast.makeText(this, "col", Toast.LENGTH_SHORT).show();
        }*/
        return super.onOptionsItemSelected(item);
    }

}