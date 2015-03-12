package br.com.gpaengenharia.classes.xmls;

import android.content.Context;
import android.util.Log;
import org.xmlpull.v1.XmlSerializer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.gpaengenharia.classes.WebService;
import br.com.gpaengenharia.classes.http.Http;

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
     * Faz download do XML do webservice e salva localmente
     * @param usuarioId
     * @return true: houve atualizaçao, false: nao houve atualizaçao
     * @throws IOException
     */
    public boolean criaXmlProjetosPessoaisWebservice(int usuarioId) throws IOException {
        /*//faz o download do XML
        final String xml = Http.getInstance(Http.NORMAL)
                .downloadArquivo("http://192.168.1.103:8888/GPA/public/webservice/projetos",super.contexto);
        //Log.i("xml", xml);*/
        /**
         * TODO nao deixar o webservice ser chamado sem restricao
         */
        String xml = WebService.projetos(usuarioId);
        if (xml != null) {
            try {
                this.arquivoXML = super.contexto.openFileOutput(super.nomeAquivoXML, 0);
                this.arquivoXML.write(xml.getBytes());
                this.arquivoXML.close();
            } catch (FileNotFoundException e) {
                Log.e("erro IO", e.getMessage());
            }
            return true;
        }else
            return false;
    }

    /**
     * Grava o arquivo XML passado como parametro, esse metodo e usado pelo Dialog gravar comentario
     * @param xml
     * @throws IOException
     */
    public void criaXmlProjetosPessoaisWebservice(String xml) throws IOException {
        try {
            this.arquivoXML = super.contexto.openFileOutput(super.nomeAquivoXML, 0);
            this.arquivoXML.write(xml.getBytes());
            this.arquivoXML.close();
        } catch (FileNotFoundException e) {
            Log.e("erro IO", e.getMessage());
        }
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