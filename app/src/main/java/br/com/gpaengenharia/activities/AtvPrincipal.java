package br.com.gpaengenharia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
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

/*
Lista as tarefas pessoais com opção de trocar para tarefas da equipe, hoje e semana.
Também dá opção de agrupamento por tarefas ou projetos
 */
public class AtvPrincipal extends Activity implements OnGroupClickListener, OnChildClickListener{
    // <Projeto, List<Tarefa>> árvore de projetos com sublista de tarefas em cada
    private TreeMap<String, List<String>> projetosTreeMap;
    // List<Tarefa> tarefasProjetos nó de projetosTreeMap
    private List<String> tarefasProjetos;
    private ExpandableListView lvProjetos;//listView expansível dos projetos
    private Adaptador adaptador;
    //instância polimórfica contendo os dados dos projetos pessoais, equipe, hoje e semana
    private ProvedorDados provedorDados;
    private Boolean colapsa = true;//não deixa colapsar o listView qdo agrupado por tarefas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_principal);
        this.lvProjetos = (ExpandableListView) findViewById(R.id.LVprojetos);
        this.lvProjetos.setOnGroupClickListener(this);
        this.lvProjetos.setOnChildClickListener(this);
        //polimorfismo da classe ProvedorDados para ProvedorDadosTarefasPessoais
        this.provedorDados = new ProvedorDadosTarefasPessoais(this);
        agrupaTarefas();
    }

    //retorna a árvore de projetos invertida apenas com as tarefas
    private void agrupaTarefas(){
        this.projetosTreeMap = this.provedorDados.getTarefas(true);
        setAdaptador();
        this.colapsa = true;
    }

    //retorna a árvore de projetos padrão com sublista de tarefas em cada projeto
    private void agrupaProjetos(){
        this.projetosTreeMap = this.provedorDados.getTarefas(false);
        setAdaptador();
        this.colapsa = false;
    }

    //adapta os projetos no listView expansível
    private void setAdaptador(){
        this.tarefasProjetos = new ArrayList<String>(this.projetosTreeMap.keySet());
        this.adaptador = new Adaptador(this, this.projetosTreeMap, this.tarefasProjetos);
        this.lvProjetos.setAdapter(this.adaptador);
    }

    //opções do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.novatarefa:
                startActivity(new Intent(AtvPrincipal.this, AtvTarefa.class));
                break;
            case R.id.projetos_pessoais:
                this.provedorDados = new ProvedorDadosTarefasPessoais(this);//polimorfismo
                agrupaTarefas();
                break;
            case R.id.projetos_equipe:
                this.provedorDados = new ProvedorDadosTarefasEquipe(this);//polimorfismo
                agrupaTarefas();
                break;
            case R.id.projetos_hoje:
                this.provedorDados = new ProvedorDadosTarefasHoje(this);//polimorfismo
                agrupaTarefas();
                break;
            case R.id.projetos_semana:
                this.provedorDados = new ProvedorDadosTarefasSemana(this);//polimorfismo
                agrupaTarefas();
                break;
            case R.id.agrupamento_tarefa:
                agrupaTarefas();
                break;
            case R.id.agrupamento_projeto:
                agrupaProjetos();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.atv_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return this.colapsa;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        startActivity(new Intent(AtvPrincipal.this, AtvTarefa.class));
        /*Toast.makeText(AtvPrincipal.this, projetosTreeMap.get(tarefasProjetos.get(groupPosition)).get(childPosition)
                + " - " + tarefasProjetos.get(groupPosition), Toast.LENGTH_SHORT).show();*/
        return false;
    }
}