package br.com.gpaengenharia.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProvedorDadosSimples {
    protected List<String> tarefasList;

    //gera List de tarefas
    protected List<String> getDadosList() {

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

        return tarefasList;
    }

}
