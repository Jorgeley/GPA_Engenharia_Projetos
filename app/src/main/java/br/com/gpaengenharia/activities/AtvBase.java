package br.com.gpaengenharia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import br.com.gpaengenharia.R;
import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.beans.Tarefa;
import br.com.gpaengenharia.classes.AdaptadorProjetos;
import br.com.gpaengenharia.classes.AdaptadorTarefas;
import br.com.gpaengenharia.classes.Notificacao;
import br.com.gpaengenharia.classes.Utils;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDados;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasEquipe;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasHoje;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasPessoais;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasSemana;

/**
 * Activity Base para todos os usuarios do sistema
 * Lista as tarefas pessoais com opção de trocar para tarefas da equipe, hoje e semana.
 * Também dá opção de agrupamento por tarefas ou projetosPessoais
 */
public abstract class AtvBase extends Activity implements OnGroupClickListener, OnChildClickListener{

    /**
     * desliza o layout dashboard da esquerda para a direita
     * @return null, pois não é pra voltar na activity anterior
     */
    private ViewFlipper viewFlipper; //desliza os layouts
    @Override
    public Intent getParentActivityIntent() {
        Utils.deslizaLayoutEsquerda(this.viewFlipper, findViewById(R.id.LayoutDashboard));
        return null;
    }

    /**
     * seta os views comuns dos layouts Adm e Colaborador, chamado no OnCreate
     */;
    private ExpandableListView lvProjetos;//listView expansível dos projetosPessoais
    //instância polimórfica que provê os dados dos projetosPessoais pessoais, equipe, hoje e semana
    private static ProvedorDados provedorDados;
    protected void setViews(){
        Log.i("onCreate", String.valueOf(atualizaListView));
        this.viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        this.lvProjetos = (ExpandableListView) findViewById(R.id.LVprojetos);
        this.lvProjetos.setOnGroupClickListener(this);
        this.lvProjetos.setOnChildClickListener(this);
        //polimorfismo da classe ProvedorDados para ProvedorDadosTarefasPessoais
        this.projetosPessoais(true);
    }

    @Override
    protected void onResume() {
        Log.i("onResume", String.valueOf(atualizaListView));
        super.onResume();
        if (AtvLogin.usuario == null)
            startActivity(new Intent(this, AtvLogin.class));
        else {
            atualizaListView = true;
            this.atualizaListView();
        }

    }

    /**
     * atualiza tarefas caso a classe ServicoTarefas tenha setado a flag atualizaListView
     */
    // <Projeto, List<Tarefa>> árvore de projetosPessoais com sublista de tarefas em cada projeto
    private TreeMap<Projeto, List<Tarefa>> projetosTreeMap;
    // <Tarefa, List<Projeto>> inversao do projetosTreeMap
    private TreeMap<Tarefa, List<Projeto>> tarefasTreeMap;
    //flag setada pela classe ServicoTarefas indicando que houve atualizaçao das tarefas
    public static boolean atualizaListView;
    @Override
    public void onUserInteraction() {
        Log.i("onUserInteraction", String.valueOf(atualizaListView));
        this.atualizaListView();
    }

    private void atualizaListView(){
        if (atualizaListView){
            this.provedorDados = new ProvedorDadosTarefasPessoais(this, false);
            this.projetosTreeMap = null;
            this.tarefasTreeMap = null;
            this.adaptadorTarefas = null;
            this.adaptadorProjetos = null;
            agrupaTarefas();
            Notificacao.cancell(this,1);
            atualizaListView = false;
        }
    }

    /**
     * repassa para a classe Utils o trabalho de deslizar as telas
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

    /**
     * retorna a árvore de projetosPessoais invertida, lista de tarefas contendo sublista de projetosPessoais
     */
    private char agrupamento = 't';//t=agrupamento tarefas, p=agrupamento projetosPessoais
    private void agrupaTarefas(){
        if (this.projetosTreeMap == null || this.projetosTreeMap.isEmpty())
            this.projetosTreeMap = this.provedorDados.getTreeMapBeanProjetosTarefas();
        if (this.tarefasTreeMap == null || this.tarefasTreeMap.isEmpty()) {
            this.tarefasTreeMap = new TreeMap<Tarefa, List<Projeto>>();
            //gera novo TreeMap invertido com Tarefa e List<Projeto>
            for (Map.Entry<Projeto, List<Tarefa>> projetoTarefas : this.projetosTreeMap.entrySet()){
                List<Projeto> projetos = new ArrayList<Projeto>();
                projetos.add(projetoTarefas.getKey());
                for (Tarefa tarefa : projetoTarefas.getValue())
                    this.tarefasTreeMap.put(tarefa, projetos);
            }
        }
        setAdaptador(true);
        //diminui a distancia entre cada grupo do ExpandableListView (miauuuuu)
        this.lvProjetos.setDividerHeight(-20);
        this.agrupamento = 't';
    }

    /**
     * retorna a árvore de projetosPessoais padrão: lista de projetosPessoais contendo sublista de tarefas
     */
    private void agrupaProjetos(){
        if (this.projetosTreeMap == null || this.projetosTreeMap.isEmpty())
            this.projetosTreeMap = this.provedorDados.getTreeMapBeanProjetosTarefas();
        setAdaptador(false);
        this.lvProjetos.setDividerHeight(0);
        this.agrupamento = 'p';
    }

