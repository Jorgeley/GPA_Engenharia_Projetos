package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import br.com.gpaengenharia.activities.AtvLogin;
import br.com.gpaengenharia.classes.xmls.XmlTarefasSemana;

/**
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasSemana extends ProvedorDados implements ProvedorDadosInterface{
    private static Set<Integer> idsTarefasSemana = new HashSet<Integer>(Arrays.asList(0));
    private Context contexto;

    public ProvedorDadosTarefasSemana(Context contexto, boolean forcarAtualizacao) {
        this.contexto = contexto;
        File arquivo = new File(contexto.getFilesDir()+"/"+ XmlTarefasSemana.getNomeArquivoXML());
        if (!arquivo.exists() || forcarAtualizacao)
            try {
                XmlTarefasSemana xmlTarefasSemana = new XmlTarefasSemana(this.contexto);
                xmlTarefasSemana.criaXmlProjetosSemanaWebservice(AtvLogin.usuario.getId(), true);
            } catch (IOException e) {
                e.printStackTrace();
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
        XmlTarefasSemana xml = new XmlTarefasSemana(this.contexto);
        super.projetosTreeMapBean = xml.leXmlProjetosTarefas();
        this.setIdsTarefasSemana(xml.idsTarefas);
    }

    public static Set<Integer> getIdsTarefasSemana(){
        return idsTarefasSemana;
    }

    public void setIdsTarefasSemana(Set<Integer> idsTarefasSemana){
        this.idsTarefasSemana = idsTarefasSemana;
    }
}