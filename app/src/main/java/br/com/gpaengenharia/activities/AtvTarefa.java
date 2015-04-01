package br.com.gpaengenharia.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import br.com.gpaengenharia.R;
import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.beans.Tarefa;
import br.com.gpaengenharia.beans.Usuario;
import br.com.gpaengenharia.classes.ServicoTarefas;
import br.com.gpaengenharia.classes.Utils;
import br.com.gpaengenharia.classes.Utils.DatePickerFragment;
import br.com.gpaengenharia.classes.Utils.DatePickerFragment.Listener;
import br.com.gpaengenharia.classes.WebService;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasEquipe;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasHoje;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasPessoais;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasSemana;
import br.com.gpaengenharia.classes.xmls.XmlProjeto;
import br.com.gpaengenharia.classes.xmls.XmlTarefasEquipe;
import br.com.gpaengenharia.classes.xmls.XmlTarefasHoje;
import br.com.gpaengenharia.classes.xmls.XmlTarefasPessoais;
import br.com.gpaengenharia.classes.xmls.XmlTarefasSemana;
import br.com.gpaengenharia.classes.xmls.XmlUsuario;

/**
 * Activity de gerenciamento de tarefas
 */
public class AtvTarefa extends FragmentActivity implements Listener, OnItemSelectedListener, OnClickListener{
    private Projeto projeto; //bean
    private Tarefa tarefa; //bean
    private EditText EdtTarefa;
    private EditText EdtDescricao;
    private EditText EdtDialogo;
    private EditText EdtVencimento;
    private Spinner SpnResponsavel;
    private Spinner SpnProjeto;
    private String[] responsaveis = new String[]{ "responsável" };//arrayString do spinner responsaveis
    private String[] projetos = new String[]{ "projetosPessoais" };//arraytring do spinner projeto
    private Usuario usuario;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_tarefa);
        Utils.contexto = this;
        EdtTarefa = (EditText) findViewById(R.id.EDTtarefa);
        EdtDescricao = (EditText) findViewById(R.id.EDTdescricao);
        EdtDialogo = (EditText) findViewById(R.id.EDTdialogo);
        EdtDialogo.setMovementMethod(new ScrollingMovementMethod());
        EdtVencimento = (EditText) findViewById(R.id.EDTvencimento);
        SpnResponsavel = (Spinner) findViewById(R.id.SPNresponsavel);
        SpnProjeto = (Spinner) findViewById(R.id.SPNprojeto);
        //caso usuário seja administrador, adiciona botões de administração no layout
        if (AtvLogin.usuario != null) {
            if (AtvLogin.usuario.getPerfil().equals("adm") )
                addBotoes();
            else{ //desabilita Spinners
                this.SpnProjeto.setEnabled(false);
                this.SpnResponsavel.setEnabled(false);
            }
        }
    }

    @Override
    protected void onResume(){
        Bundle bundleTarefa = getIntent().getExtras();
        //se tiver sido enviado objetos Projeto e Tarefa, recupera-os e seta nos views
        if (bundleTarefa != null) {
            this.projeto = bundleTarefa.getParcelable("projeto");
            this.tarefa = bundleTarefa.getParcelable("tarefa");
            EdtTarefa.setText(this.tarefa.getNome());
            this.projetos[0] = this.projeto.getNome();
            SpnProjeto.setAdapter(Utils.setAdaptador(this, this.projetos));
            this.responsaveis[0] = this.tarefa.getResponsavel()!=null ? this.tarefa.getResponsavel().getNome() : "responsavel";
            SpnResponsavel.setAdapter(Utils.setAdaptador(this, this.responsaveis));
            EdtDescricao.setText(Html.fromHtml(this.tarefa.getDescricao()));
            if (this.tarefa.getComentario()!=null)
                EdtDialogo.setText(Html.fromHtml(this.tarefa.getComentario()));
            SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
            String data = formatoData.format(this.tarefa.getVencimento());//seta data
            EdtVencimento.setText(data);
        }else{
            TableRow TrDialogo = (TableRow) findViewById(R.id.TRdialogo);
            TrDialogo.setVisibility(View.GONE);
            SpnProjeto.setOnItemSelectedListener(this);
            if (SpnProjeto.getAdapter() == null){
                /**
                 * busca lista de projetos e usuarios
                 * TODO atualizar essas listas quando houver novos projetos ou usuarios
                 */
                new AsyncTask<Void, Void, List<Projeto>>(){
                    @Override
                    protected List<Projeto> doInBackground(Void... voids) {
                        List<Projeto> projetos = null;
                        try {
                            File arquivo = new File(AtvTarefa.this.getFilesDir() + "/" + XmlProjeto.getNomeArquivoXML());
                            XmlProjeto xmlProjeto = new XmlProjeto(AtvTarefa.this);
                            if (!arquivo.exists())
                                xmlProjeto.criaXmlProjetosWebservice(false);
                            projetos = xmlProjeto.leXmlProjetos();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return projetos;
                    }
                    @Override
                    protected void onPostExecute(final List<Projeto> projetos) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SpnProjeto.setAdapter(new ArrayAdapter<>(AtvTarefa.this, android.R.layout.simple_spinner_item, projetos));
                            }
                        });
                    }
                }.execute();
            }
            SpnResponsavel.setOnItemSelectedListener(this);
            if (SpnResponsavel.getAdapter() == null){
                new AsyncTask<Void, Void, List<Usuario>>(){
                    @Override
                    protected List<Usuario> doInBackground(Void... voids) {
                        List<Usuario> usuarios = null;
                        try {
                            File arquivo = new File(AtvTarefa.this.getFilesDir() + "/" + XmlUsuario.getNomeArquivoXML());
                            XmlUsuario xmlUsuario = new XmlUsuario(AtvTarefa.this);
                            if (!arquivo.exists())
                                xmlUsuario.criaXmlUsuariosWebservice(false);
                            usuarios = xmlUsuario.leXmlUsuarios();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return usuarios;
                    }
                    @Override
                    protected void onPostExecute(final List<Usuario> usuarios) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SpnResponsavel.setAdapter(new ArrayAdapter<>(AtvTarefa.this, android.R.layout.simple_spinner_item, usuarios));
                            }
                        });
                    }
                }.execute();
            }
        }
        super.onResume();
    }

    /**retorna a data do datePicker
     * @param data
     */
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
        //Habilita Spinners
        this.SpnProjeto.setEnabled(true);
        this.SpnResponsavel.setEnabled(true);
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
        getMenuInflater().inflate(R.menu.atv_tarefa, menu);
        return true;
    }

    View layoutComentario = null;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionbar_comenta:
            case R.id.menu_comenta:
                this.novoComentario();
                break;
            case R.id.actionbar_grava:
            case R.id.menu_grava:
                this.grava();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * grava tarefa via webservice
     */
    private void grava(){
        String nome = ((EditText) findViewById(R.id.EDTtarefa)).getText().toString();
        String descricao = ((EditText) findViewById(R.id.EDTdescricao)).getText().toString();
        String vencimentoString = ((EditText) findViewById(R.id.EDTvencimento)).getText().toString();
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        Date vencimento = new Date();
        try {
            vencimento = formatoData.parse(vencimentoString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Tarefa tarefa = new Tarefa(Parcel.obtain());
        tarefa.setNome(nome);
        tarefa.setDescricao(descricao);
        tarefa.setVencimento(vencimento);
        tarefa.setResponsavel(this.usuario);
        tarefa.setProjeto(this.projeto);
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean ok = WebService.gravaTarefa(tarefa);
                if (ok) {
                    ServicoTarefas servicoTarefas = new ServicoTarefas();
                    servicoTarefas.setContexto(AtvTarefa.this);
                    servicoTarefas.run();
                }
                return ok;
            }
            @Override
            protected void onPostExecute(Boolean ok) {
                if (ok) {
                    Toast.makeText(AtvTarefa.this, "Tarefa Gravada", Toast.LENGTH_SHORT).show();
                    AtvTarefa.this.finish();
                }else
                    Toast.makeText(AtvTarefa.this, "Erro ao tentar gravar Tarefa", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    //utilizado pelo UIdget comentarios
    public void novoComentario(View v){
        this.novoComentario();
    }

    private void novoComentario(){
        LayoutInflater factory = LayoutInflater.from(this);
        layoutComentario = factory.inflate(R.layout.comentario, null);
        final AlertDialog.Builder comentario = new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.actionbar_comenta)
                .setView(layoutComentario)
                .setPositiveButton(R.string.actionbar_grava, this);
        comentario.show();
    }

    /**adiciona comentario via webservice em segundo plano
     * @param dialogInterface
     * @param i
     */
    private ComentarioTask aTaskComentario = null;
    private ProgressBar prgComentario;
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        prgComentario = (ProgressBar) findViewById(R.id.prgComentario);
        Utils.barraProgresso(this, this.prgComentario, true);
        EditText EdtComentario = (EditText) layoutComentario.findViewById(R.id.EDTcomentario);
        String comentario = EdtComentario.getText().toString();
        aTaskComentario = new ComentarioTask(comentario);
        aTaskComentario.execute((Void)null);
    }

    /**
     * Classe responsavel por adicionar o comentario em segundo plano
     */
    public class ComentarioTask extends AsyncTask<Void, Void, Boolean> {
        private final String textoComentario;

        public ComentarioTask(String textoComentario) {
            this.textoComentario = textoComentario;
        }

        @Override
        protected Boolean doInBackground(Void... params){
            //chama o webservice
            WebService webService = new WebService();
            webService.setIdUsuario(AtvLogin.usuario.getId());
            final Object[] resposta = webService.gravacomentario(
                    AtvTarefa.this.tarefa.getId(),
                    this.textoComentario
            );
            /**se deu resultado o webservice entao sinaliza para a Activity AtvBase atualizar o
             * TreeMap e tambem ja atualiza localmente os Xmls de todas as tarefas
             * TODO melhorar essa logica, pois deveria atualizar apenas os XMLs da tarefa comentada
             */
            if (resposta != null) {
                /**flag enviada p/ Activity AtvBase sinalizando que deve atualizar o TreeMap se
                 * caso reabrir a tarefa que confere com o Id abaixo, pois houve atualizaçao dos
                 * comentarios da mesma pela Activity AtvTarefa (esta)  */
                AtvBase.atualizarTarefaId = AtvTarefa.this.tarefa.getId();
                Vector<Boolean> flagsSincroniza = (Vector<Boolean>) resposta[1];
                try { //grava localmente o Xml atualizado resultante do webservice
                    if (flagsSincroniza.get(0)) {
                        XmlTarefasPessoais xmlTarefasPessoais = new XmlTarefasPessoais(AtvTarefa.this);
                        xmlTarefasPessoais.criaXmlProjetosPessoaisWebservice(AtvLogin.usuario.getId(), true);
                    }
                    if (flagsSincroniza.get(1)) {
                        XmlTarefasEquipe xmlTarefasEquipe = new XmlTarefasEquipe(AtvTarefa.this);
                        xmlTarefasEquipe.criaXmlProjetosEquipesWebservice(AtvLogin.usuario.getId(), true);
                    }
                    if (flagsSincroniza.get(2)) {
                        XmlTarefasHoje xmlTarefasHoje = new XmlTarefasHoje(AtvTarefa.this);
                        xmlTarefasHoje.criaXmlProjetosHojeWebservice(AtvLogin.usuario.getId(), true);
                    }
                    if (flagsSincroniza.get(3)) {
                        XmlTarefasSemana xmlTarefasSemana = new XmlTarefasSemana(AtvTarefa.this);
                        xmlTarefasSemana.criaXmlProjetosSemanaWebservice(AtvLogin.usuario.getId(), true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //necessario para atualizar o EdtDialogo com o comentario adicionado
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String comentarioString = (String) resposta[0];
                        Spanned comentario = Html.fromHtml(EdtDialogo.getText()+comentarioString+"\n");
                        EdtDialogo.setText(comentario);
                    }
                });
                return true;
            }else
                return false;
        }

        @Override
        protected void onPostExecute(final Boolean successo) {
            aTaskComentario = null;
            Utils.barraProgresso(AtvTarefa.this, prgComentario, false);
            if (successo) {
                Toast.makeText(AtvTarefa.this, "comentario gravado", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(AtvTarefa.this, "nao foi possivel gravar o comentario", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getSelectedItem() instanceof Usuario)
            this.usuario = (Usuario) parent.getSelectedItem();
        else if (parent.getSelectedItem() instanceof Projeto)
            this.projeto = (Projeto) parent.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}