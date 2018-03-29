package com.ibeiliao.deployment.log.test;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 功能:
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/17
 */
public class CommandTest {

    @Test
    public void ansibleTest() throws Exception{
        String[] args = {"ansible", "all", "-m",  "shell", "-a", "sh ~/test.sh", "-f", "20"};
        Process process = Runtime.getRuntime().exec(args);
        InputStream inputStream = process.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String s = "";
        while((s = reader.readLine()) != null){
            System.out.println(s);
        }
        reader.close();
    }

    @Test
    public void ansibleCopy() throws Exception{



        String[] args = {"ansible", "all", "-m",  "copy", "-a", "src=~/Downloads/monitor_shell_output.py   dest=~/temp/monitor_shell_output.py  force=false mode=744", "-f", "20"};
        Process process = Runtime.getRuntime().exec(args);
        InputStream inputStream = process.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String s = "";
        while((s = reader.readLine()) != null){
            System.out.println(s);
        }
        reader.close();

    }




}
