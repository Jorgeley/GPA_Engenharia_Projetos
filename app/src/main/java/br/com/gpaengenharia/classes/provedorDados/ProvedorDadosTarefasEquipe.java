package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import br.com.gpaengenharia.activities.AtvLogin;
import br.com.gpaengenharia.classes.xmls.XmlTarefasEquipe;

/**
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasEquipe extends ProvedorDados implements ProvedorDadosInterface {
    private Context contexto;

    public ProvedorDadosTarefasEquipe(Context contexto) {
        File arquivo = new File(contexto.getFilesDir()+"/"+ XmlTarefasEquipe.getNomeArquivoXML());
        if (!arquivo.exists())
            try {
                XmlTarefasEquipe.criaXmlProjetosEquipesWebservice(AtvLogin.usuario.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        this.contexto = contexto;
        setProjetosTreeMapBean();
    }

    /** {@inheritDoc} **/
    @Override
    public TreeMap<String, List<String>> getTarefas(boolean inverteAgrupamento) {
        return super.getTarefas(inverteAgrupamento);
    }

    /** {@inheritDoc} **/
    @Override
    public void setProjetosTreeMapBean(){
            XmlTarefasEquipe xml = new XmlTarefasEquipe(this.contexto);
            super.projetosTreeMapBean = xml.leXml();
    }

}