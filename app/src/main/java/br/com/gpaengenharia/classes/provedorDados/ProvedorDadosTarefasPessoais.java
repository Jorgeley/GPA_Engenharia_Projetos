package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import br.com.gpaengenharia.classes.xmls.XmlTarefasPessoais;

/**
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasPessoais extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasPessoais(Context contexto) {
        this.contexto = contexto;
        setProjetosTreeMapBean();
    }

    /** {@inheritDoc} */
    @Override
    public TreeMap<String, List<String>> getTarefas(boolean inverteAgrupamento) {
        return super.getTarefas(inverteAgrupamento);
    }

    /** {@inheritDoc} */
    @Override
    public void setProjetosTreeMapBean() {
        if (super.projetosTreeMapBean.isEmpty()) {
            XmlTarefasPessoais xml = new XmlTarefasPessoais(this.contexto);
            super.projetosTreeMapBean = xml.leXml();
        }
    }
}

class DownloadTask extends AsyncTask<Void, Void, Boolean> {
    private Context contexto;

    private DownloadTask(Context contexto){
        this.contexto = contexto;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
            try {
                XmlTarefasPessoais xmlTarefasPessoais = new XmlTarefasPessoais(this.contexto);
                xmlTarefasPessoais.criaXmlProjetosPessoaisWebservice();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
    }

    @Override
    protected void onPostExecute(final Boolean successo) {
        if (successo) {
        }
    }
}