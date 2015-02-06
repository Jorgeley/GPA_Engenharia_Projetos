package br.com.gpaengenharia.activities;

import android.app.Activity;
import android.content.Intent;
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
import br.com.gpaengenharia.classes.Utils;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDados;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasEquipe;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasHoje;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasPessoais;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasSemana;

/** Lista as tarefas pessoais com opção de trocar para tarefas da equipe, hoje e semana.
 Também dá opção de agrupamento por tarefas ou projetos
 */
abstract class AtvBase extends Activity implements OnGroupClickListener, OnChildClickListener{
    // <Projeto, List<Tarefa>> árvore de projetos com sublista de tarefas em cada projeto
    private TreeMap<String, List<String>> projetosTreeMap;
    private ExpandableListView lvProjetos;//listView expansível dos projetos
    private Adaptador adaptador; //adaptador do listView
    //instância polimórfica que provê os dados dos projetos pessoais, equipe, hoje e semana
    private ProvedorDados provedorDados;
    private char agrupamento = 't';//t=agrupamento tarefas, p=agrupamento projetos
    private ViewFlipper viewFlipper; //desliza os layouts
    private Animation animFadein; //animaçãozinha para o dashboard

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
        // List<Tarefa> tarefasProjetos sublista de projetosTreeMap
        List<String> tarefasProjetos;
        tarefasProjetos = new ArrayList<String>(this.projetosTreeMap.keySet());
        this.adaptador = new Adaptador(this, this.projetosTreeMap, tarefasProjetos);
        this.lvProjetos.setAdapter(this.adaptador);
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

    /**infla o xml do menu comum ao Adm e Colabordor
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
            //v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            startActivity(new Intent(AtvBase.this, AtvTarefa.class));
            return true;
        }else
            return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        startActivity(new Intent(AtvBase.this, AtvTarefa.class));
        /*Toast.makeText(AtvBase.this, projetosTreeMap.get(tarefasProjetos.get(groupPosition)).get(childPosition)
                + " - " + tarefasProjetos.get(groupPosition), Toast.LENGTH_SHORT).show();*/
        return false;
    }

}