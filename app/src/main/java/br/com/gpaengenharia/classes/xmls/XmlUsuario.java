package br.com.gpaengenharia.classes.xmls;

import android.content.Context;

import java.io.IOException;

import br.com.gpaengenharia.classes.WebService;

/**
 * Chama o metodo do Webservice que retorna o XML dos usuarios
 */
public class XmlUsuario extends Xml implements XmlInterface {

    public XmlUsuario(Context contexto) {
        super(contexto);
        setNomeArquivoXML();
    }

    //nome do arquivo para gravar o xml
    private final static String nomeArquivoXML = "usuarios.xml";

    public static String getNomeArquivoXML() {
        return nomeArquivoXML;
    }

    @Override
    public void setArquivoXML() {

    }

    /** {@inheritDoc} */
    @Override
    public void setNomeArquivoXML() {
        super.nomeArquivoXML = this.nomeArquivoXML;
    }

    /**
     * Faz download do XML via webservice e salva localmente
     * @return true: houve atualizaçao, false: nao houve atualizaçao
     * @throws java.io.IOException
     */
    public static boolean criaXmlUsuariosWebservice(boolean forcarAtualizacao) throws IOException {
        /**
         * TODO nao deixar o webservice ser chamado sem restricao
         */
        WebService webService = new WebService();
        webService.setForcarAtualizacao(forcarAtualizacao);
        String xml = webService.getUsuarios();
        if (xml != null) {
            escreveXML(xml);
            return true;
        }else
            return false;
    }

    /**
     * Reescreve o arquivo XML passado como parametro
     * @param xml
     * @throws java.io.IOException
     */
    public void criaXmlUsuariosWebservice(String xml) throws IOException {
        escreveXML(xml);
    }

}