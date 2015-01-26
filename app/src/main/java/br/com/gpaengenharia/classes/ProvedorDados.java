package br.com.gpaengenharia.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProvedorDados{
    private TreeMap<String, List<String>> projetosTreeMap = new TreeMap<String, List<String>>();

    //gera TreeMap de projetos contendo tarefas como nós
    public TreeMap<String, List<String>> getDadosTreeMap() {

        //cria ArrayList de tarefas
        List<String> terraplanagem = new ArrayList<String>();
        List<String> asfaltamento = new ArrayList<String>();
        List<String> condominio = new ArrayList<String>();

        //adiciona tarefas no ArrayList
        for (int i = 0; i < ArrayDados.terraplanagem.length; i++) {
            terraplanagem.add(ArrayDados.terraplanagem[i]);
        }
        Collections.sort(terraplanagem);//ordena

        for (int i = 0; i < ArrayDados.asfaltamento.length; i++) {
            asfaltamento.add(ArrayDados.asfaltamento[i]);
        }
        Collections.sort(asfaltamento);

        for (int i = 0; i < ArrayDados.condominio.length; i++) {
            condominio.add(ArrayDados.condominio[i]);
        }
        Collections.sort(condominio);

        //adiciona tarefas no projeto
        projetosTreeMap.put("Terraplanagem Aeroporto", terraplanagem);
        projetosTreeMap.put("Asfaltamento Periferia", asfaltamento);
        projetosTreeMap.put("Condominio Centro", condominio);

        return projetosTreeMap;
    }

    //inverte o TreeMap colocando os projetos como nós das tarefas
    public TreeMap<String, List<String>> inverteTreeMap(){
        TreeMap<String, List<String>> projetosTreeMapInvertido = new TreeMap<String, List<String>>();
        if (projetosTreeMap.isEmpty())
            getDadosTreeMap();
        for (Map.Entry<String, List<String>> projeto : projetosTreeMap.entrySet()){
            String tituloProjeto = projeto.getKey();
            List<String> projetos = new ArrayList<String>();
            projetos.add(tituloProjeto);
            List<String> tarefas = projeto.getValue();
            for (String tarefa : tarefas){
                projetosTreeMapInvertido.put(tarefa + "\n" + tituloProjeto, projetos);
            }
        }
        return projetosTreeMapInvertido;
    }

}