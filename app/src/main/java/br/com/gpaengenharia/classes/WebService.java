package br.com.gpaengenharia.classes;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.util.Vector;
import br.com.gpaengenharia.beans.Usuario;

/**
 * Classe responsavel pela comunicaçao entre o servidor e o app
 */
public class WebService {
    //private static String SERVIDOR = "192.168.0.118:8888";
    private static String SERVIDOR = "192.168.1.103:8888";
    //Namespace of the Webservice - can be found in WSDL
    private static String NAMESPACE = "http://"+SERVIDOR+"/GPA/public/webservice/soap/";
    //Webservice URL - WSDL File location
    private static String URL = "http://"+SERVIDOR+"/GPA/public/webservice/soap";//Make sure you changed IP address
    //SOAP Action URI again Namespace + Web method name
    private static String SOAP_ACTION = "http://"+SERVIDOR+"/GPA/public/webservice/soap#";

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
     * pega o XML de projetos com tarefas oo idUsuario
     * @param idUsuario
     * @return XML de projetos com as tarefas
     */
    public static String projetos(int idUsuario) {
        //Log.i("idUsuario", String.valueOf(idUsuario));
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetos");
        //setando parametros do método do webservice 'projetos'
        PropertyInfo idUsuarioWebservice = new PropertyInfo();
        idUsuarioWebservice.setName("idUsuario");
        idUsuarioWebservice.setValue(idUsuario);
        idUsuarioWebservice.setType(Integer.class);
        requisicao.addProperty(idUsuarioWebservice);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        //requisição HTTP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        String xml = null;
        try {//faz a chamada do método 'projetos' do webservice
            //Log.i("SOAP", SOAP_ACTION+"projetos");
            androidHttpTransport.call(SOAP_ACTION + "projetos", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
            //Log.i("xml", resposta);
        } catch (Exception e) {
            //se não conseguir autenticar retorna null
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * grava comentario de uma tarefa do usuario
     * @param idUsuario
     * @param idTarefa
     * @param textoComentario
     * @return XML de projetos com as tarefas
     */
    public static String gravaComentario(int idUsuario, int idTarefa, String textoComentario){
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "gravaComentario");
        //setando parametro 'idUsuario' do método do webservice 'gravaComentario'
        PropertyInfo idUsuarioWebservice = new PropertyInfo();
        idUsuarioWebservice.setName("idUsuario");
        idUsuarioWebservice.setValue(idUsuario);
        idUsuarioWebservice.setType(Integer.class);
        requisicao.addProperty(idUsuarioWebservice);
        //setando parametro 'idTarefa' do método do webservice 'gravaComentario'
        PropertyInfo idTarefaWebservice = new PropertyInfo();
        idTarefaWebservice.setName("idTarefa");
        idTarefaWebservice.setValue(idTarefa);
        idTarefaWebservice.setType(Integer.class);
        requisicao.addProperty(idTarefaWebservice);
        //setando parametro 'textoComentario' do método do webservice 'gravaComentario'
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
        String xml = null;
        try {//faz a chamada do método 'gravaComentario' do webservice
            //Log.i("SOAP", SOAP_ACTION+"projetos");
            androidHttpTransport.call(SOAP_ACTION + "gravaComentario", envelope);
            //pegando a resposta
            xml = (String) envelope.getResponse();
            //Log.i("xml", resposta);
        } catch (Exception e) {
            //se não conseguir autenticar retorna null
            e.printStackTrace();
        }
        return xml;
    }

}