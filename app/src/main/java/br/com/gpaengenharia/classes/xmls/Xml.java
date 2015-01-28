package br.com.gpaengenharia.classes.xmls;

import android.content.Context;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.beans.Tarefa;

//TODO: refatorar essa parada aqui pra ter herança pq vou precisar mto de xml em tudo
public abstract class Xml{
    protected Context contexto;
    protected FileOutputStream arquivoXML;
    private TreeMap<Projeto,List<Tarefa>> projetos = new TreeMap<Projeto,List<Tarefa>>();
    private List<Tarefa> tarefas = new ArrayList<Tarefa>();
    protected String nomeAquivoXML;

    public Xml(Context contexto){
        this.contexto = contexto;
    }

    public TreeMap<Projeto, List<Tarefa>> leXmlTeste(){
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            //InputStream in_s = contexto.getAssets().open(nomeAquivoXML);
            InputStream in_s = contexto.openFileInput(nomeAquivoXML);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            parseXML(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projetos;
    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException{
        int tipoEvento = parser.getEventType();
        Projeto projetoAtual = null;
        while (tipoEvento != XmlPullParser.END_DOCUMENT){
            String nomeNode = null;
            switch (tipoEvento){
                /*case XmlPullParser.START_DOCUMENT:
                    List<Tarefa> tarefas = new ArrayList<Tarefa>();
                    break;*/
                case XmlPullParser.START_TAG:
                    nomeNode = parser.getName();
                    if (nomeNode.equals("projeto")) {
                        projetoAtual = new Projeto();
                        projetoAtual.setNome(parser.getAttributeValue(0));
                        //Log.i("novo projeto", projetoAtual.getNome());
                    }else if (nomeNode.equals("tarefa")) {
                        Tarefa tarefaAtual = new Tarefa();
                        tarefaAtual.setNome(parser.nextText());
                        //Log.i("adicionando", tarefaAtual.getNome());
                        tarefas.add(tarefaAtual);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    nomeNode = parser.getName();
                    if (nomeNode.equalsIgnoreCase("projeto")) {
                        //Log.i("adicionando projeto", projetoAtual.getNome());
                        projetos.put(projetoAtual, tarefas);
                        tarefas = new ArrayList<Tarefa>();
                    }
            }
            tipoEvento = parser.next();
        }
    }

    public void log(){
        Log.i("qtd",String.valueOf(projetos.size()));
        for (Map.Entry<Projeto, List<Tarefa>> projeto : projetos.entrySet()){
            String tituloProjeto = projeto.getKey().getNome();
            List<Tarefa> tarefasProjeto = projeto.getValue();
            for (Tarefa tarefa : tarefasProjeto){
                Log.i(tituloProjeto, tarefa.getNome());
            }
        }
    }
}