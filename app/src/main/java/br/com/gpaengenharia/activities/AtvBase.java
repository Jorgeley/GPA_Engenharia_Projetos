package br.com.gpaengenharia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
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
import android.widget.TextView;
import android.widget.ViewFlipper;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.R;
import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.beans.Tarefa;
import br.com.gpaengenharia.classes.AdaptadorProjetos;
import br.com.gpaengenharia.classes.AdaptadorTarefas;
import br.com.gpaengenharia.classes.Utils;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDados;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasEquipe;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasHoje;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasPessoais;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasSemana;

/**
 * Activity Base para todos os atores do sistema
 * Lista as tarefas pessoais com opção de trocar para tarefas da equipe, hoje e semana.
 * Também dá opção de agrupamento por tarefas ou projetos
 */
abstract class AtvBase extends Activity implements OnGroupClickListener, OnChildClickListener{
    // <Projeto, List<Tarefa>> árvore de projetos com sublista de tarefas em cada projeto
    private TreeMap<Projeto, List<Tarefa>> projetosTreeMap;
    // <Tarefa, List<Projeto>> inversao do TreeMap acima: árvore de tarefas com sublista de projetos em cada tarefa
    private TreeMap<Tarefa, List<Projeto>> tarefasTreeMap = new TreeMap<Tarefa, List<Projeto>>();
    private ExpandableListView lvProjetos;//listView expansível dos projetos
    private AdaptadorProjetos adaptadorProjetos; //adaptadorProjetos do listView
    private AdaptadorTarefas adaptadorTarefas; //adaptadorProjetos do listView
    //instância polimórfica que provê os dados dos projetos pessoais, equipe, hoje e semana
    private ProvedorDados provedorDados;
    private char agrupamento = 't';//t=agrupamento tarefas, p=agrupamento projetos
    private ViewFlipper viewFlipper; //desliza os layouts
    private Animation animFadein; //animaçãozinha para o dashboard

    /**
     * desliza o layout dashboard da esquerda para a direita
     * @return null, pois não é pra voltar na activity anterior
     */
    @Override
    public Intent getParentActivityIntent() {
        Utils.deslizaLayoutEsquerda(this.viewFlipper, findViewById(R.id.LayoutDashboard));
        return null;
    }

    /** seta os views comuns dos layouts Adm e Colaborador */
    protected void setViews(){
        this.viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        this.animFadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        this.lvProjetos = (ExpandableListView) findViewById(R.id.LVprojetos);
        this.lvProjetos.setOnGroupClickListener(this);
        this.lvProjetos.setOnChildClickListener(this);
        //polimorfismo da classe ProvedorDados para ProvedorDadosTarefasPessoais
        this.provedorDados = new ProvedorDadosTarefasPessoais(this);
        agrupaTarefas();
    }

    /**repassa para a classe Utils o trabalho de deslizar as telas
     * @param event
     * @return false se o evento onTouch foi capturado, true se ao contrário
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Utils.contexto = this;
        return Utils.onTouchEvent(  event,
                                    this.viewFlipper,
                                    findViewById(R.id.LayoutDashboard),
                                    findViewById(R.id.LayoutTarefas));
    }

    /** retorna a árvore de projetos invertida apenas com as tarefas */
    private void agrupaTarefas(){
        this.projetosTreeMap = this.provedorDados.getTreeMapBeanProjetosTarefas();
        if (tarefasTreeMap.isEmpty()) {
            //gera novo TreeMap invertido com Tarefa e List<Projeto>
            Object[] projetosArray = this.projetosTreeMap.keySet().toArray();//chaves(Projeto) do TreeMap p/ array
            for (Object objetoProjeto : projetosArray) { //para cada objeto Projeto...
                Projeto projeto = (Projeto) objetoProjeto; //...pega o objeto Projeto...
                //...pega objetos(Tarefa) no indice (Projeto) do TreeMap e transforma em array
                Object[] tarefasArray = this.projetosTreeMap.get(projeto).toArray();
                for (Object objetoTarefa : tarefasArray) { //para cada objeto Tarefa...
                    Tarefa tarefa = (Tarefa) objetoTarefa; //...pega o objeto Tarefa...
                    ArrayList<Projeto> projetos = new ArrayList<Projeto>(); //...cria ArrayList de Projeto...
                    projetos.add(projeto); //...adiciona o objeto Projeto no ArrayList...
                    //...e finalmente adiciona o objeto Tarefa como indice do novo TreeMap contendo ArrayList de Projeto
                    this.tarefasTreeMap.put(tarefa, projetos);
                }
            }
        }
        setAdaptador(true);
        this.lvProjetos.setDividerHeight(-20);//diminui a distancia entre cada grupo do ExpandableListView (miauuuuu)
        this.agrupamento = 't';
    }

    /** retorna a árvore de projetos padrão com sublista de tarefas em cada projeto */
    private void agrupaProjetos(){
        this.projetosTreeMap = this.provedorDados.getTreeMapBeanProjetosTarefas();
        setAdaptador(false);
        this.agrupamento = 'p';
    }

