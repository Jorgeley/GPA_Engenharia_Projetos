package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.classes.xmls.XmlTarefasSemana;

/**
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasSemana extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasSemana(Context contexto) {
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
            XmlTarefasSemana xml = new XmlTarefasSemana(this.contexto);
            xml.criaXmlProjetosSemanaTeste();
            super.projetosTreeMapBean = xml.leXml();
        }
    }
}