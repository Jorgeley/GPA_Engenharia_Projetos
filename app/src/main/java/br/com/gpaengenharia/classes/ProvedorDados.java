package br.com.gpaengenharia.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProvedorDados implements{
    private TreeMap<String, List<String>> projetosTreeMap = new TreeMap<String, List<String>>();
    protected List<String> tarefas = new ArrayList<String>();

    //gera List de tarefas
    public List<String> getTarefas(){
        //adiciona tarefas no ArrayList
        for (String[] projeto : ArrayDados.asfaltamento)
            for (String tarefa : projeto)
                tarefas.add(tarefa + '\n' + projeto);
        for (String[] projeto : ArrayDados.condominio)
            for (String tarefa : projeto)
                tarefas.add(tarefa + '\n' + projeto);
        for (String[] projeto : ArrayDados.terraplanagem)
            for (String tarefa : projeto)
                tarefas.add(tarefa + '\n' + projeto);
        Collections.sort(tarefas);
        return tarefas;
    }

    //gera TreeMap de projetos contendo tarefas como nós
    public TreeMap<String, List<String>> getTarefas() {
        //adiciona tarefas no projeto
        projetosTreeMap.put("Terraplanagem Aeroporto", tarefas);
        projetosTreeMap.put("Asfaltamento Periferia", tarefas);
        projetosTreeMap.put("Condominio Centro", tarefas);
        return projetosTreeMap;
    }

}
