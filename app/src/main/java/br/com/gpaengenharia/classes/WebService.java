package br.com.gpaengenharia.classes;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Vector;
import br.com.gpaengenharia.beans.Usuario;

/**
 * Classe responsavel pela comunicaçao entre o app e o servidor
 */
public class WebService{

    //private static String SERVIDOR = "192.168.0.118:8888";
    //private static String SERVIDOR = "192.168.1.103:8888";
    private static String SERVIDOR = "www.grupo-gpa.com";
    //Namespace of the Webservice - can be found in WSDL
    //private static String NAMESPACE = "http://"+SERVIDOR+"/GPA/public/webservice/soap/";
    private static String NAMESPACE = "http://"+SERVIDOR+"/webservice/soap/";
    //Webservice URL - WSDL File location
    //private static String URL = "http://"+SERVIDOR+"/GPA/public/webservice/soap";//Make sure you changed IP address
    private static String URL = "http://"+SERVIDOR+"/webservice/soap";
    //SOAP Action URI again Namespace + Web method name
    //private static String SOAP_ACTION = "http://"+SERVIDOR+"/GPA/public/webservice/soap#";
    private static String SOAP_ACTION = "http://"+SERVIDOR+"/webservice/soap#";
    //id do usuario para todas as operaçoes no webservice
    private int idUsuario;
    //flag enviada ao servidor do webservice indicando p/ retornar os projetos, mesmo estando atualizados
    private boolean forcarAtualizacao = false;

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public boolean isForcarAtualizacao() {
        return this.forcarAtualizacao;
    }

    public void setForcarAtualizacao(boolean forcarAtualizacao) {
        this.forcarAtualizacao = forcarAtualizacao;
    }

