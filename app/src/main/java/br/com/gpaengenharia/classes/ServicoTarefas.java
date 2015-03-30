package br.com.gpaengenharia.classes;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

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
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasEquipe;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasHoje;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasPessoais;
import br.com.gpaengenharia.classes.provedorDados.ProvedorDadosTarefasSemana;
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
        //arquivo XML contendo todas as tarefas
        File arquivo = new File(this.getContexto().getFilesDir() + "/" + XmlTarefasPessoais.getNomeArquivoXMLTudo());
        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", new Locale("pt", "BR"));
        Date data = new Date();
        data.setTime(arquivo.lastModified());//pega a data de modificaçao do arquivo XML
        String ultimaSincronizacao = formatoData.format(data);
        Vector<Integer> idsTarefas;//ids das tarefas atualizadas para criar notificaçao
        TreeMap<Projeto, List<Tarefa>> projetosTarefas; //treeMap de beans projetos contendo beans tarefas em cada
        try {
            XmlTarefasPessoais xmlTarefasPessoais = new XmlTarefasPessoais(this);
            //chama o webservice que verifica se ha tarefas novas de acordo com a data de modificaçao do XML local
            idsTarefas = xmlTarefasPessoais.sincronizaXmlTudoWebservice(AtvLogin.usuario.getId(), ultimaSincronizacao);
            if (idsTarefas != null) {
                //Log.i("idsTarefas", String.valueOf(idsTarefas));
                XmlTarefasPessoais xml = new XmlTarefasPessoais(this);
                //monta treeMap de beans projetos contendo beans tarefas em cada
                projetosTarefas = xml.leXmlTarefas(idsTarefas);
                if (!projetosTarefas.isEmpty()) {
                    //Log.i("projetosTarefas", String.valueOf(projetosTarefas));
                    for (Map.Entry<Projeto, List<Tarefa>> projetoTarefas : projetosTarefas.entrySet()) {
                        //para cada tarefa nova cria uma notificaçao contendo a mesma
                        for (Tarefa tarefa : projetoTarefas.getValue()) {
                            Bundle bundleTarefa = new Bundle();
                            bundleTarefa.putParcelable("projeto", projetoTarefas.getKey());
                            bundleTarefa.putParcelable("tarefa", tarefa);
                            Intent atvTarefa = new Intent(this, AtvTarefa.class);
                            atvTarefa.putExtras(bundleTarefa);
                            Notificacao.create(this,
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
                xmlTarefasPessoais = null;
                XmlTarefasEquipe xmlTarefasEquipe = null;
                XmlTarefasHoje xmlTarefasHoje = null;
                XmlTarefasSemana xmlTarefasSemana = null;
                //verifica se tem que atualizar os outros XML's baseando-se no id das tarefas atualizadas
                for (Integer idTarefa : idsTarefas) {
                    if (!(xmlTarefasPessoais instanceof XmlTarefasPessoais) &&
                            ProvedorDadosTarefasPessoais.getIdsTarefasPessoais().contains(idTarefa)) {
                        xmlTarefasPessoais = new XmlTarefasPessoais(this);
                        xmlTarefasPessoais.criaXmlProjetosPessoaisWebservice(AtvLogin.usuario.getId(), true);
                    }
                    if (!(xmlTarefasEquipe instanceof XmlTarefasEquipe) &&
                            ProvedorDadosTarefasEquipe.getIdsTarefasEquipes().contains(idTarefa)) {
                        xmlTarefasEquipe = new XmlTarefasEquipe(this);
                        xmlTarefasEquipe.criaXmlProjetosEquipesWebservice(AtvLogin.usuario.getId(), true);
                    }
                    if ( !(xmlTarefasHoje instanceof XmlTarefasHoje) &&
                            ProvedorDadosTarefasHoje.getIdsTarefasHoje().contains(idTarefa)) {
                        xmlTarefasHoje = new XmlTarefasHoje(this);
                        xmlTarefasHoje.criaXmlProjetosHojeWebservice(AtvLogin.usuario.getId(), true);
                    }
                    if ( !(xmlTarefasSemana instanceof XmlTarefasSemana) &&
                            ProvedorDadosTarefasSemana.getIdsTarefasSemana().contains(idTarefa)) {
                        xmlTarefasSemana = new XmlTarefasSemana(this);
                        xmlTarefasSemana.criaXmlProjetosSemanaWebservice(AtvLogin.usuario.getId(), true);
                    }

                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
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
