package br.com.gpaengenharia.classes.provedorDados;

import android.content.Context;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.classes.xmls.XmlTarefasPessoais;

/*
 Monta TreeMap de beans <Projeto, List<Tarefa>>
 herda de ProvedorDados e implementa ProvedorDadosInterface
  */
public class ProvedorDadosTarefasPessoais extends ProvedorDados implements ProvedorDadosInterface{
    private Context contexto;

    public ProvedorDadosTarefasPessoais(Context contexto) {
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
            XmlTarefasPessoais xml = new XmlTarefasPessoais(this.contexto);
            xml.criaXmlProjetosPessoaisTeste();
            super.projetosTreeMapBean = xml.leXmlTeste();
        }
    }
}
