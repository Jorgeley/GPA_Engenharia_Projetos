package br.com.gpaengenharia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ViewFlipper;
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

/**
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
    private char agrupamento = 't';//t=agrupamento tarefas, p=agrupamento projetos
    private float x1,x2, y1, y2; //coordenadas de touchEvent
    private ViewFlipper viewFlipper; //desliza os layouts
    private Animation animFadein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_principal);
        this.viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        this.animFadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        this.lvProjetos = (ExpandableListView) findViewById(R.id.LVprojetos);
        this.lvProjetos.setOnGroupClickListener(this);
        this.lvProjetos.setOnChildClickListener(this);
        //polimorfismo da classe ProvedorDados para ProvedorDadosTarefasPessoais
        this.provedorDados = new ProvedorDadosTarefasPessoais(this);
        agrupaTarefas();
    }

    //detecta movimentos de fling (deslizar entre telas)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                x1 = event.getX();
                y1 = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = event.getX();
                y2 = event.getY();
                //desliza layouts da esquerda pra direita
                if (x1 < x2) {
                    //se já está no layout dashboard então não precisa deslizar
                    if (this.viewFlipper.getCurrentView().getId() == R.id.LayoutDashboard)
                        break;
                    this.viewFlipper.setInAnimation(this, R.anim.entra_esquerda);
                    this.viewFlipper.setOutAnimation(this, R.anim.sai_direita);
                    this.viewFlipper.setDisplayedChild(this.viewFlipper.indexOfChild(findViewById(R.id.LayoutDashboard)));//showNext()
                }else if (x1 > x2) {//desliza layouts da direita pra esquerda
                        //se já está no layout tarefas então não precisa deslizar
                        if (this.viewFlipper.getCurrentView().getId() == R.id.LayoutTarefas)
                            break;
                        this.deslizaLayoutTarefas();
                }
                break;
            }
                /*//de cima para baixo
                if (y1 < y2){
                    Toast.makeText(this, "UP to Down Swap Performed", Toast.LENGTH_LONG).show();
                }
                //de baixo pra cima
                if (y1 > y2){
                    Toast.makeText(this, "Down to UP Swap Performed", Toast.LENGTH_LONG).show();
                }
                break;*/
        }
        return false;
    }

    /** retorna a árvore de projetos invertida apenas com as tarefas */
    private void agrupaTarefas(){
        this.projetosTreeMap = this.provedorDados.getTarefas(true);
        setAdaptador();
        //expande todos os grupos
        for (int grupo = 0; grupo < this.adaptador.getGroupCount(); grupo++)
            this.lvProjetos.expandGroup(grupo);
        this.agrupamento = 't';
    }

    /** retorna a árvore de projetos padrão com sublista de tarefas em cada projeto */
    private void agrupaProjetos(){
        this.projetosTreeMap = this.provedorDados.getTarefas(false);
        setAdaptador();
        this.agrupamento = 'p';
    }

    /** adapta os projetos no listView expansível */
    private void setAdaptador(){
        this.tarefasProjetos = new ArrayList<String>(this.projetosTreeMap.keySet());
        this.adaptador = new Adaptador(this, this.projetosTreeMap, this.tarefasProjetos);
        this.lvProjetos.setAdapter(this.adaptador);
    }

    /** opções do menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.novatarefa:
                startActivity(new Intent(AtvPrincipal.this, AtvTarefa.class));
                break;
            case R.id.projetos_pessoais:
                this.projetosPessoais();
                break;
            case R.id.projetos_equipe:
                this.projetosEquipe();
                break;
            case R.id.projetos_hoje:
                this.projetosHoje();
                break;
            case R.id.projetos_semana:
                this.projetosSemana();
                break;
            case R.id.agrupamento_tarefa:
            case R.id.actionbar_tarefa:
                agrupaTarefas();
                break;
            case R.id.agrupamento_projeto:
            case R.id.actionbar_projeto:
                agrupaProjetos();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //métodos sobrecarregados utilizados pelo menu acima e pelos botões da view atv_principal
    public void projetosPessoais(View v){
        v.startAnimation(animFadein);
        this.deslizaLayoutTarefas();
        this.provedorDados = new ProvedorDadosTarefasPessoais(this);//polimorfismo
        agrupaTarefas();
    }

    public void projetosPessoais(){
        this.provedorDados = new ProvedorDadosTarefasPessoais(this);//polimorfismo
        agrupaTarefas();
    }

    public void projetosEquipe(View v){
        v.startAnimation(animFadein);
        this.deslizaLayoutTarefas();
        this.provedorDados = new ProvedorDadosTarefasEquipe(this);//polimorfismo
        agrupaTarefas();
    }

    public void projetosEquipe(){
        this.provedorDados = new ProvedorDadosTarefasEquipe(this);//polimorfismo
        agrupaTarefas();
    }

    public void projetosHoje(View v){
        v.startAnimation(animFadein);
        this.deslizaLayoutTarefas();
        this.provedorDados = new ProvedorDadosTarefasHoje(this);//polimorfismo
        agrupaTarefas();
    }

    public void projetosHoje(){
        this.provedorDados = new ProvedorDadosTarefasHoje(this);//polimorfismo
        agrupaTarefas();
    }

    public void projetosSemana(View v){
        v.startAnimation(animFadein);
        this.deslizaLayoutTarefas();
        this.provedorDados = new ProvedorDadosTarefasSemana(this);//polimorfismo
        agrupaTarefas();
    }

    public void projetosSemana(){
        this.provedorDados = new ProvedorDadosTarefasSemana(this);//polimorfismo
        agrupaTarefas();
    }

    private void deslizaLayoutTarefas(){
        this.viewFlipper.setInAnimation(this, R.anim.entra_direita);
        this.viewFlipper.setOutAnimation(this, R.anim.sai_esquerda);
        this.viewFlipper.setDisplayedChild(this.viewFlipper.indexOfChild(findViewById(R.id.LayoutTarefas)));//showPrevious();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.atv_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        if (this.agrupamento == 't') {
            startActivity(new Intent(AtvPrincipal.this, AtvTarefa.class));
            return true;
        }else
            return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        startActivity(new Intent(AtvPrincipal.this, AtvTarefa.class));
        /*Toast.makeText(AtvPrincipal.this, projetosTreeMap.get(tarefasProjetos.get(groupPosition)).get(childPosition)
                + " - " + tarefasProjetos.get(groupPosition), Toast.LENGTH_SHORT).show();*/
        return false;
    }
}