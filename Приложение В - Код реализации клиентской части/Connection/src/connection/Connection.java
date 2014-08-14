package connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple HTTP Client, which implementing get and post queries.
 */
public class Connection {
    
    /**
     * Метод читает из потока данные и преобразует в строку
     * @param in - входной поток
     * @param encoding - кодировка данных
     * @return - данные в виде строки
     */
    private String readStreamToString(InputStream in, String encoding) throws IOException {
	StringBuffer b = new StringBuffer();
	InputStreamReader r = new InputStreamReader(in, encoding);
	int c;
	while ((c = r.read()) != -1) {
            b.append((char)c);
	}
	return b.toString();
    }
	
    private List post(String url, QueryString query) throws IOException, JSONException {
        //устанавливаем соединение
        URLConnection conn = null;
        conn = new URL(url).openConnection();

	//мы будем писать POST данные в out stream
	conn.setDoOutput(true);
		
	OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "ASCII");
	out.write(query.toString());
	out.write("\r\n");
	out.flush();
	out.close();
		
	//читаем то, что отдал нам сервер и отправляем на уровень выше для обработки
	String html = readStreamToString(conn.getInputStream(), "UTF-8");
//        System.out.println(html);

        JSONObject myjson = new JSONObject(html);
        List result = null;
        if (!myjson.has("Error")) {
            result = new ArrayList();
            if (myjson.get("Respond").toString().equals("Success") | myjson.get("Respond").getClass().equals(Integer.class)) {
                result.add(myjson.get("Respond"));
                return result;
            }
            JSONArray array = myjson.getJSONArray("Respond");
            for (int i=0; i<array.length(); i++) {
                result.add((JSONObject)array.get(i));
            }
        } else {
            JOptionPane.showMessageDialog(null, myjson.get("Error"), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }
    
    public List request(String destination, Map fields) throws IOException, JSONException {
	QueryString query = new QueryString();
        JSONObject json = new JSONObject();
        Set<Entry<String, Object>> fieldsMap = fields.entrySet();
        for (Entry<String, Object> field:fieldsMap) {
            json.put(field.getKey(), field.getValue());
        }
        query.add(destination, json);
        List obj = this.post("http://projectl.org/scripts/" + destination + ".php", query);
        return obj;
    }
}
