package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.classes.xmls.XmlTarefasPessoais;

public class ProvedorDadosTarefasPessoais extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasPessoais(Context contexto) {
        this.contexto = contexto;
        setProjetosTreeMapBean();
    }

    @Override
    public TreeMap<String, List<String>> getTarefas(Boolean inverteAgrupamento) {
        return super.getTarefas(inverteAgrupamento);
    }

    @Override
    public void setProjetosTreeMapBean() {
        if (projetosTreeMapBean.isEmpty()) {
            XmlTarefasPessoais xml = new XmlTarefasPessoais(contexto);
            xml.criaXmlProjetosPessoaisTeste();
            projetosTreeMapBean = xml.leXmlTeste();
        }
    }
}
