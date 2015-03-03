package br.com.gpaengenharia.classes.xmls;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.beans.Tarefa;

/**
Lê xml nomeAquivoXML e grava em TreeMap <Projeto, List<Tarefa>> contendo
cada projeto com sua lista de tarefas
 */
public abstract class Xml{
    protected Context contexto;
    //TreeMap de beans contendo cada projeto com sua lista de tarefas
    private TreeMap<Projeto,List<Tarefa>> projetos = new TreeMap<Projeto,List<Tarefa>>();
    protected String nomeAquivoXML;//nome do arquivo para ler o xml

    public Xml(Context contexto){
        this.contexto = contexto;
    }

    /** abre o arquivo xml para leitura e retorna o TreeMap de beans <Projeto List<Tarefa>>
     * @return TreeMap Projeto(bean), ListTarefa(bean) */
    public TreeMap<Projeto, List<Tarefa>> leXml(){
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            //InputStream in_s = contexto.getAssets().open(nomeAquivoXML);
            InputStream in_s = this.contexto.openFileInput(this.nomeAquivoXML);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            parseXML(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.projetos;//retorna o TreeMap de beans
    }

    /**para cada node do Xml, adiciona no TreeMap de beans <Projeto List<Tarefa>>
     * @param parser
     * @throws XmlPullParserException,IOException */
    private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException{
        //lista de beans Tarefa node de cada projeto no TreeMap
        List<Tarefa> tarefas = new ArrayList<Tarefa>();
        int tipoEvento = parser.getEventType();
        Projeto projetoAtual = null;
        Set<String> tagsProjeto = new HashSet<String>(Arrays.asList("nome", "responsavel"));
        Set<String> tagsTarefa = new HashSet<String>(Arrays.asList("nome", "responsavel", "descricao", "comentarios", "vencimento"));
        //enquanto não chega no fim do documento xml...
        while (tipoEvento != XmlPullParser.END_DOCUMENT){
            String nomeNode = parser.getName();
            switch (tipoEvento){
                case XmlPullParser.START_TAG: //se é inicio de nova tag no xml...
                    if (nomeNode.equals("projeto")) {//...se tag é projeto...
                        projetoAtual = new Projeto(Parcel.obtain());
                        projetoAtual.setId(Integer.valueOf(parser.getAttributeValue(0)));
                        parser.nextTag();
                        nomeNode = parser.getName();
                        while (tagsProjeto.contains(nomeNode)) {
                            switch (nomeNode) {
                                case "nome": projetoAtual.setNome(parser.nextText()); break;
                                case "responsavel": projetoAtual.setResponsavel(parser.nextText()); break;
                            }
                            parser.nextTag();
                            nomeNode = parser.getName();
                        }
                    }
                    if (nomeNode.equals("tarefa")) {//...se tag é tarefa...
                        Tarefa tarefaAtual = new Tarefa(Parcel.obtain());
                        tarefaAtual.setId(Integer.valueOf(parser.getAttributeValue(0)));
                        parser.nextTag();
                        nomeNode = parser.getName();
                        while (tagsTarefa.contains(nomeNode)) {
                            switch (nomeNode){
                                case "nome" : tarefaAtual.setNome(parser.nextText()); break;
                                case "responsavel" : tarefaAtual.setResponsavel(parser.nextText()); break;
                                case "descricao" : tarefaAtual.setDescricao(parser.nextText()); break;
                                case "comentarios" : //TODO arrumar esse gato aqui
                                    parser.nextTag();
                                    break;
                                case "vencimento" :
                                    SimpleDateFormat formatoData = new SimpleDateFormat("MM/dd/yyyy", new Locale("pt", "BR"));
                                    Date data = null;
                                    try {
                                        data = formatoData.parse(parser.nextText());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    tarefaAtual.setVencimento(data);
                                    break;
                            }
                            tarefas.add(tarefaAtual);//adiciona bean Tarefa na lista
                            parser.nextTag();
                            nomeNode = parser.getName();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG://se é fim de tag...
                    if (nomeNode.equalsIgnoreCase("projeto")) {//...se tag é projeto...
                        //...adiciona no TreeMap os beans Projeto e List<Tarefa>
                        this.projetos.put(projetoAtual, tarefas);
                        tarefas = new ArrayList<Tarefa>();
                    }
                    break;
            }
            tipoEvento = parser.next();
        }
        //this.log();
    }

    /** gera log dos beans Projeto e Tarefa no TreeMap*/
    public void log(){
        Log.i("qtd",String.valueOf(this.projetos.size()));
        for (Map.Entry<Projeto, List<Tarefa>> projeto : this.projetos.entrySet()){
            String tituloProjeto = projeto.getKey().getNome();
            List<Tarefa> tarefasProjeto = projeto.getValue();
            for (Tarefa tarefa : tarefasProjeto){
                Log.i(projeto.getKey().getId()+':'+tituloProjeto, tarefa.getId()+':'+tarefa.getNome());
            }
        }
    }
}