    /**
     * adapta os projetos no listView expansível
     * @param inverte true = TreeMap <Tarefa,ArrayList<Projeto>>
     *                false = TreeMap <Projeto, ArrayList<Tarefa>>
     */
    private void setAdaptador(boolean inverte){
        if (inverte) {
            //singleton
            if (!(this.adaptadorTarefas instanceof AdaptadorTarefas))
                this.adaptadorTarefas = new AdaptadorTarefas(this, this.tarefasTreeMap);
            this.lvProjetos.setAdapter(this.adaptadorTarefas);
            //expande todos os grupos
            for (int grupo = 0; grupo < this.adaptadorTarefas.getGroupCount(); grupo++)
                this.lvProjetos.expandGroup(grupo);
        }else {
            //singleton
            if (!(this.adaptadorProjetos instanceof AdaptadorProjetos))
                this.adaptadorProjetos = new AdaptadorProjetos(this, this.projetosTreeMap);
            this.lvProjetos.setAdapter(this.adaptadorProjetos);
        }
    }

    /**opções comuns dos menus Adm e Colaborador
     * @param item
     * @return o menu selecionado
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.novatarefa:
            case R.id.menu_novatarefa:
                startActivity(new Intent(AtvBase.this, AtvTarefa.class));
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

    //métodos sobrecarregados utilizados pelo menu acima e pelos botões da view layout_base
    public void projetosPessoais(View v){
        v.startAnimation(this.animFadein);
        Utils.deslizaLayoutDireita(this.viewFlipper, findViewById(R.id.LayoutTarefas));
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasPessoais)) {
            this.provedorDados = new ProvedorDadosTarefasPessoais(this);//polimorfismo
            agrupaTarefas();
        }
    }

    public void projetosPessoais(){
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasPessoais)) {
            this.provedorDados = new ProvedorDadosTarefasPessoais(this);//polimorfismo
            agrupaTarefas();
        }
    }

    public void projetosEquipe(View v){
        v.startAnimation(this.animFadein);
        Utils.deslizaLayoutDireita(this.viewFlipper, findViewById(R.id.LayoutTarefas));
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasEquipe)) {
            this.provedorDados = new ProvedorDadosTarefasEquipe(this);//polimorfismo
            agrupaTarefas();
        }
    }

    public void projetosEquipe(){
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasEquipe)) {
            this.provedorDados = new ProvedorDadosTarefasEquipe(this);//polimorfismo
            agrupaTarefas();
        }
    }

    public void projetosHoje(View v){
        v.startAnimation(this.animFadein);
        Utils.deslizaLayoutDireita(this.viewFlipper, findViewById(R.id.LayoutTarefas));
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasHoje)) {
            this.provedorDados = new ProvedorDadosTarefasHoje(this);//polimorfismo
            agrupaTarefas();
        }
    }

    public void projetosHoje(){
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasHoje)) {
            this.provedorDados = new ProvedorDadosTarefasHoje(this);//polimorfismo
            agrupaTarefas();
        }
    }

    public void projetosSemana(View v){
        v.startAnimation(this.animFadein);
        Utils.deslizaLayoutDireita(this.viewFlipper, findViewById(R.id.LayoutTarefas));
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasSemana)) {
            this.provedorDados = new ProvedorDadosTarefasSemana(this);//polimorfismo
            agrupaTarefas();
        }
    }

    public void projetosSemana(){
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasSemana)) {
            this.provedorDados = new ProvedorDadosTarefasSemana(this);//polimorfismo
            agrupaTarefas();
        }
    }

    /**infla o xml do menu comum ao Adm e Colaborador
     * @param menu
     * @return o MenuInflater para adicionar mais opções de menu
     */
    public MenuInflater criaMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_base, menu);
        return inflater;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        if (this.agrupamento == 't') {
            //envia o objeto Tarefa parcelable selecionado para atvTarefa
            Bundle bundleTarefa = new Bundle();
            bundleTarefa.putParcelable("tarefa", (Tarefa) parent.getExpandableListAdapter().getGroup(groupPosition));
            bundleTarefa.putParcelable("projeto", (Projeto) parent.getExpandableListAdapter().getChild(groupPosition, 0));
            Intent atvTarefa = new Intent(AtvBase.this, AtvTarefa.class);
            atvTarefa.putExtras(bundleTarefa);
            //v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            startActivity(atvTarefa);
            return true;
        }else
            return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (this.agrupamento == 'p') {
            Bundle bundleTarefa = new Bundle();
            bundleTarefa.putParcelable("tarefa", (Tarefa) parent.getExpandableListAdapter().getChild(groupPosition, childPosition));
            Intent atvTarefa = new Intent(AtvBase.this, AtvTarefa.class);
            atvTarefa.putExtras(bundleTarefa);
            startActivity(atvTarefa);
            return true;
        }else
            return false;
    }

}