    /**
     * adapta os projetosPessoais no listView expansível
     * @param inverte true = TreeMap <Tarefa,ArrayList<Projeto>>
     *                false = TreeMap <Projeto, ArrayList<Tarefa>>
     */
    private AdaptadorProjetos adaptadorProjetos; //adaptadorProjetos do listView
    private AdaptadorTarefas adaptadorTarefas; //adaptadorTarefas do listView
    private void setAdaptador(boolean inverte){
        if (inverte) {
            //singleton
            if (!(this.adaptadorTarefas instanceof AdaptadorTarefas))
                this.adaptadorTarefas = new AdaptadorTarefas(this, this.tarefasTreeMap);
            this.lvProjetos.setAdapter(this.adaptadorTarefas);
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
                this.projetosPessoais(false);
                break;
            case R.id.projetos_equipe:
                this.projetosEquipes();
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


    /**TODO talvez seja melhor eu ter um objeto ProvedorDados para cada um ao inves de polimorfismo
     * para nao ficar reinstanciando, ou seja, se clicar no menu Pessoais, depois Equipes, e de novo
     * Pessoais tera reinstanciado o ProvedorDadosTarefasPessoais duas vezes... think about it...
     */
    //métodos sobrecarregados utilizados pelo menu acima e pelos botões da view layout_base
    public void projetosPessoais(View v){
        Utils.deslizaLayoutDireita(this.viewFlipper, findViewById(R.id.LayoutTarefas));
        this.projetosPessoais(false);
    }

    public void projetosPessoais(final boolean primeiraExecucao){
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasPessoais)) {
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    AtvBase.setProvedorDados(new ProvedorDadosTarefasPessoais(AtvBase.this, primeiraExecucao));//polimorfismo
                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    agrupaTarefas();
                }
            }.execute();
        }
    }

    public void projetosEquipes(View v){
        Utils.deslizaLayoutDireita(this.viewFlipper, findViewById(R.id.LayoutTarefas));
        this.projetosEquipes();
    }

    public void projetosEquipes(){
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasEquipe)) {
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    AtvBase.setProvedorDados(new ProvedorDadosTarefasEquipe(AtvBase.this));
                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    agrupaTarefas();
                }
            }.execute();
        }
    }

    public void projetosHoje(View v){
        Utils.deslizaLayoutDireita(this.viewFlipper, findViewById(R.id.LayoutTarefas));
        this.projetosHoje();
    }

    public void projetosHoje(){
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasHoje)) {
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    AtvBase.setProvedorDados(new ProvedorDadosTarefasHoje(AtvBase.this));
                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    agrupaTarefas();
                }
            }.execute();
        }
    }

    public void projetosSemana(View v){
        Utils.deslizaLayoutDireita(this.viewFlipper, findViewById(R.id.LayoutTarefas));
        this.projetosSemana();
    }

    public void projetosSemana(){
        //singleton
        if (!(this.provedorDados instanceof ProvedorDadosTarefasSemana)) {
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    AtvBase.setProvedorDados(new ProvedorDadosTarefasSemana(AtvBase.this));
                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    agrupaTarefas();
                }
            }.execute();
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

    /**Quando clica no grupo do ExpandableListView chama Activity AtvTarefa e repassa
     * os beans Projeto e Tarefa
     * @param parent
     * @param v
     * @param groupPosition
     * @param id
     * @return
     */
    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        if (this.agrupamento == 't') {
            Tarefa tarefa = (Tarefa) parent.getExpandableListAdapter().getGroup(groupPosition);
            this.atualizaTarefaTreeMap(tarefa.getId());
            //envia o objeto Tarefa parcelable selecionado para atvTarefa
            Bundle bundleTarefa = new Bundle();
            bundleTarefa.putParcelable("tarefa", (Tarefa) parent.getExpandableListAdapter().getGroup(groupPosition));
            bundleTarefa.putParcelable("projeto", (Projeto) parent.getExpandableListAdapter().getChild(groupPosition, 0));
            Intent atvTarefa = new Intent(AtvBase.this, AtvTarefa.class);
            atvTarefa.putExtras(bundleTarefa);
            startActivity(atvTarefa);
            return true;
        }else
            return false;
    }

    /**Quando clica no sub item do grupo do ExpandableListView chama Activity AtvTarefa e repassa
     * os beans Projeto e Tarefa
     * @param parent
     * @param v
     * @param groupPosition
     * @param childPosition
     * @param id
     * @return
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (this.agrupamento == 'p') {
            Tarefa tarefa = (Tarefa) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
            this.atualizaTarefaTreeMap(tarefa.getId());
            Bundle bundleTarefa = new Bundle();
            bundleTarefa.putParcelable("projeto", (Projeto) parent.getExpandableListAdapter().getGroup(groupPosition));
            bundleTarefa.putParcelable("tarefa", tarefa);
            Intent atvTarefa = new Intent(AtvBase.this, AtvTarefa.class);
            atvTarefa.putExtras(bundleTarefa);
            startActivity(atvTarefa);
            return true;
        }else
            return false;
    }

    /**atualiza o tarefasTreeMap caso uma tarefa tenha sido alterada pela Activity AtvTarefa
     * @param idTarefa
     */
    // flag enviada pela Activity AtvTarefa p/ saber qual tarefa atualizar no TreeMap
    public static int atualizarTarefaId;
    private void atualizaTarefaTreeMap(int idTarefa){
        //se a tarefa clicada conferir com a atualizada pela activity AtvTarefa...
        if (idTarefa == atualizarTarefaId) { //...entao reconstroi o TreeMap
            this.provedorDados = new ProvedorDadosTarefasPessoais(this, false);
            this.tarefasTreeMap.clear();
            this.adaptadorTarefas = null;
            agrupaTarefas();
            atualizarTarefaId = 0; //sinaliza que ja atualizou o TreeMap
        }
    }

    public static void setProvedorDados(ProvedorDados provedorDados){
        AtvBase.provedorDados = provedorDados;
    }

}