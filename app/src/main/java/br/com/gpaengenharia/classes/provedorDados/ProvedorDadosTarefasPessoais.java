package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import br.com.gpaengenharia.activities.AtvLogin;
import br.com.gpaengenharia.classes.xmls.XmlTarefasPessoais;

/**
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasPessoais extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasPessoais(Context contexto, boolean forcarAtualizacao) {
        this.contexto = contexto;
        File arquivo = new File(contexto.getFilesDir()+"/"+XmlTarefasPessoais.getNomeArquivoXML());
        if (!arquivo.exists()) {
            try {
                XmlTarefasPessoais xmlTarefasPessoais = new XmlTarefasPessoais(this.contexto);
                xmlTarefasPessoais.criaXmlProjetosPessoaisWebservice(AtvLogin.usuario.getId(), forcarAtualizacao);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
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