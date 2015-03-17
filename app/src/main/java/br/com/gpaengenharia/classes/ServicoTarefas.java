package br.com.gpaengenharia.classes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import br.com.gpaengenharia.R;
import br.com.gpaengenharia.activities.AtvAdministrador;
import br.com.gpaengenharia.activities.AtvBase;
import br.com.gpaengenharia.activities.AtvColaborador;
import br.com.gpaengenharia.activities.AtvLogin;
import br.com.gpaengenharia.classes.xmls.XmlTarefasPessoais;

/**
 * serviço agendado pela classe AgendaServico para ser executado de 10 em 10 minutos
 * verifica via webservice se houve atualizaçoes nas tarefas do usuario
 */
public class ServicoTarefas extends Service implements Runnable{

    @Override
    public IBinder onBind(Intent intent) {
        return null; //sem interatividade com o serviço
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (AtvLogin.usuario != null)
            new Thread(this).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void run() {
        //baixa o XML de tarefas pessoais via werbservice e cria o arquivo localmente caso houve alteraçoes
        boolean xml = false;
        try {
            XmlTarefasPessoais xmlTarefasPessoais = new XmlTarefasPessoais(this);
            xml = xmlTarefasPessoais.criaXmlProjetosPessoaisWebservice(AtvLogin.usuario.getId(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (xml) {
            Intent intent;
            if (AtvLogin.usuario.getPerfil().equals("adm"))
                intent = new Intent(this, AtvAdministrador.class);
            else
                intent = new Intent(this, AtvColaborador.class);
            Notificacao.create(this,
                    "GPA",
                    "tarefas atualizadas",
                    R.drawable.logo_notificacao,
                    1,
                    intent
            );
            AtvBase.atualizaListView = true;
        }
    }
}
