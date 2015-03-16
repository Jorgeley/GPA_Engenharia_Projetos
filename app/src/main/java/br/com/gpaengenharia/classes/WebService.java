package br.com.gpaengenharia.classes;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
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
    //chamada do webservice no servidor em segundo plano
    public WebserviceAsyncTask webserviceTask;
    //id do usuario para todas as operaçoes no webservice
    public int idUsuario;
    //xml resultante do AsyncTask
    public static String xml = null;
    //flag enviada ao servidor do webservice indicando p/ retornar os projetos, mesmo estando atualizados
    public static boolean login = false;

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
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
        //Log.i("idUsuario", String.valueOf(idUsuario));
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetosPessoais");
        //setando parametros do método do webservice 'projetosPessoais'
        requisicao.addProperty(getUsuario());
        PropertyInfo loginWebservice = new PropertyInfo();
        /**
         * no servidor do webservice o atributo 'novasTarefas' da classe 'AclUsuario' indica se ha
         * novas tarefas p/ serem enviadas, entao no login pode ser que nao carregue as tarefas
         * devido a esse atributo, portanto a flag abaixo 'login' indica que deve retornar as
         * tarefas independentemente do atributo 'novasTarefas' (miaaaaaaaaaauuuuuuuuuuu)
         */
        loginWebservice.setName("login");
        loginWebservice.setValue(login);
        loginWebservice.setType(Boolean.class);
        requisicao.addProperty(loginWebservice);
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        webserviceTask = new WebserviceAsyncTask(envelope, "projetosPessoais") {
            @Override
            public void onGetSoapEnvelope(Object resposta) {
                //pegando a resposta
               Log.i("resposta", (String) resposta);
               xml = (String) resposta;
            }
        };
        webserviceTask.execute();
        return xml;
    }

    /**
     * pega o XML de projetosEquipes com tarefas do idUsuario
     * @return XML de projetosEquipes com as tarefas
     */
    public String projetosEquipes() {
        //Log.i("idUsuario", String.valueOf(idUsuario));
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, "projetosEquipes");
        //setando parametros do método do webservice 'projetosEquipes'
        requisicao.addProperty(this.getUsuario());
        //evelopando a requisição
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(requisicao);
        final String[] xml = {null};
        webserviceTask = new WebserviceAsyncTask(envelope, "projetosEquipes") {
            @Override
            public void onGetSoapEnvelope(Object resposta) {
                //pegando a resposta
                xml[0] = (String) resposta;
            }
        };
        webserviceTask.execute();
        Log.i("xml", xml[0]);
        return xml[0];
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
        String xml = null;
        String comentario = null;
        try {//faz a chamada do método 'gravacomentario' do webservice
            androidHttpTransport.call(SOAP_ACTION + "gravacomentario", envelope);
            //pegando a resposta
            Vector<String> resposta = (Vector<String>) envelope.getResponse();
            xml = resposta.get(0);
            comentario = resposta.get(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] respostas = {xml, comentario};
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
     * Faz a chamada dos metodos do webservice no servidor www.grupo-gpa.com
     */
    private abstract class WebserviceAsyncTask extends AsyncTask<Void, Void, Object> implements WebServiceAsyncTaskResposta{
        private HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        private SoapSerializationEnvelope envelope;
        private String metodoSOAP;

        private WebserviceAsyncTask(SoapSerializationEnvelope envelope, String metodoSOAP) {
            this.envelope = envelope;
            this.metodoSOAP = metodoSOAP;
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                androidHttpTransport.call(SOAP_ACTION + metodoSOAP, this.envelope);
                return this.envelope.getResponse();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object resposta) {
            this.onGetSoapEnvelope(resposta);
        }

        //repassa para a classe que instanciou a resposta do webservice
        @Override
        public abstract void onGetSoapEnvelope(Object resposta);
    }

}