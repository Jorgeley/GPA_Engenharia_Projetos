package br.com.gpaengenharia.classes.provedorDados;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.beans.Tarefa;

public abstract class ProvedorDados{
    protected TreeMap<Projeto, List<Tarefa>> projetosTreeMapBean = new TreeMap<Projeto, List<Tarefa>>();

    //gera TreeMap de projetos contendo tarefas como nós
    public TreeMap<String, List<String>> getDadosTreeMapTeste() {
        TreeMap<String, List<String>> projetosTreeMap = new TreeMap<String, List<String>>();
        //cria ArrayList de tarefas
        List<String> terraplanagem = new ArrayList<String>();
        List<String> asfaltamento = new ArrayList<String>();
        List<String> condominio = new ArrayList<String>();
        //adiciona tarefas no ArrayList
        for (int i = 0; i < ArrayDadosTarefas.terraplanagem.length; i++) {
            terraplanagem.add(ArrayDadosTarefas.terraplanagem[i]);
        }
        Collections.sort(terraplanagem);//ordena
        for (int i = 0; i < ArrayDadosTarefas.asfaltamento.length; i++) {
            asfaltamento.add(ArrayDadosTarefas.asfaltamento[i]);
        }
        Collections.sort(asfaltamento);
        for (int i = 0; i < ArrayDadosTarefas.condominio.length; i++) {
            condominio.add(ArrayDadosTarefas.condominio[i]);
        }
        Collections.sort(condominio);
        //adiciona tarefas no projeto
        projetosTreeMap.put("Terraplanagem Aeroporto", terraplanagem);
        projetosTreeMap.put("Asfaltamento Periferia", asfaltamento);
        projetosTreeMap.put("Condominio Centro", condominio);

        return projetosTreeMap;
    }

    /*
    Busca os beans Projeto e Tarefa do Xml e transforma em TreeMap de String
    @param Boolean inverteAgrupamento:
        se True inverte o agrupamento do TreeMap agrupando por tarefas
        se False deixa agrupamento por projetos e tarefas com nós dos projetos
     */
    public TreeMap<String, List<String>> getTarefas(Boolean inverteAgrupamento){
        TreeMap<String, List<String>> projetosTreeMapString = new TreeMap<String, List<String>>();
        for (Map.Entry<Projeto, List<Tarefa>> projeto : projetosTreeMapBean.entrySet()){
            Projeto projetoAtual = projeto.getKey();
            List<Tarefa> tarefasBean = projeto.getValue();
            if (inverteAgrupamento) {
                for (Tarefa tarefa : tarefasBean)
                    projetosTreeMapString.put(tarefa.getNome() + "\n" + projetoAtual.getNome(), null);
            }else{
                List<String> tarefasString = new ArrayList<String>();
                for (Tarefa tarefa : tarefasBean)
                    tarefasString.add(tarefa.getNome());
                projetosTreeMapString.put(projetoAtual.getNome(), tarefasString);
            }
        }
        return projetosTreeMapString;
    }

}