package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.classes.xmls.XmlTarefasHoje;

/*
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasHoje extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasHoje(Context contexto) {
        this.contexto = contexto;
        setProjetosTreeMapBean();
    }

    /*
    Retorna TreeMap de String com projetos contendo sublista de tarefas cada um.
    Se inverteAgrupamento true então inverte e retorna TreeMap de String apenas com tarefas
     */
    @Override
    public TreeMap<String, List<String>> getTarefas(Boolean inverteAgrupamento) {
        return super.getTarefas(inverteAgrupamento);
    }

    //Busca o TreeMap de beans Projeto e Tarefa do XML
    @Override
    public void setProjetosTreeMapBean() {
        if (super.projetosTreeMapBean.isEmpty()) {
            XmlTarefasHoje xml = new XmlTarefasHoje(this.contexto);
            xml.criaXmlProjetosHojeTeste();
            super.projetosTreeMapBean = xml.leXmlTeste();
        }
    }
}
