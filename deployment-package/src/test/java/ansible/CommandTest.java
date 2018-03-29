package ansible;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class CommandTest {

    @Test
    public void ansibleTest() throws Exception {
        String[] args = {"ansible", "-i", "/etc/ansible/hosts.inventory", "all", "-m", "shell", "-a", "sh ~/test.sh", "-f", "20"};
        Process process = Runtime.getRuntime().exec(args);
        InputStream inputStream = process.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String s = "";
        while ((s = reader.readLine()) != null) {
            System.out.println(s);
        }
        reader.close();
    }

    @Test
    public void ansibleCopy() throws Exception {

        String[] args = {"ansible", "-i", "/etc/ansible/hosts.inventory", "all", "-m", "copy", "-a", "src=/Users/kevin/test.sh   dest=/home/compile/shell  force=yes mode=755", "-f", "20"};
        Process process = Runtime.getRuntime().exec(args);
        InputStream inputStream = process.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String s = "";
        while ((s = reader.readLine()) != null) {
            System.out.println(s);
        }
        reader.close();
    }


    @Test
    public void test() throws UnsupportedEncodingException {
        String str = URLDecoder.decode("\350\257\267\346\261\202", "UTF-8");
       String[] s = str.substring(1).split("%");
       StringBuffer s8 = new StringBuffer();
       for(int i=0;i<s.length;i++){
    	   s8.append("\\"+Integer.toOctalString(Integer.valueOf(s[i],16)));
       }
       System.out.println(s8.toString());
    }

}

