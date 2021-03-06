package br.com.gpaengenharia.classes.xmls;

import android.content.Context;
import android.util.Log;
import org.xmlpull.v1.XmlSerializer;
import java.io.FileNotFoundException;
import java.io.IOException;

import br.com.gpaengenharia.beans.Usuario;
import br.com.gpaengenharia.classes.WebService;

/**
 * Chama o metodo do Webservice que retorna o XML das tarefas das equipes do usuario
 */
public class XmlTarefasEquipe extends Xml implements XmlInterface {

    public XmlTarefasEquipe(Context contexto) {
        super(contexto);
        setNomeArquivoXML();
    }

    //nome do arquivo para gravar o xml
    private final static String nomeArquivoXML = "tarefasEquipe.xml";

    public static String getNomeArquivoXML() {
        return nomeArquivoXML;
    }

    /** {@inheritDoc} */
    @Override
    public void setNomeArquivoXML() {
        super.nomeArquivoXML = this.nomeArquivoXML;
    }

    /** {@inheritDoc} */
    @Override
    public void setArquivoXML() {
        criaXmlProjetosEquipeTeste();
    }

    /**
     * Faz download do XML via webservice e salva localmente
     * @param usuario
     * @return true: houve atualizaçao, false: nao houve atualizaçao
     * @throws java.io.IOException
     */
    public static boolean criaXmlProjetosEquipesWebservice(Usuario usuario, boolean forcarAtualizacao) throws IOException {
        /**
         * TODO nao deixar o webservice ser chamado sem restricao
         */
        WebService webService = new WebService();
        webService.setUsuario(usuario);
        webService.setForcarAtualizacao(forcarAtualizacao);
        String xml = webService.projetosEquipes();
        if (xml != null) {
            escreveXML(xml);
            return true;
        }else
            return false;
    }

    /**
     * Reescreve o arquivo XML passado como parametro, esse metodo e usado pelo Dialog
     * 'gravar comentario' na 'AtvTarefa'
     * @param xml
     * @throws IOException
     */
    public void criaXmlProjetosEquipesWebservice(String xml) throws IOException {
        escreveXML(xml);
    }

    /**
    Cria XML exemplo e grava no dir do projeto
     */
    public void criaXmlProjetosEquipeTeste() {
        try {
            this.arquivoXML = super.contexto.openFileOutput(super.nomeArquivoXML, 0);
        } catch (FileNotFoundException e) {
            Log.e("erro IO", e.getMessage());
        }
        XmlSerializer serializadorXML = android.util.Xml.newSerializer();
        try {
            serializadorXML.setOutput(this.arquivoXML, "UTF-8");
            serializadorXML.startDocument(null, Boolean.valueOf(true));
            serializadorXML.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializadorXML.startTag(null, "GPA");
            serializadorXML.startTag(null, "projetosPessoais-equipe");
            for (int projeto=1; projeto<5; projeto++) {
                serializadorXML.startTag(null, "projeto");
                serializadorXML.attribute(null, "nome", "Projeto Equipe Exemplo " + String.valueOf(projeto));
                for (int tarefa=1; tarefa<5; tarefa++) {
                    serializadorXML.startTag(null, "tarefa");
                    serializadorXML.text("Tarefa Equipe Exemplo " + tarefa + projeto);
                    serializadorXML.endTag(null, "tarefa");;
                }
                serializadorXML.endTag(null, "projeto");
            }
            serializadorXML.endTag(null, "projetosPessoais-equipe");
            serializadorXML.endTag(null, "GPA");
            serializadorXML.endDocument();
            serializadorXML.flush();
            this.arquivoXML.close();
        } catch (Exception e) {
            Log.e("erro serializerXML", e.getMessage());
        }
    }
}