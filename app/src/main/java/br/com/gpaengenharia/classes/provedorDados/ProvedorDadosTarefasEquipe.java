package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.classes.xmls.XmlTarefasEquipe;

public class ProvedorDadosTarefasEquipe extends ProvedorDados implements ProvedorDadosInterface {
    private Context contexto;

    public ProvedorDadosTarefasEquipe(Context contexto) {
        this.contexto = contexto;
    }

    @Override
    public TreeMap<String, List<String>> getTarefas(Boolean inverteAgrupamento) {
        setProjetosTreeMapBean();
        return super.getTarefas(inverteAgrupamento);
    }

    @Override
    public void setProjetosTreeMapBean(){
        if (projetosTreeMapBean.isEmpty()) {
            XmlTarefasEquipe xml = new XmlTarefasEquipe(contexto);
            xml.criaXmlProjetosEquipeTeste();
            projetosTreeMapBean = xml.leXmlTeste();
        }
    }

}
