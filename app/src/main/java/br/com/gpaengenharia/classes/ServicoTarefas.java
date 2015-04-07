package br.com.gpaengenharia.classes;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;

import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import br.com.gpaengenharia.R;
import br.com.gpaengenharia.activities.AtvBase;
import br.com.gpaengenharia.activities.AtvLogin;
import br.com.gpaengenharia.activities.AtvTarefa;
import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.beans.Tarefa;
import br.com.gpaengenharia.classes.xmls.Xml;
import br.com.gpaengenharia.classes.xmls.XmlTarefasEquipe;
import br.com.gpaengenharia.classes.xmls.XmlTarefasHoje;
import br.com.gpaengenharia.classes.xmls.XmlTarefasPessoais;
import br.com.gpaengenharia.classes.xmls.XmlTarefasSemana;

/**
 * serviço agendado pela classe AgendaServico para ser executado de 10 em 10 minutos
 * verifica via webservice se houve atualizaçoes nas tarefas do usuario, se sim, baixa
 * XML atualizado e cria notificaçao para cada tarefa atualizada
 */
public class ServicoTarefas extends Service implements Runnable{
    private Context contexto;

    @Override
    public IBinder onBind(Intent intent) {
        return null; //sem interatividade com o serviço
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (AtvLogin.usuario != null)
            new Thread(this).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void run(){
        //arquivo XML contendo as tarefas atualizadas
        File arquivo = new File(this.getContexto().getFilesDir() + "/" + Xml.getNomeArquivoXMLatualizadas());
        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", new Locale("pt", "BR"));
        Date data = new Date();
        data.setTime(arquivo.lastModified());//pega a data de modificaçao do arquivo XML
        String ultimaSincronizacao = formatoData.format(data);
        Vector<Vector<Object>> respostasSincroniza;//ids das tarefas atualizadas e flags de quais XML's atualizar
        TreeMap<Projeto, List<Tarefa>> projetosTarefas; //treeMap de beans projetos contendo beans tarefas em cada
        try {
            Xml xml = new Xml(this.getContexto());
            //chama o webservice que verifica se ha tarefas novas de acordo com a data de modificaçao do XML local
            respostasSincroniza = xml.sincronizaXmlTudoWebservice(AtvLogin.usuario, ultimaSincronizacao);
            if (respostasSincroniza != null) {
                //monta treeMap de beans projetos contendo beans tarefas em cada
                projetosTarefas = xml.leXmlProjetosTarefas(respostasSincroniza.get(0)); //indice 0 contem ids das tarefas atualizadas
                if (!projetosTarefas.isEmpty()) {
                    //Log.i("projetosTarefas", String.valueOf(projetosTarefas));
                    for (Map.Entry<Projeto, List<Tarefa>> projetoTarefas : projetosTarefas.entrySet()) {
                        //para cada tarefa nova cria uma notificaçao contendo a mesma
                        for (Tarefa tarefa : projetoTarefas.getValue()) {
                            Bundle bundleTarefa = new Bundle();
                            bundleTarefa.putParcelable("projeto", projetoTarefas.getKey());
                            bundleTarefa.putParcelable("tarefa", tarefa);
                            Intent atvTarefa = new Intent(this.getContexto(), AtvTarefa.class);
                            atvTarefa.putExtras(bundleTarefa);
                            Notificacao.create(this.getContexto(),
                                    "GPA",
                                    tarefa.getNome() + " atualizada",
                                    R.drawable.logo_notificacao,
                                    tarefa.getId(), //se for igual os extras nao atualizam
                                    atvTarefa
                            );
                            //sinaliza para a atvBase atualizar o listView
                            AtvBase.atualizaListView = true;
                        }
                    }
                }
                //faz a atualizaçao dos XML's baseando-se nas flags enviadas pelo webservice
                XmlTarefasPessoais xmlTarefasPessoais = null;
                XmlTarefasEquipe xmlTarefasEquipe = null;
                XmlTarefasHoje xmlTarefasHoje = null;
                XmlTarefasSemana xmlTarefasSemana = null;
                //indice [1][0] contem flag para sincronizar XML tarefas pessoais
                Boolean sincronizaPessoais = (Boolean) respostasSincroniza.get(1).get(0);
                //indice [1][1] contem flag para sincronizar XML tarefas equipes
                Boolean sincronizaEquipes = (Boolean) respostasSincroniza.get(1).get(1);
                //indice [1][2] contem flag para sincronizar XML tarefas hoje
                Boolean sincronizaHoje = (Boolean) respostasSincroniza.get(1).get(2);
                //indice [1][3] contem flag para sincronizar XML tarefas semana
                Boolean sincronizaSemana = (Boolean) respostasSincroniza.get(1).get(3);
                if (sincronizaPessoais) {
                    xmlTarefasPessoais = new XmlTarefasPessoais(this.getContexto());
                    xmlTarefasPessoais.criaXmlProjetosPessoaisWebservice(AtvLogin.usuario, true);
                }
                if (sincronizaEquipes) {
                    xmlTarefasEquipe = new XmlTarefasEquipe(this.getContexto());
                    xmlTarefasEquipe.criaXmlProjetosEquipesWebservice(AtvLogin.usuario, true);
                }
                if (sincronizaHoje) {
                    xmlTarefasHoje = new XmlTarefasHoje(this.getContexto());
                    xmlTarefasHoje.criaXmlProjetosHojeWebservice(AtvLogin.usuario, true);
                }
                if (sincronizaSemana) {
                    xmlTarefasSemana = new XmlTarefasSemana(this.getContexto());
                    xmlTarefasSemana.criaXmlProjetosSemanaWebservice(AtvLogin.usuario, true);
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        //porque Deus quis assim...
        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            public void run(){
                if (AtvBase.prgTarefas != null)
                    AtvBase.prgTarefas.setVisibility(View.GONE);
            }
        });
    }

    public void setContexto(Context contexto){
        this.contexto = contexto;
    }

    public Context getContexto(){
        if (this.contexto == null)
            return getApplicationContext();
        else
            return this.contexto;
    }
}