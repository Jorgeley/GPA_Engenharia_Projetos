package br.com.gpaengenharia.classes;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import br.com.gpaengenharia.beans.Tarefa;

public class XmlGPA{
    private Context contexto;
    private FileOutputStream arquivoXML;
    protected static final String nomeAquivoXML = "GPA.xml";

    public XmlGPA(Context contexto){
        this.contexto = contexto;
    }

    /*
    Cria XML exemplo e grava no dir do projeto
     */
    public Boolean criaXMLteste() {
        try {
            arquivoXML = contexto.openFileOutput(nomeAquivoXML, 0);
        } catch (FileNotFoundException e) {
            Log.e("erro IO", e.getMessage());
        }
        XmlSerializer serializadorXML = Xml.newSerializer();
        try {
            serializadorXML.setOutput(arquivoXML, "UTF-8");
            serializadorXML.startDocument(null, Boolean.valueOf(true));
            serializadorXML.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializadorXML.startTag(null, "GPA");
            serializadorXML.startTag(null, "tarefas-pessoais");
            for (int projeto=1; projeto<5; projeto++) {
                for (int tarefa=1; tarefa<5; tarefa++) {
                    serializadorXML.startTag(null, "tarefa");
                    serializadorXML.attribute(null, "nome", "Tarefa Exemplo " + String.valueOf(tarefa));
                    serializadorXML.startTag(null, "projeto");
                    serializadorXML.text("Projeto Exemplo " + projeto);
                    serializadorXML.endTag(null, "projeto");
                    serializadorXML.endTag(null, "tarefa");
                }
            }
            serializadorXML.endTag(null, "tarefas-pessoais");
            serializadorXML.endTag(null, "GPA");
            serializadorXML.endDocument();
            serializadorXML.flush();
            arquivoXML.close();
        } catch (Exception e) {
            Log.e("erro serializerXML", e.getMessage());
        }
        return true;
    }

    public void leXMLteste(){
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
    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException{
        ArrayList<Tarefa> tarefas = null;
        int tipoEvento = parser.getEventType();
        Tarefa tarefaAtual = null;
        while (tipoEvento != XmlPullParser.END_DOCUMENT){
            String nomeNode = null;
            switch (tipoEvento){
                case XmlPullParser.START_DOCUMENT:
                    tarefas = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    nomeNode = parser.getName();
                    if (nomeNode.equals("tarefa")) {
                        tarefaAtual = new Tarefa();
                        tarefaAtual.setNome(parser.getAttributeValue(0));
                    }else if (nomeNode.equals("projeto"))
                        tarefaAtual.setProjeto(parser.nextText());
                    break;
                case XmlPullParser.END_TAG:
                    nomeNode = parser.getName();
                    if (nomeNode.equalsIgnoreCase("tarefa") && tarefaAtual != null)
                        tarefas.add(tarefaAtual);
            }
            tipoEvento = parser.next();
        }
        Iterator<Tarefa> iterador = tarefas.iterator();
        while (iterador.hasNext()) {
            Tarefa tarefa = iterador.next();
            Log.i(tarefa.getNome(),tarefa.getProjeto());
        }
    }
}