    /**
     * faz o login via webservice no servidor e retorna o objeto Usuario
     * @param login
     * @param senha
     * @return Usuario
     */
    public static Usuario login(String login, String senha) {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "autentica");
        //setando parametros do método do webservice 'autentica'
        PropertyInfo loginWebservice = new PropertyInfo();
        PropertyInfo senhaWebservice = new PropertyInfo();
        loginWebservice.setName("login");
        loginWebservice.setValue(login);
        loginWebservice.setType(String.class);
        requisicao.addProperty(loginWebservice);
        senhaWebservice.setName("senha");
        senhaWebservice.setValue(senha);
        senhaWebservice.setType(String.class);
        requisicao.addProperty(senhaWebservice);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        //bean Usuario
        Usuario usuario = new Usuario();
        try {//faz a chamada do método 'autentica' do webservice
            androidHttpTransport.call(SOAP_ACTION + "autentica", envelope);
            //pegando a resposta
            Vector<SoapObject> resposta = (Vector<SoapObject>) envelope.getResponse();
            //Log.i("usuario ", String.valueOf(resposta));
            usuario.setId((Integer) resposta.get(0).getPrimitiveProperty("id"));
            usuario.setNome((String) resposta.get(0).getPrimitiveProperty("nome"));
            usuario.setPerfil((String) resposta.get(1).getPrimitiveProperty("perfil"));
        } catch (Exception e) {
            //se não conseguir autenticar retorna null
            usuario = null;
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * pega o XML de projetosPessoais com tarefas do idUsuario
     * @return XML de projetosPessoais com as tarefas
     */
    public String projetosPessoais() {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetosPessoais");
        //setando parametros do método do webservice 'projetosPessoais'
        requisicao.addProperty(this.getUsuario());
        requisicao.addProperty(this.getForcarAtualizacao());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xml = null;
        try {//faz a chamada do método 'projetosPessoais' do webservice
            androidHttpTransport.call(SOAP_ACTION + "projetosPessoais", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * pega o XML de projetosEquipes com tarefas do idUsuario
     * @return XML de projetosEquipes com as tarefas
     */
    public String projetosEquipes() {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetosEquipes");
        //setando parametros do método do webservice 'projetosEquipes'
        requisicao.addProperty(this.getUsuario());
        requisicao.addProperty(this.getForcarAtualizacao());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xml = null;
        try {//faz a chamada do método 'projetosEquipes' do webservice
            androidHttpTransport.call(SOAP_ACTION + "projetosEquipes", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * pega o XML de projetos da data de hoje com tarefas do idUsuario
     * @return XML de projetos com as tarefas de hoje
     */
    public String projetosHoje() {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetosHoje");
        //setando parametros do método do webservice 'projetosHoje'
        requisicao.addProperty(this.getUsuario());
        requisicao.addProperty(this.getForcarAtualizacao());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xml = null;
        try {//faz a chamada do método 'projetosHoje' do webservice
            androidHttpTransport.call(SOAP_ACTION + "projetosHoje", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * pega o XML de projetos com tarefas da semana atual
     * @return XML de projetos com as tarefas da semana
     */
    public String projetosSemana() {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetosSemana");
        //setando parametros do método do webservice 'projetosSemana'
        requisicao.addProperty(this.getUsuario());
        requisicao.addProperty(this.getForcarAtualizacao());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xml = null;
        try {//faz a chamada do método 'projetosSemana' do webservice
            androidHttpTransport.call(SOAP_ACTION + "projetosSemana", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * grava comentario de uma tarefa do usuario
     * @param idTarefa
     * @param textoComentario
     * @return XML de projetosPessoais com as tarefas
     */
    public String[] gravacomentario(int idTarefa, String textoComentario){
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "gravacomentario");
        //setando parametro 'idUsuario' do método do webservice 'gravacomentario'
        requisicao.addProperty(this.getUsuario());
        //setando parametro 'idTarefa' do método do webservice 'gravacomentario'
        PropertyInfo idTarefaWebservice = new PropertyInfo();
        idTarefaWebservice.setName("idTarefa");
        idTarefaWebservice.setValue(idTarefa);
        idTarefaWebservice.setType(Integer.class);
        requisicao.addProperty(idTarefaWebservice);
        //setando parametro 'textoComentario' do método do webservice 'gravacomentario'
        PropertyInfo textoComentarioWebservice = new PropertyInfo();
        textoComentarioWebservice.setName("textoComentario");
        textoComentarioWebservice.setValue(textoComentario);
        textoComentarioWebservice.setType(String.class);
        requisicao.addProperty(textoComentarioWebservice);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xmlTarefasPessoais = null;
        String xmlTarefasEquipes = null;
        String xmlTarefasHoje = null;
        String xmlTarefasSemana = null;
        String comentario = null;
        try {//faz a chamada do método 'gravacomentario' do webservice
            androidHttpTransport.call(SOAP_ACTION + "gravacomentario", envelope);
            //pegando a resposta
            Vector<String> resposta = (Vector<String>) envelope.getResponse();
            xmlTarefasPessoais = resposta.get(0);
            xmlTarefasEquipes = resposta.get(1);
            xmlTarefasHoje = resposta.get(2);
            xmlTarefasSemana = resposta.get(3);
            comentario = resposta.get(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] respostas = {xmlTarefasPessoais, xmlTarefasEquipes, xmlTarefasHoje, xmlTarefasSemana, comentario};
        return respostas;
    }

    /**
     * retorna o parametro 'idUsuario' para setar na requisiçao do webservice
     * @return PropertyInfo idUsuario
     */
    private PropertyInfo getUsuario(){
        PropertyInfo idUsuarioWebservice = new PropertyInfo();
        idUsuarioWebservice.setName("idUsuario");
        idUsuarioWebservice.setValue(this.getIdUsuario());
        idUsuarioWebservice.setType(Integer.class);
        return idUsuarioWebservice;
    }

    /**
     * retorna o parametro 'forcarAtualizacao' para setar na requisicao do webservice
     * @return PropertyInfo forcarAtualizacao
     */
    private PropertyInfo getForcarAtualizacao(){
        PropertyInfo forcarAtualizacao = new PropertyInfo();
        /**
         * no servidor do webservice o atributo 'novasTarefas' da classe 'AclUsuario' indica se ha
         * novas tarefas p/ serem enviadas, entao no forcarAtualizacao pode ser que nao carregue as tarefas
         * devido a esse atributo, portanto a flag abaixo 'forcarAtualizacao' indica que deve retornar as
         * tarefas independentemente do atributo 'novasTarefas' (miaaaaaaaaaauuuuuuuuuuu)
         */
        forcarAtualizacao.setName("forcarAtualizacao");
        forcarAtualizacao.setValue(this.isForcarAtualizacao());
        forcarAtualizacao.setType(Boolean.class);
        return forcarAtualizacao;
    }

}