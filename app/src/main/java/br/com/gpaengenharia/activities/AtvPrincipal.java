package br.com.gpaengenharia.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.R;
import br.com.gpaengenharia.classes.Adaptador;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDados;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasEquipe;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasHoje;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasPessoais;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasSemana;

public class AtvPrincipal extends Activity implements OnGroupClickListener, OnChildClickListener{
    //tarefasProjetos nó de projetosTreeMap
    private TreeMap<String, List<String>> projetosTreeMap;
    private List<String> tarefasProjetos;
    private ExpandableListView lvProjetos;
    private Adaptador adaptador;
    private ProvedorDados provedorDados;//instância polimórfica
    private Boolean colapsa = true;//não deixar colapsar o listView qdo agrupado por tarefas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_principal);
        lvProjetos = (ExpandableListView) findViewById(R.id.LVprojetos);
        lvProjetos.setOnGroupClickListener(this);
        lvProjetos.setOnChildClickListener(this);
        provedorDados = new ProvedorDadosTarefasPessoais(this); //polimorfismo
        ordemTarefas();
    }

    private void ordemTarefas(){
        projetosTreeMap = provedorDados.getTarefas(true);
        setAdaptador();
        colapsa = true;
    }

    private void ordemProjetos(){
        projetosTreeMap = provedorDados.getTarefas(false);
        setAdaptador();
        colapsa = false;
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
            case R.id.submenu_pessoais:
                provedorDados = new ProvedorDadosTarefasPessoais(this);//polimorfismo
                ordemTarefas();
                break;
            case R.id.submenu_equipe:
                provedorDados = new ProvedorDadosTarefasEquipe(this);//polimorfismo
                ordemTarefas();
                break;
            case R.id.submenu_hoje:
                provedorDados = new ProvedorDadosTarefasHoje(this);//polimorfismo
                ordemTarefas();
                break;
            case R.id.submenu_semana:
                provedorDados = new ProvedorDadosTarefasSemana(this);//polimorfismo
                ordemTarefas();
                break;
            case R.id.submenu_tarefa:
                ordemTarefas();
                break;
            case R.id.submenu_projeto:
                ordemProjetos();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return colapsa;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Toast.makeText(AtvPrincipal.this, projetosTreeMap.get(tarefasProjetos.get(groupPosition)).get(childPosition)
                + " - " + tarefasProjetos.get(groupPosition), Toast.LENGTH_SHORT).show();
        return false;
    }
}