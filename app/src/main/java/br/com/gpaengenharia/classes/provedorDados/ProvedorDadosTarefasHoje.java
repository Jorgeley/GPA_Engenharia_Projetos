package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.classes.xmls.XmlTarefasHoje;

public class ProvedorDadosTarefasHoje extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasHoje(Context contexto) {
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
            XmlTarefasHoje xml = new XmlTarefasHoje(contexto);
            xml.criaXmlProjetosHojeTeste();
            projetosTreeMapBean = xml.leXmlTeste();
        }
    }
}
