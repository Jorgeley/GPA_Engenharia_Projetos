package br.com.gpaengenharia.classes;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import br.com.gpaengenharia.activities.AtvLogin;
import br.com.gpaengenharia.beans.Usuario;

public class WebService {
    //Namespace of the Webservice - can be found in WSDL
    private static String NAMESPACE = "http://192.168.1.103/GPA/public/webservice/soap/";
    //Webservice URL - WSDL File location
    private static String URL = "http://192.168.1.103/GPA/public/webservice/soap";//Make sure you changed IP address
    //SOAP Action URI again Namespace + Web method name
    private static String SOAP_ACTION = "http://192.168.1.103/GPA/public/webservice/soap#";

    public static Usuario loginWebservice(String login, String senha, String metodo) {
        //requisição SOAP
        SoapObject requisicao = new SoapObject(NAMESPACE, metodo);
        //setando propriedades do método do webservice 'autentica'
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
            androidHttpTransport.call(SOAP_ACTION + metodo, envelope);
            //pegando a resposta
            SoapObject resposta = (SoapObject) envelope.getResponse();
            usuario.setId((Integer) resposta.getPrimitiveProperty("id"));
            usuario.setNome((String) resposta.getPrimitiveProperty("nome"));
            usuario.setPerfil((String) resposta.getPrimitiveProperty("perfil"));
            //Log.i("usuario " + resposta.getPrimitiveProperty("nome"), String.valueOf(resposta));
        } catch (Exception e) {
            //se não conseguir autenticar retorna null
            usuario = null;
            e.printStackTrace();
        }
        return usuario;
    }
}