package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.beans.Tarefa;
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
            XmlTarefasPessoais xml = new XmlTarefasPessoais(this.contexto);
            super.projetosTreeMapBean = xml.leXml();
    }
}