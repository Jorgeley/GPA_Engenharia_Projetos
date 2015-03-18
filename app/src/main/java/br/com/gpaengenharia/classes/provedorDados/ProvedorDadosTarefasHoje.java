package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import br.com.gpaengenharia.activities.AtvLogin;
import br.com.gpaengenharia.classes.xmls.XmlTarefasEquipe;
import br.com.gpaengenharia.classes.xmls.XmlTarefasHoje;

/**
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasHoje extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasHoje(Context contexto, boolean forcarAtualizacao) {
        this.contexto = contexto;
        File arquivo = new File(contexto.getFilesDir()+"/"+ XmlTarefasHoje.getNomeArquivoXML());
        if (!arquivo.exists() || forcarAtualizacao)
            try {
                XmlTarefasHoje xmlTarefasHoje = new XmlTarefasHoje(this.contexto);
                xmlTarefasHoje.criaXmlProjetosHojeWebservice(AtvLogin.usuario.getId(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        setProjetosTreeMapBean();
    }

    /** {@inheritDoc} **/
    @Override
    public TreeMap<String, List<String>> getTarefas(boolean inverteAgrupamento) {
        return super.getTarefas(inverteAgrupamento);
    }

    /** {@inheritDoc} **/
    @Override
    public void setProjetosTreeMapBean() {
        XmlTarefasHoje xml = new XmlTarefasHoje(this.contexto);
        super.projetosTreeMapBean = xml.leXml();
    }
}