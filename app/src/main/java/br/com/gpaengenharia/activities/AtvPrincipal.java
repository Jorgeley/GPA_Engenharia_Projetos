package br.com.gpaengenharia.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.R;
import br.com.gpaengenharia.classes.Adaptador;
import br.com.gpaengenharia.classes.ProvedorDados;

public class AtvPrincipal extends Activity {

    //tarefasProjetos nó de projetosTreeMap
    private TreeMap<String, List<String>> projetosTreeMap;
    private List<String> tarefasProjetos;
    private ExpandableListView lvProjetos;
    private Adaptador adaptador;
    private ProvedorDados provedorDados = new ProvedorDados();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_principal);
        lvProjetos = (ExpandableListView) findViewById(R.id.LVprojetos);
        ordemTarefas();
        lvProjetos.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View clickedView, int groupPosition, int childPosition, long id) {
                Toast.makeText(AtvPrincipal.this, projetosTreeMap.get(tarefasProjetos.get(groupPosition)).get(childPosition)
                        + " - " + tarefasProjetos.get(groupPosition), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void ordemTarefas(){
        projetosTreeMap = provedorDados.inverteTreeMap();
        setAdaptador();
    }

    private void ordemProjetos(){
        projetosTreeMap = provedorDados.getDadosTreeMap();
        setAdaptador();
    }

    private void setAdaptador(){
        tarefasProjetos = new ArrayList<String>(projetosTreeMap.keySet());
        adaptador = new Adaptador(this, projetosTreeMap, tarefasProjetos);
        lvProjetos.setAdapter(adaptador);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.atv_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_tarefas:
                ordemTarefas();
                break;
            case R.id.menu_projetos:
                ordemProjetos();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}