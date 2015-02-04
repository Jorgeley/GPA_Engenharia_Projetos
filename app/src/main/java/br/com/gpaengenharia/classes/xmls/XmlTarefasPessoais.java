package br.com.gpaengenharia.classes.xmls;

import android.content.Context;
import android.util.Log;
import org.xmlpull.v1.XmlSerializer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
Cria arquivo xml de exemplo para teste
herda Xml e implementa XmlInterface
 */
public class XmlTarefasPessoais extends Xml implements XmlInterface{
    //nome do arquivo para gravar o xml
    private final static String nomeArquivoXML = "tarefasPessoais.xml";
    //arquivo para gravar o xml
    private FileOutputStream arquivoXML;

    public XmlTarefasPessoais(Context contexto) {
        super(contexto);
        setNomeArquivoXML();
    }

    /** {@inheritDoc} */
    @Override
    public void setArquivoXML() {
        criaXmlProjetosPessoaisTeste();
    }

    /** {@inheritDoc} */
    @Override
    public void setNomeArquivoXML() {
        super.nomeAquivoXML = this.nomeArquivoXML;
    }

    /**
    Cria XML exemplo e grava no dir do projeto
     */
    public void criaXmlProjetosPessoaisTeste() {
        try {
            this.arquivoXML = super.contexto.openFileOutput(super.nomeAquivoXML, 0);
        } catch (FileNotFoundException e) {
            Log.e("erro IO", e.getMessage());
        }
        XmlSerializer serializadorXML = android.util.Xml.newSerializer();
        try {
            serializadorXML.setOutput(this.arquivoXML, "UTF-8");
            serializadorXML.startDocument(null, Boolean.valueOf(true));
            serializadorXML.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializadorXML.startTag(null, "GPA");
            serializadorXML.startTag(null, "projetos-pessoais");
            for (int projeto=1; projeto<5; projeto++) {
                serializadorXML.startTag(null, "projeto");
                serializadorXML.attribute(null, "nome", "Projeto Exemplo " + String.valueOf(projeto));
                for (int tarefa=1; tarefa<5; tarefa++) {
                    serializadorXML.startTag(null, "tarefa");
                    serializadorXML.text("Tarefa Exemplo " + tarefa + projeto);
                    serializadorXML.endTag(null, "tarefa");;
                }
                serializadorXML.endTag(null, "projeto");
            }
            serializadorXML.endTag(null, "projetos-pessoais");
            serializadorXML.endTag(null, "GPA");
            serializadorXML.endDocument();
            serializadorXML.flush();
            this.arquivoXML.close();
        } catch (Exception e) {
            Log.e("erro serializerXML", e.getMessage());
        }
    }
}