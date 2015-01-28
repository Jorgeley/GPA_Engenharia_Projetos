package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.classes.xmls.XmlTarefasSemana;

public class ProvedorDadosTarefasSemana extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasSemana(Context contexto) {
        this.contexto = contexto;
    }

    @Override
    public TreeMap<String, List<String>> getTarefas(Boolean inverteAgrupamento) {
        setProjetosTreeMapBean();
        return super.getTarefas(inverteAgrupamento);
    }

    @Override
    public void setProjetosTreeMapBean() {
        if (projetosTreeMapBean.isEmpty()) {
            XmlTarefasSemana xml = new XmlTarefasSemana(contexto);
            xml.criaXmlProjetosSemanaTeste();
            projetosTreeMapBean = xml.leXmlTeste();
        }
    }
}